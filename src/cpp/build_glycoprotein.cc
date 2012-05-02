// Author: Robert Davis
//
#include "config.h"

#include <exception>
#include <iostream>
#include <fstream>
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

/*
using molecular_dynamics::glycoprotein_builder::CYSPair;
using molecular_dynamics::glycoprotein_builder::GlycosylationInfo;
using molecular_dynamics::glycoprotein_builder::GlycosylationSpot;
using molecular_dynamics::glycoprotein_builder::PdbInfo;
using molecular_dynamics::glycoprotein_builder::PdbMapping;
using molecular_dynamics::glycoprotein_builder::PdbModificationInfo;
using molecular_dynamics::glycoprotein_builder::PdbResidueInfo;
*/

using namespace pdb;

/*
const char *kHeadMap[] = {
  "ALA", "NALA",
  "ARG", "NARG",
  "ASN", "NASN",
  "ASP", "NASP",
  "CYS", "NCYS",
  "CYX", "NCYX",
  "GLN", "NGLN",
  "GLU", "NGLU",
  "GLY", "NGLY",
  "HID", "NHID",
  "HIE", "NHIE",
  "HIP", "NHIP",
  "ILE", "NILE",
  "LEU", "NLEU",
  "LYS", "NLYS",
  "MET", "NMET",
  "PHE", "NPHE",
  "PRO", "NPRO",
  "SER", "NSER",
  "THR", "NTHR",
  "TRP", "NTRP",
  "TYR", "NTYR",
  "VAL", "NVAL",
  "HIS", "NHIS",
  "GUA", "DG5",
  "ADE", "DA5",
  "CYT", "DC5",
  "THY", "DT5",
  "G", "RG5",
  "A", "RA5",
  "C", "RC5",
  "U", "RU5",
  "DG", "DG5",
  "DA", "DA5",
  "DC", "DC5",
  "DT", "DT5"
};

const char *kTailMap[] = {
  "ALA", "CALA",
  "ARG", "CARG",
  "ASN", "CASN",
  "ASP", "CASP",
  "CYS", "CCYS",
  "CYX", "CCYX",
  "GLN", "CGLN",
  "GLU", "CGLU",
  "GLY", "CGLY",
  "HID", "CHID",
  "HIE", "CHIE",
  "HIP", "CHIP",
  "ILE", "CILE",
  "LEU", "CLEU",
  "LYS", "CLYS",
  "MET", "CMET",
  "PHE", "CPHE",
  "PRO", "CPRO",
  "SER", "CSER",
  "THR", "CTHR",
  "TRP", "CTRP",
  "TYR", "CTYR",
  "VAL", "CVAL",
  "HIS", "CHIS",
  "GUA", "DG3",
  "ADE", "DA3",
  "CYT", "DC3",
  "THY", "DT3",
  "G", "RG3",
  "A", "RA3",
  "C", "RC3",
  "U", "RU3",
  "DG", "DG3",
  "DA", "DA3",
  "DC", "DC3",
  "DT", "DT3"
};
*/

const char *kResidueMap[] = {
  "HIS", "HIE",
};

#ifndef ARRAY_SIZE
#define ARRAY_SIZE(x) (sizeof(x)/sizeof(x[0]))
#endif

GlycoproteinBuildInfo *read_build_info(char *file) {
    GlycoproteinBuildInfo *build_info = new GlycoproteinBuildInfo;

    std::fstream input(file, std::ios::in | std::ios::binary);
    if (!build_info->ParseFromIstream(&input)) {
        return NULL;
    }

    return build_info;
}

