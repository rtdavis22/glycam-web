package org.glycam;

import org.glycam.CPP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class Sequence {
    private String sequence;

    public Sequence(String sequence) {
        this.sequence = sequence;
    }

    // This checks if a particular sequence in GLYCAM condensed nomenclature has correct syntax
    // and can be built. If it does not, an error message is returned. Otherwise, an empty
    // string is returned.
    public String validate() {
        String defaultError = "Error in sequence";
        try {
            Process process = CPP.exec("validate_structure " + sequence);
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
