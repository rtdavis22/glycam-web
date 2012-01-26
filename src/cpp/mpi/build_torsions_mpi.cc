// Author: Robert Davis
//
// This program takes as a command-line argument a protocol buffer that includes a glycan sequence
// in condensed GLYCAM nomenclature, a list of linkage values indicating the possible phi, psi,
// and omega values for each linkages, and, optionally, solvation information. It builds all
// structures conforming to the specified glycosidic torsions and outputs an AMBER topology file and
// restart and pdb files for each structure to the current directory. The topology file is named
// "structure.top", and the restart and pdb file names start with a 1-based index for the structure
// followed by ".rst" or ".pdb". 
// The program writes a protocol buffer to standard output that represents the results of the
// build. This includes, for each structure, a list of all the custom glycosidic torsion values set
// within the structure and the resulting minimized energy of the structure. The order of the
// structures in the protocol buffer corresponds to the indices of the file names.

#include "config.h"

#include "gmml/gmml.h"

#include "BuildInfoPB.pb.h"
#include "BuildResultsPB.pb.h"

#include <algorithm>
#include <iostream>
#include <fstream>
#include <vector>

#include <mpi.h>

#include "config.h"

using namespace gmml;
using namespace std;

using molecular_dynamics::oligosaccharide_builder::BuildInfo;
using molecular_dynamics::oligosaccharide_builder::BuildResults;

std::vector<std::string>& split(const std::string& str, char delimiter,
                                std::vector<std::string>& elements) {
    std::stringstream ss(str);
    std::string item;
    while (getline(ss, item, delimiter))
        elements.push_back(item);
    return elements;
}

int get_index(vector<double>& vec, double number)  {
    vector<double>::iterator it;
    if ((it = std::find(vec.begin(), vec.end(), number)) == vec.end()) {
        vec.push_back(number);
        return vec.size() - 1;
    }
    return std::distance(vec.begin(), it);
}

struct SolvationInfo {
    double distance;
    double closeness;
};

