package servlets.molecular_dynamics.glycoprotein_builder;

import org.glycam.molecular_dynamics.glycoprotein_builder.GlycoproteinSession;
import org.glycam.molecular_dynamics.glycoprotein_builder.GlycosylationSite;
import org.glycam.pdb.PdbFilePB.GlycosylationInfo;
import org.glycam.pdb.PdbFilePB.PdbResidueInfo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/tools/molecular-dynamics/glycoprotein-builder/attach-glycans")
public class AttachGlycans extends HttpServlet {
    private static final String PAGE =
            "/WEB-INF/view/tools/molecular_dynamics/glycoprotein_builder/AttachGlycans.jsp";

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        GlycoproteinSession session = Utils.getGlycoproteinSession(request);
        if (session == null) {
            Utils.redirectToUploadPage(response);
            return;
        }

        String removeSiteCode = request.getParameter("remove");
        if (removeSiteCode != null) {
            removeGlycan(session, removeSiteCode);
        }

        prepareRequest(request, session);

        forwardToPage(request, response);
    }

    private void removeGlycan(GlycoproteinSession session, String siteCode) {
        GlycosylationSite site = Utils.getGlycosylationSite(session, siteCode);
        if (site != null)
            site.removeGlycanSession();
    }

    private void prepareRequest(HttpServletRequest request, GlycoproteinSession session) {
        request.setAttribute("nSites", session.getNLinkingSites());
        request.setAttribute("oSites", session.getOLinkingSites());

        request.setAttribute("showChainIdColumn", session.anyGlycosylatedSitesWithChainId());
        request.setAttribute("showICodeColumn", session.anyGlycosylatedSitesWithICode());
    }

    private void forwardToPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        getServletConfig().getServletContext().getRequestDispatcher(PAGE)
                          .forward(request, response);
    }
}
