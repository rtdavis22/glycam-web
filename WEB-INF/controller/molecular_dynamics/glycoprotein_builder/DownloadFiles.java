package servlets.molecular_dynamics.glycoprotein_builder;

import org.glycam.FileUtils;
import org.glycam.molecular_dynamics.glycoprotein_builder.BuildRequest;
import org.glycam.molecular_dynamics.glycoprotein_builder.GlycoproteinSession;

import servlets.HttpUtils;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.File;
import javax.servlet.http.*;
import java.io.*;

import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;

@WebServlet("/tools/molecular-dynamics/glycoprotein-builder/download-files")
public class DownloadFiles extends HttpServlet {
    private enum DownloadType { ZIP, TARGZ }

    private static final String RELATIVE_OUTPUT_PATH = "/userdata/tools/mdfiles/";

    private File getAbsoluteOutputPath(String uid) {
        return new File(getServletContext().getRealPath(RELATIVE_OUTPUT_PATH), uid);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String uuid = request.getParameter("uuid");
        File outputDirectory = getAbsoluteOutputPath(uuid);
        String filesToDownload = "structure.*";

        DownloadType downloadType = getDownloadType(request);
        File compressedFile = null;
        if (downloadType == DownloadType.TARGZ) {
            compressedFile = FileUtils.createTarGz(outputDirectory, filesToDownload,
                                                   "glycoprotein.tar.gz");
        } else {
            compressedFile = FileUtils.createZip(outputDirectory, filesToDownload,
                                                 "glycoprotein.zip");
        }

        response.setContentType("application/octet-stream");
        HttpUtils.writeResponse(response, compressedFile);
        compressedFile.delete();
    }

    private DownloadType getDownloadType(HttpServletRequest request) {
        String fileType = request.getParameter("filetype");
        if (fileType != null && fileType.equals("zip")) {
            return DownloadType.ZIP;
        } else {
            return DownloadType.TARGZ;
        }
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
            
        BuildRequest buildRequest = glycoproteinSession.getBuildRequest();

        // If there is no current build request, start a new one.
        if (buildRequest == null) {
            // This is a unique identifier associated with this build.
            String uuid = UUID.randomUUID().toString();

            File outputDirectory = getAbsoluteOutputPath(uuid);
            buildRequest = glycoproteinSession.build(outputDirectory, uuid);
        }

        long waited = 0;
        while (buildRequest.getStatus() != BuildRequest.Status.DONE && waited < 10000) {
            try {
                Thread.sleep(2000);
            } catch(InterruptedException ignore) {}
            waited += 2000;
        }

        // Reset the sessions build request, in case this session is built again.
        glycoproteinSession.resetBuildRequest();


        String uuid = buildRequest.getUUID();
        request.setAttribute("uuid", uuid);

	getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/view/tools/molecular_dynamics/glycoprotein_builder/DownloadFiles.jsp"
        ).forward(request, response);
    }
}
