#include "config.h"

#include <iostream>
#include <string>
#include <vector>

#include "gmml/gmml.h"

#include "src/cpp/PdbFilePB.pb.h"

using std::string;
using std::vector;

using namespace gmml;

using namespace pdb;

namespace {

double kSulfurCutoff = 2.5;

}

void set_residue_info(PdbResidueInfo *info,
                      const PdbResidueId *pdb_id) {
    info->set_chain_id(string(1, pdb_id->chain_id));
    info->set_res_num(pdb_id->res_num);
    info->set_i_code(string(1, pdb_id->i_code));
}

void add_close_cys_residues(PdbFileStructure *structure, PreprocessingInfo *info) {
    CoordinateGrid<int> grid(kSulfurCutoff);
    for (int i = 0; i < structure->residue_count(); i++) {
        const Residue *residue = structure->residues(i);
        for (int j = 0; j < residue->size(); j++) {
            grid.insert(residue->atoms(j)->coordinate(), i);
        }
    }

    for (int i = 0; i < structure->residue_count(); i++) {
        const PdbResidueId *pdb_id = structure->map_residue_index(i);
        if (pdb_id == NULL)
            continue;
 
        Residue *residue = structure->residues(i);
        if (residue->name() != "CYS")
            continue;
        int sulfur_index = structure->get_atom_index(i, "SG");
        if (sulfur_index == -1)
            continue;
        Atom *sulfur = structure->atoms(sulfur_index);
        vector<int> *close_residues = grid.retrieve_adjacent_cells(
                sulfur->coordinate());
        vector<int> already_examined;
        for (int j = 0; j < close_residues->size(); j++) {
            const PdbResidueId *close_pdb_id = structure->map_residue_index((*close_residues)[j]);
            if (close_pdb_id == NULL) continue;
            int other_residue_index = structure->map_residue(close_pdb_id->chain_id,
                                                             close_pdb_id->res_num,
                                                             close_pdb_id->i_code);
            if (other_residue_index == -1 || other_residue_index == i)
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
                set_residue_info(residue1_info, pdb_id);
                PdbResidueInfo *residue2_info = cys_pair->mutable_cys2();
                set_residue_info(residue2_info, close_pdb_id);
                cys_pair->set_distance(distance);
                bool bonded = structure->is_bonded(sulfur_index,
                                                   other_sulfur_index);
                cys_pair->set_bonded(bonded);
            }
        }
    }
}

void add_chain(const PdbFileStructure& structure, PreprocessingInfo *info,
               const PdbChain *chain) {
    vector<int> residue_indices;
    residue_indices.reserve(chain->size());
    for (int i = 0; i < chain->size(); i++) {
        const PdbResidueId *pdb_id = chain->at(i);
        int residue_index = structure.map_residue(pdb_id->chain_id,
                                                  pdb_id->res_num,
                                                  pdb_id->i_code);
        residue_indices.push_back(residue_index);
    }

    int cur_start = 0;
    for (int i = 1; i < chain->size(); i++) {
        int carbon_index = structure.get_atom_index(residue_indices[i - 1],
                                                    "C");
        int nitrogen_index = structure.get_atom_index(residue_indices[i],
                                                      "N");
        if (carbon_index == -1 || nitrogen_index == -1) {
            // This should not happen.
            return;
        }
        if (!structure.is_bonded(carbon_index, nitrogen_index)) {
            //Add chain from chain_start to i - 1
            ChainInfo *chain_info = info->add_chain_info();
            set_residue_info(chain_info->mutable_start(),
                             chain->at(cur_start));
            set_residue_info(chain_info->mutable_end(),
                             chain->at(i - 1));
            chain_info->set_start_name(structure.residues(
                    residue_indices[cur_start])->name());
            chain_info->set_end_name(structure.residues(
                    residue_indices[i - 1])->name());
            if (cur_start > 0) {
                chain_info->set_nterminal_type(ChainInfo::COCH3);
            } else {
                chain_info->set_nterminal_type(ChainInfo::NH3);
            }
            chain_info->set_missing_nterminal(cur_start != 0);
            chain_info->set_cterminal_type(ChainInfo::NH2);
            chain_info->set_missing_cterminal(true);
            cur_start = i;
        }
    }
    ChainInfo *chain_info = info->add_chain_info();
    set_residue_info(chain_info->mutable_start(),
                     chain->at(cur_start));
    set_residue_info(chain_info->mutable_end(),
                     chain->tail());
    chain_info->set_start_name(structure.residues(residue_indices[cur_start])->name());
    chain_info->set_end_name(structure.residues(residue_indices.back())->name());
    if (cur_start > 0) {
        chain_info->set_nterminal_type(ChainInfo::COCH3);
    } else {
        chain_info->set_nterminal_type(ChainInfo::NH3);
    }
    chain_info->set_missing_nterminal(cur_start != 0);
    chain_info->set_cterminal_type(ChainInfo::CO2);
    chain_info->set_missing_cterminal(false);
}

