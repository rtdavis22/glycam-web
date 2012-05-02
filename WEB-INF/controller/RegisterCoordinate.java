package servlets;

import org.glycam.data.DatabaseUtils;
import org.glycam.Logging;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/register-coordinate")
public class RegisterCoordinate extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException, javax.servlet.ServletException {
        double latitude = Double.parseDouble(request.getParameter("latitude"));
        double longitude = Double.parseDouble(request.getParameter("longitude"));
        
        Connection conn = DatabaseUtils.getDataConnection("jdbc/glycamweb");
        if (conn == null) {
            return;
        }

        String query = "INSERT INTO Visitor (timestamp, location) VALUES (CURDATE(), GeomFromText('POINT(? ?)'));";

        try {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO Visitor (timestamp, location) VALUES (CURDATE(), GeomFromText('POINT(" + latitude + " " + longitude + ")'));");
            //statement.setDouble(1, latitude);
            ///statement.setDouble(2, longitude);
            try {
                statement.executeUpdate();
            } finally {
                conn.close();
            }
        } catch (SQLException e) {
            Logging.logger.severe(e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Exception ignore) {}
        }
    }

}
