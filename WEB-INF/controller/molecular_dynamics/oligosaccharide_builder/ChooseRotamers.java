package servlets.molecular_dynamics.glycan_builder;

import org.glycam.Linkage;
import org.glycam.molecular_dynamics.glycan_builder.GlycanSession;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/tools/molecular-dynamics/oligosaccharide-builder/choose-rotamers")
public class ChooseRotamers extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        GlycanSession glycanSession = (GlycanSession)session.getAttribute("glycanSession");
       if (glycanSession == null) {
           response.sendRedirect("build-glycan");
           return;
       }

        List<Linkage> linkages = glycanSession.getLinkages();

        for (int i = 2; i < linkages.size(); i++) {
	    Linkage linkage = linkages.get(i);
            if (!linkage.isFlexibleOmega() && !linkage.isFlexiblePhi())
		continue;
	    if (!linkage.isPhiSet()) {
		linkage.getPhiValues().clear();
		String[] boxes = request.getParameterValues(Integer.toString(i));
		if (boxes != null) {
		    for (int j = 0; j < boxes.length; j++) {
			if (boxes[j].equals("t")) 
                            linkage.addPhiValue(180.0);
			else if (boxes[j].equals("g")) 
                            linkage.addPhiValue(60.0);
			else if (boxes[j].equals("-g")) 
                            linkage.addPhiValue(-60.0);
		    }
                }
	    }
	    if (linkage.isFlexibleOmega() && !linkage.isOmegaSet()) {
		linkage.getOmegaValues().clear();
		String[] boxes = request.getParameterValues(Integer.toString(i));
		if (boxes != null) {
		    for (int j = 0; j < boxes.length; j++) {
			if (boxes[j].equals("gg")) 
                            linkage.addOmegaValue(180.0);
			else if (boxes[j].equals("gt")) 
                            linkage.addOmegaValue(-60.0);
			else if (boxes[j].equals("tg")) 
                            linkage.addOmegaValue(60.0);
		    }
                }
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

        request.setAttribute("sequence", glycanSession.getSequence().toString());

        request.setAttribute("linkages", glycanSession.getLinkages());

        boolean showPhiColumn = glycanSession.hasFlexiblePhis();
        request.setAttribute("showPhiColumn", Boolean.valueOf(showPhiColumn));

        boolean showOmegaColumn = glycanSession.hasFlexibleOmegas();
        request.setAttribute("showOmegaColumn", Boolean.valueOf(showOmegaColumn));
 
        getServletConfig().getServletContext().getRequestDispatcher(
            "/WEB-INF/view/tools/molecular_dynamics/oligosaccharide_builder/ChooseRotamers.jsp"
        ).forward(request, response);
    }
}
