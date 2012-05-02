#include "config.h"

#include <cassert>

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

PdbFileStructure *kStructure;

PdbFileStructure *kUnmappedStructure;

PreprocessingInfo *kPreprocessingInfo;

double kSulfurCutoff = 2.5;

}  // namespace

void set_residue_info(PdbResidueInfo *info,
                      const PdbResidueId *pdb_id) {
    info->set_chain_id(string(1, pdb_id->chain_id));
    info->set_res_num(pdb_id->res_num);
    info->set_i_code(string(1, pdb_id->i_code));
}

/*
class AddCloseCysResidues {
  public:
    static double kSulfurCutoff = 2.5;

    AddCloseCysResidues() : grid_(kSulfurCutoff) {
        for (int i = 0; i < kStructure->residue_count(); i++) {
            const Residue *residue = structure->residues(i);
            if (residue->name() == "CYS" && residue->atoms("SG") != NULL)
                grid_.insert(residue->atoms("CG")->coordinate(), i);
        }
    }

    void operator()() {
        for (int i = 0; i < kStructure->residues_count(); i++) {
            visit_residue(i);
        }
    }

  private:
    void visit_residue(int index) {
        if (kStructure->residues(index)->name() == "CYS") {
            
        }
    }

    CoordinateGrid<int> grid_;
};
*/

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
            int other_residue_index = structure->map_residue(PdbResidueId(close_pdb_id->chain_id,
                                                             close_pdb_id->res_num,
                                                             close_pdb_id->i_code));
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

class AddAminoAcidChains {
  public:
    void operator()() {
        for (int i = 0; i < kStructure->chain_count(); i++) {
            visit(*kStructure->chains(i));
        }
    }

  private:
    void visit(const PdbChain& chain) {
        if (!is_amino_acid_chain(chain))
            return;
        ChainInfo *chain_info = kPreprocessingInfo->add_chain_info();
        set_chain_head_info(chain_info, chain);
        set_chain_tail_info(chain_info, chain);
        set_chain_gap_info(chain_info, chain);
    }

    void set_chain_head_info(ChainInfo *chain_info, const PdbChain& chain) {
        string head_name = get_name(*chain.head());
        if (head_name == "ACE") {
            set_residue_info(chain_info->mutable_start(), *chain.at(1));
            chain_info->add_possible_nterminal(COCH3);
            chain_info->set_nterminal_type(COCH3);
        } else {
            set_residue_info(chain_info->mutable_start(), *chain.head());
            chain_info->add_possible_nterminal(NH3);
            chain_info->set_nterminal_type(NH3);
            if (amino_acid_can_be_nonionic(*chain.head())) {
                chain_info->add_possible_nterminal(COCH3);
            }
        }
    }

    bool amino_acid_can_be_nonionic(const PdbResidueId& residue) {
        int index = kUnmappedStructure->map_residue(residue);
        string name = get_name(residue);
        if (name == "HIS")
            name = "HID";
        Structure *non_ionic_residue = build(name);
        assert(non_ionic_residue != NULL);
        Residue *mapped_residue =
                CompleteResidue()(kUnmappedStructure->residues(index),
                                  non_ionic_residue);
        delete non_ionic_residue;
        if (mapped_residue != NULL) {
            delete mapped_residue;
            return true;
        }
        return false;
    }

    std::string get_name(const PdbResidueId& residue) {
        int index = get_residue_index(residue);
        assert(index != -1);
        return kStructure->residues(index)->name();
    }

    int get_residue_index(const PdbResidueId& residue) {
        return kStructure->map_residue(residue);
    }

    void set_chain_tail_info(ChainInfo *chain_info, const PdbChain& chain) {
        string tail_name = get_name(*chain.tail());
        if (tail_name == "NME") {
            set_residue_info(chain_info->mutable_end(),
                             *chain.at(chain.size() - 2));
            chain_info->add_possible_cterminal(NHCH3);
            chain_info->set_cterminal_type(NHCH3);
        } else {
            set_residue_info(chain_info->mutable_end(), *chain.tail());
            chain_info->add_possible_cterminal(CO2);
            chain_info->set_cterminal_type(CO2);
            if (amino_acid_can_be_nonionic(*chain.tail())) {
                chain_info->add_possible_cterminal(NH2);
                chain_info->add_possible_cterminal(NHCH3);
            }
        }
    }

