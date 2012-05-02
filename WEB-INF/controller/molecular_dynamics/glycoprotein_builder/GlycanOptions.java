package servlets.molecular_dynamics.glycoprotein_builder;

import org.glycam.molecular_dynamics.glycan_builder.GlycanSession;
import org.glycam.molecular_dynamics.glycoprotein_builder.GlycoproteinSession;
import org.glycam.molecular_dynamics.glycoprotein_builder.GlycosylationSite;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/tools/molecular-dynamics/glycoprotein-builder/glycan-options")
public class GlycanOptions extends HttpServlet {
    private static final String PAGE =
            "/WEB-INF/view/tools/molecular_dynamics/glycoprotein_builder/GlycanOptions.jsp";

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        GlycoproteinSession session = Utils.getGlycoproteinSession(request);
        if (session == null) {
            Utils.redirectToUploadPage(response);
            return;
        }

        String edit = request.getParameter("edit");
        if (edit != null) {
            setCurrentGlycanSession(session, edit);
        }

        GlycanSession glycanSession = session.getCurrentGlycanSession();

        if (glycanSession == null) {
            response.sendRedirect("attach-glycans");
            return;
        }

        prepareRequest(request, glycanSession);

        forwardToPage(request, response);
    }

    private void setCurrentGlycanSession(GlycoproteinSession session, String siteCode) {
        GlycosylationSite site = Utils.getGlycosylationSite(session, siteCode);
        GlycanSession glycanSession = null;
        if (site != null || site.isGlycosylated())
            glycanSession = site.getGlycanSession();
        session.setCurrentGlycanSession(glycanSession);
    }

    private void prepareRequest(HttpServletRequest request, GlycanSession glycanSession) {
        request.setAttribute("glycanSession", glycanSession);

        request.setAttribute("showSetAngles",
                             Boolean.valueOf(glycanSession.getLinkageCount() > 2));

        boolean showChooseRotamers = glycanSession.hasFlexiblePhis() ||
                                     glycanSession.hasFlexibleOmegas();

        request.setAttribute("showChooseRotamers", Boolean.valueOf(showChooseRotamers));
    }

    private void forwardToPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        getServletConfig().getServletContext().getRequestDispatcher(PAGE)
                          .forward(request, response);
    }
}
