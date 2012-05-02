package servlets.molecular_dynamics.glycoprotein_builder;

import org.glycam.molecular_dynamics.glycan_builder.GlycanSession;
import org.glycam.molecular_dynamics.glycoprotein_builder.GlycoproteinSession;
import org.glycam.molecular_dynamics.glycoprotein_builder.GlycosylationSiteList;
import org.glycam.pdb.PdbFilePB.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/tools/molecular-dynamics/glycoprotein-builder/choose-locations")
public class ChooseLocations extends HttpServlet {
    private static final String PAGE =
            "/WEB-INF/view/tools/molecular_dynamics/glycoprotein_builder/ChooseLocations.jsp";

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        GlycoproteinSession session = Utils.getGlycoproteinSession(request);
        if (session == null) {
            Utils.redirectToUploadPage(response);
            return;
        }

        GlycanSession glycanSession = session.getCurrentGlycanSession();
        if (glycanSession == null) {
            response.sendRedirect("attach-glycans");
            return;
        }

        GlycosylationSiteList nSites = session.getNLinkingSites();
        for (int i = 0; i < nSites.size(); i++) {
            String[] selected = request.getParameterValues("n" + (i + 1));
            if (selected == null || selected.length != 1)
                continue;
            nSites.attachGlycanSession(i, new GlycanSession(glycanSession));
        }

        GlycosylationSiteList oSites = session.getOLinkingSites();
        for (int i = 0; i < oSites.size(); i++) {
            String[] selected = request.getParameterValues("o" + (i + 1));
            if (selected == null || selected.length != 1)
                continue;
            oSites.attachGlycanSession(i, new GlycanSession(glycanSession));
        }

        session.removeCurrentGlycanSession();

        response.sendRedirect("attach-glycans");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        GlycoproteinSession session = Utils.getGlycoproteinSession(request);
        if (session == null) {
            Utils.redirectToUploadPage(response);
            return;
        }

        prepareGetRequest(request, session);

        forwardToPage(request, response);
    }

    private void prepareGetRequest(HttpServletRequest request, GlycoproteinSession session) {
        request.setAttribute("nSites", session.getNLinkingSites());
        request.setAttribute("oSites", session.getOLinkingSites());
    }

    private void forwardToPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
	getServletConfig().getServletContext().getRequestDispatcher(PAGE)
                          .forward(request, response);
    }
}
