// Author: Robert Davis

package org.glycam.molecular_dynamics.oligosaccharide_builder;

import org.glycam.CPP;
import org.glycam.Logging;
import org.glycam.molecular_dynamics.LinkageValues;
import org.glycam.molecular_dynamics.SolvationSettings;
import org.glycam.molecular_dynamics.oligosaccharide_builder.BuildInfoPB.BuildInfo;
import org.glycam.molecular_dynamics.oligosaccharide_builder.BuildResultsPB.BuildResults;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

public class BuildRequest implements Runnable {
    public class StructureComparator implements java.util.Comparator<ResultStructure> {
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
     * Represents the status of a build request.
     */
    public enum Status {
        /** run has not been called yet. Remove this and run when constructed, I think. */
        NOT_STARTED,

        /** The build is in progess. */
        WORKING,

        /** The build has successfully finished. */
        DONE,

        /** The build terminated with an error. */
        ERROR
    }

    // A unique identifier associated with this build.
    private String uuid;

    // The directory where the builds output is going to.
    private File outputDirectory;

    // The status of the request.
    private Status status;

    // This is a protocol buffer that's sent to a c++ process to tell it what to build.
    private BuildInfo buildInfo;

    // Each structure in the list has a map from the linkage index to the angle values of
    // that linkage. The protocol buffer from the c++ process could be used directly but this
    // is a preferable data structure.
    private ArrayList<ResultStructure> resultStructures;

    // run() should be called here, I think.
    BuildRequest(BuildInfo buildInfo, File outputDirectory, String uuid) {
        this.buildInfo = buildInfo;
        resultStructures = null;
        status = Status.NOT_STARTED;
        this.uuid = uuid;
        this.outputDirectory = outputDirectory;
        outputDirectory.mkdirs();
    }

    public int getStructuresBuiltSoFar() {
        String[] files = outputDirectory.list();
        int count = 0;
        for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith(".pdb"))
               count++;
        }
        return count;
    }

    public ArrayList<ResultStructure> getResultStructures() {
        return resultStructures;
    }

    public String getUUID() { return uuid; }
    public Status getStatus() { return status; }

    public void run() {
        status = Status.WORKING;
 
        BuildResults results = null;

        try {
            File tempFile = File.createTempFile("build_request", null);
            FileOutputStream output = new FileOutputStream(tempFile);
            buildInfo.writeTo(output);
            output.close();
            String command = "build_torsions_mpi " + tempFile.getPath();
            Process process = CPP.execMPI(command, 16, outputDirectory);
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

        resultStructures = parseResults(results);

        // Sort the structures by energy, from lowest to highest.
        java.util.Collections.sort(resultStructures, new StructureComparator());

        if (resultStructures.size() > 0) {
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

    // Rename the files according to the order of the result structures.
    private void renameFiles() {
        java.util.TreeMap<Integer, Integer> map = new java.util.TreeMap<Integer, Integer>();
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

    private ArrayList<ResultStructure> parseResults(BuildResults results) {
        ArrayList<ResultStructure> resultStructures = new ArrayList<ResultStructure>();

        int structureIndex = 0;
        for (BuildResults.Structure structure : results.getStructureList()) {
            SortedMap<Integer, LinkageValues> structureAngles =
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
            // The last parameter is the Boltzmann probability. We'll put a dummy value in here.
            resultStructures.add(new ResultStructure(structureAngles, structureIndex++, energy, 0));
        }
        return resultStructures;
    }
}
