package servlets.pdb_preprocessor;

import org.glycam.pdb.preprocessing.PreprocessingSession;
import org.glycam.pdb.preprocessing.UnrecognizedResidue;
import org.glycam.pdb.preprocessing.UnrecognizedResiduesSection;

import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;

@WebServlet("/tools/pdb-preprocessor/unrecognized-residues")
public class UnrecognizedResidues extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UnrecognizedResiduesSection section = getSection(request);
        if (section == null) {
            Utils.redirectToMenu(response);
            return;
        }

        for (int i = 0; i < section.getResidueCount(); i++) {
            String[] selected = request.getParameterValues("residue_" + (i + 1));
            // Warning: disabled checkboxes arent POSTed.
            //section.updateRemoved(i, selected != null && selected.length == 1);
        }

        response.sendRedirect("menu");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UnrecognizedResiduesSection section = getSection(request);
        if (section == null) {
            Utils.redirectToMenu(response);
            return;
        }

        request.setAttribute("residues", section.getUnrecognizedResidues());
        request.setAttribute("showChainIdCol", Boolean.valueOf(section.anyChainIdsSet()));
        request.setAttribute("showICodeCol", Boolean.valueOf(section.anyICodesSet()));

	getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/view/tools/pdb_preprocessor/UnrecognizedResidues.jsp"
        ).forward(request, response);
    }

    private UnrecognizedResiduesSection getSection(HttpServletRequest request) {
        PreprocessingSession session = Utils.getPreprocessingSession(request);
        return (session == null)?null:session.getUnrecognizedResiduesSection();
    }

}
