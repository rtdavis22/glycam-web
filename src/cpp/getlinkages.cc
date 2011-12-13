#include "gmml/gmml.h"

#include <iostream>

using namespace gmml;
using namespace std;

struct NameAndRingType {
    NameAndRingType(const string& parsed_name);

    string name;
    ResidueClassification::RingType ring_type;
};

NameAndRingType::NameAndRingType(const string& parsed_name)
        : name(parsed_name), ring_type(ResidueClassification::kPyranose) {
    if (name.size() > 3) {
        char ring_letter = name[3];
        if (ring_letter == 'p' || ring_letter == 'f') {
            name.erase(name.begin() + 3);
            if (ring_letter == 'f') {
                ring_type = ResidueClassification::kFuranose;
            }
        }
    }
}

string get_residue_string(const ParsedResidue *residue) {
    stringstream ss;
    if (residue->isomer == ResidueClassification::kIsomerD)
        ss << "D";
    else
        ss << "L";
    ss << residue->name;
    if (residue->configuration == ResidueClassification::kAlpha)
        ss << "a";
    else
        ss << "b";
    return ss.str();
}

string construct_linkage_name(const ParsedResidue *child,
                              const ParsedResidue *parent) {
    stringstream ss;
    ss << get_residue_string(child);
    ss << child->anomeric_carbon << "-" << child->oxygen_position;
    ss << get_residue_string(parent);
    return ss.str();
}

vector<double> *get_omega_values(const string& name) {
    vector<double> *values = new vector<double>;
    values->push_back(60.0);
    values->push_back(-60.0);
    if (name != "Glc" && name != "Man" && name != "GlcNAc"
            && name != "ManNAc") {
        values->push_back(180.0);
    }
    return values;
}

// If there are not phi values, NULL is returned to save a dynamic allocation.
vector<double> *get_phi_values(const string& name) {
    if (name == "Neu5Ac" || name == "NeuNAc" || name == "Neu5Gc" ||
            name == "NeuNGc") {
        vector<double> *values = new vector<double>;
        values->push_back(180.0);
        values->push_back(-60.0);
        return values;
    }
    return NULL;
}

void write_comma_list(std::ostream& out, const vector<double> *values) {
    if (values->size() > 0) {
        out << (*values)[0];
        for (int i = 1; i < values->size(); i++)
            out << "," << (*values)[i];
    }
}

int main(int argc, char **argv) {
    string glycan(argv[1]);

    GlycamParser parser;
    parser.dont_parse_derivatives();
    ArrayTree<ParsedResidue*> *residue_tree = parser.get_array_tree(glycan);

    cout << residue_tree->begin()->first->name << " 0 0 -:-" << endl;
    
    if (residue_tree->size() > 0) {
        cout << get_residue_string((residue_tree->begin() + 1)->first);
        cout << " 0 0 -:-" << endl;
    }

    for (int i = 2; i < residue_tree->size(); i++) {
        const ParsedResidue *residue = (*residue_tree)[i].first;
        int parent_index = (*residue_tree)[i].second;
        string linkage_name =
                construct_linkage_name(residue,
                                       (*residue_tree)[parent_index].first);

        vector<double> *phi_values = NULL;
        vector<double> *omega_values = NULL;

        const ParsedResidue *parent = (*residue_tree)[parent_index].first;

        string parent_name = NameAndRingType(parent->name).name;
        string name = NameAndRingType(residue->name).name;
        if ((residue->oxygen_position == 6)) {
            omega_values = get_omega_values(parent_name);
        }

        phi_values = get_phi_values(name);

        cout << linkage_name;
        if (omega_values == NULL)
            cout << " 0";
        else
            cout << " 1";

        if (phi_values == NULL)
            cout << " 0";
        else
            cout << " 1";

        cout << " ";
        if (phi_values != NULL) {
            write_comma_list(cout, phi_values);
        } else {
            cout << "-";
        }

        cout << ":";
        if (omega_values != NULL) {
            write_comma_list(cout, omega_values);
        } else {
            cout << "-";
        }

        cout << endl;
    }
}
