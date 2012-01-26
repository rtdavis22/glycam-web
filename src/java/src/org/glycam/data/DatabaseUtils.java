// Author: Robert Davis

package org.glycam.data;

import org.glycam.Logging;

import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

// This class includes global configuration information.
public class DatabaseUtils {
    private DatabaseUtils() {
        throw new AssertionError();
    }

    // Returns a connect to the data source represented by the given resource name. If there is an
    // error, this returns null instead of throwing.
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
