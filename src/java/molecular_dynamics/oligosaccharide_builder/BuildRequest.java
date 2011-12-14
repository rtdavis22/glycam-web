package molecular_dynamics.oligosaccharide_builder;

import molecular_dynamics.LinkageValues;
import molecular_dynamics.SolvationSettings;

import molecular_dynamics.oligosaccharide_builder.BuildInfoPB.BuildInfo;
import molecular_dynamics.oligosaccharide_builder.BuildResultsPB.BuildResults;

import cplusplus.CPP;

import java.io.*;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

public class BuildRequest implements Runnable {
    // NOT_STARTED: run() has not been called yet.
    // WORKING: the build is in progress.
    // DONE: the build has successfully finished.
    // ERROR: the build has unsuccessfully finished.
    public enum Status { NOT_STARTED, WORKING, DONE, ERROR }

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
    // TODO: make a new class "ResultStructure", which contains a SortedMap<>.
    private ArrayList<SortedMap<Integer, LinkageValues>> resultStructures;

    // Pass in the target File in the constructor.
    public BuildRequest(BuildInfo buildInfo, File outputDirectory, String uuid) {
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

    public ArrayList<SortedMap<Integer, LinkageValues>> getResultStructures() {
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
            System.out.println("process exited with value " + process.waitFor());
        } catch(IOException e) {
            status = Status.ERROR;
            return;
        } catch(InterruptedException e) {
            status = Status.ERROR;
            return;
        }

        resultStructures = parseResults(results);

        status = Status.DONE;
    }

    private ArrayList<SortedMap<Integer, LinkageValues>> parseResults(BuildResults results) {
        ArrayList<SortedMap<Integer, LinkageValues>> resultStructures =
                new ArrayList<SortedMap<Integer, LinkageValues>>();

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
            resultStructures.add(structureAngles);
        }
        return resultStructures;
    }
}
