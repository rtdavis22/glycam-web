package org.glycam.molecular_dynamics.glycoprotein_builder;

import org.glycam.molecular_dynamics.glycoprotein_builder.PdbInfoPB.CYSPair;
import org.glycam.molecular_dynamics.glycoprotein_builder.PdbInfoPB.PdbInfo;
import org.glycam.molecular_dynamics.glycoprotein_builder.PdbInfoPB.PdbResidueInfo;
import org.glycam.CPP;
import org.glycam.Logging;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GlycoproteinSession {
    private File pdbFile;

    private PdbInfo pdbInfo;

    public GlycoproteinSession(File pdbFile) {
        this.pdbFile = pdbFile;
        initializePdbInfo();
    }

    public List<PdbResidueInfo> getHisResidues() { return pdbInfo.getHisResidueList(); }
    public List<CYSPair> getCloseCYSPairs() { return pdbInfo.getCloseCysPairList(); }
    public List<PdbResidueInfo> getAsnResidues() { return pdbInfo.getAsnResidueList(); }

    private void initializePdbInfo() {
        String command = "get_pdb_info " + pdbFile.getPath();
        try {
            Process process = CPP.exec(command);
            pdbInfo = PdbInfo.parseFrom(process.getInputStream());
            Logging.logger.info("Process " + command + " exited with value " + process.waitFor());
        } catch (IOException e) {
            Logging.logger.severe(e.getMessage());
        } catch (InterruptedException e) {
            Logging.logger.severe(e.getMessage());
        }
    }
}
