#include <fstream>
#include <iostream>
#include <sstream>

#include "gmml/gmml.h"

using namespace std;
using namespace gmml;

int main(int argc, char *argv[]) {
    add_path("/opt/apache-tomcat-6.0.32/webapps/glycam-web/amber/prep_files");
    
    load_prep_file("Glycam_06.prep");
    load_prep_file("Neu5Gc_a_06.prep");
    load_prep_file("sulfate.prep");
    load_prep_file("ACE.prep");
    load_prep_file("MEX.prep");
    
    string input(argv[1]);
  
    Structure *structure = glycam_build(input);
}