    void set_chain_gap_info(ChainInfo *chain_info, const PdbChain& chain) {
        for (int i = 1; i < chain.size(); i++) {
            if (gap_exists(chain, i)) {
                int tail_index = get_residue_index(*chain.at(i - 1));
                int head_index = get_residue_index(*chain.at(i));

                ChainGap *gap = kPreprocessingInfo->add_chain_gap();
                gap->mutable_chain_info()->CopyFrom(*chain_info);

                set_gap_head_info(gap, chain, i - 1);
                set_gap_tail_info(gap, chain, i);
            }
        }
    }

    bool gap_exists(const PdbChain& chain, int residue_after_gap) {
        int index_after_gap = get_residue_index(chain, residue_after_gap);
        int index_before_gap = get_residue_index(chain, residue_after_gap - 1);
        assert(index_after_gap != -1 && index_before_gap != -1);

        const Residue *after_gap = kStructure->residues(index_after_gap);
        const Residue *before_gap = kStructure->residues(index_before_gap);
        if (is_tail_cap(before_gap->name()) || is_head_cap(after_gap->name()))
            return true;
        
        return !is_nc_connected(index_before_gap, index_after_gap);
    }

    bool is_nc_connected(int n_side_residue, int c_side_residue) const {
        int c_index = kStructure->get_atom_index(n_side_residue, "C");
        int n_index = kStructure->get_atom_index(c_side_residue, "N");
        assert(c_index != -1 && n_index != -1);
        return kStructure->is_bonded(c_index, n_index);
    }

    int get_residue_index(const PdbChain& chain, int chain_index) {
        return get_residue_index(*chain.at(chain_index));
    }

    void set_gap_head_info(ChainGap *gap, const PdbChain& chain,
                           int residue_before_gap) {
        int residue_index = get_residue_index(chain, residue_before_gap);
        string residue_name = kStructure->residues(residue_index)->name();
        if (is_tail_cap(residue_name)) {
            assert(residue_before_gap > 0);
            CTerminalType terminal_type = get_cterminal_type(residue_name);
            gap->set_cterminal_type(terminal_type);
            set_residue_info(gap->mutable_start(),
                             *chain.at(residue_before_gap - 1));
            gap->add_possible_cterminal(terminal_type);
        } else {
            set_residue_info(gap->mutable_start(),
                             *chain.at(residue_before_gap));
            gap->add_possible_cterminal(CO2);
            if (amino_acid_can_be_nonionic(*chain.at(residue_before_gap))) {
                gap->add_possible_cterminal(NH2);
                gap->add_possible_cterminal(NHCH3);
                gap->set_cterminal_type(NHCH3);
            } else {
                gap->set_cterminal_type(CO2);
            }
        }
    }

    void set_gap_tail_info(ChainGap *gap, const PdbChain& chain,
                           int residue_after_gap) {
        int residue_index = get_residue_index(chain, residue_after_gap);
        string residue_name = kStructure->residues(residue_index)->name();
        if (is_head_cap(residue_name)) {
            assert(residue_after_gap < chain.size() - 1);
            NTerminalType terminal_type = get_nterminal_type(residue_name);
            gap->set_nterminal_type(terminal_type);
            set_residue_info(gap->mutable_end(),
                             *chain.at(residue_after_gap + 1));
            gap->add_possible_nterminal(terminal_type);
        } else {
            set_residue_info(gap->mutable_end(),
                             *chain.at(residue_after_gap));
            gap->add_possible_nterminal(NH3);
            if (amino_acid_can_be_nonionic(*chain.at(residue_after_gap))) {
                gap->add_possible_nterminal(COCH3);
                gap->set_nterminal_type(COCH3);
            } else {
                gap->set_nterminal_type(NH3);
            }
        }
    }


