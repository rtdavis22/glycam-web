package servlets.molecular_dynamics.glycoprotein_builder;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;

import org.glycam.molecular_dynamics.glycoprotein_builder.GlycoproteinSession;
import org.glycam.pdb.preprocessing.*;
import servlets.Utils;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/tools/molecular-dynamics/glycoprotein-builder/upload-pdb")
public class UploadPdb extends HttpServlet {
    private static final String PAGE =
            "/WEB-INF/view/tools/molecular_dynamics/glycoprotein_builder/UploadPdb.jsp";

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forwardToPage(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        File pdbFile = Utils.uploadFile(request, "pdb");
        if (pdbFile == null) {
            request.setAttribute("error", "Error loading PDB file.");
            forwardToPage(request, response);
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("glycoproteinSession", new GlycoproteinSession(pdbFile));
        // This stuff should probably happen in GlycoproteinSession.
        PreprocessingSession preprocessingSession = new PreprocessingSession(pdbFile);
        preprocessingSession.setDestination("Continue to Glycoprotein Builder >",
                             "/tools/molecular-dynamics/glycoprotein-builder/done-preprocessing");
        preprocessingSession.setPreviousUrl(
                    "/tools/molecular-dynamics/glycoprotein-builder/upload-pdb");

        session.setAttribute("pdbPreprocessingSession", preprocessingSession);
            
        response.sendRedirect("/tools/pdb-preprocessor/menu");
    }

    private void forwardToPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        getServletConfig().getServletContext().getRequestDispatcher(PAGE)
                          .forward(request, response);
    }
}
