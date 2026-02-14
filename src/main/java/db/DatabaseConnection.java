package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    private static volatile DatabaseConnection instance;

    private final String url;
    private final String user;
    private final String password;

    private DatabaseConnection(String url, String user, String password) {
        this.url ="jdbc:postgresql://localhost:5432/api";
        this.user = "postgres";
        this.password = "1234";
    }

    public static DatabaseConnection getInstance(String url, String user, String password) {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection(url, user, password);
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
