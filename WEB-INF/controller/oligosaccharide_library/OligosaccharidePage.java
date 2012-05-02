package servlets.oligosaccharide_library;

import org.glycam.data.DatabaseUtils;
import org.glycam.oligosaccharide_library.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/oligosaccharide-library/page")
public class OligosaccharidePage extends HttpServlet {
    private final int itemsPerPage = 60;

    int getNumPages(int categoryId) {
        Connection conn = DatabaseUtils.getDataConnection("jdbc/oligosaccharide_library");

        if (conn == null) {
            System.out.println("No connection to database");
            return -1;
        }

	String query = "SELECT count(*) FROM structure WHERE category_id = ?";

        int count = -1;
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, categoryId);
            try {
                ResultSet rs = statement.executeQuery();
                rs.next();
                count = rs.getInt(1);
            } finally {
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Exception e) {}
        }

        if (count < 0) {
            return -1;
        }
        return (int)Math.ceil(count*1.0/itemsPerPage);
    }

    ArrayList<Item> getPageContents(int categoryId, int pageNumber) {
        ArrayList<Item> items = new ArrayList<Item>();

        Connection conn = DatabaseUtils.getDataConnection("jdbc/oligosaccharide_library");

        if (conn == null) {
            System.out.println("No connection to database");
            return items;
        }

        String query = "SELECT id, sequence, name FROM structure WHERE category_id = ? " +
                       " ORDER BY order_id,id LIMIT ?,?";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, categoryId);
            statement.setInt(2, itemsPerPage*(pageNumber - 1));
            statement.setInt(3, itemsPerPage);
            try {
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    String sequence = rs.getString("sequence");
                    String name = rs.getString("name");
                    int id = rs.getInt("id");
                    items.add(new Item(id, sequence, name, "desc"));
                }
            } finally {
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (Exception e) {}
        }

        return items;
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) {
        try {
            int page = 1;
            if (request.getParameter("page") != null)
                page = Integer.parseInt(request.getParameter("page"));

            int category = 1;
            if (request.getParameter("category") != null)
                category = Integer.parseInt(request.getParameter("category"));

            ArrayList<Item> items = getPageContents(category, page);
            request.setAttribute("items", items);

            request.setAttribute("page", page);
            request.setAttribute("category", category);

            int numPages = getNumPages(category);
            // I can't believe I'm writing this code. JSTL wants something to iterate over.
            ArrayList<Integer> pages = new ArrayList<Integer>();
            for (int i = 0; i < numPages; i++)
                pages.add(i + 1);
            request.setAttribute("pages", pages);
            request.setAttribute("numPages", numPages);

            getServletConfig().getServletContext().getRequestDispatcher(
                "/oligosaccharide_library/OligosaccharidePage.jsp"
            ).forward(request, response);

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
