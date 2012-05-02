package servlets.services;

import org.glycam.CPP;
import org.glycam.Logging;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/services/drawglycan")
public class DrawGlycan extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String structure = (String)request.getParameter("structure");
        if (structure == null)
            return;
 
        String args = "";

        String edgeLabels = (String)request.getParameter("edgelabels");
        if (edgeLabels != null && edgeLabels.equals("on"))
            args += " -edgelabels";

        String positionLabels = (String)request.getParameter("position_labels");
        if (positionLabels != null && positionLabels.equals("off"))
            args += " -hide_position_labels";

        String configLabels = (String)request.getParameter("config_labels");
        if (configLabels != null && configLabels.equals("off"))
            args += " -hide_config_labels";

        String dpi = (String)request.getParameter("dpi");
        if (dpi != null)
            args += " -dpi " + dpi;

        args += " " + structure;

        String shellCommand = "draw_glycan " + args + " | dot -T png | " +
                                             "convert - -transparent white -";
        try {
            Process process = CPP.execShellCommand(shellCommand);
            process.getErrorStream().close();
            process.getOutputStream().close();
            InputStream is = process.getInputStream();
            BufferedImage image = ImageIO.read(is);
            process.waitFor();
            is.close();
            response.setContentType("image/png");
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            Logging.logger.severe(e.getMessage());
        } catch (InterruptedException e) {
            Logging.logger.severe(e.getMessage());
        }
    }
}
