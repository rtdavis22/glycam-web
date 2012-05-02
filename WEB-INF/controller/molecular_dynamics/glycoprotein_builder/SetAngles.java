package servlets.molecular_dynamics.glycoprotein_builder;

import org.glycam.Linkage;
import org.glycam.molecular_dynamics.glycan_builder.GlycanSession;
import org.glycam.molecular_dynamics.glycoprotein_builder.GlycoproteinSession;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/tools/molecular-dynamics/glycoprotein-builder/set-angles")
public class SetAngles extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(true);

        GlycoproteinSession glycoproteinSession =
                (GlycoproteinSession)session.getAttribute("glycoproteinSession");
        if (glycoproteinSession == null) {
            response.sendRedirect("upload-pdb");
            return;
        }

        GlycanSession glycanSession = glycoproteinSession.getCurrentGlycanSession();
        if (glycanSession == null) {
            response.sendRedirect("upload-pdb");
            return;
        }

        List<Linkage> linkages = glycanSession.getLinkages();

        for (int i = 2; i < linkages.size(); i++) {
            Linkage l = linkages.get(i);
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
    
        response.sendRedirect("glycan-options");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);

        GlycoproteinSession glycoproteinSession =
                (GlycoproteinSession)session.getAttribute("glycoproteinSession");
        if (glycoproteinSession == null) {
            response.sendRedirect("upload-pdb");
            return;
        }

        GlycanSession glycanSession = glycoproteinSession.getCurrentGlycanSession();
        if (glycanSession == null) {
            response.sendRedirect("upload-pdb");
            return;
        }

        String sequence = glycanSession.getSequence().toString();
        request.setAttribute("sequence", sequence);

        List<Linkage> linkages = glycanSession.getLinkages();
        request.setAttribute("linkages", glycanSession.getLinkages());

        boolean showOmegaColumn = glycanSession.hasFlexibleOmegas();
        request.setAttribute("showOmegaColumn", Boolean.valueOf(showOmegaColumn));

        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/view/tools/molecular_dynamics/glycoprotein_builder/SetAngles.jsp"
        ).forward(request, response);
    }
}
