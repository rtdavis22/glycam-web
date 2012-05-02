package org.glycam.pdb.preprocessing;

import org.glycam.pdb.PdbFilePB.*;

import java.util.ArrayList;
import java.util.List;

public class MissingResiduesSection implements PreprocessingSection {
    private List<ChainGap> gaps;

    MissingResiduesSection(List<ChainGap> gaps) {
        this.gaps = new ArrayList<ChainGap>(gaps);
    }

    public List<ChainGap> getGaps() {
        return gaps;
    }

    public int getGapCount() {
        return gaps.size();
    }

    public void updateTerminalTypes(int index, NTerminalType nTerminalType,
                                    CTerminalType cTerminalType) {
        ChainGap curGap = gaps.get(index);
        if (curGap.getNterminalType() != nTerminalType ||
                curGap.getCterminalType() != cTerminalType) {
            ChainGap newChainGap = ChainGap.newBuilder(curGap)
                                           .setNterminalType(nTerminalType)
                                           .setCterminalType(cTerminalType)
                                           .build();
            gaps.set(index, newChainGap);
        }
    }

    public boolean isEnabled() {
        return !gaps.isEmpty();
    }

    public String getUrl() {
        return "missing-residues";
    }

    public String getName() {
        return "Missing Residues";
    }

    public String getSummary() {
        int size = gaps.size();
        if (size == 0)
            return "None.";
        return size + " gap" + ((size > 1)?"s":"") + " in amino acid chains.";
    }

    public Status getStatus() {
        return Status.GOOD;
    }

    public void addToPreprocessingResults(PreprocessingResults.Builder results) {
        for (ChainGap gap : gaps) {
            results.addChainGap(gap);
        }
    }
}
