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

public class OligosaccharideSession {
    // The full name of the glycan.
    private String structure;

    // 
    private ArrayList<Linkage> linkages;

    // This is null if the structure is not solvated.
    private SolvationSettings solvationSettings;

    private BuildRequest buildRequest;

    public OligosaccharideSession(String structure) throws java.io.IOException {
        this.structure = structure;
        initLinkages(structure);
        solvationSettings = null;
        buildRequest = null;
    }

    public BuildRequest getBuildRequest() { return buildRequest; }
    public void resetBuildRequest() { buildRequest = null; }

    public BuildRequest build(File outputDirectory, String uid) {
        BuildInfo build_info = buildProtocolBuffer();

        buildRequest = new BuildRequest(build_info, outputDirectory, uid);
        Thread thread = new Thread(buildRequest);
        thread.start();
        return buildRequest;
    }

    // Accessors
    public String getStructure() { return structure; }

    public ArrayList<Linkage> getLinkages() { return linkages; }

    public SolvationSettings getSolvationSettings() {
        return solvationSettings;
    }

    // Mutators
    public void setSolvationSettings(SolvationSettings options) {
        solvationSettings = options;
    }

    // TODO: Modify this to use protocol buffers.
    private void initLinkages(String structure) throws java.io.IOException {
        linkages = new ArrayList<Linkage>();
        boolean valid = true;
        try {
            Process process = CPP.exec("get_linkages " + structure);
            BufferedReader is = new BufferedReader(
                 new InputStreamReader(process.getInputStream())
            );
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

    public boolean hasFlexiblePhis() {
        for (int i = 2; i < linkages.size(); i++) {
            if (linkages.get(i).isFlexiblePhi())
                return true;
        }
        return false;
    }

    public boolean hasFlexibleOmegas() {
        for (int i = 2; i < linkages.size(); i++) {
            if (linkages.get(i).isFlexibleOmega())
                return true;
        }
        return false;
    }

    // Remove this.
   /*
    public String getLinkageString() {
        String ret = "";
        if (linkages.size() > 0)
            ret += linkages.get(0).getString();
        for (int i = 1; i < linkages.size(); i++) {
            ret += ";" + linkages.get(i).getString();
        }
        return ret;
    }
*/

    // 
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
