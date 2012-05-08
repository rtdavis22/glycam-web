// Author: Robert Davis
//
#include "config.h"

#include <exception>
#include <iostream>
#include <fstream>
#include <stdexcept>
#include <string>

#include "gmml/gmml.h"

#include "src/cpp/BuildInfoPB.pb.h"
#include "src/cpp/PdbFilePB.pb.h"

using namespace gmml;

using std::string;
using std::cerr;
using std::cout;
using std::endl;

using molecular_dynamics::glycan_builder::BuildInfo;

using namespace pdb;

GlycoproteinBuildInfo *read_build_info(char *file) {
    GlycoproteinBuildInfo *build_info = new GlycoproteinBuildInfo;

    std::fstream input(file, std::ios::in | std::ios::binary);
    if (!build_info->ParseFromIstream(&input)) {
        return NULL;
    }

    return build_info;
}

// This should be in a different file.
void load_files() {
    add_path(string(PROJECT_ROOT) + "dat/");

    load_prep_file("prep_files/GLYCAM_06h.prep");
    load_prep_file("prep_files/HOH.prep");

    load_parameter_file("param_files/parm99.dat");
    load_parameter_file("param_files/GLYCAM_06h.dat");

    load_library_file("library_files/all_amino94.lib");
    load_library_file("library_files/all_aminoct94.lib");
    load_library_file("library_files/all_aminont94.lib");

    load_library_file("library_files/GLYCAM_amino_06h.lib");
    load_library_file("library_files/GLYCAM_aminoct_06h.lib");
    load_library_file("library_files/GLYCAM_aminont_06h.lib");
    load_library_file("library_files/tip3pbox.off");
}

PdbResidueId get_pdb_id(const PdbResidueInfo& info) {
    return PdbResidueId(info.chain_id()[0], info.res_num(), info.i_code()[0]);
}

class CreatePdbStructure {
  public:
    explicit CreatePdbStructure(const GlycoproteinBuildInfo *build_info)
            : build_info_(build_info),
              preprocessing_results_(&build_info->preprocessing_results()),
              pdb_file_(new PdbFile(File(build_info->pdb_file()))),
              builder_(*pdb_file_) {
        builder_.use_residue_map();
    }

    ~CreatePdbStructure() {
        delete pdb_file_;
    }

    PdbFileStructure *operator()() {
        initialize_builder();
        return builder_.build();
    }

  private:
    void initialize_builder() {
        add_residues_to_remove();
        add_his_mappings();
        add_cys_bond_mappings();
        add_chain_mappings();
        add_gap_mappings();
        add_glycosylation_mappings();
    }

    void add_residues_to_remove() {
        for (int i = 0; i < preprocessing_results_->residue_to_remove_size();
                i++) {
            const PdbResidueInfo& info =
                    preprocessing_results_->residue_to_remove(i);
            PdbResidueId pdb_id = get_pdb_id(info);
            builder_.add_residue_to_remove(&pdb_id);
        } 
    }

    void add_his_mappings() {
        for (int i = 0; i < preprocessing_results_->his_mapping_size(); i++) {
            const PdbMapping& mapping = preprocessing_results_->his_mapping(i);
            PdbResidueId pdb_id = get_pdb_id(mapping.residue());
            builder_.add_mapping(pdb_id, mapping.mapped_name());
        }
    }

    void add_cys_bond_mappings() {
        for (int i = 0; i < preprocessing_results_->close_cys_pair_size();
                i++) {
            const CYSPair& pair = preprocessing_results_->close_cys_pair(i);
            const PdbResidueInfo& first = pair.cys1();
            const PdbResidueInfo& second = pair.cys2();
            string mapped_name = (pair.bonded())?"CYX":"CYS";
            builder_.add_mapping(get_pdb_id(first), mapped_name);
            builder_.add_mapping(get_pdb_id(second), mapped_name);
        }
    }

    void add_chain_mappings() {
        for (int i = 0; i < preprocessing_results_->chain_info_size(); i++) {
            add_chain_mappings(preprocessing_results_->chain_info(i));
        }
    }

    void add_chain_mappings(const ChainInfo& chain_info) {
        add_nterminal_mapping(chain_info.start(), chain_info.nterminal_type());
        add_cterminal_mapping(chain_info.end(), chain_info.cterminal_type());
    }

    void add_nterminal_mapping(const PdbResidueInfo& residue,
                               NTerminalType type) {
        PdbResidueId pdb_id = get_pdb_id(residue);
        string mapped_name = builder_.map_pdb_residue(&pdb_id, residue.name(),
                                                      false, false);
        if (type == NH3) {
            mapped_name = "N" + mapped_name;
        }
        builder_.add_mapping(pdb_id, mapped_name);
    }

