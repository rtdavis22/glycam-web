// Author: Robert Davis
//DONT USE THEM NO MO
//
// This program takes a pdb file name as a command-line argument and returns
// a protocol buffer with the following information:
//  - pairs of CYS residues which could be disulfide bonded
//  - HIS residues
//  - ASN residues

#include "config.h"

#include <algorithm>
#include <iostream>
#include <map>
#include <string>
#include <vector>

#include "gmml/gmml.h"

#include "src/cpp/PdbInfoPB.pb.h"

using std::map;
using std::string;
using std::vector;

using namespace gmml;

using molecular_dynamics::glycoprotein_builder::CYSPair;
using molecular_dynamics::glycoprotein_builder::GlycosylationSpot;
using molecular_dynamics::glycoprotein_builder::PdbInfo;
using molecular_dynamics::glycoprotein_builder::PdbResidueInfo;

namespace {

// This is the maximum distance that sulfurs in CYS residues can be in order
// for a disulfide bond to be deemed "possible".
const double kSulfurCutoff = 2.5;


//const char *kNAccessPath = 

}

void set_residue_info(PdbResidueInfo *info, int index, Triplet<int> *pdb_id) {
    info->set_index(index);
    info->set_chain_id(string(1, pdb_id->first));
    info->set_res_num(pdb_id->second);
    info->set_i_code(string(1, pdb_id->third));
}

void set_residue_info(PdbResidueInfo *info, PdbFileStructure::pdb_iterator it) {
    set_residue_info(info, it->second, it->first);
}

void add_close_cys_residues(PdbFileStructure *structure, PdbInfo *info) {
    CoordinateGrid<Triplet<int>*> grid(kSulfurCutoff);
    for (PdbFileStructure::pdb_iterator it = structure->pdb_begin();
            it != structure->pdb_end(); ++it) {
        const Residue *residue = structure->residues(it->second);
        for (int i = 0; i < residue->size(); i++) {
            grid.insert(residue->atoms(i)->coordinate(), it->first);
        }
    }

    for (PdbFileStructure::pdb_iterator it = structure->pdb_begin();
            it != structure->pdb_end(); ++it) {
        Residue *residue = structure->residues(it->second);
        if (residue->name() != "CYS")
            continue;
        int sulfur_index = structure->get_atom_index(it->second, "SG");
        if (sulfur_index == -1)
            continue;
        Atom *sulfur = structure->atoms(sulfur_index);
        vector<Triplet<int>*> *close_residues = grid.retrieve_adjacent_cells(
                sulfur->coordinate());
        vector<int> already_examined;
        for (int i = 0; i < close_residues->size(); i++) {
            Triplet<int> *pdb_id = (*close_residues)[i];
            int other_residue_index = structure->map_residue(pdb_id->first,
                                                             pdb_id->second,
                                                             pdb_id->third);
            if (other_residue_index == -1 || other_residue_index == it->second)
                continue;
            if (std::find(already_examined.begin(), already_examined.end(),
                          other_residue_index) != already_examined.end())
                continue;
            already_examined.push_back(other_residue_index);
            Residue *other_residue = structure->residues(other_residue_index);
            if (other_residue->name() != "CYS")
                continue;
            int other_sulfur_index =
                    structure->get_atom_index(other_residue_index, "SG");
            // The second check is to make sure we don't double-count.
            if (other_sulfur_index == -1 || other_sulfur_index < sulfur_index)
                continue;
            Atom *other_sulfur = structure->atoms(other_sulfur_index);
            double distance = measure(other_sulfur->coordinate(),
                                      sulfur->coordinate());
            if (distance < kSulfurCutoff) {
                CYSPair *cys_pair = info->add_close_cys_pair();
                PdbResidueInfo *residue1_info = cys_pair->mutable_cys1();
                set_residue_info(residue1_info, it);
                PdbResidueInfo *residue2_info = cys_pair->mutable_cys2();
                set_residue_info(residue2_info, other_residue_index, pdb_id);
                cys_pair->set_distance(distance);
                bool bonded = structure->is_bonded(sulfur_index,
                                                   other_sulfur_index);
                cys_pair->set_bonded(bonded);
            }
        }
    }
}

