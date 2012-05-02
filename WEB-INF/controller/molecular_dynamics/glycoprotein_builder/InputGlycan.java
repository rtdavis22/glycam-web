package servlets.molecular_dynamics.glycoprotein_builder;

import org.glycam.GlycamSequence;
import org.glycam.molecular_dynamics.glycan_builder.GlycanSession;
import org.glycam.molecular_dynamics.glycoprotein_builder.GlycoproteinSession;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/tools/molecular-dynamics/glycoprotein-builder/input-glycan")
public class InputGlycan extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        GlycoproteinSession glycoproteinSession =
                (GlycoproteinSession)session.getAttribute("glycoproteinSession");
        if (glycoproteinSession == null) {
            response.sendRedirect("upload-pdb");
            return;
        }

        String sequenceText = (String)request.getParameter("structure");

        GlycamSequence sequence = new GlycamSequence(sequenceText);

        String error = sequence.validate();
        if (error.equals("")) {
            GlycanSession glycanSession = new GlycanSession(sequence);
            glycoproteinSession.setCurrentGlycanSession(glycanSession);
            response.sendRedirect("glycan-options");
            return;
        }
        request.setAttribute("error", error);

        forwardToPage(request, response);
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

        forwardToPage(request, response);
    }

    private void forwardToPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/view/tools/molecular_dynamics/glycoprotein_builder/InputGlycan.jsp"
        ).forward(request, response);
    }
}