    bool is_amino_acid_chain(const PdbChain& chain) {
        if (chain.size() < 2)
            return false;
        for (int i = 0; i < chain.size(); i++) {
            if (!is_valid_chain_residue(*chain.at(i)))
                return false;
        }
        return true;
    }

    bool is_valid_chain_residue(const PdbResidueId& residue) {
        string name = get_name(residue);
        return amino_acids_.lookup(name) || is_cap(name);
    }

    static bool is_cap(const string& name) {
        return is_tail_cap(name) || is_head_cap(name);
    }

    static bool is_tail_cap(const string& name) {
        return name == "NME" || name == "NHE";
    }

    static CTerminalType get_cterminal_type(const string& name) {
        assert(is_tail_cap(name));
        if (name == "NHE")
            return NH2;
        else if (name == "NME")
            return NHCH3;
        assert(false);
    }

    static bool is_head_cap(const string& name) {
        return name == "ACE";
    }

    static NTerminalType get_nterminal_type(const string& name) {
        assert(is_head_cap(name));
        if (name == "ACE")
            return COCH3;
        assert(false);
    }

    void set_residue_info(PdbResidueInfo *residue_info,
                          const PdbResidueId& id) {
        residue_info->set_chain_id(string(1, id.chain_id));
        residue_info->set_res_num(id.res_num);
        residue_info->set_i_code(string(1, id.i_code));
        residue_info->set_name(get_name(id));
    }

    AminoAcidCodeSet amino_acids_;
};

// I should probably have a separate class for managing this stuff.
void load_prep_files() {
    load_prep_file("prep_files/GLYCAM_06h.prep");
    load_prep_file("prep_files/HOH.prep");
}

void load_parameter_set() {
    load_parameter_file("param_files/parm99.dat");
    load_parameter_file("param_files/GLYCAM_06h.dat");
}

void load_library_files() {
    load_library_file("library_files/all_amino94.lib");
    load_library_file("library_files/all_aminoct94.lib");
    load_library_file("library_files/all_aminont94.lib");
    load_library_file("library_files/GLYCAM_amino_06h.lib");
    load_library_file("library_files/GLYCAM_aminoct_06h.lib");
    load_library_file("library_files/GLYCAM_aminont_06h.lib");
}

void configure_environment() {
    add_path(string(PROJECT_ROOT) + "dat/");
    load_prep_files();
    load_parameter_set();
    load_library_files();
    load_amino_acid_mappings();
}

void set_mapped_structure(const PdbFile& pdb_file) {
    PdbStructureBuilder builder(pdb_file);
    builder.use_residue_map();
    builder.add_mapping("HIS", "HIP");
    kStructure = builder.build();
}

void set_unmapped_structure(const PdbFile& pdb_file) {
    PdbStructureBuilder builder(pdb_file);
    builder.ignore_residue_map();
    kUnmappedStructure = builder.build();
}

class AddHisResidues {
  public:
    AddHisResidues()
            : hid_(build("HID")), hie_(build("HIE")), hip_(build("HIP")) {
        assert(hid_ != NULL);
        assert(hie_ != NULL);
        assert(hip_ != NULL);
    }

    ~AddHisResidues() {
        delete hid_;
        delete hie_;
        delete hip_;
    }

    void operator()() const {
        for (int i = 0; i < kStructure->residue_count(); i++) {
            if (kStructure->residues(i)->name() == "HIS") {
                HisResidue *his = kPreprocessingInfo->add_his_residue();
                const PdbResidueId *pdb_id = kStructure->map_residue_index(i);
                assert(pdb_id != NULL);
                set_residue_info(his->mutable_residue_info(), pdb_id);
                add_possible_mappings(his, i);
            }
        }
    }

  private:
    void add_possible_mappings(HisResidue *his, int index) const {
        if (can_be_mapped_to(index, hid_))
            his->add_possible_mapping_type(HID);
        if (can_be_mapped_to(index, hie_))
            his->add_possible_mapping_type(HIE);
        if (can_be_mapped_to(index, hip_))
            his->add_possible_mapping_type(HIP);
    }

