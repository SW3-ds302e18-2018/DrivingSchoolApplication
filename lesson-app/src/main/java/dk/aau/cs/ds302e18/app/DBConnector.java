package dk.aau.cs.ds302e18.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ResourceBundle;

/* Responsible for creating a connection to the database. */
public class DBConnector {
    private Connection connection;

    public Connection createConnectionObject() {
        ResourceBundle reader = ResourceBundle.getBundle("application");
        String mysqlUrl = reader.getString("spring.datasource.url");
        String mysqlUsername = reader.getString("spring.datasource.username");
        String mysqlPassword = reader.getString("spring.datasource.password");

        try {
            // create a mysql database connection
            String myDriver = "org.gjt.mm.mysql.Driver";
            Class.forName(myDriver);
            connection = DriverManager.getConnection(mysqlUrl, mysqlUsername, mysqlPassword);
        } catch (Exception e) {
            //System.err.println("Got an exception!");
            e.printStackTrace();
        }
        return connection;
    }

    public Connection getConnection() {
        return connection;
    }
}
