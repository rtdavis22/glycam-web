// Author: Robert Davis
//
// This program takes as a command-line argument a glycan in condensed GLYCAM nomenclature.
// For each residue in the glycan, one line is printed to standard output containing information
// about the linkage between the residue and it's parent residue.
// Each line contains 4 pieces of information that are separated by whitespace:
// 1. A name for the linkage. For the aglycon, this is just the aglycon's name.
// 2. A 1 if this linkage can take multiple omega values and a 0 otherwise.
// 3. A 1 if this linkage can take multiple phi values and a 0 otherwise.
// 4. A comma-separated list of possible phi values, a colon, and a comma-separated list of
//    possible omega values. If there are no phi or omega values, a hyphen is printed instead,
//
// For example, if DManpa1-6[DGlcpNAcb1-4][DNeup5Aca2-3]DManpb1-OME is the command-line argument,
// the expected output is:
// OME 0 0 -:-
// DManpb 0 0 -:-
// DNeup5Aca2-3DManpb 0 1 180,-60:-
// DGlcpNAcb1-4DManpb 0 0 -:-
// DManpa1-6DManpb 1 0 -:60,-60
//
// TODO: Modify this to use a protocol buffer.

#include "gmml/gmml.h"

#include <iostream>
#include <sstream>
#include <string>
#include <vector>

using namespace gmml;

using std::cout;
using std::endl;
using std::string;
using std::stringstream;
using std::vector;

// This is used to get the name and ring type from a parsed residue name.
// gmml should be modified to do this, so there should be no need to do this.
struct NameAndRingType {
    NameAndRingType(const string& parsed_name);

    string name;
    ResidueClassification::RingType ring_type;
};

// Returns a string representation of this residue.
string get_residue_string(const ParsedResidue *residue);

// Construct a linkages name from two residues.
string construct_linkage_name(const ParsedResidue *child,
                              const ParsedResidue *parent);

// Returns the possible omega values associated with this sugar.
vector<double> *get_omega_values(const string& name);

// Returns the possible phi values associated with this sugar.
vector<double> *get_phi_values(const string& name);

void write_comma_list(std::ostream& out, const vector<double> *values);

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

