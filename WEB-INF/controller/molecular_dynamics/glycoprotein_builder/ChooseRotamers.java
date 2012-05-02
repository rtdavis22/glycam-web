package servlets.molecular_dynamics.glycoprotein_builder;

import org.glycam.Linkage;
import org.glycam.molecular_dynamics.glycan_builder.GlycanSession;
import org.glycam.molecular_dynamics.glycoprotein_builder.GlycoproteinSession;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/tools/molecular-dynamics/glycoprotein-builder/choose-rotamers")
public class ChooseRotamers extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response)
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

        List<Linkage> linkages = glycanSession.getLinkages();

        for (int i = 2; i < linkages.size(); i++) {
	    Linkage l = linkages.get(i);
            if (!l.isFlexibleOmega() && !l.isFlexiblePhi())
		continue;
	    if (!l.isPhiSet()) {
		l.getPhiValues().clear();
		String[] boxes = request.getParameterValues("phi_" + Integer.toString(i));
		if (boxes != null) {
		    for (int j = 0; j < boxes.length; j++) {
			if (boxes[j].equals("t")) 
                            l.addPhiValue(180.0);
			else if (boxes[j].equals("g")) 
                            l.addPhiValue(60.0);
			else if (boxes[j].equals("-g")) 
                            l.addPhiValue(-60.0);
		    }
                }
	    }
	    if (l.isFlexibleOmega() && !l.isOmegaSet()) {
		l.getOmegaValues().clear();
		String[] boxes = request.getParameterValues("omega_" + Integer.toString(i));
		if (boxes != null) {
		    for (int j = 0; j < boxes.length; j++) {
			if (boxes[j].equals("gg")) 
                            l.addOmegaValue(180.0);
			else if (boxes[j].equals("gt")) 
                            l.addOmegaValue(-60.0);
			else if (boxes[j].equals("tg")) 
                            l.addOmegaValue(60.0);
		    }
                }
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

        boolean showPhiColumn = glycanSession.hasFlexiblePhis();
        request.setAttribute("showPhiColumn", Boolean.valueOf(showPhiColumn));

        boolean showOmegaColumn = glycanSession.hasFlexibleOmegas();
        request.setAttribute("showOmegaColumn", Boolean.valueOf(showOmegaColumn));
 
        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/view/tools/molecular_dynamics/glycoprotein_builder/ChooseRotamers.jsp"
        ).forward(request, response);
    }
}
