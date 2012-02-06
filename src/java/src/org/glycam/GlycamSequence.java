package org.glycam;

import org.glycam.CPP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A sequence in GLYCAM condensed nomenclature.
 *
 * @author Robert Davis
 */
public class GlycamSequence {
    /**
     * The sequence in GLYCAM condensed nomenclature.
     */
    private final String sequence;

    /**
     * Constructs a sequence object from a sequence in GLYCAM condensed nomenclature.
     *
     * @param sequence a sequence in GLYCAM condensed nomenclature.
     */
    public GlycamSequence(String sequence) {
        this.sequence = sequence;
    }

    /**
     * Returns the sequence.
     *
     * @return the sequence.
     */
    @Override
    public String toString() {
        return sequence;
    }

    /**
     * Checks if the sequence is valid.
     * @return {@code ""} if the sequence is valid in condensed GLYCAM nomenclature and can be
     *         built. Otherwise, an error message is returned.
     */
    public String validate() {
        String defaultError = "Error in sequence";
        try {
            Process process = CPP.exec("validate_sequence " + sequence);
            if (process.waitFor() != 0) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String line;
                if ((line = reader.readLine()) != null) {
                    return line;
                } else {
                    return defaultError;
                }
            }
        } catch (IOException e) {
            return defaultError;
        } catch (InterruptedException e) {
            return defaultError;
        }
        return "";
    }

    /**
     * Returns the list of linkages associated with this sequence, one per residue.
     *
     * The order of the linkages is the reverse of where they appear in the sequence.
     *
     * TODO: Use a protocol buffer for this.
     *
     * @return a list of linkages, one for each residue.
     *
     * @throws IOException if there was an error executing the C++ program used to get the linkages.
     */
    public List<Linkage> getLinkages() throws IOException {
        List<Linkage> linkages = new ArrayList<Linkage>();
        boolean valid = true;
        try {
            Process process = CPP.exec("get_linkages " + sequence);
            BufferedReader is = new BufferedReader(new InputStreamReader(process.getInputStream()));
            process.waitFor();
            String line;
            while ((line = is.readLine()) != null) {
                String[] tokens = line.split(" ");
                boolean hasOmega = false;
                if (tokens[1].equals("1"))
                    hasOmega = true;
                boolean hasPhis = false;
                if (tokens[2].equals("1"))
                    hasPhis = true;
                Linkage linkage = new Linkage(tokens[0], hasOmega, hasPhis);
                String[] valueTokens = tokens[3].split(":");
                String[] phiValues = valueTokens[0].split(",");
                String[] omegaValues = valueTokens[1].split(",");
                for (int i = 0; i < phiValues.length; i++)
                    if (phiValues[i].length() > 0 && !phiValues[i].equals("-"))
                        linkage.addPhiValue(Double.parseDouble(phiValues[i]));
                for (int i = 0; i < omegaValues.length; i++)
                    if (omegaValues[i].length() > 0 &&
                            !omegaValues[i].equals("-"))
                        linkage.addOmegaValue(
                            Double.parseDouble(omegaValues[i])
                        );
                linkages.add(linkage);
            }
        } catch (IOException e) {
            Logging.logger.severe(e.getMessage());
            throw e;
        } catch (InterruptedException e) {
            Logging.logger.severe(e.getMessage());
        }
        return linkages;
    }
}
