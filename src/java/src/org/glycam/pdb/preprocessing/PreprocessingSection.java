package org.glycam.pdb.preprocessing;

import org.glycam.pdb.PdbFilePB.*;

public interface PreprocessingSection extends MenuItem {
    void addToPreprocessingResults(PreprocessingResults.Builder results);
}
