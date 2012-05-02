package org.glycam.molecular_dynamics.glycan_builder;

import org.glycam.CPP;
import org.glycam.GlycamSequence;
import org.glycam.Linkage;
import org.glycam.Logging;
import org.glycam.molecular_dynamics.SolvationSettings;
import org.glycam.molecular_dynamics.glycan_builder.BuildInfoPB.BuildInfo;
import org.glycam.molecular_dynamics.glycan_builder.BuildInfoPB.SolvationInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The relevant information needed to build glycans.
 *
 * @author Robert Davis
 */
public class GlycanSession {
    /**
     * The sequence in GLYCAM condensed nomenclature.
     */
    private final GlycamSequence sequence;

    /**
     * Linkage information, one per residue.
     */
    private List<Linkage> linkages;

    private SolvationSettings solvationSettings = null;

    /**
     * The build request this session is waiting on.
     */
    private BuildRequest buildRequest = null;

    public GlycanSession(GlycamSequence sequence) throws IOException {
        this.sequence = sequence;
        this.linkages = sequence.getLinkages();
    }

    public GlycanSession(GlycanSession glycanSession) {
        this.sequence = glycanSession.sequence;
        this.linkages = new ArrayList<Linkage>();
        for (Linkage linkage : glycanSession.linkages) {
            linkages.add(new Linkage(linkage));
        }
    }

    public GlycamSequence getSequence() {
        return sequence;
    }

    /**
     * Returns the linkages of the glycan.
     */
    public List<Linkage> getLinkages() {
        return linkages;
    }

    public Linkage getLinkage(int i) {
        return linkages.get(i);
    }

    public int getLinkageCount() {
        return linkages.size();
    }

    public SolvationSettings getSolvationSettings() {
        return solvationSettings;
    }

    public void setSolvationSettings(SolvationSettings options) {
        solvationSettings = options;
    }

    /**
     * Returns the total number of glycans this session would generate if {@link build} were
     * called on it.
     * 
     * @return the number of glycans this session generates.
     */
    public int getTotalStructureCount() {
        int totalStructures = 1;
        for (int i = 2; i < linkages.size(); i++) {
            if (linkages.get(i).getPhiValues().size() > 0)
                totalStructures *= linkages.get(i).getPhiValues().size();
            if (linkages.get(i).getPsiValues().size() > 0)
                totalStructures *= linkages.get(i).getPsiValues().size();
            if (linkages.get(i).getOmegaValues().size() > 0)
                totalStructures *= linkages.get(i).getOmegaValues().size();
        }
        return totalStructures;
    }

    /**
     * Returns true if the glycan has any flexible phi angles.
     * Note: I think I can set a variable in the constructor rather than doing the work each time
     * this is called. Same for below.
     */
    public boolean hasFlexiblePhis() {
        for (int i = 2; i < linkages.size(); i++) {
            if (linkages.get(i).isFlexiblePhi())
                return true;
        }
        return false;
    }

    /**
     * Returns true if the glycan has any flexible omega angles.
     */
    public boolean hasFlexibleOmegas() {
        for (int i = 2; i < linkages.size(); i++) {
            if (linkages.get(i).isFlexibleOmega())
                return true;
        }
        return false;
    }

    /**
     * Returns the build request that's associated with this session.
     *
     * @return the build request associated with session or {@code null} if there isn't one.
     */
    public BuildRequest getBuildRequest() {
        return buildRequest;
    }

    /**
     * Resets the build request associated with this session, if there is one.
     */
    public void resetBuildRequest() {
        buildRequest = null;
    }

    /**
     * Builds the glycan(s).
     *
     * This function returns before the build is finished. The BuildRequest object that's returned
     * contains information about the status of the build.
     *
     * Future calls to getBuildRequest() will return the BuildRequest that's created until
     * resetBuildRequest() is called or until build() is called again.
     *
     * @param outputDirectory the directory where the output files will be placed.
     */
    public BuildRequest build(File outputDirectory) {
        buildRequest = new BuildRequest(buildProtocolBuffer(), outputDirectory);
        return buildRequest;
    }

    /**
     * Builds the protocol buffer representing the information in this session.
     */
    public BuildInfo buildProtocolBuffer() {
        BuildInfo.Builder info = BuildInfo.newBuilder();
        info.setGlycan(sequence.toString());

        for (Linkage linkage : linkages) {
            BuildInfo.Linkage.Builder linkageInfo = BuildInfo.Linkage.newBuilder();
            for (double value : linkage.getPhiValues())
                linkageInfo.addPhiValue(value);
            for (double value : linkage.getPsiValues())
                linkageInfo.addPsiValue(value);
            for (double value : linkage.getOmegaValues())
                linkageInfo.addOmegaValue(value);
            info.addLinkage(linkageInfo);
        }

        if (solvationSettings != null) {
            SolvationInfo.Builder solvationInfo = SolvationInfo.newBuilder();
            solvationInfo.setDistance(solvationSettings.getBuffer());
            solvationInfo.setCloseness(solvationSettings.getCloseness());
            solvationInfo.setShape(SolvationInfo.Shape.CUBIC);
            info.setSolvationInfo(solvationInfo.build());
        }

        return info.build();
    }
}
