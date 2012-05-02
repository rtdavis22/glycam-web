package servlets.oligosaccharide_library;

import javax.servlet.http.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.OutputStream;
import java.awt.image.BufferedImage;

import javax.servlet.annotation.WebServlet;

@WebServlet("/oligosaccharide-library/menu")
public class OligosaccharideMenu extends HttpServlet {

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) {
        try {
            //String structure = (String)request.getParameter("structure");

            getServletConfig().getServletContext().getRequestDispatcher(
                "/oligosaccharide_library/OligosaccharideMenu.jsp"
            ).forward(request, response);

        } catch(Exception e) {
            e.printStackTrace(); 
        }
    }
}
