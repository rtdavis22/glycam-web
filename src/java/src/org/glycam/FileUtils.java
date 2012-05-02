package org.glycam;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    private FileUtils() {}

    public static File createTarGz(File directory, String fileList, String outputName) {
        createCompressedFile("tar -czf", directory, fileList, outputName);
        return getFileInDirectory(directory, outputName);
    }

    public static File createZip(File directory, String fileList, String outputName) {
        createCompressedFile("zip", directory, fileList, outputName);
        return getFileInDirectory(directory, outputName);
    }

    private static void createCompressedFile(String compressCommand, File directory,
                                             String fileList, String outputName) {
        String[] cmd = { "/bin/sh", "-c",
                         compressCommand + " " + outputName + " " + fileList };
        try {
            Process process = Runtime.getRuntime().exec(cmd, null, directory);
            process.waitFor();
        } catch (IOException e) {
            Logging.logger.severe(e.getMessage());
        } catch (InterruptedException e) {
            Logging.logger.severe(e.getMessage());
        }
    }

    private static File getFileInDirectory(File directory, String fileName) {
        for (File file : directory.listFiles()) {
            if (file.getName().equals(fileName))
                return file;
        }
        return null;
    }
}
