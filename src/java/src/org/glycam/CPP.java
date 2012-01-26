// Author: Robert Davis

package org.glycam;

import org.glycam.configuration.Configuration;

import java.io.File;
import java.io.IOException;

// These are utility functions to make interacting with the C++ programs easier.
public class CPP {
    private static String path = Configuration.getProjectRoot() + "/bin/";

    // This is to ensure that this class cannot be instantiated.
    private CPP() {
        throw new AssertionError();
    }

    public static String getBinPath() { return path; }

    public static Process exec(String command) throws IOException {
        return exec(command, null);
    }

    public static Process exec(String command, File dir) throws IOException {
        return execFullCommand(path + command, dir);
    }

    public static Process execShellCommand(String command) throws IOException {
        return execShellCommand(command, null);
    }

    public static Process execShellCommand(String command, File dir) throws IOException {
        return execFullShellCommand(path + command, dir);
    }

    public static Process execMPI(String command, int numProcessors) throws IOException {
        return execMPI(command, numProcessors, null);
    }

    public static Process execMPI(String command, int numProcessors, File dir) throws IOException {
        String shellCommand = "mpirun -np " + numProcessors + " " + path + command;
        return execFullShellCommand(shellCommand, dir);
    }

    private static Process execFullCommand(String command, File dir) throws IOException {
        Logging.logger.info("Execing command " + command);
        return Runtime.getRuntime().exec(command, null, dir);
    }

    private static Process execFullShellCommand(String command, File dir) throws IOException {
        String[] cmd = { "/bin/sh", "-c", command };
        Logging.logger.info("Execing shell command " + command);
        return Runtime.getRuntime().exec(cmd, null, dir);
    }
}
