package server.chat.auth;

import server.chat.User;
import server.db.DataBase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {
    private static final List<User> clients = new ArrayList<>();
    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (User client : clients) {
            if(client.getLogin().equals(login) & client.getPassword().equals(password)) {
                return client.getUsername();
            }
        }
        return null;
    }

    @Override
    public void createClientsFromDB() throws SQLException {
        DataBase dataBase = new DataBase();
        try {
            dataBase.connection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        List<String> users = dataBase.selectUsers();
        for (String usr:users){
            String[] parts = usr.split("\\s+", 3);
            clients.add(new User(parts[0],parts[2],parts[1]));
        }
        dataBase.disconnect();
    }

    @Override
    public void changeNickname(String login, String newNick) throws SQLException {
        for (User client : clients) {
            if(client.getLogin().equals(login)) {
                DataBase dataBase = new DataBase();
                try {
                    dataBase.connection();
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
                dataBase.updateNick(login,newNick);
                dataBase.disconnect();
                client.setLogin(newNick);
            }
        }
    }

    @Override
    public void startAuthentication() {
        System.out.println("Стар аутентификации");
    }

    @Override
    public void endAuthentication() {
        System.out.println("Окончание аутентификации");

    }
}
