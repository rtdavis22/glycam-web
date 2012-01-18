// Author: Robert Davis
//
// This program outputs a graphviz file representing the glycan specified in
// condensed glycam notation on the command line.

#include "gmml/gmml.h"

#include <iostream>
#include <string>

using std::string;

using namespace gmml;

int main(int argc, char **argv) {
    GlycanDrawer drawer;

    for (int i = 1; i < argc - 1; i++) {
        string arg(argv[i]);
        if (arg == "-edgelabels") {
            drawer.show_edge_labels();
        } else if (arg == "-dpi") {
            try {
                drawer.set_dpi(convert_string<int>(string(argv[i + 1])));
            } catch(...) {
                // Don't complain.
            }
            i++;
        } else if (arg == "-hide_config_labels") {
            drawer.hide_config_labels();
        } else if (arg == "-hide_position_labels") {
            drawer.hide_position_labels();
        }
    }

    drawer.print_file(string(argv[argc - 1]));
}
