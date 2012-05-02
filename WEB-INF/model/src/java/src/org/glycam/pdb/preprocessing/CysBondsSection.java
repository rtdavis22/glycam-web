package org.glycam.pdb.preprocessing;

import org.glycam.pdb.PdbFilePB.*;

import java.util.ArrayList;
import java.util.List;

public class CysBondsSection implements PreprocessingSection {
    private List<CYSPair> cysPairs;

    CysBondsSection(List<CYSPair> cysPairs) {
        this.cysPairs = new ArrayList<CYSPair>(cysPairs);
        for (int i = 0; i < getPairCount(); i++)
            updateBonded(i, true);
    }

    public List<CYSPair> getCysPairs() {
        return cysPairs;
    }

    public int getPairCount() {
        return cysPairs.size();
    }

    public void updateBonded(int index, boolean value) {
        if (cysPairs.get(index).getBonded() != value)
            toggleBondedValue(index);
    }

    private void toggleBondedValue(int index) {
        boolean curValue = cysPairs.get(index).getBonded();
        CYSPair newPair = CYSPair.newBuilder(cysPairs.get(index)).setBonded(!curValue).build();
        cysPairs.set(index, newPair);
    }

    public boolean anyChainIdsSet() {
        for (CYSPair cysPair : cysPairs) {
            if (!cysPair.getCys1().getChainId().equals(" "))
                return true;
            if (!cysPair.getCys2().getChainId().equals(" "))
                return true;
        }
        return false;
    }

    public boolean anyICodesSet() {
        for (CYSPair cysPair : cysPairs) {
            if (!cysPair.getCys1().getICode().equals(" "))
                return true;
            if (!cysPair.getCys2().getICode().equals(" "))
                return true;
        }
        return false;
    }

    public boolean isEnabled() {
        return !cysPairs.isEmpty();
    }

    public String getUrl() {
        return "cys-bonds";
    }

    public String getName() {
        return "Disulfide Bonds";
    }

    public String getSummary() {
        if (cysPairs.isEmpty())
            return "None.";
        int numBondsSet = getNumBondsSet();
        String summary = numBondsSet + " bond" + ((numBondsSet > 1)?"s":"") + " set.";
        return summary;
    }

    private int getNumBondsSet() {
        int count = 0;
        for (CYSPair cysPair : cysPairs) {
            if (cysPair.getBonded())
                count++;
        }
        return count;
    }

    public Status getStatus() {
        return Status.GOOD;
    }

    public void addToPreprocessingResults(PreprocessingResults.Builder results) {
        for (CYSPair cysPair : cysPairs) {
            results.addCloseCysPair(cysPair);
        }
    }
}
