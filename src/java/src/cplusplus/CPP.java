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
        System.out.println("Execing command " + path + command);
        return Runtime.getRuntime().exec(path + command);
    }

    public static Process exec(String command, File dir) throws IOException {
        return Runtime.getRuntime().exec(path + command, null, dir);
    }

    // should call the one below
    public static Process execMPI(String command, int numProcessors)
                                  throws java.io.IOException {
        String fullCommand = "/programs/bin/mpirun -np " + numProcessors + " " + path + command;
        return Runtime.getRuntime().exec(fullCommand);
    }

    public static Process execMPI(String command, int numProcessors, java.io.File dir)
            throws java.io.IOException {
        String fullCommand = "/programs/bin/mpirun -np " + numProcessors + " " + path + command;
        System.out.println("Execing command " + fullCommand);
        
        return Runtime.getRuntime().exec(fullCommand, null, dir);
    }

    // This checks if a particular sequence in GLYCAM condensed nomenclature has correct syntax
    // and can be built. If it does not, an error message is returned. Otherwise, an empty
    // string is returned.
    public static String validateStructure(String sequence) {
        String defaultError = "Error in sequence";
        try {
            Process process = exec("validate_structure " + sequence);
            if (process.waitFor() != 0) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String line;
                if ((line = reader.readLine()) != null)
                    return line;
                else
                    return defaultError;
            }
        } catch (IOException e) {
            return defaultError;
        } catch (InterruptedException e) {
            return defaultError;
        }
        return "";
    }
}
