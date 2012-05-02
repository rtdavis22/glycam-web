package servlets.pdb_preprocessor;

import org.glycam.pdb.preprocessing.PreprocessingSession;
import org.glycam.pdb.preprocessing.UnrecognizedAtomsSection;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;

@WebServlet("/tools/pdb-preprocessor/unrecognized-atoms")
public class UnrecognizedAtoms extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UnrecognizedAtomsSection section = getSection(request);
        if (section == null) {
            Utils.redirectToMenu(response);
            return;
        }

        request.setAttribute("atoms", section.getUnrecognizedAtoms());
        request.setAttribute("showChainIdCol", Boolean.valueOf(section.anyChainIdsSet()));
        request.setAttribute("showICodeCol", Boolean.valueOf(section.anyICodesSet()));

	getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/view/tools/pdb_preprocessor/UnrecognizedAtoms.jsp"
        ).forward(request, response);
    }

    private UnrecognizedAtomsSection getSection(HttpServletRequest request) {
        PreprocessingSession session = Utils.getPreprocessingSession(request);
        return (session == null)?null:session.getUnrecognizedAtomsSection();
    }
}
