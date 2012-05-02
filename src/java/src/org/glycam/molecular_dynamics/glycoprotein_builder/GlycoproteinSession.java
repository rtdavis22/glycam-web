package org.glycam.molecular_dynamics.glycoprotein_builder;

import org.glycam.CPP;
import org.glycam.Logging;
import org.glycam.molecular_dynamics.glycan_builder.GlycanSession;
import org.glycam.pdb.PdbFilePB.GlycoproteinBuildInfo;
import org.glycam.pdb.PdbFilePB.GlycoproteinInfo;
import org.glycam.pdb.PdbFilePB.GlycosylationSpot;
import org.glycam.pdb.PdbFilePB.PreprocessingResults;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GlycoproteinSession {
    private File pdbFile;

    private GlycosylationSiteList nLinkingSites;

    private GlycosylationSiteList oLinkingSites;

    private GlycanSession currentGlycanSession = null;

    private PreprocessingResults preprocessingResults = null;

    // The current build request. This is null if there is no build request.
    private BuildRequest buildRequest = null;

    public GlycoproteinSession(File pdbFile) {
        this.pdbFile = pdbFile;
        this.nLinkingSites = new GlycosylationSiteList();
        this.oLinkingSites = new GlycosylationSiteList();
        initSession();
    }

    public void setPreprocessingResults(PreprocessingResults preprocessingResults) {
        this.preprocessingResults = preprocessingResults;
    }

    public GlycosylationSiteList getNLinkingSites() {
        return nLinkingSites;
    }

    public GlycosylationSiteList getOLinkingSites() {
        return oLinkingSites;
    }

    public GlycanSession getCurrentGlycanSession() {
        return currentGlycanSession;
    }

    public boolean anyGlycosylatedSitesWithChainId() {
        return nLinkingSites.isAnyGlycosylatedAndWithChainId() ||
               oLinkingSites.isAnyGlycosylatedAndWithChainId();
    }

    public boolean anyGlycosylatedSitesWithICode() {
        return nLinkingSites.isAnyGlycosylatedAndWithICode() ||
               oLinkingSites.isAnyGlycosylatedAndWithICode();
    }

    public BuildRequest getBuildRequest() { return buildRequest; }

    public void setCurrentGlycanSession(GlycanSession glycanSession) {
        currentGlycanSession = glycanSession;
    }

    public void removeCurrentGlycanSession() {
        currentGlycanSession = null;
    }

    public void setBuildRequest(BuildRequest buildRequest) {
        this.buildRequest = buildRequest;
    }

    public BuildRequest build(File outputDirectory, String uid) {
        GlycoproteinBuildInfo info = buildBuildInfo();
        buildRequest = new BuildRequest(info, outputDirectory, uid);
        Thread thread = new Thread(buildRequest);
        thread.start();
        return buildRequest;
    }

    public void resetBuildRequest() {
        buildRequest = null;
    }

    private void initSession() {
        try {
            initFromCpp();
        } catch (IOException e) {
            Logging.logger.severe(e.getMessage());
        } catch (InterruptedException e) {
            Logging.logger.severe(e.getMessage());
        }
    }

    private void initFromCpp() throws IOException, InterruptedException {
        String command = getCppCommand();
        Process process = CPP.exec(command);
        initFromGlycoproteinInfo(GlycoproteinInfo.parseFrom(process.getInputStream()));
        Logging.logger.info("Process " + command + " exited with value " + process.waitFor());
    }

    private String getCppCommand() {
        return "get_glycoprotein_info " + pdbFile.getPath();
    }

    private void initFromGlycoproteinInfo(GlycoproteinInfo glycoproteinInfo) {
        initLinkingSites(glycoproteinInfo.getGlycosylationSpotList());

        nLinkingSites.sortByLikeliness();
        oLinkingSites.sortByLikeliness();
    }

    private void initLinkingSites(List<GlycosylationSpot> spots) {
        // Maybe have the PB send have 2 lists so I don't have to do this here.
        for (GlycosylationSpot spot : spots) {
            String name = spot.getName();
            if (name.equals("ASN")) {
                nLinkingSites.add(spot);
            } else if (name.equals("SER") || name.equals("THR")) {
                oLinkingSites.add(spot);
            }
        }
    }

    private GlycoproteinBuildInfo buildBuildInfo() {
        GlycoproteinBuildInfo.Builder info = GlycoproteinBuildInfo.newBuilder();

        info.setPdbFile(pdbFile.getPath());
        info.setPreprocessingResults(preprocessingResults);

        nLinkingSites.addGlycosylatedSitesToBuildInfo(info);
        oLinkingSites.addGlycosylatedSitesToBuildInfo(info);

        return info.build();
    }
}
