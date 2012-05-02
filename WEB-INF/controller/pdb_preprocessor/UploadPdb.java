package servlets.pdb_preprocessor;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;

import org.glycam.Utils;
import org.glycam.pdb.preprocessing.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/tools/pdb-preprocessor/upload-pdb")
public class UploadPdb extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        forwardToPage(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String error = "";

        File pdbFile = null;
        try {
            if (ServletFileUpload.isMultipartContent(request)) {
                ServletFileUpload upload = new ServletFileUpload();
                FileItemIterator iter = upload.getItemIterator(request);
                while (iter.hasNext()) {
                    FileItemStream item = iter.next();
                    String name = item.getFieldName();
                    InputStream stream = item.openStream();
                    if (!item.isFormField()) {
                        pdbFile = File.createTempFile("pdb", null);

                        BufferedOutputStream fOut = null;
                        try {
                            fOut = new BufferedOutputStream(new FileOutputStream(pdbFile));
                            byte[] buffer = new byte[32*1024];
                            int bytesRead = 0;
                            while ((bytesRead = stream.read(buffer)) != -1) {
                                fOut.write(buffer, 0, bytesRead);
                            }
                        } catch (Exception e) {
                            throw new IOException(e.toString());
                        } finally {
                            fOut.flush();
                            fOut.close();
                            stream.close();
                        }
                    }
                }
            }
        } catch (FileUploadException e) {
            error = "Error uploading file";
        }

        if (error == "") {
            //error = Utils.validatePdb(pdbFile);
        }

        if (error == "") {
            HttpSession session = request.getSession(true);
            session.setAttribute("pdbPreprocessingSession", new PreprocessingSession(pdbFile));
            response.sendRedirect("menu");
            return;
        }

        request.setAttribute("error", error);

        forwardToPage(request, response);
    }

    private void forwardToPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        getServletConfig().getServletContext().getRequestDispatcher(
                "/WEB-INF/view/tools/pdb_preprocessor/UploadPdb.jsp"
        ).forward(request, response);
    }
}
