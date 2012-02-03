package org.glycam.molecular_dynamics.glycoprotein_builder;

import org.glycam.CPP;
import org.glycam.Logging;
import org.glycam.molecular_dynamics.glycoprotein_builder.PdbInfoPB.CYSPair;
import org.glycam.molecular_dynamics.glycoprotein_builder.PdbInfoPB.GlycosylationInfo;
import org.glycam.molecular_dynamics.glycoprotein_builder.PdbInfoPB.GlycosylationSpot;
import org.glycam.molecular_dynamics.glycoprotein_builder.PdbInfoPB.PdbInfo;
import org.glycam.molecular_dynamics.glycoprotein_builder.PdbInfoPB.PdbMapping;
import org.glycam.molecular_dynamics.glycoprotein_builder.PdbInfoPB.PdbModificationInfo;
import org.glycam.molecular_dynamics.glycoprotein_builder.PdbInfoPB.PdbResidueInfo;
import org.glycam.molecular_dynamics.oligosaccharide_builder.OligosaccharideSession;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GlycoproteinSession {
    public static class AttachedGlycan {
        private PdbResidueInfo residueInfo;
        private OligosaccharideSession session;

        public AttachedGlycan(PdbResidueInfo residueInfo, OligosaccharideSession session) {
            this.residueInfo = residueInfo;
            this.session = session;
        }

        public PdbResidueInfo getResidueInfo() { return residueInfo; }

        public OligosaccharideSession getSession() { return session; }
    }

    private File pdbFile;

    // This is a protocol buffer with the relevant information needed by [C++ program here]
    // to build the glycoprotein.
    private PdbModificationInfo.Builder modificationInfo;

    private List<GlycosylationSpot> nLinkingSpots;
    private List<GlycosylationSpot> oLinkingSpots;

    private OligosaccharideSession currentGlycanSession;

    private List<AttachedGlycan> attachedGlycans;

    // The current build request. This is null if there is no build request.
    private BuildRequest buildRequest = null;

    public GlycoproteinSession(File pdbFile) {
        this.pdbFile = pdbFile;
        this.nLinkingSpots = new ArrayList<GlycosylationSpot>();
        this.oLinkingSpots = new ArrayList<GlycosylationSpot>();
        initializePdbInfo();
        this.currentGlycanSession = null;
        this.attachedGlycans = new ArrayList<AttachedGlycan>();
    }

    public PdbModificationInfo.Builder getModificationInfo() { return modificationInfo; }

    public List<PdbMapping> getHisResidues() { return modificationInfo.getHisMappingList(); }

    public List<CYSPair> getCloseCYSPairs() { return modificationInfo.getCloseCysPairList(); }

    public List<GlycosylationSpot> getNLinkingSpots() { return nLinkingSpots; }
    public List<GlycosylationSpot> getOLinkingSpots() { return oLinkingSpots; }

    public OligosaccharideSession getCurrentGlycanSession() { return currentGlycanSession; }

    public List<AttachedGlycan> getAttachedGlycans() { return attachedGlycans; }

    public BuildRequest getBuildRequest() { return buildRequest; }

    public void setCurrentGlycanSession(OligosaccharideSession glycanSession) {
        currentGlycanSession = glycanSession;
    }

    public void setBuildRequest(BuildRequest buildRequest) {
        this.buildRequest = buildRequest;
    }

    public BuildRequest build(File outputDirectory, String uid) {
        PdbModificationInfo info = buildModificationInfo();
        buildRequest = new BuildRequest(info, outputDirectory, uid);
        Thread thread = new Thread(buildRequest);
        thread.start();
        return buildRequest;
    }

    public void resetBuildRequest() {
        buildRequest = null;
    }

    private void initializePdbInfo() {
        String command = "get_pdb_info " + pdbFile.getPath();
        try {
            Process process = CPP.exec(command);
            PdbInfo pdbInfo = PdbInfo.parseFrom(process.getInputStream());
            initializeModificationInfo(pdbInfo);
            Logging.logger.info("Process " + command + " exited with value " + process.waitFor());
        } catch (IOException e) {
            Logging.logger.severe(e.getMessage());
        } catch (InterruptedException e) {
            Logging.logger.severe(e.getMessage());
        }
    }

    private void initializeModificationInfo(PdbInfo pdbInfo) {
        this.modificationInfo = PdbModificationInfo.newBuilder();
        List<PdbResidueInfo> hisResidues = pdbInfo.getHisResidueList();
        for (PdbResidueInfo residue : hisResidues) {
            PdbMapping.Builder mapping = PdbMapping.newBuilder();
            mapping.setResidue(PdbResidueInfo.newBuilder(residue).clone());
            mapping.setMappedName("HIE");
            this.modificationInfo.addHisMapping(mapping);
        }

        for (CYSPair pair : pdbInfo.getCloseCysPairList()) {
            this.modificationInfo.addCloseCysPair(CYSPair.newBuilder(pair).clone());
        }

        for (GlycosylationSpot spot : pdbInfo.getGlycosylationSpotList()) {
            String name = spot.getName();
            if (name.equals("ASN")) {
                nLinkingSpots.add(spot);
            } else if (name.equals("SER") || name.equals("THR")) {
                oLinkingSpots.add(spot);
            }
        }

        Collections.sort(nLinkingSpots, new GlycosylationSpotComparator());
        Collections.sort(oLinkingSpots, new GlycosylationSpotComparator());
    }

    private class GlycosylationSpotComparator implements Comparator<GlycosylationSpot> {
        @Override
        public int compare(GlycosylationSpot lhs, GlycosylationSpot rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }

    private PdbModificationInfo buildModificationInfo() {
        PdbModificationInfo.Builder info = modificationInfo.clone();
        info.setPdbFile(pdbFile.getPath());
        for (AttachedGlycan glycan : attachedGlycans) {
            GlycosylationInfo.Builder glycosylationInfo = GlycosylationInfo.newBuilder();
            glycosylationInfo.setSpot(glycan.getResidueInfo());
            glycosylationInfo.setGlycan(glycan.getSession().buildProtocolBuffer());
            info.addGlycosylation(glycosylationInfo.build());
        }
        return info.build();
    }
}
