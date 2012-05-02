// REMOVE THIS FILE

package org.glycam.pdb.preprocessing;

import org.glycam.CPP;
import org.glycam.Logging;
import org.glycam.pdb.PdbFilePB.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Observer Pattern? Menu is observer that observes the session
// Need to dynamically update for unknown residues, for instance. IE of all unknowns are deleted,
// it should be green.
public class Menu {
    public static abstract class MenuItem {
        public enum Status { GOOD, NEEDS_ATTENTION, BAD }

        public abstract boolean isEnabled();
        public abstract Status getStatus();
        public abstract String getUrl();
        public abstract String getKey();
        public abstract String getValue();
    }

    static class CYSBondsMenuItem extends MenuItem {
        private int bondCount;
        private boolean visited;

        public CYSBondsMenuItem(int bondCount) {
            this.bondCount = bondCount;
            visited = false;
        }

        public boolean isEnabled() {
            return bondCount > 0;
        }

        public Status getStatus() {
            if (bondCount > 0 && !visited) {
                return Status.NEEDS_ATTENTION;
            }
            return Status.GOOD;
        }

        public String getUrl() {
            return "cys-bonds";
        }

        public String getKey() {
            return "Disulfide Bonds";
        }

        public String getValue() {
            return bondCount + " potential bonds.";
        }

        public void visit() {
            visited = true;
        }
    }

    static class HISMappingsMenuItem extends MenuItem {
        private int hisCount;
        private boolean visited;

        public HISMappingsMenuItem(int hisCount) {
            this.hisCount = hisCount;
        }

        public boolean isEnabled() {
            return hisCount > 0;
        }

        public Status getStatus() {
            if (hisCount > 0 && !visited) {
                return Status.NEEDS_ATTENTION;
            }
            return Status.GOOD;
        }

        public String getUrl() {
            return "his-mappings";
        }

        public String getKey() {
            return "Histidine Protonation Choices";
        }

        public String getValue() {
            return hisCount + " histidines.";
        }

        public void visit() {
            visited = true;
        }
    }

    static class UnknownResiduesMenuItem extends MenuItem {
        private PreprocessingSession session;

        public UnknownResiduesMenuItem(PreprocessingSession session) {
            this.session = session;
        }

        public boolean isEnabled() {
            return true;
        }

        public Status getStatus() {
            // Query the session
            return Status.GOOD;
        }

        public String getUrl() {
            return "unknown-residues";
        }

        public String getKey() {
            return "Unrecognized Residues";
        }

        public String getValue() {
            // Query the session
            return "";
        }
    }

    static class UnknownAtomsMenuItem extends MenuItem {
        private int atomCount;

        public UnknownAtomsMenuItem(int atomCount) {
            this.atomCount = atomCount;
        }

        public boolean isEnabled() {
            return atomCount > 0;
        }

        public Status getStatus() {
            if (atomCount > 0) {
                return Status.BAD;
            }
            return Status.GOOD;
        }

        public String getUrl() {
            return "unknown-atoms";
        }

        public String getKey() {
            return "Unrecognized Atoms";
        }

        public String getValue() {
            if (atomCount == 0) {
                return "None";
            }
            return atomCount + " unrecognized atoms.";
        }
    }

    //static class TerminalResiduesMenuItem extends MenuItem {
    //    private int chainCount;
    //}

    private List<MenuItem> menuItems;

    // Make package private.
    public Menu(PreprocessingSession session) {
        menuItems = new ArrayList<MenuItem>();
        menuItems.add(new UnknownResiduesMenuItem(session));
        menuItems.add(new UnknownAtomsMenuItem(session.getUnknownAtoms().size()));
        menuItems.add(new HISMappingsMenuItem(session.getHisResidues().size()));
        menuItems.add(new CYSBondsMenuItem(session.getCloseCYSPairs().size()));
      //  menuItems.add(new TerminalResiduesMenuItem(session.getChainInfo().size()));
    }

    public List<MenuItem> getMenuItems() {
        return null;
    }
}
