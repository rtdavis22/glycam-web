package servlets.molecular_dynamics.glycan_builder;

import org.glycam.FileUtils;
import org.glycam.LinkageValues;
import org.glycam.molecular_dynamics.glycan_builder.BuildRequest;
import org.glycam.molecular_dynamics.glycan_builder.GlycanSession;
import org.glycam.molecular_dynamics.glycan_builder.ResultStructure;

import servlets.HttpUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Robert Davis
 */
@WebServlet("/tools/molecular-dynamics/oligosaccharide-builder/download-files")
public class DownloadFiles extends HttpServlet {
    private enum DownloadType { ZIP, TARGZ, PDB }

    private static final String RELATIVE_OUTPUT_PATH = "/userdata/tools/mdfiles/";

    private File getAbsoluteOutputPath(String uid) {
        return new File(getServletContext().getRealPath(RELATIVE_OUTPUT_PATH), uid);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // TODO: Change all and selected to use this download_type.
        String dType = request.getParameter("download_type");
        if (dType.startsWith("pdb")) {
            int index = Integer.parseInt(dType.substring(3));
            File outputDirectory = getOutputDirectory(request);
            File pdbFile = new File(outputDirectory, index + ".pdb");
            response.setContentType("chemical/x-pdb");
            HttpUtils.writeResponse(response, pdbFile);
            return;
        }


        String filesToDownload = getFilesToDownload(request);
        DownloadType downloadType = getDownloadType(request);
        File outputDirectory = getOutputDirectory(request);

        File compressedFile = null;
        if (downloadType == DownloadType.TARGZ) {
            compressedFile = FileUtils.createTarGz(outputDirectory, filesToDownload,
                                                   "structures.tar.gz");
        } else {
            compressedFile = FileUtils.createZip(outputDirectory, filesToDownload,
                                                 "structures.zip");
        }
        response.setContentType("application/octet-stream");
        HttpUtils.writeResponse(response, compressedFile);
        compressedFile.delete();
    }

    private String getFilesToDownload(HttpServletRequest request) {
        String filesToDownload = "";
        String submitValue = (String)request.getParameter("submit");
        if (submitValue != null && submitValue.equals("Download Selected Structures")) {
            String[] boxes = request.getParameterValues("structure");
            if (boxes != null) {
                for (int i = 0; i < boxes.length; i++) {
                    filesToDownload += " " + boxes[i] + ".*";
                }
            }
        } else {
            filesToDownload += "*.rst *.pdb *.top";
        }
        filesToDownload += " structure*";
        return filesToDownload;
    }

    private DownloadType getDownloadType(HttpServletRequest request) {
        String fileType = request.getParameter("filetype");
        if (fileType != null && fileType.equals("zip")) {
            return DownloadType.ZIP;
        } else {
            return DownloadType.TARGZ;
        }
    }

    private File getOutputDirectory(HttpServletRequest request) {
        return getAbsoluteOutputPath(getRequestUID(request));
    }

    private String getRequestUID(HttpServletRequest request) {
        return request.getParameter("uid");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException { 
        HttpSession session = request.getSession(true);
        GlycanSession glycanSession = (GlycanSession)session.getAttribute("glycanSession");

        if (glycanSession == null) {
            response.sendRedirect("build-glycan");
            return;
        }

        BuildRequest buildRequest = glycanSession.getBuildRequest();

        boolean isBuildDone = true;
        if (buildRequest == null) {
            buildGlycanSession(glycanSession);
            isBuildDone = false;
        } else if (buildRequest.getStatus() != BuildRequest.Status.DONE) {
            isBuildDone = false;
        }
        if (!isBuildDone) {
            request.setAttribute("numStructures", glycanSession.getTotalStructureCount());
            sendToWaitPage(request, response);
            return;
        }

        // Reset the session's build request, in case this session is built again.
        glycanSession.resetBuildRequest();

        prepareRequest(request, buildRequest);
        sendToDownloadPage(request, response);
    }

    private void buildGlycanSession(GlycanSession glycanSession) {
        File outputDirectory = getAbsoluteOutputPath(getNewUID());
        glycanSession.build(outputDirectory);
    }

    private String getNewUID() {
        return UUID.randomUUID().toString();
    }

    private void sendToWaitPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/view/tools/molecular_dynamics/oligosaccharide_builder/WaitPage.jsp"
        ).forward(request, response);
    }

    private void sendToDownloadPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/view/tools/molecular_dynamics/oligosaccharide_builder/DownloadFiles.jsp"
        ).forward(request, response);
    }

    private void prepareRequest(HttpServletRequest request, BuildRequest buildRequest) {
        List<ResultStructure> resultStructures = buildRequest.getResultStructures();
        request.setAttribute("resultStructures", resultStructures);

        File outputDirectory = buildRequest.getOutputDirectory();
        request.setAttribute("uid", outputDirectory.getName());

        boolean showPhiColumn = false;
        boolean showOmegaColumn = false;
        if (resultStructures.size() > 0) {
            Map<Integer, LinkageValues> first = resultStructures.get(0).getAngles();
            request.setAttribute("numVaryingLinkages", first.size());
            for (Map.Entry<Integer, LinkageValues> entry : first.entrySet()) {
                if (entry.getValue().isPhiSet())
                    showPhiColumn = true;
                if (entry.getValue().isOmegaSet())
                    showOmegaColumn = true;
            }
        }

        request.setAttribute("showPhiColumn", Boolean.valueOf(showPhiColumn));
        request.setAttribute("showOmegaColumn", Boolean.valueOf(showOmegaColumn));

        int totalStructures = resultStructures.size();
        request.setAttribute("totalStructures", totalStructures);
    }
}
