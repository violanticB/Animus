package animus.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Ethan Borawski
 */
public class DatabaseManager {
    private Connection connection;

    /**
     * This class can be used to create a connection to an
     * sql server via JDBC. It also includes a few utility methods
     * for accessibility.
     *
     * @param host DB Host
     * @param user User
     * @param pass Password
     * @param database Database Name
     * @throws SQLException
     */
    public DatabaseManager(String host, String user, String pass, String database)
            throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":3306/" + database +
                            "?user=" + user + "&password=" + pass + "&autoReconnect=true"
            );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * Connection to JDBC
     * @return Connection
     */
    public Connection getConnection() {
        return connection;
    }
    /**
     * The statement is cached and the execution path
     * is pre-determined on the database server allowing
     * it to be executed multiple times in an efficient manner.
     *
     * @param query SQL Query
     * @return Prepared Statement
     * @throws SQLException
     */
    public PreparedStatement prepareStatement(String query)
            throws SQLException {
        return connection.prepareStatement(query);
    }
}