    bool can_be_mapped_to(int residue_index, const Structure *structure) const {
        Residue *mapped_residue =
                CompleteResidue()(kUnmappedStructure->residues(residue_index),
                                  structure);
        if (mapped_residue == NULL) {
            return false;
        } else {
            delete mapped_residue;
            return true;
        }
    }

    const Structure *hid_;
    const Structure *hie_;
    const Structure *hip_;
};

int main(int argc, char *argv[]) {
    if (argc != 2) {
        return -1;
    }

    configure_environment();

    PreprocessingInfo info;
    kPreprocessingInfo = &info;

    File file(argv[1]);
    PdbFile pdb_file(file);
    set_unmapped_structure(pdb_file);
    set_mapped_structure(pdb_file);

    add_close_cys_residues(kStructure, &info);

/*
    for (int i = 0; i < kStructure->residue_count(); i++) {
        const PdbResidueId *pdb_id = kStructure->map_residue_index(i);
        if (pdb_id == NULL)
            continue;

        Residue *residue = kStructure->residues(i);
        string name = residue->name();
        if (name == "HIS") {
            PdbResidueInfo *residue_info = info.add_his_residue();
            set_residue_info(residue_info, pdb_id);
        }
    }
*/
    AddHisResidues()();

    AminoAcidCodeSet amino_acids;
/*
    for (int i = 0; i < kStructure->chain_count(); i++) {
        const PdbChain *chain = kStructure->chains(i);
        if (is_amino_acid_chain(*kStructure, chain)) {
            add_chain(*kStructure, &info, chain);
        }
    }
*/
    AddAminoAcidChains()();

    const PdbMappingResults *mapping_results = kStructure->get_mapping_results();
    for (int i = 0; i < mapping_results->unknown_residue_count(); i++) {
        const PdbResidueId *pdb_id = mapping_results->get_unknown_residue(i);
        PdbResidueInfo *unknown_residue = info.add_unknown_residue();
        set_residue_info(unknown_residue, pdb_id);
        int residue_index = kStructure->map_residue(PdbResidueId(pdb_id->chain_id,
                                                    pdb_id->res_num,
                                                    pdb_id->i_code));
        const Residue *residue = kStructure->residues(residue_index);
        unknown_residue->set_name(residue->name());
    }

    for (int i = 0; i < mapping_results->unknown_atom_count(); i++) {
        int serial = mapping_results->get_unknown_atom(i);
        int atom_index = kStructure->map_atom(serial);
        int residue_index = kStructure->get_residue_index(atom_index);
        const PdbResidueId *pdb_id =
                kStructure->map_residue_index(residue_index);
        UnknownAtomInfo *unknown_atom = info.add_unknown_atom();
        set_residue_info(unknown_atom->mutable_residue_info(), pdb_id);
        unknown_atom->mutable_residue_info()->set_name(kStructure->residues(residue_index)->name());
        unknown_atom->set_serial(serial);
        unknown_atom->set_name(kStructure->atoms(atom_index)->name());
    }

    for (int i = 0; i < mapping_results->removed_hydrogen_count(); i++) {
        const PdbRemovedAtom *removed_atom = mapping_results->get_removed_hydrogen(i);

        //const PdbResidueId *pdb_id =
        //        structure->map_residue_index(residue_index);

        RemovedAtomInfo *removed_atom_info = info.add_removed_atom();

        set_residue_info(removed_atom_info->mutable_residue_info(), &removed_atom->residue());

        // map the pdb_id and try to get a residue name to set here.
        //removed_atom->mutable_residue_info()->set_name(structure->residues(residue_index)->name());
        removed_atom_info->set_serial(removed_atom->serial());
        removed_atom_info->set_name(removed_atom->atom().name());
    }

    if (!info.SerializeToOstream(&std::cout)) {
        std::cerr << "Failed to write protocol buffer." << std::endl;
        return -1;
    }

    return 0;
}
