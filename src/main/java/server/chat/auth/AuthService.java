package server.chat.auth;

import java.sql.SQLException;

public interface AuthService {
    String getUsernameByLoginAndPassword(String login, String password);
    void createClientsFromDB() throws SQLException;
    void changeNickname(String login, String newNick) throws SQLException;
    void startAuthentication();
    void endAuthentication();
}
