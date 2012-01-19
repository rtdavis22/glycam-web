// Author: Robert Davis
//
// This program checks to make sure a particular sequence in GLYCAM condensed nomenclature has the
// correct sequence and can be built. The sequence is taken as a command-line argument. If there is
// an error, the program writes an error message to standard output and returns with a non-zero exit
// status.

#include "config.h"

#include <exception>
#include <iostream>
#include <string>

#include "gmml/gmml.h"

int main(int argc, char *argv[]) {
    gmml::add_path(std::string(PROJECT_ROOT) + "dat/prep_files/");
    
    gmml::load_prep_file("Glycam_06.prep");
    gmml::load_prep_file("Neu5Gc_a_06.prep");
    gmml::load_prep_file("sulfate.prep");
    gmml::load_prep_file("ACE.prep");
    gmml::load_prep_file("MEX.prep");
    
    if (argc < 2) {
        return -1;
    }
    std::string input(argv[1]);

    try {
        gmml::Structure *structure = gmml::glycam_build(input);
    } catch(const std::exception& e) {
        std::cout << e.what() << std::endl;
        return -1;
    }

    return 0;
}
