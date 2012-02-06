package org.glycam;

import org.glycam.CPP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.File;

/**
 * Utility functions.
 *
 * @author Robert Davis
 */
public class Utils {
    /**
     * Checks to see if a PDB file is valid.
     *
     * @return {@code ""} if the PDB is valid. Otherwise, an error message.
     */
    public static String validatePdb(File pdbFile) {
        String defaultError = "Invalid pdb";
        try {
            Process process = CPP.exec("validate_pdb " + pdbFile.getPath());
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
