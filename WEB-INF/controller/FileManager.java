package servlets;

import java.io.File;
import java.util.AbstractMap;
import java.util.HashMap;

// This class can be filled in when I want to remove files that are uploaded. It keeps a count of
// how many times a file is being used, since a file can be used for multiple things, like
// the pdb preprocessor and glycoprotein builder. It'll delete the file when the use count is 0.
public class FileManager {
    private AbstractMap<File, int> fileUseCount;

    public FileManager() {
        fileUseCount = new HashMap<File, int>();
    }

}
