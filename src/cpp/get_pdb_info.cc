// Author: Robert Davis

#include "gmml/gmml.h"

#include "PdbInfoPB.pb.h"

using namespace gmml;

using molecular_dynamics::glycoprotein_builder::CYSPair;
using molecular_dynamics::glycoprotein_builder::PdbInfo;
using molecular_dynamics::glycoprotein_builder::PdbResidueInfo;

int main(int argc, char *argv[]) {
    if (argc != 2) {
        return -1;
    }

    PdbFileStructure *structure = PdbFileStructure::build(argv[1]);

    // Add stuff in pdb_file_structure.h to iterate over Impl::residue_map;
}
