package servlets.molecular_dynamics.glycan_builder;

import org.glycam.molecular_dynamics.glycan_builder.GlycanSession;

import org.glycam.GlycamSequence;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/tools/molecular-dynamics/oligosaccharide-builder/options")
public class Options extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);

        String sequenceInUrl = (String)request.getParameter("sequence");
        if (sequenceInUrl != null) {
            GlycamSequence sequence = new GlycamSequence(sequenceInUrl);
            String error = sequence.validate();
            if (!error.equals("")) {
                response.sendRedirect("build-glycan");
                return;
            }
            session.setAttribute("glycanSession", new GlycanSession(sequence));
        } else if (session.getAttribute("glycanSession") == null) {
            response.sendRedirect("build-glycan");
            return;
        }

        GlycanSession glycanSession = (GlycanSession)session.getAttribute("glycanSession");

        int totalStructures = glycanSession.getTotalStructureCount();
        request.setAttribute("totalStructures", totalStructures);

        request.setAttribute("showSetAngles", 
                             Boolean.valueOf(glycanSession.getLinkageCount() > 2));

        boolean showChooseRotamers = glycanSession.hasFlexiblePhis() ||
                                     glycanSession.hasFlexibleOmegas();
        request.setAttribute("showChooseRotamers", Boolean.valueOf(showChooseRotamers));

	getServletConfig().getServletContext().getRequestDispatcher(
            "/WEB-INF/view/tools/molecular_dynamics/oligosaccharide_builder/Options.jsp"
        ).forward(request, response);
    }
}
