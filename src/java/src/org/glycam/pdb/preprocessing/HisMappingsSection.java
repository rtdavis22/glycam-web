package org.glycam.pdb.preprocessing;

import org.glycam.pdb.PdbFilePB.*;

import java.util.ArrayList;
import java.util.List;

public class HisMappingsSection implements PreprocessingSection {
    private List<HisMapping> hisMappings;

    HisMappingsSection(List<HisResidue> hisResidues) {
        this.hisMappings = new ArrayList<HisMapping>();
        for (HisResidue hisResidue : hisResidues) {
            this.hisMappings.add(new HisMapping(hisResidue));
        }
    }

    public List<HisMapping> getMappings() {
        return hisMappings;
    }

    public int getHisCount() {
        return hisMappings.size();
    }

    public void updateMapping(int index, HisMappingType type) {
        hisMappings.get(index).setMappedType(type);
    }

    public boolean anyChainIdsSet() {
        for (HisMapping mapping : hisMappings) {
            if (!mapping.getResidueInfo().getChainId().equals(" "))
                return true;
        }
        return false;
    }

    public boolean anyICodesSet() {
        for (HisMapping mapping : hisMappings) {
            if (!mapping.getResidueInfo().getICode().equals(" "))
                return true;
        }
        return false;
    }

    public boolean isEnabled() {
        return !hisMappings.isEmpty();
    }

    public String getUrl() {
        return "his-mappings";
    }

    public String getName() {
        return "Histidine Protonation";
    }

    public String getSummary() {
        int hidCount = 0;
        int hieCount = 0;
        int hipCount = 0;
        for (HisMapping mapping : hisMappings) {
            switch (mapping.getMappedType()) {
                case HIE:
                    hieCount++;
                    break;
                case HID:
                    hidCount++;
                    break;
                case HIP:
                    hipCount++;
                    break;
            }
        }

        return formatSummaryString(hidCount, hieCount, hipCount);
    }

    private String formatSummaryString(int hidCount, int hieCount, int hipCount) {
        String summary = "";
        if (hidCount > 0)
            summary += hidCount + " HIS-&delta;";
        if (hieCount > 0) {
            if (summary != "")
                summary += ", ";
            summary += hieCount + " HIS-&epsilon;";
        }
        if (hipCount > 0) {
            if (summary != "")
                summary += ", ";
            summary += hipCount + " HIS<sup>+</sup>";
        }
        if (summary == "")
            summary = "None.";
        return summary;
    }

    private boolean isCustomSet() {
        for (HisMapping mapping : hisMappings) {
            if (mapping.getMappedType() != HisMappingType.HIE)
                return true;
        }
        return false;
    }

    public Status getStatus() {
        return Status.GOOD;
    }

    public void addToPreprocessingResults(PreprocessingResults.Builder results) {
        for (HisMapping hisMapping : hisMappings) {
            PdbMapping.Builder pdbMapping = PdbMapping.newBuilder();
            pdbMapping.setResidue(hisMapping.getResidueInfo());
            // Maybe the PreprocessingResults protocol buffer should have an enum field
            // for the HIS mapped name rather than a string.
            pdbMapping.setMappedName(hisMapping.getMappedType().name());
            results.addHisMapping(pdbMapping.build());
        }
    }
}
