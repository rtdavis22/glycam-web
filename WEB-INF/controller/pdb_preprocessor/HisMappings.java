package servlets.pdb_preprocessor;

import org.glycam.pdb.PdbFilePB.HisMappingType;
import org.glycam.pdb.preprocessing.HisMappingsSection;
import org.glycam.pdb.preprocessing.PreprocessingSession;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;

@WebServlet("/tools/pdb-preprocessor/his-mappings")
public class HisMappings extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HisMappingsSection section = getSection(request);
        if (section == null) {
            Utils.redirectToMenu(response);
            return;
        }

        for (int i = 0; i < section.getHisCount(); i++) {
            String mappedType = request.getParameter("his_" + (i + 1));
            if (mappedType != null)
                section.updateMapping(i, HisMappingType.valueOf(mappedType));
        }

        response.sendRedirect("menu");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HisMappingsSection section = getSection(request);
        if (section == null) {
            Utils.redirectToMenu(response);
            return;
        }

        request.setAttribute("hisResidues", section.getMappings());
        request.setAttribute("showHisChainIdColumn", Boolean.valueOf(section.anyChainIdsSet()));
        request.setAttribute("showHisICodeColumn", Boolean.valueOf(section.anyICodesSet()));

	getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/view/tools/pdb_preprocessor/HisMappings.jsp"
        ).forward(request, response);
    }

    private HisMappingsSection getSection(HttpServletRequest request) {
        PreprocessingSession session = Utils.getPreprocessingSession(request);
        return (session == null)?null:session.getHisMappingsSection();
    }

}