int main(int argc, char *argv[]) {
    gmml::add_path(std::string(PROJECT_ROOT) + "dat/");
    
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


    load_amino_acid_mappings();

    //for (int i = 0; i < ARRAY_SIZE(kHeadMap); i += 2) {
    //    add_head_mapping(kHeadMap[i], kHeadMap[i + 1]);
    //}

    //for (int i = 0; i < ARRAY_SIZE(kTailMap); i += 2) {
    //    add_tail_mapping(kTailMap[i], kTailMap[i + 1]);
    //}

    for (int i = 0; i < ARRAY_SIZE(kResidueMap); i += 2) {
        add_residue_mapping(kResidueMap[i], kResidueMap[i + 1]);
    }

    if (argc < 2) {
        return -1;
    }

cout << "reading build info" << endl;
    GlycoproteinBuildInfo *build_info = read_build_info(argv[1]);
cout << "done reading" << endl;

cout << "getting preprocessing results" << endl;
    const PreprocessingResults& preprocessing_results =
            build_info->preprocessing_results();
cout << "done" << endl;

cout << "reading pdb " << build_info->pdb_file() << endl;
    PdbFile pdb(File(build_info->pdb_file()));
cout << "done" << endl;

    PdbStructureBuilder builder(pdb);
    builder.use_residue_map();

    cout << "residues to remove: " << preprocessing_results.residue_to_remove_size() << endl;
    for (int i = 0; i < preprocessing_results.residue_to_remove_size(); i++) {
        const PdbResidueInfo& info = preprocessing_results.residue_to_remove(i);
        PdbResidueId id(info.chain_id()[0], info.res_num(), info.i_code()[0]);
        cout << "removed residue " << id.chain_id << " " << id.res_num << " " << id.i_code << endl;
        builder.add_residue_to_remove(&id);
    }

    cout << "his mappings: " << preprocessing_results.his_mapping_size() << endl;
    for (int i = 0; i < preprocessing_results.his_mapping_size(); i++) {
        const PdbMapping& mapping = preprocessing_results.his_mapping(i);
        const PdbResidueInfo& info = mapping.residue();
        builder.add_mapping(PdbResidueId(info.chain_id()[0], info.res_num(),
                            info.i_code()[0]), mapping.mapped_name());
    }

    for (int i = 0; i < preprocessing_results.close_cys_pair_size(); i++) {
        const CYSPair& pair = preprocessing_results.close_cys_pair(i);
        const PdbResidueInfo& first = pair.cys1();
        const PdbResidueInfo& second = pair.cys2();
        std::string mapped_name = (pair.bonded())?"CYX":"CYS";
        builder.add_mapping(PdbResidueId(first.chain_id()[0], first.res_num(),
                            first.i_code()[0]), mapped_name);
        builder.add_mapping(PdbResidueId(second.chain_id()[0], second.res_num(),
                            second.i_code()[0]), mapped_name);
    }

    for (int i = 0; i < preprocessing_results.residue_to_remove_size(); i++) {
        const PdbResidueInfo& residue = preprocessing_results.residue_to_remove(i);
        PdbResidueId pdb_id(residue.chain_id()[0], residue.res_num(), residue.i_code()[0]);
        builder.add_residue_to_remove(&pdb_id);
    }


    for (int i = 0; i < preprocessing_results.chain_info_size(); i++) {
        const ChainInfo& chain_info = preprocessing_results.chain_info(i);
        const PdbResidueInfo& start = chain_info.start();
        PdbResidueId dd(start.chain_id()[0], start.res_num(), start.i_code()[0]);

        if (chain_info.nterminal_type() == COCH3) {
//            bool already_capped = (start.name() == "ACE");
//            if (!already_capped) {
              PdbResidueId cc(start.chain_id()[0], start.res_num(), start.i_code()[0]);
              string cur_mapped_name = builder.map_pdb_residue(&cc, start.name(), false, false);
              builder.add_mapping(cc, cur_mapped_name);
               cout << "adding a mapping to " << cur_mapped_name << endl;
//            }
        } else if (chain_info.nterminal_type() == NH3) {
            string name = start.name();
            if (name == "HIS") {
                PdbResidueId cc(start.chain_id()[0], start.res_num(), start.i_code()[0]);
                name = "N" + builder.map_pdb_residue(&cc, "HIS", false, false);
            } else if (name == "CYS") {
                name = "N" + builder.map_pdb_residue(&dd, "CYS", false, false);
            } else {
                name = "N" + name;
            }
            builder.add_mapping(PdbResidueId(start.chain_id()[0], start.res_num(), start.i_code()[0]),
                                name);
            cout << "adding a mapping to " << name << endl;
            
        }

        const PdbResidueInfo& end = chain_info.end();
        PdbResidueId cc(end.chain_id()[0], end.res_num(), end.i_code()[0]);
        if (chain_info.cterminal_type() == NH2 || chain_info.cterminal_type() == NHCH3) {
//            bool already_capped = (end.name() == "NME" || end.name() == "NHE");
//            if (!already_capped) {
                string cur_mapped_name = builder.map_pdb_residue(&cc, end.name(), false, false);
                builder.add_mapping(cc, cur_mapped_name);
//            }
        } else if (chain_info.cterminal_type() == CO2) {
            string name = end.name();
            if (name == "HIS") {
                name = "C" + builder.map_pdb_residue(&cc, "HIS", false, false);
            } else if (name == "CYS") {
                name = "C" + builder.map_pdb_residue(&cc, "CYS", false, false);
            } else {
                name = "C" + name;
            }
            builder.add_mapping(PdbResidueId(end.chain_id()[0], end.res_num(), end.i_code()[0]),
                                name);;
        }
    }

    for (int i = 0; i < preprocessing_results.chain_gap_size(); i++) {
        const ChainGap& chain_gap = preprocessing_results.chain_gap(i);

        const PdbResidueInfo& start = chain_gap.start();
        PdbResidueId cc(start.chain_id()[0], start.res_num(), start.i_code()[0]);
        if (chain_gap.cterminal_type() == NH2 || chain_gap.cterminal_type() == NHCH3) {
            string cur_mapped_name = builder.map_pdb_residue(&cc, start.name(), false, false);
            builder.add_mapping(cc, cur_mapped_name);
        } else if (chain_gap.cterminal_type() == CO2) {
            string name = start.name();
            if (name == "HIS") {
                name = "C" + builder.map_pdb_residue(&cc, "HIS", false, false);
            } else {
                name = "C" + name;
            }
            builder.add_mapping(PdbResidueId(start.chain_id()[0], start.res_num(), start.i_code()[0]),
                                name);;
        }





        const PdbResidueInfo& end = chain_gap.end();

        if (chain_gap.nterminal_type() == COCH3) {
            PdbResidueId cc(end.chain_id()[0], end.res_num(), end.i_code()[0]);
            string cur_mapped_name = builder.map_pdb_residue(&cc, end.name(), false, false);
            builder.add_mapping(cc, cur_mapped_name);
            cout << "adding a mapping to " << cur_mapped_name << endl;
        } else if (chain_gap.nterminal_type() == NH3) {
            string name = end.name();
            if (name == "HIS") {
                PdbResidueId cc(end.chain_id()[0], end.res_num(), end.i_code()[0]);
                name = "N" + builder.map_pdb_residue(&cc, "HIS", false, false);
            } else {
                name = "N" + name;
            }
            builder.add_mapping(PdbResidueId(end.chain_id()[0], end.res_num(), end.i_code()[0]),
                                name);
            cout << "adding a mapping to " << name << endl;

        }





    }

    for (int i = 0; i < build_info->glycosylation_size(); i++) {
        const GlycosylationInfo& info = build_info->glycosylation(i);
        const GlycosylationSpot& spot = info.spot();
        const PdbResidueInfo& residue_info = spot.info();
        if (spot.name() == "ASN") {
            builder.add_mapping(PdbResidueId(residue_info.chain_id()[0],
                                residue_info.res_num(),
                                residue_info.i_code()[0]),
                                "NLN");
        }
    }

    PdbResidueId xx('C', 87);
    cout << "mapping C 87 to " <<  builder.map_pdb_residue(&xx, "PRO", true, false) << endl;

    PdbFileStructure *structure = builder.build();

    const PdbMappingResults *mapping_results = structure->get_mapping_results();
    for (int i = 0; i < mapping_results->unknown_residue_count(); i++) {
        const PdbResidueId *pdb_id = mapping_results->get_unknown_residue(i);
        cout << "Unknown residue " << pdb_id->chain_id << " " << pdb_id->res_num << " " <<
                pdb_id->i_code << " " << structure->residues(*pdb_id)->name() << endl;
    }

    for (int i = 0; i < mapping_results->unknown_atom_count(); i++) {
        int index = mapping_results->get_unknown_atom(i);
        cout << "unknown atom: " << index << endl;
    }

    for (int i = 0; i < preprocessing_results.chain_info_size(); i++) {
        const ChainInfo& chain_info = preprocessing_results.chain_info(i);

        const PdbResidueInfo& start = chain_info.start();
        if (chain_info.nterminal_type() == COCH3) {
            int index = structure->map_residue(PdbResidueId(start.chain_id()[0],
                                               start.res_num(),
                                               start.i_code()[0]));
            int n_index = structure->get_atom_index(index, "N");
            const Structure::AdjList& adj_atoms = structure->bonds(n_index);

            bool is_already_capped = false;
            for (int j = 0; j < adj_atoms.size(); j++) {
                if (structure->get_residue_index(adj_atoms[j]) != index) {
                    is_already_capped = true;
                    break;
                }
            }
            if (!is_already_capped) {

                structure->set_tail(index, "N");
                cout << "attaching an ACE to " << index << endl;
                Structure *t = build("ACE");
                if (t == NULL) 
                    cout << "NO ACE!!!" << endl;
                t->set_head(0, "C");
                int new_index = structure->attach(t);
                cout << "ACE index is " << new_index << endl;
            }
        }

        const PdbResidueInfo& end = chain_info.end();

        int tail_residue = structure->map_residue(PdbResidueId(end.chain_id()[0],
                                                               end.res_num(),
                                                               end.i_code()[0]));

            int c_index = structure->get_atom_index(tail_residue, "C");
            const Structure::AdjList& adj_atoms = structure->bonds(c_index);
            bool is_already_capped = false;
            for (int j = 0; j < adj_atoms.size(); j++) {
                if (structure->get_residue_index(adj_atoms[j]) != tail_residue) {
                    is_already_capped = true;
                    break;
                }
            }
            if (is_already_capped) continue;



        structure->set_tail(tail_residue, "C");
        if (chain_info.cterminal_type() == NHCH3) {
            Structure *nme = build("NME");
            if (nme == NULL)
                  cout << "NO NME" << endl;
            nme->set_head(0, "N");
            int index = structure->attach(nme);
            cout << "new NME index is " << index << endl;
        } else if (chain_info.cterminal_type() == NH2 && end.name() != "NHE") {
            Structure *nhe = build("NHE");
            if (nhe == NULL)
                  cout << "NO NHE" << endl;
            nhe->set_head(0, "N");
            int index = structure->attach(nhe);
            cout << "new NHE index is " << index << endl;
        }
    }

    for (int i = 0; i < preprocessing_results.chain_gap_size(); i++) {
        const ChainGap& chain_gap = preprocessing_results.chain_gap(i);

        const PdbResidueInfo& start = chain_gap.start();

        int tail_residue = structure->map_residue(PdbResidueId(start.chain_id()[0],
                                                               start.res_num(),
                                                               start.i_code()[0]));
        structure->set_tail(tail_residue, "C");
        if (chain_gap.cterminal_type() == NHCH3) {
            Structure *nme = build("NME");
            nme->set_head(0, "N");
            int index = structure->attach(nme);
        } else if (chain_gap.cterminal_type() == NH2) {
            Structure *nhe = build("NHE");
            nhe->set_head(0, "N");
            int index = structure->attach(nhe);
        }



        const PdbResidueInfo& end = chain_gap.end();

        if (chain_gap.nterminal_type() == COCH3) {
            int index = structure->map_residue(PdbResidueId(end.chain_id()[0],
                                               end.res_num(),
                                               end.i_code()[0]));
            structure->set_tail(index, "N");
            Structure *t = build("ACE");
            t->set_head(0, "C");
            int new_index = structure->attach(t);
        }

    }


    for (int i = 0; i < preprocessing_results.close_cys_pair_size(); i++) {
        const CYSPair& pair = preprocessing_results.close_cys_pair(i);
        const PdbResidueInfo& first = pair.cys1();
        const PdbResidueInfo& second = pair.cys2();
        int cys1_index = structure->map_residue(PdbResidueId(first.chain_id()[0],
                                                first.res_num(),
                                                first.i_code()[0]));
        int cys2_index = structure->map_residue(PdbResidueId(second.chain_id()[0],
                                                second.res_num(),
                                                second.i_code()[0]));
        int atom1 = structure->get_atom_index(cys1_index, "SG");
        int atom2 = structure->get_atom_index(cys2_index, "SG");
        if (pair.bonded()) {
            structure->add_bond(atom1, atom2);
        } else {
            structure->remove_bond(atom1, atom2);
        }
    }

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

    cout << "writing pdb " << endl;

    structure->print_pdb_file("structure.pdb");

    cout << "done writing pdb" << endl;

    structure->print_amber_top_file("structure.top");
    structure->print_coordinate_file("structure.rst");

    return 0;
}
