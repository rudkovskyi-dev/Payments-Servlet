package ua.rudkovskyi.payments.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLUtil {
    private MySQLUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static Connection getConnection()
            throws SQLException, ClassNotFoundException {
        String hostName = "localhost";
        String dbName = "payments";
        String userName = "idea";
        String password = "idea";
        return getConnection(hostName, dbName, userName, password);
    }

    public static Connection getConnection(
            String hostName, String dbName, String userName, String password)
            throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String connectionURL = "jdbc:mysql://" + hostName + ":3306/" + dbName;

        return DriverManager.getConnection(connectionURL, userName, password);
    }
}
