// Author: Robert Davis
//
#include "config.h"

#include <exception>
#include <iostream>
#include <fstream>
#include <string>

#include "gmml/gmml.h"

#include "src/cpp/PdbInfoPB.pb.h"

using namespace gmml;

using std::cerr;
using std::cout;
using std::endl;

using molecular_dynamics::glycoprotein_builder::CYSPair;
using molecular_dynamics::glycoprotein_builder::GlycosylationInfo;
using molecular_dynamics::glycoprotein_builder::GlycosylationSpot;
using molecular_dynamics::glycoprotein_builder::PdbInfo;
using molecular_dynamics::glycoprotein_builder::PdbMapping;
using molecular_dynamics::glycoprotein_builder::PdbModificationInfo;
using molecular_dynamics::glycoprotein_builder::PdbResidueInfo;

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


const char *kResidueMap[] = {
  "HIS", "HIE",
};

#ifndef ARRAY_SIZE
#define ARRAY_SIZE(x) (sizeof(x)/sizeof(x[0]))
#endif


int main(int argc, char *argv[]) {
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

    for (int i = 0; i < ARRAY_SIZE(kHeadMap); i += 2) {
        add_head_mapping(kHeadMap[i], kHeadMap[i + 1]);
    }

    for (int i = 0; i < ARRAY_SIZE(kTailMap); i += 2) {
        add_tail_mapping(kTailMap[i], kTailMap[i + 1]);
    }

    for (int i = 0; i < ARRAY_SIZE(kResidueMap); i += 2) {
        add_residue_mapping(kResidueMap[i], kResidueMap[i + 1]);
    }

    if (argc < 2) {
        return -1;
    }

    PdbModificationInfo modification_info;

    std::fstream input(argv[1], std::ios::in | std::ios::binary);
    if (!modification_info.ParseFromIstream(&input)) {
        return -2;
    }

    PdbFile pdb(modification_info.pdb_file());

    PdbStructureBuilder builder(pdb);

    for (int i = 0; i < modification_info.his_mapping_size(); i++) {
        const PdbMapping& mapping = modification_info.his_mapping(i);
        const PdbResidueInfo& info = mapping.residue();
        builder.add_mapping(info.chain_id()[0], info.res_num(),
                            info.i_code()[0], mapping.mapped_name());
    }

    // Remember to add/remove bonds after we build the structure.
    for (int i = 0; i < modification_info.close_cys_pair_size(); i++) {
        const CYSPair& pair = modification_info.close_cys_pair(i);
        const PdbResidueInfo& first = pair.cys1();
        const PdbResidueInfo& second = pair.cys2();
        std::string mapped_name = (pair.bonded())?"CYX":"CYS";
        builder.add_mapping(first.chain_id()[0], first.res_num(),
                            first.i_code()[0], mapped_name);
        builder.add_mapping(second.chain_id()[0], second.res_num(),
                            second.i_code()[0], mapped_name);
    }

    PdbFileStructure *structure = builder.build();

    for (int i = 0; i < modification_info.close_cys_pair_size(); i++) {
        const CYSPair& pair = modification_info.close_cys_pair(i);
        const PdbResidueInfo& first = pair.cys1();
        const PdbResidueInfo& second = pair.cys2();
        int cys1_index = structure->map_residue(first.chain_id()[0],
                                                first.res_num(),
                                                first.i_code()[0]);
        int cys2_index = structure->map_residue(second.chain_id()[0],
                                                second.res_num(),
                                                second.i_code()[0]);
        int atom1 = structure->get_atom_index(cys1_index, "SG");
        int atom2 = structure->get_atom_index(cys2_index, "SG");
        if (pair.bonded()) {
            structure->add_bond(atom1, atom2);
        } else {
            structure->remove_bond(atom1, atom2);
        }
    }

    structure->print_pdb_file("structure.pdb");

    return 0;
}
