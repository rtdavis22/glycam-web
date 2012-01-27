// Author: Robert Davis
//
// This program takes a pdb file name as a command-line argument and returns
// a protocol buffer with the following information:
//  - pairs of CYS residues which could be disulfide bonded
//  - HIS residues
//  - ASN residues

#include <algorithm>
#include <iostream>
#include <string>
#include <vector>

#include "gmml/gmml.h"

#include "PdbInfoPB.pb.h"

using std::string;
using std::vector;

using namespace gmml;

using molecular_dynamics::glycoprotein_builder::CYSPair;
using molecular_dynamics::glycoprotein_builder::PdbInfo;
using molecular_dynamics::glycoprotein_builder::PdbResidueInfo;

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
    double CYS_cutoff = 2.5;

    CoordinateGrid<Triplet<int>*> grid(CYS_cutoff);
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
            if (other_sulfur_index == -1 || other_sulfur_index < sulfur_index)
                continue;
            Atom *other_sulfur = structure->atoms(other_sulfur_index);
            double distance = measure(other_sulfur->coordinate(),
                                      sulfur->coordinate());
            if (distance < CYS_cutoff) {
                CYSPair *cys_pair = info->add_close_cys_pair();
                PdbResidueInfo *residue1_info = cys_pair->mutable_cys1();
                residue1_info = new PdbResidueInfo;
                set_residue_info(residue1_info, it);
                PdbResidueInfo *residue2_info = cys_pair->mutable_cys2();
                residue2_info = new PdbResidueInfo;
                set_residue_info(residue2_info, other_residue_index, pdb_id);
                cys_pair->set_distance(distance);
                // Do this!:
                //cys_pair->set_bonded();
            }
        }
    }
}

int main(int argc, char *argv[]) {
    if (argc != 2) {
        return -1;
    }

    PdbFileStructure *structure = PdbFileStructure::build(argv[1]);

    PdbInfo info;

    add_close_cys_residues(structure, &info);

    for (PdbFileStructure::pdb_iterator it = structure->pdb_begin();
            it != structure->pdb_end(); ++it) {
        Residue *residue = structure->residues(it->second);
        if (residue->name() == "HIS") {
            PdbResidueInfo *residue_info = info.add_his_residue();
            set_residue_info(residue_info, it);
        }
    }

    if (!info.SerializeToOstream(&std::cout)) {
        std::cerr << "Failed to write protocol buffer." << std::endl;
        return -1;
    }

    return 0;
}
