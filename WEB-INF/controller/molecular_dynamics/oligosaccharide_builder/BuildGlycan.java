package servlets.molecular_dynamics.glycan_builder;

import org.glycam.molecular_dynamics.glycan_builder.GlycanSession;

import org.glycam.GlycamSequence;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/tools/molecular-dynamics/oligosaccharide-builder/build-glycan")
public class BuildGlycan extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        sendToPage(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String sequenceText = (String)request.getParameter("structure");
        GlycamSequence sequence = new GlycamSequence(sequenceText);

        String error = sequence.validate();
        if (error.equals("")) {
            HttpSession session = request.getSession(true);
            session.setAttribute("glycanSession", new GlycanSession(sequence));
            response.sendRedirect("options");
            return;
        }
        request.setAttribute("error", error);

        sendToPage(request, response);
    }

    private void sendToPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        getServletConfig().getServletContext().getRequestDispatcher(
            "/WEB-INF/view/tools/molecular_dynamics/oligosaccharide_builder/BuildGlycan.jsp"
        ).forward(request, response);
    }
}
