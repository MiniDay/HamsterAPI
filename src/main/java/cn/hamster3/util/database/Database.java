package cn.hamster3.util.database;

import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private Connection connection;

    public Database(String host, String port, String usingDatabase, String user, String password) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&autoReconnect=true", user, password);

        Statement statement = connection.createStatement();
        statement.execute("CREATE DATABASE IF NOT EXISTS " + usingDatabase + ";");
        statement.execute("USE " + usingDatabase + ";");
        statement.close();
    }

    public Database(String host, int port, String usingDatabase, String user, String password) throws SQLException, ClassNotFoundException {
        this(host, String.valueOf(port), usingDatabase, user, password);
    }

    public Database(ConfigurationSection config) throws SQLException, ClassNotFoundException {
        String host = config.getString("mysql.host");
        String port = config.getString("mysql.port");
        String database = config.getString("mysql.database");
        String user = config.getString("mysql.user");
        String password = config.getString("mysql.password");
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&autoReconnect=true", user, password);

        Statement statement = connection.createStatement();
        statement.execute("CREATE DATABASE IF NOT EXISTS " + database + ";");
        statement.execute("USE " + database + ";");
        statement.close();
    }

    public Connection getConnection() {
        return connection;
    }

}
