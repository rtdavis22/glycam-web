package org.glycam.pdb.preprocessing;

import org.glycam.pdb.PdbFilePB.*;

import java.util.ArrayList;
import java.util.List;

public class TerminalResiduesSection implements PreprocessingSection {
    private List<ChainInfo> chains;

    TerminalResiduesSection(List<ChainInfo> chains) {
        this.chains = new ArrayList<ChainInfo>(chains);
    }

    public List<ChainInfo> getChains() {
        return chains;
    }

    public int getChainCount() {
        return chains.size();
    }

    public void updateTerminalTypes(int index, NTerminalType nTerminalType,
                                    CTerminalType cTerminalType) {
        ChainInfo curChainInfo = chains.get(index);
        if (curChainInfo.getNterminalType() != nTerminalType ||
                curChainInfo.getCterminalType() != cTerminalType) {
            ChainInfo newChainInfo = ChainInfo.newBuilder(curChainInfo)
                                              .setNterminalType(nTerminalType)
                                              .setCterminalType(cTerminalType)
                                              .build();
            chains.set(index, newChainInfo);
        }
    }

    // Maybe have a Utils.isChainIdSet(PdbResidueInfo info) { !return info.chainId.equals(" "); }
    // in pdb package.
    public boolean anyStartChainIdsSet() {
        for (ChainInfo chain : chains) {
            if (!chain.getStart().getChainId().equals(" "))
                return true;
        }
        return false;
    }

    public boolean anyInconsistentEndChainIds() {
        for (ChainInfo chain : chains) {
            if (!chain.getEnd().getChainId().equals(chain.getStart().getChainId()))
                return true;
        }
        return false;
    }

    public boolean anyStartICodesSet() {
        for (ChainInfo chain : chains) {
            if (!chain.getStart().getICode().equals(" "))
                return true;
        }
        return false;
    }

    public boolean anyEndICodesSet() {
        for (ChainInfo chain : chains) {
            if (!chain.getEnd().getICode().equals(" "))
                return true;
        }
        return false;
    }

    public boolean isEnabled() {
        return !chains.isEmpty();
    }

    public String getUrl() {
        return "terminal-residues";
    }

    public String getName() {
        return "Chain Termination";
    }

    public String getSummary() {
        int size = chains.size();
        if (size == 0)
            return "None.";
        String summary = "";
        if (allZwitterionic()) {
            summary += "Zwitterionic";
        } else if (allNeutralCaps()) {
            summary += "Neutral Caps";
        } else {
            summary += "Mixed";
        }
        summary += " (" + size + " chain" + ((size > 1)?"s":"") + ").";
        return summary;
    }

    private boolean allZwitterionic() {
        for (ChainInfo chain : chains) {
            if (chain.getNterminalType() != NTerminalType.NH3)
                return false;
            if (chain.getCterminalType() != CTerminalType.CO2)
                return false;
        }
        return true;
    }

    private boolean allNeutralCaps() {
        for (ChainInfo chain : chains) {
            if (chain.getNterminalType() == NTerminalType.NH3)
                return false;
            if (chain.getCterminalType() == CTerminalType.CO2)
                return false;
        }
        return true;
    }

    public Status getStatus() {
        return Status.GOOD;
    }

    public void addToPreprocessingResults(PreprocessingResults.Builder results) {
        for (ChainInfo chain : chains) {
            results.addChainInfo(chain);
        }
    }
}
