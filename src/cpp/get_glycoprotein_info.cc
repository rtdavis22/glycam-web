// Author: Robert Davis
//
// This program takes a pdb file name as a command-line argument and returns
// a protocol buffer with the following information:
//  - pairs of CYS residues which could be disulfide bonded
//  - HIS residues
//  - ASN residues

#include "config.h"

#include <algorithm>
#include <iostream>
#include <map>
#include <string>
#include <vector>

#include "gmml/gmml.h"

#include "src/cpp/PdbFilePB.pb.h"

using std::map;
using std::string;
using std::vector;

using namespace gmml;

using namespace pdb;

//using molecular_dynamics::glycoprotein_builder::CYSPair;
//using molecular_dynamics::glycoprotein_builder::GlycosylationSpot;
//using molecular_dynamics::glycoprotein_builder::PdbInfo;
//using molecular_dynamics::glycoprotein_builder::PdbResidueInfo;

void set_residue_info(PdbResidueInfo *info, int index, const PdbResidueId *pdb_id) {
    //info->set_index(index);
    info->set_chain_id(string(1, pdb_id->chain_id));
    info->set_res_num(pdb_id->res_num);
    info->set_i_code(string(1, pdb_id->i_code));
}

vector<int> *get_likely_oglycosylation_locations(const Structure& structure) {
    NetOGlycRunner runner(File(string(PROJECT_ROOT) + "deps/netoglyc/netOglyc"));
    vector<vector<int>*> *proteins = find_proteins(structure);
    for (int i = 0; i < proteins->size(); i++) {
        FastaSequence *sequence = FastaSequence::create(structure,
                                                        *(*proteins)[i]);
        runner.add_sequence(*sequence);
        delete sequence;
    }
    NetOGlycResults *results = runner.run();
    vector<int> *likely_locations = new vector<int>;
    for (int i = 0; i < results->sequence_count(); i++) {
        const vector<int>& protein = *(*proteins)[i];
        const OGlycosylationLocations *locations =
                results->get_predicted_locations(i);
        const vector<int>& serine_locations = locations->get_serine_locations();
        for (int j = 0; j < serine_locations.size(); j++) {
            likely_locations->push_back(protein[serine_locations[j]]);
        }
        const vector<int>& threonine_locations =
                locations->get_threonine_locations();
        for (int j = 0; j < threonine_locations.size(); j++) {
            likely_locations->push_back(protein[threonine_locations[j]]);
        }
    }
    delete results;
    return likely_locations;
}


map<int, string> *get_contexts(const Structure& structure) {
    AminoAcidCodeSet amino_acid_codes;
    int context_size = 2;
    map<int, string> *context_map = new map<int, string>;
    vector<vector<int>*> *proteins = find_proteins(structure);
    for (int i = 0; i < proteins->size(); i++) {        
        const vector<int>& protein = *(*proteins)[i];
        for (int j = 0; j < protein.size(); j++) {
            string name = structure.residues(protein[j])->name();
            if (name == "ASN" || name == "THR" || name == "SER") {
                int lower_bound = std::max(0, j - context_size);
                int upper_bound = std::min(static_cast<int>(protein.size()) - 1,
                                           j + context_size);
                string context = "";
                for (int k = lower_bound; k <= upper_bound; k++) {
                    string code = structure.residues(protein[k])->name();
                    context += amino_acid_codes.get_fasta_letter(code);
                    if (j == k)
                        context += '*';
                }
                context_map->insert(map<int, string>::value_type(protein[j], context));
            }
        }
    }
    return context_map;
}


int main(int argc, char *argv[]) {
    if (argc != 2) {
        return -1;
    }

    string naccess_path = string(PROJECT_ROOT) + "deps/naccess/";
    NAccess::set_naccess_path(naccess_path.c_str());
    NAccessResults naccess_results(argv[1]);
    RsaInfo *rsa_info = naccess_results.rsa_info();

    File file(argv[1]);
    PdbFile pdb_file(file);
    PdbFileStructure *structure = PdbStructureBuilder(pdb_file).build();

    GlycoproteinInfo info;

    vector<int> *oglycosylation_locations =
            get_likely_oglycosylation_locations(*structure);

    vector<int> *asns_with_sequon = get_asns_with_sequon(*structure);

    map<int, string> *context_map = get_contexts(*structure);

    for (int i = 0; i < structure->residue_count(); i++) {
        const PdbResidueId *pdb_id = structure->map_residue_index(i);
        if (pdb_id == NULL)
            continue;
        Residue *residue = structure->residues(i);
        string name = residue->name();
        if (name == "ASN" || name == "SER" || name == "THR") {
            bool is_likely = false;
            if (name == "ASN") {
                is_likely = std::find(asns_with_sequon->begin(),
                                      asns_with_sequon->end(), i) !=
                        asns_with_sequon->end();
            } else if (name == "SER" || name == "THR") {
                is_likely = std::find(oglycosylation_locations->begin(),
                                      oglycosylation_locations->end(),
                                      i) !=
                        oglycosylation_locations->end();
            }
            GlycosylationSpot *spot = info.add_glycosylation_spot();
            spot->set_name(name);
            spot->set_likely(is_likely);
            // Use the other lookup() when the const problem is fixed.
            const ResidueAccessibilityInfo *sasa_info =
                    rsa_info->lookup(pdb_id->chain_id, pdb_id->res_num,
                                     pdb_id->i_code);
            if (sasa_info == NULL) {
                spot->set_sasa(-1);
            } else {
                spot->set_sasa(sasa_info->side_chains()->relative);
            }
            set_residue_info(spot->mutable_info(), i, pdb_id);

            map<int, string>::const_iterator it = context_map->find(i);
            string context = (it != context_map->end())?it->second:"";
            spot->set_context(context);

        }
    }

    if (!info.SerializeToOstream(&std::cout)) {
        std::cerr << "Failed to write protocol buffer." << std::endl;
        return -1;
    }

    return 0;
}
