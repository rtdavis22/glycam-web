package servlets.pdb_preprocessor;

import org.glycam.pdb.preprocessing.CysBondsSection;
import org.glycam.pdb.preprocessing.PreprocessingSession;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;

@WebServlet("/tools/pdb-preprocessor/cys-bonds")
public class CysBonds extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CysBondsSection section = getSection(request);
        if (section == null) {
            Utils.redirectToMenu(response);
            return;
        }

        for (int i = 0; i < section.getPairCount(); i++) {
            String[] selected = request.getParameterValues("cys_" + (i + 1));
            section.updateBonded(i, selected != null && selected.length == 1);
        }

        Utils.redirectToMenu(response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CysBondsSection section = getSection(request);
        if (section == null) {
            Utils.redirectToMenu(response);
            return;
        }

        request.setAttribute("cysPairs", section.getCysPairs());
        request.setAttribute("showCYSChainIdColumn", Boolean.valueOf(section.anyChainIdsSet()));
        request.setAttribute("showCYSICodeColumn", Boolean.valueOf(section.anyICodesSet()));

	getServletConfig().getServletContext().getRequestDispatcher(
            "/WEB-INF/view/tools/pdb_preprocessor/CysBonds.jsp"
        ).forward(request, response);
    }

    private CysBondsSection getSection(HttpServletRequest request) {
        PreprocessingSession session = Utils.getPreprocessingSession(request);
        return (session == null)?null:session.getCysBondsSection();
    }
}
