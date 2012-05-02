package servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * HTTP utility functions.
 *
 * @author Robert Davis
 */
public class HttpUtils {
    public static void writeResponse(HttpServletResponse response, File file) throws IOException {
        response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
        response.setHeader("Content-Length", String.valueOf(file.length()));
        FileInputStream fileIn = new FileInputStream(file);
        ServletOutputStream outStream = response.getOutputStream();
        byte[] outputByte = new byte[4096];
        while (fileIn.read(outputByte, 0, 4096) != -1) {
            outStream.write(outputByte, 0, 4096);
        }
        fileIn.close();
        outStream.flush();
        outStream.close();
    }
}
