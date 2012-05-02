package servlets.molecular_dynamics.glycoprotein_builder;

import org.glycam.molecular_dynamics.glycoprotein_builder.GlycoproteinSession;
import org.glycam.pdb.preprocessing.PreprocessingSession;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/tools/molecular-dynamics/glycoprotein-builder/done-preprocessing")
public class DonePreprocessing extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        GlycoproteinSession glycoproteinSession = Utils.getGlycoproteinSession(request);
        if (glycoproteinSession == null) {
            Utils.redirectToUploadPage(response);
            return;
        }

        PreprocessingSession preprocessingSession =
                (PreprocessingSession)request.getSession(true)
                                             .getAttribute("pdbPreprocessingSession");
        if (preprocessingSession == null) {
            Utils.redirectToUploadPage(response);
            return;
        }
        glycoproteinSession.setPreprocessingResults(
                preprocessingSession.createPreprocessingResults());

        response.sendRedirect("attach-glycans");
    }
}