    void add_cterminal_mapping(const PdbResidueInfo& residue,
                               CTerminalType type) {
        PdbResidueId pdb_id = get_pdb_id(residue);
        string mapped_name = builder_.map_pdb_residue(&pdb_id, residue.name(),
                                                      false, false);
        if (type == CO2) {
            mapped_name = "C" + mapped_name;
        }
        builder_.add_mapping(pdb_id, mapped_name);
    }

    void add_gap_mappings() {
        for (int i = 0; i < preprocessing_results_->chain_gap_size(); i++) {
            add_gap_mappings(preprocessing_results_->chain_gap(i));
        }
    }

    void add_gap_mappings(const ChainGap& chain_gap) {
        add_cterminal_mapping(chain_gap.start(), chain_gap.cterminal_type());
        add_nterminal_mapping(chain_gap.end(), chain_gap.nterminal_type());
    }

    void add_glycosylation_mappings() {
        for (int i = 0; i < build_info_->glycosylation_size(); i++) {
            const GlycosylationInfo& info = build_info_->glycosylation(i);
            const GlycosylationSpot& spot = info.spot();
            const PdbResidueInfo& residue_info = spot.info();
            if (spot.name() == "ASN") {
                builder_.add_mapping(get_pdb_id(residue_info), "NLN");
            }
        }
    }

    PdbFile *pdb_file_;
    const GlycoproteinBuildInfo *build_info_;
    const PreprocessingResults *preprocessing_results_;
    PdbStructureBuilder builder_;
};

class CreateGlycoprotein {
  public:
    CreateGlycoprotein(PdbFileStructure *structure,
                       GlycoproteinBuildInfo *build_info)
            : structure_(structure), build_info_(build_info),
              preprocessing_results_(&build_info_->preprocessing_results()) {}

    void operator()() {
        add_or_remove_cys_bonds();
        add_chain_caps();
        add_gap_caps();
    }

  private:
    void add_or_remove_cys_bonds() {
        for (int i = 0; i < preprocessing_results_->close_cys_pair_size();
                i++) {
            add_or_remove_cys_bond(preprocessing_results_->close_cys_pair(i));
        }
    }

    void add_or_remove_cys_bond(const CYSPair& cys_pair) {
        int sulfur1 = get_cys_sulfur_index(cys_pair.cys1());
        int sulfur2 = get_cys_sulfur_index(cys_pair.cys2());
        if (sulfur1 == -1 || sulfur2 == -1) {
            throw std::invalid_argument("Could not find sulfur atom in CYS.");
        }
        if (cys_pair.bonded()) {
            structure_->add_bond(sulfur1, sulfur2);
        } else {
            structure_->remove_bond(sulfur1, sulfur2);
        }
    }

    int get_cys_sulfur_index(const PdbResidueInfo& residue) {
        PdbResidueId pdb_id = get_pdb_id(residue);
        int residue_index = structure_->map_residue(pdb_id);
        if (residue_index == -1) {
            throw std::invalid_argument("Invalid Pdb Residue Id.");
        }
        return structure_->get_atom_index(residue_index, "SG");
    }

    void add_chain_caps() {
        for (int i = 0; i < preprocessing_results_->chain_info_size(); i++) {
            add_chain_caps(preprocessing_results_->chain_info(i));
        }
    }

    void add_chain_caps(const ChainInfo& chain_info) {
        attach_nterminal_cap(chain_info.start(),
                             chain_info.nterminal_type());
        attach_cterminal_cap(chain_info.end(),
                             chain_info.cterminal_type());
    }

    void add_gap_caps() {
        for (int i = 0; i < preprocessing_results_->chain_gap_size(); i++) {
            add_gap_caps(preprocessing_results_->chain_gap(i));
        }
    }

    void add_gap_caps(const ChainGap& gap) {
        attach_cterminal_cap(gap.start(), gap.cterminal_type());
        attach_nterminal_cap(gap.end(), gap.nterminal_type());
    }

    void attach_nterminal_cap(const PdbResidueInfo& residue,
                              NTerminalType type) {
        if (type != COCH3) {
            return;
        }
        PdbResidueId pdb_id = get_pdb_id(residue);
        int residue_index = structure_->map_residue(pdb_id);
        if (is_already_capped(residue_index, "N")) {
            return;
        }
        Structure *cap = build_nterminal_cap(type);
        structure_->set_tail(residue_index, "N");
        structure_->attach(cap);
        delete cap;
    }

    void attach_cterminal_cap(const PdbResidueInfo& residue,
                              CTerminalType type) {
        if (type != NHCH3 && type != NH2) {
            return;
        }
        PdbResidueId pdb_id = get_pdb_id(residue);
        int residue_index = structure_->map_residue(pdb_id);
        if (is_already_capped(residue_index, "C")) {
            return;
        }
        Structure *cap = build_cterminal_cap(type);
        structure_->set_tail(residue_index, "C");
        structure_->attach(cap);
        delete cap;
    }

