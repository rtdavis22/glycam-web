package servlets.pdb_preprocessor;

import org.glycam.pdb.PdbFilePB.ChainInfo;
import org.glycam.pdb.PdbFilePB.CTerminalType;
import org.glycam.pdb.PdbFilePB.NTerminalType;
import org.glycam.pdb.preprocessing.MissingResiduesSection;
import org.glycam.pdb.preprocessing.PreprocessingSession;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;

@WebServlet("/tools/pdb-preprocessor/missing-residues")
public class MissingResidues extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        MissingResiduesSection section = getSection(request);
        if (section == null) {
            Utils.redirectToMenu(response);
            return;
        }

        for (int i = 0; i < section.getGapCount(); i++) {
            section.updateTerminalTypes(i, getNTerminalType(request, i),
                                        getCTerminalType(request, i));
        }

        response.sendRedirect("menu");
    }

    private NTerminalType getNTerminalType(HttpServletRequest request, int index) {
        return getNTerminalType(request.getParameter("nterminal_" + (index + 1)));
    }

    private NTerminalType getNTerminalType(String value) {
        return NTerminalType.valueOf(value);
    }

    private CTerminalType getCTerminalType(HttpServletRequest request, int index) {
        return getCTerminalType(request.getParameter("cterminal_" + (index + 1)));
    }

    private CTerminalType getCTerminalType(String value) {
        return CTerminalType.valueOf(value);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        MissingResiduesSection section = getSection(request);
        if (section == null) {
            Utils.redirectToMenu(response);
            return;
        }

        request.setAttribute("gaps", section.getGaps());

	getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/view/tools/pdb_preprocessor/MissingResidues.jsp"
        ).forward(request, response);
    }

    private MissingResiduesSection getSection(HttpServletRequest request) {
        PreprocessingSession session = Utils.getPreprocessingSession(request);
        return (session == null)?null:session.getMissingResiduesSection();
    }
}
