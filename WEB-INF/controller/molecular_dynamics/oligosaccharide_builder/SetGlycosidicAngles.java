package servlets.molecular_dynamics.glycan_builder;

import org.glycam.Linkage;
import org.glycam.molecular_dynamics.glycan_builder.GlycanSession;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/tools/molecular-dynamics/oligosaccharide-builder/set-glycosidic-angles")
public class SetGlycosidicAngles extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(true);
        GlycanSession glycanSession = (GlycanSession)session.getAttribute("glycanSession");
        List<Linkage> linkages = glycanSession.getLinkages();

        for (int i = 2; i < glycanSession.getLinkageCount(); i++) {
            Linkage l = glycanSession.getLinkage(i);
            String phi = request.getParameter(i + "-phi");
            String psi = request.getParameter(i + "-psi");
            String omega = request.getParameter(i + "-omega");
            if (phi != null && !phi.equals("")) {
                l.getPhiValues().clear();
                l.addPhiValue(Double.parseDouble(phi));
                l.setPhiSet(true);
            }
            else if (phi != null) {
                l.setPhiSet(false);
            }
            if (psi != null && !psi.equals("")) {
                l.getPsiValues().clear();
                l.addPsiValue(Double.parseDouble(psi));
                l.setPsiSet(true);
            }
            else if (psi != null) {
                l.setPsiSet(false);
            }
            if (omega != null && !omega.equals("")) {
                l.getOmegaValues().clear();
                l.addOmegaValue(Double.parseDouble(omega));
                l.setOmegaSet(true);
            }
            else if (omega != null) {
                l.setOmegaSet(false);
            }
        }
    
        response.sendRedirect("options");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);

        GlycanSession glycanSession = (GlycanSession)session.getAttribute("glycanSession");
        if (glycanSession == null) {
            response.sendRedirect("options");
            return;
        }

        String sequence = glycanSession.getSequence().toString();
        request.setAttribute("sequence", sequence);

        request.setAttribute("linkages", glycanSession.getLinkages());

        boolean showOmegaColumn = glycanSession.hasFlexibleOmegas();
        request.setAttribute("showOmegaColumn", Boolean.valueOf(showOmegaColumn));

        getServletConfig().getServletContext().getRequestDispatcher(
            "/WEB-INF/view/tools/molecular_dynamics/oligosaccharide_builder/SetGlycosidicAngles.jsp"
        ).forward(request, response);
    }
}
