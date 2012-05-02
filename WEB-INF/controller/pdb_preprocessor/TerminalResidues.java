package servlets.pdb_preprocessor;

import org.glycam.pdb.PdbFilePB.ChainInfo;
import org.glycam.pdb.PdbFilePB.CTerminalType;
import org.glycam.pdb.PdbFilePB.NTerminalType;
import org.glycam.pdb.preprocessing.PreprocessingSession;
import org.glycam.pdb.preprocessing.TerminalResiduesSection;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;

@WebServlet("/tools/pdb-preprocessor/terminal-residues")
public class TerminalResidues extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        TerminalResiduesSection section = getSection(request);
        if (section == null) {
            Utils.redirectToMenu(response);
            return;
        }

        for (int i = 0; i < section.getChainCount(); i++) {
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
        TerminalResiduesSection section = getSection(request);
        if (section == null) {
            Utils.redirectToMenu(response);
            return;
        }

        request.setAttribute("chains", section.getChains());
        request.setAttribute("showChainIdCol1", Boolean.valueOf(section.anyStartChainIdsSet()));
        request.setAttribute("showChainIdCol2",
                             Boolean.valueOf(section.anyInconsistentEndChainIds()));
        request.setAttribute("showICodeCol1", Boolean.valueOf(section.anyStartICodesSet()));
        request.setAttribute("showICodeCol2", Boolean.valueOf(section.anyEndICodesSet()));

	getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/view/tools/pdb_preprocessor/TerminalResidues.jsp"
        ).forward(request, response);
    }

    private TerminalResiduesSection getSection(HttpServletRequest request) {
        PreprocessingSession session = Utils.getPreprocessingSession(request);
        return (session == null)?null:session.getTerminalResiduesSection();
    }
}
