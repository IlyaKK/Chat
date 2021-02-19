package server.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {
    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;

    public void connection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/db/mainDB.db");
        statement = connection.createStatement();
    }

    public void disconnect() throws SQLException {
        connection.close();
    }

    public List<String> selectUsers() throws SQLException {
        resultSet = statement.executeQuery("SELECT login, username, password FROM Users");
        List<String> users = new ArrayList<>();
        while (resultSet.next()){
            users.add(String.format("%s %s %s", resultSet.getString("login"), resultSet.getString("username"), resultSet.getString("password")));
        }
        return users;
    }

    private static String selectUser(String login, String password) throws SQLException {
        resultSet = statement.executeQuery(String.format("SELECT password, username FROM Users WHERE login = '%s'",login));
        if(resultSet.isClosed()){
            return null;
        }
        if(resultSet.getString("password").equals(password)){
            System.out.println("Авторизация прошла успешно");
            return resultSet.getString("username");
        }else {
            System.out.println("Ошибка пароля");
        }
        return null;
    }

    private static void updateUsername(String login, String newUsername) throws SQLException {
        statement.executeUpdate(String.format("UPDATE Users SET username = '%s' WHERE login = '%s'",newUsername,login));
    }

    public void updateNick(String nick, String newNick) throws SQLException {
        statement.executeUpdate(String.format("UPDATE Users SET login = '%s' WHERE login = '%s'",newNick,nick));
    }

    private static void createUsers(String login, String userName, String password) {
        for(int i = 5; i < 1000; i++) {
           // statement.executeUpdate(String.format("INSERT INTO Users (login,password,username) VALUES ('%s','%s','%s')",));
        }
    }
}
