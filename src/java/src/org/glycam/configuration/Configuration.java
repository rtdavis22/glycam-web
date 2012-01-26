// Author: Robert Davis

package org.glycam.configuration;

public class Configuration {
    // The root directory of the project. This variable is put here by autoconf as a substitution
    // variable. There may be a better way to do this that doesn't require reconfiguring the
    // project whenever this file is modified.
    // TODO: Make this a java.io.File.
    private static final String projectRoot = "/opt/apache-tomcat-6.0.32/webapps/ROOT/WEB-INF/glycam-web";

    public static String getProjectRoot() { return projectRoot; }
}
