package org.glycam.pdb.preprocessing;

import org.glycam.pdb.PdbFilePB.*;

import java.util.ArrayList;
import java.util.List;

public class RemovedAtomsSection implements PreprocessingSection {
    private List<RemovedAtomInfo> removedAtoms;

    RemovedAtomsSection(List<RemovedAtomInfo> removedAtoms) {
        this.removedAtoms = removedAtoms;
    }

    public List<RemovedAtomInfo> getRemovedAtoms() {
        return removedAtoms;
    }

    public int getAtomCount() {
        return removedAtoms.size();
    }

    public boolean anyChainIdsSet() {
        for (RemovedAtomInfo atom : removedAtoms) {
            if (!atom.getResidueInfo().getChainId().equals(" "))
                return true;
        }
        return false;
    }

    public boolean anyICodesSet() {
        for (RemovedAtomInfo atom : removedAtoms) {
            if (!atom.getResidueInfo().getICode().equals(" "))
                return true;
        }
        return false;
    }

    public boolean isEnabled() {
        return !removedAtoms.isEmpty();
    }

    public String getUrl() {
        return "removed-atoms";
    }

    public String getName() {
        return "Replaced Hydrogens";
    }

    public String getSummary() {
        int size = removedAtoms.size();
        if (size > 0) {
            return size + " to be replaced.";
        }
        return "None.";
    }

    public Status getStatus() {
        return Status.GOOD;
    }

    public void addToPreprocessingResults(PreprocessingResults.Builder results) {
        // When the PDB file is built, it will ignore unknown hydrogens, so we don't need to
        // do anything here.
    }
}
