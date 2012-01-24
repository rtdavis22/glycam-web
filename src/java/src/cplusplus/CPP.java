// Author: Robert Davis

package cplusplus;

import configuration.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;

// These are utility functions to make interacting with the c++ programs easier.
public class CPP {
    public static String get_bin_path() { return path; }

    private static String path = Configuration.getProjectRoot() + "/bin/";

    // This is to ensure that this class cannot be instantiated.
    private CPP() {
        throw new AssertionError();
    }

    public static void logOutput(Process process) throws IOException {
        BufferedReader is;
        String line;
        is = new BufferedReader(new InputStreamReader(process.getInputStream()));
        
        while ((line = is.readLine()) != null) {
            System.out.println(line);
        }

        System.out.flush();
        try {
            int status = process.waitFor();
            System.out.println("Exit status: " + status);
        } catch (InterruptedException e) {
            System.out.println(e);
            return;
        }
    }

    public static Process exec(String command) throws IOException {
        return exec(command, null);
    }

    public static Process exec(String command, File dir) throws IOException {
        return Runtime.getRuntime().exec(path + command, null, dir);
    }

    public static Process execMPI(String command, int numProcessors)
                                  throws java.io.IOException {
        return execMPI(command, numProcessors, null);
    }

    public static Process execMPI(String command, int numProcessors, java.io.File dir)
            throws java.io.IOException {
        String shellCommand = "mpirun -np " + numProcessors + " " + path + command;
        String[] cmd = { "/bin/sh", "-c", shellCommand };
 
        return Runtime.getRuntime().exec(cmd, null, dir);
    }
}
