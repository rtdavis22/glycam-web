package org.glycam.pdb.preprocessing;

import org.glycam.pdb.PdbFilePB.*;

import java.util.ArrayList;
import java.util.List;

public class UnrecognizedAtomsSection implements PreprocessingSection {
    private List<UnknownAtomInfo> unrecognizedAtoms;

    UnrecognizedAtomsSection(List<UnknownAtomInfo> unrecognizedAtoms) {
        this.unrecognizedAtoms = unrecognizedAtoms;
    }

    public List<UnknownAtomInfo> getUnrecognizedAtoms() {
        return unrecognizedAtoms;
    }

    public int getAtomCount() {
        return unrecognizedAtoms.size();
    }

    public boolean anyChainIdsSet() {
        for (UnknownAtomInfo atom : unrecognizedAtoms) {
            if (!atom.getResidueInfo().getChainId().equals(" "))
                return true;
        }
        return false;
    }

    public boolean anyICodesSet() {
        for (UnknownAtomInfo atom : unrecognizedAtoms) {
            if (!atom.getResidueInfo().getICode().equals(" "))
                return true;
        }
        return false;
    }

    public boolean isEnabled() {
        return !unrecognizedAtoms.isEmpty();
    }

    public String getUrl() {
        return "unrecognized-atoms";
    }

    public String getName() {
        return "Unrecognized Heavy Atoms";
    }

    public String getSummary() {
        int size = unrecognizedAtoms.size();
        if (size > 0) {
            return size + " unrecognized.";
        }
        return "None.";
    }

    public Status getStatus() {
         return unrecognizedAtoms.isEmpty()?Status.GOOD:Status.BAD;
    }

    public void addToPreprocessingResults(PreprocessingResults.Builder results) {
        assert unrecognizedAtoms.isEmpty();
    }
}
