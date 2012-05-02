package servlets;

import javax.servlet.http.*;

import java.io.PrintWriter;

import org.glycam.data.DatabaseUtils;
import org.glycam.Logging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/globe-data")
public class GlobeData extends HttpServlet {
    private static class Coordinate implements Comparable<Coordinate> {
        private final static int CLUMP_FACTOR = 1000;

        private final double latitude;
        private final double longitude;

        public Coordinate(double x, double y) {
            this.latitude = x;
            this.longitude = y;
        }

        public int compareTo(Coordinate other) {
            int latitudeDiff = (int)(other.latitude*CLUMP_FACTOR - this.latitude*CLUMP_FACTOR);
            if (latitudeDiff == 0)
                return 0;
            return (int)(other.longitude*CLUMP_FACTOR - this.longitude*CLUMP_FACTOR);
        }

        public double getLatitude() { return this.latitude; }
        public double getLongitude() { return this.longitude; }
    }

    private static class GlobeCoordinateInfo {
        private final Coordinate coordinate;
        private final double magnitude;

        public GlobeCoordinateInfo(Coordinate coordinate, double magnitude) {
            this.coordinate = coordinate;
            this.magnitude = magnitude;
        }

        public Coordinate getCoordinate() { return coordinate; }
        public double getMagnitude() { return magnitude; }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException, javax.servlet.ServletException {
        List<Coordinate> coordinates = getCoordinates();
        List<GlobeCoordinateInfo> globeInfo = getGlobeInfo(coordinates);

        PrintWriter out = response.getWriter();

        out.print("[");
        if (globeInfo.size() > 0) {
            out.print(getCoordinateString(globeInfo.get(0)));
        }
        for (int i = 1; i < globeInfo.size(); i++) {
            out.print(",");
            out.print(getCoordinateString(globeInfo.get(i)));
        }
        out.print("]");
    }

    private String getCoordinateString(GlobeCoordinateInfo info) {
        String str = info.getCoordinate().getLatitude() + "," +
                     info.getCoordinate().getLongitude() + "," +
                     info.getMagnitude();
                   
        return str;
    }

    private List<Coordinate> getCoordinates() {
        List<Coordinate> coordinates = new ArrayList<Coordinate>();

        Connection conn = DatabaseUtils.getDataConnection("jdbc/glycamweb");
        if (conn == null) {
            return coordinates;
        }

        String query = "SELECT X(location), Y(location) FROM Visitor;";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            try {
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    double latitude = rs.getDouble(1);
                    double longitude = rs.getDouble(2);
                    coordinates.add(new Coordinate(latitude, longitude));
                }
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
        return coordinates;
    }

    private List<GlobeCoordinateInfo> getGlobeInfo(List<Coordinate> coordinates) {
        Map<Coordinate, Integer> freq = new TreeMap<Coordinate, Integer>();
        for (int i = 0; i < coordinates.size(); i++) {
            Coordinate coordinate = coordinates.get(i);
            int count = freq.containsKey(coordinate)?freq.get(coordinate):0;
            freq.put(coordinate, count + 1);
        }
        List<GlobeCoordinateInfo> globe_info = new ArrayList<GlobeCoordinateInfo>();
        for (Map.Entry<Coordinate, Integer> entry : freq.entrySet()) {
            double magnitude = Math.log(entry.getValue())/130.0;
            globe_info.add(new GlobeCoordinateInfo(entry.getKey(), magnitude));
        }
        return globe_info;
    }
}
