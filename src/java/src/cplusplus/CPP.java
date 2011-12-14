package cplusplus;

import java.io.*; //remove
import java.io.File;

public class CPP {

    public static String get_bin_path() { return path; }

    private static String path =
        "/opt/apache-tomcat-6.0.32/webapps/glycam-web/WEB-INF/cpp/bin/";

    //private static String path =
    //   new File(".").getAbsolutePath() + "/cpp/bin/";

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

    public static Process exec(String command) throws java.io.IOException {
        System.out.println("Execing command " + path + command);
        return Runtime.getRuntime().exec(path + command);
    }

    public static Process exec(String command, File dir) 
                               throws java.io.IOException {
        return Runtime.getRuntime().exec(path + command, null, dir);
    }

    //should call the one below
    public static Process execMPI(String command, int numProcessors)
                                  throws java.io.IOException {
        String fullCommand = "/programs/bin/mpirun -np " + numProcessors + " " +
                             path + command;
        //Process process = Runtime.getRuntime().exec(fullCommand);
        //return process;
        return Runtime.getRuntime().exec(fullCommand);
    }

    public static Process execMPI(String command, int numProcessors, 
                                  java.io.File dir) throws java.io.IOException {
        String fullCommand = "/programs/bin/mpirun -np " + numProcessors + " " +
                             path + command;
        System.out.println("Execing command " + fullCommand);
        
        return Runtime.getRuntime().exec(fullCommand, null, dir);
    }

    // Modify this to return the what() of the exception.
    public static String validateStructure(String structure) {
        String defaultError = "Error in sequence";
        try {
            Process process = exec("validate_structure " + structure);
            if (process.waitFor() != 0)
                return defaultError;
        } catch(java.io.IOException e) {
            return defaultError;
        } catch(InterruptedException e) {
            return defaultError;
        }
        return "";
    }
}
