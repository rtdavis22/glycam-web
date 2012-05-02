package org.glycam.molecular_dynamics.glycoprotein_builder;

import org.glycam.CPP;
import org.glycam.Logging;
import org.glycam.pdb.PdbFilePB.GlycoproteinBuildInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BuildRequest implements Runnable {
    public enum Status { NOT_STARTED, WORKING, DONE, ERROR }

    private String uuid;

    private File outputDirectory;

    private Status status;

    private GlycoproteinBuildInfo buildInfo;

    public BuildRequest(GlycoproteinBuildInfo buildInfo, File outputDirectory, String uuid) {
        this.buildInfo = buildInfo;
        this.outputDirectory = outputDirectory;
        this.uuid = uuid;
        this.status = Status.NOT_STARTED;
        outputDirectory.mkdirs();
    }

    public Status getStatus() {
        return status;
    }

    public String getUUID() {
        return uuid;
    }

    public void run() {
        status = Status.WORKING;

        try {
            File protocolBuffer = File.createTempFile("glycoprotein_pb", null);
            FileOutputStream output = new FileOutputStream(protocolBuffer);
            buildInfo.writeTo(output);
            output.close();
            String command = "build_glycoprotein " + protocolBuffer.getPath();
            Process process = CPP.exec(command, outputDirectory);
            Logging.logger.info("process exited with value " + process.waitFor());
            status = Status.DONE;
        } catch(InterruptedException e) {
            Logging.logger.severe(e.getMessage());
            status = Status.ERROR;
            return;
        } catch(IOException e) {
            Logging.logger.severe(e.getMessage());
            status = Status.ERROR;
            return;
        }

        status = Status.DONE;
    }
}