int main(int argc, char *argv[]) {
    if (argc != 2) {
        return -1;
    }

    load_amino_acid_mappings();

    gmml::add_path(std::string(PROJECT_ROOT) + "dat/");

    gmml::load_prep_file("prep_files/Glycam_06.prep");
    gmml::load_prep_file("prep_files/Neu5Gc_a_06.prep");
    gmml::load_prep_file("prep_files/sulfate.prep");
    gmml::load_prep_file("prep_files/ACE.prep");
    gmml::load_prep_file("prep_files/MEX.prep");
    load_parameter_file("param_files/parm99.dat");
    load_parameter_file("param_files/Glycam_06h.dat");
    load_prep_file("prep_files/HOH.prep");
    load_library_file("library_files/all_amino94.lib");
    load_library_file("library_files/all_aminoct94.lib");
    load_library_file("library_files/all_aminont94.lib");

    PreprocessingInfo info;

    File file(argv[1]);
    PdbFile pdb_file(file);
    PdbStructureBuilder builder(pdb_file);
    // Added so that all HIS residues can be recognized.
    builder.add_mapping("HIS", "HIP");
    PdbFileStructure *structure = builder.build();

    add_close_cys_residues(structure, &info);

    for (int i = 0; i < structure->residue_count(); i++) {
        const PdbResidueId *pdb_id = structure->map_residue_index(i);
        if (pdb_id == NULL)
            continue;

        Residue *residue = structure->residues(i);
        string name = residue->name();
        if (name == "HIS") {
            PdbResidueInfo *residue_info = info.add_his_residue();
            set_residue_info(residue_info, pdb_id);
        }
    }

    AminoAcidCodeSet amino_acids;
    for (int i = 0; i < structure->chain_count(); i++) {
        const PdbChain *chain = structure->chains(i);
        if (chain->size() > 2) {
            add_chain(*structure, &info, chain);
        }
    }

    const PdbMappingResults *mapping_results = structure->get_mapping_results();
    for (int i = 0; i < mapping_results->unknown_residue_count(); i++) {
        const PdbResidueId *pdb_id = mapping_results->get_unknown_residue(i);
        PdbResidueInfo *unknown_residue = info.add_unknown_residue();
        set_residue_info(unknown_residue, pdb_id);
        int residue_index = structure->map_residue(pdb_id->chain_id,
                                                   pdb_id->res_num,
                                                   pdb_id->i_code);
        const Residue *residue = structure->residues(residue_index);
        unknown_residue->set_name(residue->name());
    }

    for (int i = 0; i < mapping_results->unknown_atom_count(); i++) {
        int serial = mapping_results->get_unknown_atom(i);
        int atom_index = structure->map_atom(serial);
        int residue_index = structure->get_residue_index(atom_index);
        const PdbResidueId *pdb_id =
                structure->map_residue_index(residue_index);
        UnknownAtomInfo *unknown_atom = info.add_unknown_atom();
        set_residue_info(unknown_atom->mutable_residue_info(), pdb_id);
        unknown_atom->mutable_residue_info()->set_name(structure->residues(residue_index)->name());
        unknown_atom->set_serial(serial);
        unknown_atom->set_name(structure->atoms(atom_index)->name());
    }

    if (!info.SerializeToOstream(&std::cout)) {
        std::cerr << "Failed to write protocol buffer." << std::endl;
        return -1;
    }

    return 0;
}