    bool is_already_capped(int residue_index, const string& atom_name) {
        int atom_index = structure_->get_atom_index(residue_index, atom_name);

        if (atom_index == -1) {
            throw std::invalid_argument("Atom not in residue.");
        }

        const Structure::AdjList& adj_atoms = structure_->bonds(atom_index);

        for (int i = 0; i < adj_atoms.size(); i++) {
            if (structure_->get_residue_index(adj_atoms[i]) != residue_index) {
                return true;
            }
        }
        return false;
    }

    Structure *build_nterminal_cap(NTerminalType type) {
        if (type == COCH3) {
            Structure *cap = build("ACE");
            cap->set_head(0, "C");
            return cap;
        }
        throw std::invalid_argument("Not a valid N-Terminal cap.");
    }

    Structure *build_cterminal_cap(CTerminalType type) {
        Structure *cap = NULL;
        if (type == NHCH3) {
            cap = build("NME");
        } else if (type == NH2) {
            cap = build("NHE");
        } else {
            throw std::invalid_argument("Not a valid C-Terminal cap.");
        }
        cap->set_head(0, "N");
        return cap;
    }

    PdbFileStructure *structure_;
    const GlycoproteinBuildInfo *build_info_;
    const PreprocessingResults *preprocessing_results_;
};

void check_for_unknown_residues_and_atoms(
        const PdbMappingResults& mapping_results) {
    bool unknown = false;

    for (int i = 0; i < mapping_results.unknown_residue_count(); i++) {
        unknown = true;
        const PdbResidueId *pdb_id = mapping_results.get_unknown_residue(i);
        cerr << "Unknown residue " << pdb_id->chain_id << " " <<
                pdb_id->res_num << " " + pdb_id->i_code << "." << endl;
    }

    for (int i = 0; i < mapping_results.unknown_atom_count(); i++) {
        unknown = true;
        int atom_index = mapping_results.get_unknown_atom(i);
        cerr << "Unknown atom " << atom_index << "." << endl;
    }

    if (unknown) {
        throw std::logic_error("One or more unknown atoms or residues.");
    }
}

void print_files(const PdbFileStructure *structure) {
    structure->print_pdb_file("structure.pdb");
    structure->print_amber_top_file("structure.top");
    structure->print_coordinate_file("structure.rst");
}

int main(int argc, char *argv[]) {
    load_files();
    load_amino_acid_mappings();

    if (argc != 2) {
        cerr << "Command expects one argument." << endl;
        return -1;
    }

    GlycoproteinBuildInfo *build_info = read_build_info(argv[1]);
    if (build_info == NULL) {
        cerr << "Unable to read protocol buffer." << endl;
        return -1;
    }

    PdbFileStructure *structure = CreatePdbStructure(build_info)();

    CreateGlycoprotein(structure, build_info)();

    check_for_unknown_residues_and_atoms(*structure->get_mapping_results());

    // Remove this line when the stuff below is gone.
    const PreprocessingResults& preprocessing_results = build_info->preprocessing_results();

    // This should be moved to the class above and modified.
    for (int i = 0; i < build_info->glycosylation_size(); i++) {
        const GlycosylationInfo& info = build_info->glycosylation(i);
        const PdbResidueInfo& spot_info = info.spot().info();
        const BuildInfo& glycan_info = info.glycan();
        Structure *glycan = glycam_build_without_aglycon(glycan_info.glycan());
        int spot_index = structure->map_residue(PdbResidueId(spot_info.chain_id()[0],
                                                spot_info.res_num(),
                                                spot_info.i_code()[0]));
        int tail_atom = -1;
        if (info.spot().name() == "ASN") {
            tail_atom = structure->get_atom_index(spot_index, "ND2");
            // Chi 1
            structure->set_dihedral(spot_index, "N",
                                    spot_index, "CA",
                                    spot_index, "CB",
                                    spot_index, "CG",
                                    191.6);
            // Chi 2
            structure->set_dihedral(spot_index, "CA",
                                    spot_index, "CB",
                                    spot_index, "CG",
                                    spot_index, "ND2",
                                    177.6);
        }
        if (tail_atom != -1) {
            int new_res = structure->attach_from_head(glycan, tail_atom);
            
            // Psi
            structure->set_dihedral(spot_index, "CB",
                                    spot_index, "CG",
                                    spot_index, "ND2",
                                    new_res, "C1",
                                    177.3);
            // Phi
            structure->set_dihedral(spot_index, "CG",
                                    spot_index, "ND2",
                                    new_res, "C1",
                                    new_res, "O5",
                                    -99.0); // 261.0
        }
    }

    print_files(structure);

    return 0;
}
