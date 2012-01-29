package org.glycam.molecular_dynamics.glycoprotein_builder;

import org.glycam.CPP;
import org.glycam.Logging;
import org.glycam.molecular_dynamics.glycoprotein_builder.PdbInfoPB.CYSPair;
import org.glycam.molecular_dynamics.glycoprotein_builder.PdbInfoPB.PdbInfo;
import org.glycam.molecular_dynamics.glycoprotein_builder.PdbInfoPB.PdbResidueInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GlycoproteinSession {
    private File pdbFile;

    private PdbInfo.Builder pdbInfoBuilder;

    public GlycoproteinSession(File pdbFile) {
        this.pdbFile = pdbFile;
        initializePdbInfo();
    }

    public PdbInfo.Builder getPdbInfoBuilder() { return pdbInfoBuilder; }

    public List<PdbResidueInfo> getHisResidues() { return pdbInfoBuilder.getHisResidueList(); }
    public List<CYSPair> getCloseCYSPairs() { return pdbInfoBuilder.getCloseCysPairList(); }
    public List<PdbResidueInfo> getAsnResidues() { return pdbInfoBuilder.getAsnResidueList(); }

    private void initializePdbInfo() {
        String command = "get_pdb_info " + pdbFile.getPath();
        try {
            Process process = CPP.exec(command);
            PdbInfo pdbInfo = PdbInfo.parseFrom(process.getInputStream());
            this.pdbInfoBuilder = PdbInfo.newBuilder(pdbInfo);
            Logging.logger.info("Process " + command + " exited with value " + process.waitFor());
        } catch (IOException e) {
            Logging.logger.severe(e.getMessage());
        } catch (InterruptedException e) {
            Logging.logger.severe(e.getMessage());
        }
    }
}
