package servlets.molecular_dynamics;

import org.glycam.molecular_dynamics.glycan_builder.GlycanSession;
import org.glycam.molecular_dynamics.SolvationSettings;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/tools/molecular-dynamics/solvation-options")
public class SolvationOptions extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        GlycanSession glycanSession = (GlycanSession)session.getAttribute("glycanSession");

        // timeToClose signals that the modal window can be closed. We'll go
        // ahead and set it to true here and change it if we run into an error.
        request.setAttribute("timeToClose", Boolean.TRUE);

        // If the solvate button isn't selected, bail.
        if (request.getParameter("solvate") == null ||
                request.getParameter("solvate").equals("No")) {
            glycanSession.setSolvationSettings(null);
            forwardToPage(request, response);
            return;
        }

        SolvationSettings.Shape shape = SolvationSettings.Shape.RECTANGULAR;
        double buffer = -1;
        double closeness = -1;

        if (request.getParameter("shape").equals("Cubic"))
            shape = SolvationSettings.Shape.CUBIC;

        String error = "";

        String bufferStr = request.getParameter("buffer");
        if (bufferStr == null || bufferStr.equals(""))
            error = "You must specify a solvent buffer.";
        try {
            buffer = Double.parseDouble(bufferStr);
            if (buffer < 0.0 || buffer > 30.0)
                error = "Please choose a buffer between 0 and 30.0";
        } catch(NumberFormatException e) {
            error = "Invalid solvent buffer.";
        }
        
        String closenessStr = request.getParameter("closeness");
        if (closenessStr == null || closenessStr.equals(""))
            error = "You must specify a closeness parameter.";
        try {
            closeness = Double.parseDouble(closenessStr);
            if (closeness < 0.0 || closeness > 5.0)
                error = "Please choose a closeness parameter between 0 and 5.0";
        } catch(NumberFormatException e) {
            error = "Invalid closeness parameter.";
        }

        if (error.equals("")) {
            SolvationSettings solvationSettings = new SolvationSettings(buffer, closeness, shape);
            glycanSession.setSolvationSettings(solvationSettings);
        } else {
            request.setAttribute("shape", request.getParameter("shape"));
            request.setAttribute("buffer", bufferStr);
            request.setAttribute("closeness", closenessStr);
            request.setAttribute("solvate", Boolean.TRUE);
            request.setAttribute("timeToClose", Boolean.FALSE);
            request.setAttribute("error", error);
        }

        forwardToPage(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);

        GlycanSession glycanSession = (GlycanSession)session.getAttribute("glycanSession");
        if (glycanSession == null)
            response.sendRedirect("options");

        SolvationSettings solvationSettings = glycanSession.getSolvationSettings();

        if (solvationSettings == null) {
            request.setAttribute("buffer", 8.0);
            request.setAttribute("closeness", 1.5);
            request.setAttribute("shape", "Rectangular");
        } else {
            request.setAttribute("buffer", solvationSettings.getBuffer());
            request.setAttribute("closeness", solvationSettings.getCloseness());
            if (solvationSettings.getShape() == SolvationSettings.Shape.CUBIC)
                request.setAttribute("shape", "Cubic");
            else
                request.setAttribute("shape", "Rectangular");
        }

        request.setAttribute("solvate", Boolean.valueOf(solvationSettings != null));
        request.setAttribute("timeToClose", Boolean.FALSE);

        forwardToPage(request, response);
    }

    private void forwardToPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/view/tools/molecular_dynamics/SolvationOptions.jsp"
        ).forward(request, response);
    }
}
