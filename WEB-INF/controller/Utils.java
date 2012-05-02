package servlets;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

public class Utils {
    public static File uploadFile(HttpServletRequest request, String prefix) throws IOException {
        File file = null;
        try {
            if (ServletFileUpload.isMultipartContent(request)) {
                ServletFileUpload upload = new ServletFileUpload();
                FileItemIterator iter = upload.getItemIterator(request);
                while (iter.hasNext()) {
                    FileItemStream item = iter.next();
                    String name = item.getFieldName();
                    InputStream stream = item.openStream();
                    if (!item.isFormField()) {
                        file = File.createTempFile(prefix, null);

                        BufferedOutputStream fOut = null;
                        try {
                            fOut = new BufferedOutputStream(new FileOutputStream(file));
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

                        break;
                    }
                }
            }
        } catch (FileUploadException e) {
            // Log this?
        }

        return file;
    }
}
