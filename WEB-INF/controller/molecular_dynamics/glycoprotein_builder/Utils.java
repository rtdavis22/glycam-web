package servlets.molecular_dynamics.glycoprotein_builder;

import org.glycam.molecular_dynamics.glycoprotein_builder.GlycoproteinSession;
import org.glycam.molecular_dynamics.glycoprotein_builder.GlycosylationSite;
import org.glycam.molecular_dynamics.glycoprotein_builder.GlycosylationSiteList;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

class Utils {
    static GlycoproteinSession getGlycoproteinSession(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        return (GlycoproteinSession)session.getAttribute("glycoproteinSession");
    }

    static void redirectToUploadPage(HttpServletResponse response) throws IOException {
        response.sendRedirect("upload-pdb");
    }

    static GlycosylationSite getGlycosylationSite(GlycoproteinSession session, String siteCode) {
        if (siteCode.length() < 2)
            return null;

        GlycosylationSiteList sites = null;
        char firstLetter = siteCode.charAt(0);
        if (firstLetter == 'n') {
            sites = session.getNLinkingSites();
        } else if (firstLetter == 'c') {
            sites = session.getOLinkingSites();
        } else {
            return null;
        }

        try {
            int index = Integer.parseInt(siteCode.substring(1));
            return sites.get(index);
        } catch (NumberFormatException e) {
            return null;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
