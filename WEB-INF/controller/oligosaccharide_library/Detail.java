package servlets.oligosaccharide_library;

import org.glycam.data.DatabaseUtils;
import org.glycam.Logging;
import org.glycam.oligosaccharide_library.Item;

import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/oligosaccharide-library/detail")
public class Detail extends HttpServlet {
    private Item getItem(int id) {
        Connection conn = DatabaseUtils.getDataConnection("jdbc/oligosaccharide_library");

        if (conn == null) {
            Logging.logger.severe("No database connection.");
            return null;
        }

        String query = "SELECT sequence, name FROM structure WHERE id = ?";

        Item item = null;

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, id);
            try {
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    String sequence = rs.getString("sequence");
                    String name = rs.getString("name");
                    item = new Item(id, sequence, name, "");
                }
            } finally {
                statement.close();
            }
        } catch (SQLException e) {
            Logging.logger.severe(e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Exception e) {}
        }

        return item;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws javax.servlet.ServletException, IOException {
        int page = Integer.parseInt(request.getParameter("page"));
        int category = Integer.parseInt(request.getParameter("category"));
        int id = Integer.parseInt(request.getParameter("id"));

        request.setAttribute("page", page);
        request.setAttribute("category", category);
        request.setAttribute("item", getItem(id));

        try {
            getServletConfig().getServletContext().getRequestDispatcher(
                "/oligosaccharide_library/Detail.jsp"
            ).forward(request, response);
        } catch (javax.servlet.ServletException e) {
            Logging.logger.severe(e.getMessage());
            throw e;
        } catch (IOException e) {
            Logging.logger.severe(e.getMessage());
            throw e;
        }
    }
}
