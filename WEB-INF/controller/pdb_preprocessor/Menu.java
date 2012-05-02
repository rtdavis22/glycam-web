package servlets.pdb_preprocessor;

import org.glycam.pdb.preprocessing.PreprocessingSection;
import org.glycam.pdb.preprocessing.PreprocessingSession;

import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;

@WebServlet("/tools/pdb-preprocessor/menu")
public class Menu extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PreprocessingSession session = Utils.getPreprocessingSession(request);
        if (session == null) {
            response.sendRedirect("upload-pdb");
            return;
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PreprocessingSession session = Utils.getPreprocessingSession(request);
        if (session == null) {
            response.sendRedirect("upload-pdb");
            return;
        }

        List<PreprocessingSection> menu = session.getSections();
        request.setAttribute("menu", menu);

        String submitValue = session.getDestinationLabel();
        request.setAttribute("submitValue", submitValue);

        request.setAttribute("submitUrl", session.getDestinationUrl());

        request.setAttribute("enableNext", Boolean.valueOf(session.allSectionsGood()));

        request.setAttribute("previousUrl", session.getPreviousUrl());

	getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/view/tools/pdb_preprocessor/Menu.jsp"
        ).forward(request, response);
    }
}
