package org.glycam.data;

import org.glycam.Logging;

import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * Utilities for interacting with data.
 *
 * @author Robert Davis
 */
public class DatabaseUtils {
    private DatabaseUtils() {
        throw new AssertionError();
    }

    /**
     * Returns a connection to the data source represented by the given resource name.
     *
     * @param resourceName the name of the resource.
     *
     * @return a {@link Connection} to the data source or {@code null} if there was an error.
     */
    public static Connection getDataConnection(String resourceName) {
        try {
            Context initialContext = new InitialContext();
            Context envContext = (Context)initialContext.lookup("java:comp/env");
            DataSource dataSource = (DataSource)envContext.lookup(resourceName);
            return dataSource.getConnection();
        } catch (Exception e) {
            Logging.logger.severe(e.getMessage());
        }
        return null;
    }
}
