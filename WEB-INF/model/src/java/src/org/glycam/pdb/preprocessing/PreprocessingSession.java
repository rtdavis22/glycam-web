package org.glycam.pdb.preprocessing;

import org.glycam.CPP;
import org.glycam.Logging;
import org.glycam.pdb.PdbFilePB.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PreprocessingSession {
    private CysBondsSection cysBondsSection;

    private UnrecognizedResiduesSection unrecognizedResiduesSection;

    private TerminalResiduesSection terminalResiduesSection;

    private HisMappingsSection hisMappingsSection;

    private UnrecognizedAtomsSection unrecognizedAtomsSection;

    private RemovedAtomsSection removedAtomsSection;

    private MissingResiduesSection missingResiduesSection;

    private String destinationLabel;
    private String destinationUrl;

    private String previousUrl;

    public PreprocessingSession(File pdbFile) {
        initializePdbInfo(pdbFile);
        destinationLabel = "Next >";
        destinationUrl = "temp";
        previousUrl = "upload-pdb";
    }

    public CysBondsSection getCysBondsSection() {
        return cysBondsSection;
    }

    public UnrecognizedResiduesSection getUnrecognizedResiduesSection() {
        return unrecognizedResiduesSection;
    }

    public TerminalResiduesSection getTerminalResiduesSection() {
        return terminalResiduesSection;
    }

    public HisMappingsSection getHisMappingsSection() {
        return hisMappingsSection;
    }

    public UnrecognizedAtomsSection getUnrecognizedAtomsSection() {
        return unrecognizedAtomsSection;
    }

    public RemovedAtomsSection getRemovedAtomsSection() {
        return removedAtomsSection;
    }

    public MissingResiduesSection getMissingResiduesSection() {
        return missingResiduesSection;
    }

    public String getDestinationLabel() {
        return destinationLabel;
    }

    public String getDestinationUrl() {
        return destinationUrl;
    }

    public String getPreviousUrl() {
        return previousUrl;
    }

    public void setDestination(String label, String url) {
        this.destinationLabel = label;
        this.destinationUrl = url;
    }

    public void setPreviousUrl(String url) {
        previousUrl = url;
    }

    public boolean allSectionsGood() {
        for (PreprocessingSection section : getSections()) {
            if (section.getStatus() != MenuItem.Status.GOOD)
                return false;
        }
        return true;
    }

    private void initializePdbInfo(File pdbFile) {
        String command = "get_pdb_preprocessing_info " + pdbFile.getPath();
        try {
            Process process = CPP.exec(command);
            PreprocessingInfo pdbInfo = PreprocessingInfo.parseFrom(process.getInputStream());


            cysBondsSection = new CysBondsSection(pdbInfo.getCloseCysPairList());
            unrecognizedResiduesSection =
                    new UnrecognizedResiduesSection(pdbInfo.getUnknownResidueList());
            terminalResiduesSection = new TerminalResiduesSection(pdbInfo.getChainInfoList());
            hisMappingsSection = new HisMappingsSection(pdbInfo.getHisResidueList());
            unrecognizedAtomsSection = new UnrecognizedAtomsSection(pdbInfo.getUnknownAtomList());
            removedAtomsSection = new RemovedAtomsSection(pdbInfo.getRemovedAtomList());
            missingResiduesSection = new MissingResiduesSection(pdbInfo.getChainGapList());

            //initializePreprocessingResults(pdbInfo);
            Logging.logger.info("Process " + command + " exited with value " + process.waitFor());
        } catch (IOException e) {
            Logging.logger.severe(e.getMessage());
        } catch (InterruptedException e) {
            Logging.logger.severe(e.getMessage());
        }
    }

    public PreprocessingResults createPreprocessingResults() {
        PreprocessingResults.Builder results = PreprocessingResults.newBuilder();
        for (PreprocessingSection section : getSections()) {
            section.addToPreprocessingResults(results);
        }
        return results.build();
    }

    // Move this to a different class?
    public List<PreprocessingSection> getSections() {
        List<PreprocessingSection> sections = new ArrayList<PreprocessingSection>();
        sections.add(cysBondsSection);
        sections.add(unrecognizedResiduesSection);
        sections.add(unrecognizedAtomsSection);
        sections.add(removedAtomsSection);
        sections.add(terminalResiduesSection);
        sections.add(missingResiduesSection);
        sections.add(hisMappingsSection);
        return sections;
    }
}
