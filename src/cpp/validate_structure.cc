#include "config.h"

#include <fstream>
#include <iostream>
#include <sstream>

#include "gmml/gmml.h"

using namespace std;
using namespace gmml;

int main(int argc, char *argv[]) {
    add_path(PROJECT_ROOT);
    
    load_prep_file("prep_files/Glycam_06.prep");
    load_prep_file("prep_files/Neu5Gc_a_06.prep");
    load_prep_file("prep_files/sulfate.prep");
    load_prep_file("prep_files/ACE.prep");
    load_prep_file("prep_files/MEX.prep");
    
    string input(argv[1]);
  
    Structure *structure = glycam_build(input);
}
