// Author: Robert Davis

package org.glycam;

import configuration.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;

// These are utility functions to make interacting with the C++ programs easier.
public class CPP {
    public static String get_bin_path() { return path; }

    private static String path = Configuration.getProjectRoot() + "/bin/";

    // This is to ensure that this class cannot be instantiated.
    private CPP() {
        throw new AssertionError();
    }

    public static Process exec(String command) throws IOException {
        return exec(command, null);
    }

    public static Process exec(String command, File dir) throws IOException {
        Logging.logger.info("Execing command " + path + command);
        return Runtime.getRuntime().exec(path + command, null, dir);
    }

    public static Process execMPI(String command, int numProcessors) throws java.io.IOException {
        return execMPI(command, numProcessors, null);
    }

    public static Process execMPI(String command, int numProcessors, java.io.File dir)
            throws java.io.IOException {
        String shellCommand = "mpirun -np " + numProcessors + " " + path + command;
        String[] cmd = { "/bin/sh", "-c", shellCommand };
        Logging.logger.info("Execing shell command " + shellCommand);
        return Runtime.getRuntime().exec(cmd, null, dir);
    }
}
