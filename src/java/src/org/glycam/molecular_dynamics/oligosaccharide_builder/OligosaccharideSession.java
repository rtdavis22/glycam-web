package org.glycam.molecular_dynamics.oligosaccharide_builder;

import org.glycam.molecular_dynamics.Linkage;
import org.glycam.molecular_dynamics.SolvationSettings;
import org.glycam.molecular_dynamics.oligosaccharide_builder.BuildInfoPB.BuildInfo;
import org.glycam.CPP;
import org.glycam.Logging;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The relevant information needed to build oligosaccharides.
 *
 * @author Robert Davis
 */
public class OligosaccharideSession {
    /**
     * The sequence in GLYCAM condensed nomenclature.
     */
    private String structure;

    /**
     * Linkage information, one per residue. (Should be List).
     */
    private ArrayList<Linkage> linkages;

    /**
     * Information for how to solvate the structure.
     */
    private SolvationSettings solvationSettings;

    /**
     * The build request this session is waiting on.
     */
    private BuildRequest buildRequest;

    /**
     * Initialize the session with a sequence in GLYCAM condensed nomenclature.
     *
     * @param sequence The sequence in GLYCAM condensed nomenclature. (Should take Sequence).
     */
    public OligosaccharideSession(String sequence) throws java.io.IOException {
        this.structure = sequence;
        initLinkages(sequence);
        solvationSettings = null;
        buildRequest = null;
    }

    /**
     * Returns the sequence in GLYCAM condensed nomenclature.
     */
    public String getStructure() { return structure; }

    /**
     * Returns the linkages of the oligosaccharide.
     */
    public ArrayList<Linkage> getLinkages() { return linkages; }

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

    // TODO: Modify this to use protocol buffers. This should be in Sequence.
    private void initLinkages(String structure) throws java.io.IOException {
        linkages = new ArrayList<Linkage>();
        boolean valid = true;
        try {
            Process process = CPP.exec("get_linkages " + structure);
            BufferedReader is = new BufferedReader(new InputStreamReader(process.getInputStream()));
            process.waitFor();
            String line;
            while ((line = is.readLine()) != null) {
                String[] tokens = line.split(" ");
                boolean hasOmega = false;
                if (tokens[1].equals("1"))
                    hasOmega = true;
                boolean hasPhis = false;
                if (tokens[2].equals("1"))
                    hasPhis = true;
                Linkage linkage = new Linkage(tokens[0], hasOmega, hasPhis);
                String[] valueTokens = tokens[3].split(":");
                String[] phiValues = valueTokens[0].split(",");
                String[] omegaValues = valueTokens[1].split(",");
                for (int i = 0; i < phiValues.length; i++)
                    if (phiValues[i].length() > 0 && !phiValues[i].equals("-"))
                        linkage.addPhiValue(Double.parseDouble(phiValues[i]));
                for (int i = 0; i < omegaValues.length; i++)
                    if (omegaValues[i].length() > 0 && 
                            !omegaValues[i].equals("-"))
                        linkage.addOmegaValue(
                            Double.parseDouble(omegaValues[i])
                        );
                linkages.add(linkage);
            }
        } catch (java.io.IOException e) {
            Logging.logger.severe(e.getMessage());
            throw e;
        } catch(InterruptedException e) {
            Logging.logger.severe(e.getMessage());
        }
    }

    /**
     * Returns the total number of structures this session would generate if {@link build} were
     * called on it.
     * 
     * @return the number of structures this session generates.
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
     * Returns true if the structure has any flexible phi angles.
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
     * Returns true if the structure has any flexible omega angles.
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
    public BuildRequest getBuildRequest() { return buildRequest; }

    /**
     * Resets the build request associated with this session, if there is one.
     */
    public void resetBuildRequest() { buildRequest = null; }

    /**
     * Builds the oligosaccharide(s).
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
        BuildInfo build_info = buildProtocolBuffer();

        buildRequest = new BuildRequest(build_info, outputDirectory, uid);
        Thread thread = new Thread(buildRequest);
        thread.start();
        return buildRequest;
    }

    /**
     * Build the protocol buffer representing the information in this session.
     */
    public BuildInfo buildProtocolBuffer() {
        BuildInfo.Builder info = BuildInfo.newBuilder();
        info.setGlycan(structure);

        for (int i = 0; i < linkages.size(); i++) { 
            BuildInfo.Linkage.Builder linkage = BuildInfo.Linkage.newBuilder();
            for (double value : linkages.get(i).getPhiValues())
                linkage.addPhiValue(value);
            for (double value : linkages.get(i).getPsiValues())
                linkage.addPsiValue(value);
            for (double value : linkages.get(i).getOmegaValues())
                linkage.addOmegaValue(value);
            info.addLinkage(linkage);
        }

        return info.build();
    }
}
