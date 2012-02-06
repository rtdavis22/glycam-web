package org.glycam;

import org.glycam.configuration.Configuration;

import java.io.File;
import java.io.IOException;

/**
 * Utility functions to make interacting with the C++ programs easier.
 *
 * @author Robert Davis
 */
public class CPP {
    /**
     * The path to the C++ binaries.
     */
    private static String path = Configuration.getProjectRoot() + "/bin/";

    private CPP() {
        throw new AssertionError();
    }

    /**
     * Execute a C++ executable.
     *
     * @param command the C++ executable name, followed optionally by arguments.
     *
     * @return Process the process that is created.
     *
     * @throws IOException if there was an error execing the command.
     */
    public static Process exec(String command) throws IOException {
        return exec(command, null);
    }

    /**
     * Executes a C++ executable in a specified directory.
     *
     * @param command the C++ executable name, followed optionally by arguments.
     * @param dir the directory in which to run the program.
     *
     * @return the process that is created.
     *
     * @throws IOException if there was an error execing the command.
     */
    public static Process exec(String command, File dir) throws IOException {
        return execFullCommand(path + command, dir);
    }

    /**
     * Executes a C++ executable as a shell command.
     *
     * @param command the command.
     *
     * @return the process that is created.
     *
     * @throws IOException if there was an error execing the command.
     */
    public static Process execShellCommand(String command) throws IOException {
        return execShellCommand(command, null);
    }

    /**
     * Executes a C++ executable as a shell command in a specified directory.
     *
     * @param command the command.
     * @param dir the directory in which to run the program.
     *
     * @return the process that is created.
     *
     * @throws IOException if there was an error execing the command.
     */
    public static Process execShellCommand(String command, File dir) throws IOException {
        return execFullShellCommand(path + command, dir);
    }

    /**
     * Executes a C++ executable with MPI.
     *
     * @param command the command.
     * @param numProcessors the number of processors to use.
     *
     * @return the process that is created.
     *
     * @throws IOException if there was an error execing the command.
     */
    public static Process execMPI(String command, int numProcessors) throws IOException {
        return execMPI(command, numProcessors, null);
    }

    /**
     * Executes the C++ executable with MPI in a specified directory.
     *
     * @param command the command.
     * @param numProcessors the number of processors to use.
     * @param dir the directory in which to run the program.
     *
     * @return the process that is created.
     *
     * @throws IOException if there was an error execing the command.
     */
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
