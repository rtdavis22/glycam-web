#include "gmml/gmml.h"

#include <iostream>
#include <string>

using std::string;

using namespace gmml;

int main(int argc, char **argv) {
    GlycanDrawer drawer;

    for (int i = 1; i < argc - 1; i++) {
        if (string(argv[i]) == "-edgelabels") {
            drawer.show_edge_labels();
        } else if (string(argv[i]) == "-dpi") {
            try {
                drawer.set_dpi(convert_string<int>(string(argv[i + 1])));
            } catch(...) {
                // Don't complain.
            }
            i++;
        }
    }

    drawer.print_file(string(argv[argc - 1]));
}
