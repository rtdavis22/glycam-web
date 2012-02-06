package org.glycam.molecular_dynamics.glycan_builder;

import org.glycam.CPP;
import org.glycam.LinkageValues;
import org.glycam.Logging;
import org.glycam.molecular_dynamics.SolvationSettings;
import org.glycam.molecular_dynamics.glycan_builder.BuildInfoPB.BuildInfo;
import org.glycam.molecular_dynamics.glycan_builder.BuildResultsPB.BuildResults;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A request to build the topology, restart, and PDB files for a glycan.
 *
 * @author Robert Davis
 */
public class BuildRequest implements Runnable {
    /**
     * The number of processors to use to fulfill this request.
     */
    private static final int NUM_PROCESSORS = 16;

    /**
     * Represents the status of a build request.
     */
    public enum Status {
        /** The build is in progess. */
        WORKING,

        /** The build has successfully finished. */
        DONE,

        /** The build terminated with an error. */
        ERROR
    }

    // A unique identifier associated with this build. GET rid of this.
    private String uuid;

    /**
     * The directory where generated files will be written.
     */
    private File outputDirectory;

    /**
     * The status of the build request.
     */
    private Status status;

    /**
     * A protocol buffer with the information the C++ process needs to fulfill the build request.
     */
    private BuildInfo buildInfo;

    /**
     * The conformations that this build request generated.
     */
    private List<ResultStructure> resultStructures;

    /**
     * Creates a start a build request.
     *
     * @param buildInfo a protocol buffer representing the glycan to be built.
     * @param outputDirectory the directory where files will be written.
     */
    BuildRequest(BuildInfo buildInfo, File outputDirectory, String uuid) {
        this.buildInfo = buildInfo;
        this.uuid = uuid;
        this.outputDirectory = outputDirectory;
        this.resultStructures = null;
        outputDirectory.mkdirs();
        new Thread(this).start();
    }

    /**
     * Returns the status of the build.
     */
    public Status getStatus() { return status; }

    /**
     * Returns the unique ID associated with this build.
     * Get rid of this!!.
     */
    public String getUUID() { return uuid; }

    /**
     * The structures that this request created.
     *
     * This is only valid if {@link getStatus} is Status.DONE.
     */
    public List<ResultStructure> getResultStructures() {
        return resultStructures;
    }

    /**
     * Returns the number of structures this request has built so far.
     *
     * @return the number of structures built so far.
     */
    public int getStructuresBuiltSoFar() {
        String[] files = outputDirectory.list();
        int count = 0;
        for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith(".pdb"))
               count++;
        }
        return count;
    }

    /**
     * Builds the topology, restart, and PDB files.
     */
    public void run() {
        status = Status.WORKING;
 
        BuildResults results = null;

        try {
            File tempFile = File.createTempFile("build_request", null);
            FileOutputStream output = new FileOutputStream(tempFile);
            buildInfo.writeTo(output);
            output.close();
            String command = "build_torsions_mpi " + tempFile.getPath();
            Process process = CPP.execMPI(command, NUM_PROCESSORS, outputDirectory);
            results = BuildResults.parseFrom(process.getInputStream());
            tempFile.delete();
            Logging.logger.info("process exited with value " + process.waitFor());
        } catch (IOException e) {
            Logging.logger.severe(e.getMessage());
            status = Status.ERROR;
            return;
        } catch (InterruptedException e) {
            Logging.logger.severe(e.getMessage());
            status = Status.ERROR;
            return;
        }

        resultStructures = createResultStructures(results);

        java.util.Collections.sort(resultStructures, new StructureComparator());

        if (!resultStructures.isEmpty()) {
            double minEnergy = resultStructures.get(0).getEnergy();
            // Boltzmann's constant times temperature
            double kT = 0.0019872041*300;
            for (ResultStructure structure : resultStructures) {
                structure.setBoltzmann(Math.exp((minEnergy - structure.getEnergy())/kT));
            }
        }

        renameFiles();

        status = Status.DONE;
    }

    /**
     * A comparator for {@link ResultStructure}s based on energy.
     */
    private class StructureComparator implements java.util.Comparator<ResultStructure> {
        @Override
        public int compare(ResultStructure lhs, ResultStructure rhs) {
            double difference = rhs.getEnergy() - lhs.getEnergy();
            if (difference == 0) {
                return 0;
            } else if (difference < 0) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     * Rename the files according to the order of the result structures.
     */
    private void renameFiles() {
        Map<Integer, Integer> map = new java.util.TreeMap<Integer, Integer>();
        for (int i = 0; i < resultStructures.size(); i++) {
            map.put(resultStructures.get(i).getIndex(), i);
        }

        File[] files = outputDirectory.listFiles();
        for (File file : files) {
            String name = file.getName();
            String[] tokens = name.split("\\.");
            if (tokens.length == 2 && (tokens[1].equals("pdb") || tokens[1].equals("rst"))) {
                int index = Integer.parseInt(tokens[0]) - 1;
                int new_index = map.get(index) + 1;
                file.renameTo(new File(outputDirectory, new_index + "." + tokens[1] + ".temp"));
            }
        }

        files = outputDirectory.listFiles();
        for (File file : files) {
            String name = file.getName();
            if (name.endsWith(".temp")) {
                file.renameTo(new File(outputDirectory, name.substring(0, name.length() - 5)));
            }
        }
    }

    /**
     * Creates a list of structures from a protocol buffer which represents the results of the
     * build.
     *
     * @param results a protocol buffer representing the results of the build.
     *
     * @return a list of structures that were build and information associated with them.
     */
    private List<ResultStructure> createResultStructures(BuildResults results) {
        List<ResultStructure> resultStructures = new ArrayList<ResultStructure>();
        int structureIndex = 0;
        for (BuildResults.Structure structure : results.getStructureList()) {
            Map<Integer, LinkageValues> structureAngles =
                    new TreeMap<Integer, LinkageValues>();

            for (BuildResults.FlexibleLinkage flexLinkage : structure.getFlexibleLinkageList()) {
                int index = flexLinkage.getIndex();
                BuildResults.FlexibleLinkage.Angle angleType = flexLinkage.getAngle();
                double value = flexLinkage.getValue();

                LinkageValues angleValues = structureAngles.get(new Integer(index));

                if (angleValues == null) {
                    angleValues = new LinkageValues();
                    structureAngles.put(index, angleValues);
                }

                if (angleType == BuildResults.FlexibleLinkage.Angle.PHI)
                    angleValues.setPhi(value);
                else if (angleType == BuildResults.FlexibleLinkage.Angle.PSI)
                    angleValues.setPsi(value);
                else if (angleType == BuildResults.FlexibleLinkage.Angle.OMEGA)
                    angleValues.setOmega(value);
            }
            double energy = structure.getEnergy();
            // The last parameter is the Boltzmann probability. We'll put a dummy value in here
            // for now.
            resultStructures.add(new ResultStructure(structureAngles, structureIndex++, energy, 0));
        }
        return resultStructures;
    }
}
