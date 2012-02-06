package org.glycam.molecular_dynamics.glycan_builder;

import org.glycam.CPP;
import org.glycam.GlycamSequence;
import org.glycam.Linkage;
import org.glycam.Logging;
import org.glycam.molecular_dynamics.SolvationSettings;
import org.glycam.molecular_dynamics.glycan_builder.BuildInfoPB.BuildInfo;

import java.io.File;
import java.io.IOException;
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
    private final List<Linkage> linkages;

    /**
     * Information for how to solvate the glycan.
     */
    private SolvationSettings solvationSettings = null;

    /**
     * The build request this session is waiting on.
     */
    private BuildRequest buildRequest = null;

    /**
     * Initialize the session with a sequence in GLYCAM condensed nomenclature.
     *
     * @param sequence The sequence in GLYCAM condensed nomenclature.
     */
    public GlycanSession(GlycamSequence sequence) throws IOException {
        this.sequence = sequence;
        this.linkages = sequence.getLinkages();
    }

    /**
     * Returns the sequence in GLYCAM condensed nomenclature.
     */
    public GlycamSequence getSequence() {
        return sequence;
    }

    /**
     * Returns the linkages of the glycan.
     * Don't use this anymore!
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

    /**
     * Returns the solvation information associated with this session.
     */
    public SolvationSettings getSolvationSettings() {
        return solvationSettings;
    }

    /**
     * Sets the solvation information associated with this session.
     */
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
     * <p>This function returns before the build is finished. The {@link BuildRequest} object
     * that's returned contains information about the status of the build.</p>
     *
     * <p>Future calls to {@link getBuildRequest} will return the {@link BuildRequest} that's
     * created until {@link resetBuildRequest} is called.</p>
     *
     * @param outputDirectory the directory where the output files will be placed.
     * @param uid I should get rid of this parameter, I think.
     *
     * @return the {@link BuildRequest}.
     */
    public BuildRequest build(File outputDirectory, String uid) {
        buildRequest = new BuildRequest(buildProtocolBuffer(), outputDirectory, uid);
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

        return info.build();
    }
}
