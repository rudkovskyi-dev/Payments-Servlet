package ua.rudkovskyi.payments.util;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionUtil {
    private ConnectionUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static Connection getConnection()
            throws SQLException, ClassNotFoundException {
        return MySQLUtil.getConnection();
    }

    public static void closeQuietly(Connection conn) {
        try {
            conn.close();
        } catch (Exception e) {
            //TODO Exception for conn close
        }
    }

    public static void rollbackQuietly(Connection conn) {
        try {
            conn.rollback();
        } catch (Exception e) {
            //TODO Exception for conn rollback
        }
    }
}
