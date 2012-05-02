package org.glycam.pdb.preprocessing;

import org.glycam.pdb.PdbFilePB.*;

import java.util.ArrayList;
import java.util.List;

public class UnrecognizedResiduesSection implements PreprocessingSection {
    private List<UnrecognizedResidue> unrecognizedResidues;

    UnrecognizedResiduesSection(List<PdbResidueInfo> residues) {
        this.unrecognizedResidues = new ArrayList<UnrecognizedResidue>();
        for (PdbResidueInfo residue : residues) {
            this.unrecognizedResidues.add(new UnrecognizedResidue(residue));
        }
    }

    public List<UnrecognizedResidue> getUnrecognizedResidues() {
        return unrecognizedResidues;
    }

    public int getResidueCount() {
        return unrecognizedResidues.size();
    }

    public void updateRemoved(int index, boolean value) {
        unrecognizedResidues.get(index).setToBeRemoved(value);
    }

    public boolean anyChainIdsSet() {
        for (UnrecognizedResidue residue : unrecognizedResidues) {
            if (!residue.getResidueInfo().getChainId().equals(" "))
                return true;
        }
        return false;
    }

    public boolean anyICodesSet() {
        for (UnrecognizedResidue residue : unrecognizedResidues) {
            if (!residue.getResidueInfo().getICode().equals(" "))
                return true;
        }
        return false;
    }

    public boolean isEnabled() {
        return !unrecognizedResidues.isEmpty();
    }

    public String getUrl() {
        return "unrecognized-residues";
    }

    public String getName() {
        return "Unrecognized Residues";
    }

    public String getSummary() {
        int size = unrecognizedResidues.size();
        if (size > 0) {
            return size + " removed.";
        }
        return "None.";
    }

    public Status getStatus() {
        if (!unrecognizedResidues.isEmpty() && !allToBeRemoved()) {
            return Status.NEEDS_ATTENTION;
        }
        return Status.GOOD;
    }

    private boolean allToBeRemoved() {
        for (UnrecognizedResidue residue : unrecognizedResidues) {
            if (!residue.isToBeRemoved())
                return false;
        }
        return true;
    }

    public void addToPreprocessingResults(PreprocessingResults.Builder results) {
        for (UnrecognizedResidue residue : unrecognizedResidues) {
            //if (residue.isToBeRemoved()) {
                results.addResidueToRemove(residue.getResidueInfo());
            //}
        }
    }
}
