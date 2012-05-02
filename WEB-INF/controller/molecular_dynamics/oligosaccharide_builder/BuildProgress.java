package servlets.molecular_dynamics.glycan_builder;

import org.glycam.molecular_dynamics.glycan_builder.BuildRequest;
import org.glycam.molecular_dynamics.glycan_builder.GlycanSession;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/tools/molecular-dynamics/oligosaccharide-builder/build-progress")
public class BuildProgress extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(true);

        GlycanSession glycanSession = (GlycanSession)session.getAttribute("glycanSession");
           
        PrintWriter out = response.getWriter();

        if (glycanSession != null) {
            BuildRequest buildRequest = glycanSession.getBuildRequest();
            if (buildRequest != null) {
                if (buildRequest.getStatus() == BuildRequest.Status.DONE) {
                    out.println("done");
                } else {
                    out.println(buildRequest.getStructuresBuiltSoFar());
                }
            }
        } else {
            out.println("error");
        }
    }
}
