package servlets.pdb_preprocessor;

import org.glycam.pdb.preprocessing.PreprocessingSession;

import java.io.IOException;

import javax.servlet.http.*;

class Utils {
    static PreprocessingSession getPreprocessingSession(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        return (PreprocessingSession)session.getAttribute("pdbPreprocessingSession");
    }

    static void redirectToMenu(HttpServletResponse response) throws IOException {
        response.sendRedirect("menu");
    }
}