vector<int> *get_likely_oglycosylation_locations(const Structure& structure) {
    NetOGlycRunner runner(string(PROJECT_ROOT) + "deps/netoglyc/netOglyc");
    vector<vector<int>*> *proteins = find_proteins(structure);
    for (int i = 0; i < proteins->size(); i++) {
        FastaSequence *sequence = FastaSequence::create(structure,
                                                        *(*proteins)[i]);
        runner.add_sequence(*sequence);
        delete sequence;
    }
    NetOGlycResults *results = runner.run();
    vector<int> *likely_locations = new vector<int>;
    for (int i = 0; i < results->sequence_count(); i++) {
        const vector<int>& protein = *(*proteins)[i];
        const OGlycosylationLocations *locations =
                results->get_predicted_locations(i);
        const vector<int>& serine_locations = locations->get_serine_locations();
        for (int j = 0; j < serine_locations.size(); j++) {
            likely_locations->push_back(protein[serine_locations[j]]);
        }
        const vector<int>& threonine_locations =
                locations->get_threonine_locations();
        for (int j = 0; j < threonine_locations.size(); j++) {
            likely_locations->push_back(protein[threonine_locations[j]]);
        }
    }
    delete results;
    return likely_locations;
}


map<int, string> *get_contexts(const Structure& structure) {
    AminoAcidCodeSet amino_acid_codes;
    int context_size = 2;
    map<int, string> *context_map = new map<int, string>;
    vector<vector<int>*> *proteins = find_proteins(structure);
    for (int i = 0; i < proteins->size(); i++) {        
        const vector<int>& protein = *(*proteins)[i];
        for (int j = 0; j < protein.size(); j++) {
            string name = structure.residues(protein[j])->name();
            if (name == "ASN" || name == "THR" || name == "SER") {
                int lower_bound = std::max(0, j - context_size);
                int upper_bound = std::min(static_cast<int>(protein.size()) - 1,
                                           j + context_size);
                string context = "";
                for (int k = lower_bound; k <= upper_bound; k++) {
                    string code = structure.residues(protein[k])->name();
                    context += amino_acid_codes.get_fasta_letter(code);
                    if (j == k)
                        context += '*';
                }
                context_map->insert(map<int, string>::value_type(protein[j], context));
            }
        }
    }
    return context_map;
}


int main(int argc, char *argv[]) {
    if (argc != 2) {
        return -1;
    }

    string naccess_path = string(PROJECT_ROOT) + "deps/naccess/";
    NAccess::set_naccess_path(naccess_path.c_str());
    NAccessResults naccess_results(argv[1]);
    RsaInfo *rsa_info = naccess_results.rsa_info();

    PdbFileStructure *structure = PdbFileStructure::build(argv[1]);

    PdbInfo info;

    add_close_cys_residues(structure, &info);

    vector<int> *oglycosylation_locations =
            get_likely_oglycosylation_locations(*structure);

    vector<int> *asns_with_sequon = get_asns_with_sequon(*structure);

    map<int, string> *context_map = get_contexts(*structure);

    for (PdbFileStructure::pdb_iterator it = structure->pdb_begin();
            it != structure->pdb_end(); ++it) {
        Residue *residue = structure->residues(it->second);
        string name = residue->name();
        if (name == "HIS") {
            PdbResidueInfo *residue_info = info.add_his_residue();
            set_residue_info(residue_info, it);
        } else if (name == "ASN" || name == "SER" || name == "THR") {
            bool is_likely = false;
            if (name == "ASN") {
                is_likely = std::find(asns_with_sequon->begin(),
                                      asns_with_sequon->end(), it->second) !=
                        asns_with_sequon->end();
            } else if (name == "SER" || name == "THR") {
                is_likely = std::find(oglycosylation_locations->begin(),
                                      oglycosylation_locations->end(),
                                      it->second) !=
                        oglycosylation_locations->end();
            }
            GlycosylationSpot *spot = info.add_glycosylation_spot();
            spot->set_name(name);
            spot->set_likely(is_likely);
            const ResidueAccessibilityInfo *sasa_info =
                    rsa_info->lookup(it->first->first, it->first->second,
                                     it->first->third);
            if (sasa_info == NULL) {
                spot->set_sasa(-1);
            } else {
                spot->set_sasa(sasa_info->side_chains()->relative);
            }
            set_residue_info(spot->mutable_info(), it);

            map<int, string>::const_iterator it2 = context_map->find(it->second);
            string context = (it2 != context_map->end())?it2->second:"";
            spot->set_context(context);

        }
    }

    if (!info.SerializeToOstream(&std::cout)) {
        std::cerr << "Failed to write protocol buffer." << std::endl;
        return -1;
    }

    return 0;
}
