// Author: Robert Davis
//
#include "config.h"

#include <exception>
#include <iostream>
#include <string>

#include "gmml/gmml.h"

using namespace gmml;

using std::cerr;
using std::cout;
using std::endl;

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

    PdbFileStructure *structure = NULL;
    try {
        structure = PdbFileStructure::build(argv[1]);
    } catch(const std::exception& e) {
        std::cout << e.what() << std::endl;
        return -1;
    }

    if (structure->size() == 0) {
        std::cout << "Invalid pdb file" << std::endl;
        return -1;
    }

    for (PdbFileStructure::pdb_iterator it = structure->pdb_begin();
            it != structure->pdb_end(); ++it) {
        const Residue *residue = structure->residues(it->second);
        if (residue->atoms(0)->type() == "") {
            std::cout << "Unknown residue " << residue->name() << std::endl;
            return -1;
        }
    }

    // This check may not be necessary. If we do decide to check if the
    // topology file can be written, the CYS residues need to be mapped
    // appropriately. A "hack" can be employed to modify all the CYS
    // atoms to CG to get around the disulfide bond issue.
    /*
    try {
        AmberTopFile *top_file = structure->build_amber_top_file();
    } catch(const std::exception& e) {
        std::cout << e.what() << std::endl;
        return -1;
    }
    */

    return 0;
}