int main(int argc, char *argv[]) {
    int rank, size;
    MPI_Status stat;
    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    add_path(string(PROJECT_ROOT) + "dat");

    string gas_phase_minimize_file = "min.in";
    string solvated_minimize_file = "solvated_min.in";

    load_parameter_file("param_files/parm99.dat.mod");
    load_parameter_file("param_files/Glycam_06g.dat");
    load_prep_file("prep_files/Glycam_06.prep");
    load_prep_file("prep_files/Neu5Gc_a_06.prep");
    load_prep_file("prep_files/ACE.prep");
    load_prep_file("prep_files/MEX.prep");
    load_prep_file("prep_files/sulfate.prep");
    load_library_file("library_files/all_amino94.lib");
    load_library_file("library_files/tip3pbox.off");

    BuildInfo build_info;

    std::fstream input(argv[1], ios::in | ios::binary);
    if (!build_info.ParseFromIstream(&input)) {
        return -2;
    }

    string glycan = build_info.glycan();

    Structure *structure = glycam_build(glycan);
    TorsionCombinationBuilder b(*structure);


    for (int i = 0; i < build_info.linkage_size(); i++) {
        const BuildInfo::Linkage& linkage = build_info.linkage(i);
        for (int j = 0; j < linkage.phi_value_size(); j++)
            b.add_phi_value(i, linkage.phi_value(j));
        for (int j = 0; j < linkage.psi_value_size(); j++)
            b.add_psi_value(i, linkage.psi_value(j));
        for (int j = 0; j < linkage.omega_value_size(); j++)
            b.add_omega_value(i, linkage.omega_value(j));
    }


    SolvationInfo *solvation_info = NULL;

    list<TCBStructure*> *structures = b.build();
    list<TCBStructure*>::iterator it;
    int index = 0;
    for (it = structures->begin(); it != structures->end(); ++it) {
        if (index++%(size - 1) + 1 != rank) {
            delete *it;
            continue;
        }

        MinimizationResults *results =
            (*it)->minimize(gas_phase_minimize_file);

        if (solvation_info != NULL) {
            LibraryFileStructure *b = build_library_file_structure("TIP3PBOX");
            //cout << "solvating with parameters " << solvation_info->distance <<
            //        " " << solvation_info->closeness << endl;
            SolvatedStructure *ss = solvate(**it, *b, solvation_info->distance,
                                            solvation_info->closeness);
            ss->minimize(solvated_minimize_file);
            ss->print_pdb_file(to_string(index) + ".pdb");
            ss->print_coordinate_file(to_string(index) + ".rst");
            ss->print_amber_top_file(to_string(index) + ".top");
            delete ss;
            delete b;
        } else {
            if (it == structures->begin())
                (*it)->print_amber_top_file("structure.top");
            (*it)->print_pdb_file(to_string(index) + ".pdb");
            (*it)->print_coordinate_file(to_string(index) + ".rst"); 
        }

        double vals[1];
        vals[0] = (results != NULL)?results->energy:-1;
        MPI_Send(vals, 1, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD);

        delete *it;
    }
    delete structures;
    delete structure;

    if (rank == 0) {
        vector<vector<vector<double> > > *build_info = b.get_build_info();

        // linkage_changes[i][j] is true if the ith linkage's phi/psi/omega (j)
        // varies.    
        int num_linkages = (*build_info)[0].size();
        vector<vector<bool> > linkage_changes(num_linkages);
        // phi_angles[i] is all the phi angles linkage i can take
        vector<vector<double> > phi_angles(num_linkages);
        vector<vector<double> > psi_angles(num_linkages);
        vector<vector<double> > omega_angles(num_linkages);
        for (int i = 0; i < build_info->size(); i++) {
            const vector<vector<double> >& linkages = (*build_info)[i];
            for (int j = 0; j < linkages.size(); j++) {
                const vector<double>& linkage = linkages[j];
                get_index(phi_angles[j], linkage[0]);
                get_index(psi_angles[j], linkage[1]);
                get_index(omega_angles[j], linkage[2]);
            }
        }

        for (int i = 0; i < num_linkages; i++) {
            linkage_changes[i].push_back(phi_angles[i].size() > 1);
            linkage_changes[i].push_back(psi_angles[i].size() > 1);
            linkage_changes[i].push_back(omega_angles[i].size() > 1);
        }

        BuildResults results;

        for (int i = 0; i < build_info->size(); i++) {
            BuildResults::Structure *structure = results.add_structure();

            double in[1];
            MPI_Recv(in, 1, MPI_DOUBLE, i%(size - 1) + 1, 0, MPI_COMM_WORLD,
                     &stat);
            structure->set_energy(in[0]);

            const vector<vector<double> >& linkages = (*build_info)[i];
            for (int j = 0; j < linkages.size(); j++) {
                const vector<double>& linkage = linkages[j];
                for (int k = 0; k < linkage.size(); k++) {
                    if (!linkage_changes[j][k])
                        continue;

                    BuildResults::FlexibleLinkage *flexible_linkage =
                            structure->add_flexible_linkage();
                    flexible_linkage->set_index(j);
                    
                    BuildResults::FlexibleLinkage::Angle type;
                    if (k == 0)
                        type = BuildResults::FlexibleLinkage::PHI;
                    else if (k == 1)
                        type = BuildResults::FlexibleLinkage::PSI;
                    else if (k == 2)
                        type = BuildResults::FlexibleLinkage::OMEGA;
                    
                    flexible_linkage->set_angle(type);

                    flexible_linkage->set_value(linkage[k]);
                    
                    string angle("");
                    if (k == 0)
                        angle = "phi";
                    else if (k == 1)
                        angle = "psi";
                    else if (k == 2)
                        angle = "omega";
                }
            }
        }
        delete build_info;

        if (!results.SerializeToOstream(&cout)) {
            cerr << "Failed to write protocol buffer" << endl;
            return -1;
        }
    }
    MPI_Finalize();
    return 0;
}
