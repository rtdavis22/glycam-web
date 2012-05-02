package org.glycam.pdb.preprocessing;

import org.glycam.pdb.PdbFilePB.*;

public class UnrecognizedResidue {
    private PdbResidueInfo residueInfo;
    private boolean toBeRemoved;

    UnrecognizedResidue(PdbResidueInfo residueInfo) {
        this.residueInfo = residueInfo;
        toBeRemoved = true;
    }

    public PdbResidueInfo getResidueInfo() {
        return residueInfo;
    }

    public boolean isToBeRemoved() {
        return toBeRemoved;
    }

    void setToBeRemoved(boolean value) {
        toBeRemoved = value;
    }
}
