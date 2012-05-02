package servlets.pdb_preprocessor;

import org.glycam.pdb.preprocessing.PreprocessingSession;
import org.glycam.pdb.preprocessing.RemovedAtomsSection;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;

@WebServlet("/tools/pdb-preprocessor/removed-atoms")
public class RemovedAtoms extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RemovedAtomsSection section = getSection(request);
        if (section == null) {
            Utils.redirectToMenu(response);
            return;
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RemovedAtomsSection section = getSection(request);
        if (section == null) {
            Utils.redirectToMenu(response);
            return;
        }

        request.setAttribute("atoms", section.getRemovedAtoms());
        request.setAttribute("showChainIdCol", Boolean.valueOf(section.anyChainIdsSet()));
        request.setAttribute("showICodeCol", Boolean.valueOf(section.anyICodesSet()));

	getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/view/tools/pdb_preprocessor/RemovedAtoms.jsp"
        ).forward(request, response);
    }

    private RemovedAtomsSection getSection(HttpServletRequest request) {
        PreprocessingSession session = Utils.getPreprocessingSession(request);
        return (session == null)?null:session.getRemovedAtomsSection();
    }
}
