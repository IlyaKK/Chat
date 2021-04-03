package server.chat.handler;


import server.chat.MyServer;
import server.chat.auth.AuthService;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import static server.chat.MyServer.LOGGER;

public class ClientHandler {
    private final MyServer myServer;
    private final Socket clientSocket;
    private final Socket clientSocket2;
    private DataOutputStream out;
    private DataInputStream in;
    private ObjectOutputStream oos;

    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + pass
    private static final String AUTHOK_CMD_PREFIX = "/authok"; // + username
    private static final String AUTHERR_CMD_PREFIX = "/autherr"; // + error message
    private static final String CLIENT_MSG_CMD_PREFIX = "/clientMsg"; // + msg
    private static final String SERVER_MSG_CMD_PREFIX = "/serverMsg"; // + msg
    private static final String PRIVATE_MSG_CMD_PREFIX = "/w"; //sender + p + msg
    private static final String CLIENT_ADD_CMD_PREFIX = "/addedClient"; // + clients
    private static final String CHANGE_NICKNAME_PREFIX_OK = "/changeNicknameOk";
    private static final String CHANGE_NICKNAME_PREFIX = "/changeNickname"; // + newNickname
    private static final String END_CMD_PREFIX = "/end"; //

    private String username;
    private String login;



    public ClientHandler(MyServer myServer, Socket socket, Socket socket2) {
        this.myServer = myServer;
        this.clientSocket = socket;
        this.clientSocket2 = socket2;
    }


    public void handle() throws IOException {

        out = new DataOutputStream(clientSocket.getOutputStream());
        oos = new ObjectOutputStream(clientSocket2.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());
        new Thread(() -> {
            try {
                authentication();
                sendLastMessages();
                readMessage();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    closeConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void authentication() throws IOException {
        while (true) {
           String message = in.readUTF();
            //String message = "/auth martin 1111";
            if (message.startsWith(AUTH_CMD_PREFIX)) {
                boolean isSuccessAuth = processAuthCommand(message);
                if(isSuccessAuth) {
                    break;
                }
            } else {
                out.writeUTF(AUTHERR_CMD_PREFIX + " Ошибка авторизации");
            }
        }
    }

    private boolean processAuthCommand(String message) throws IOException {
        String[] parts = message.split("\\s+", 3);
        String login = parts[1];
        String password = parts[2];

        AuthService authService = myServer.getAuthService();

        try {
            authService.createClientsFromDB();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        username = authService.getUsernameByLoginAndPassword(login, password);

        if (username != null) {
            if (myServer.isUsernameBusy(username)) {
                out.writeUTF(AUTHERR_CMD_PREFIX + " Логин уже используется");
                return false;
            }

            this.login = login;
            out.writeUTF(AUTHOK_CMD_PREFIX + " " + username);
            myServer.extractMessages();
            myServer.broadcastMessage(String.format(">>> %s присоединился к чату", username), this, SERVER_MSG_CMD_PREFIX);
            myServer.subscribe(this);
            return true;

        }

        else {
            out.writeUTF(AUTHERR_CMD_PREFIX + " Логин или пароль не соответствуют действительности");
            return false;
        }

    }

    private void readMessage() throws IOException {
        while (true) {
            String message = in.readUTF();
            LOGGER.info("message | " + username + ": " + message);
            if (message.startsWith(END_CMD_PREFIX)) {
                return;
            }
            else if (message.startsWith(PRIVATE_MSG_CMD_PREFIX)) {
                String[] parts = message.split("\\s+", 3);
                String userName = parts[1];
                String privMessage = parts[2];
                myServer.sendPrivatMessage(userName, privMessage, this);
            }else if (message.startsWith(CHANGE_NICKNAME_PREFIX)) {
                String[] parts = message.split("\\s+", 2);
                String newNick = parts[1];
                try {
                    myServer.changeNickname(this, newNick);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } else {
                myServer.broadcastMessage(message, this);
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public String getLogin() {
        return login;
    }

    public void sendMessage(String sender, String message, ClientHandler clientHandler) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        myServer.saveMessages();
        out.writeUTF(String.format("%s %s %s : %s", CLIENT_MSG_CMD_PREFIX, sender, message, dateFormat.format(date)));
    }

    public void sendServerMessage(String message) throws IOException {
        out.writeUTF(String.format("%s %s", SERVER_MSG_CMD_PREFIX, message));
        myServer.saveMessages();
    }

    public void sendMessage(String sender, String message, Boolean flagPrivateMsg) throws IOException {
        out.writeUTF(String.format("%s %s %s", CLIENT_MSG_CMD_PREFIX, sender, message));
    }

    public void sendUpdateListClients(StringBuilder message) throws IOException {
        out.writeUTF(String.format("%s %s", CLIENT_ADD_CMD_PREFIX, message));
    }

    public void sendObject (Object object) throws IOException
    {
        oos.writeObject(object);
        oos.flush();
    }

    private void sendLastMessages() throws IOException {

        sendObject(myServer.getMessages());
    }

    public void sendUpdateNickname(String newNick) throws IOException {
        out.writeUTF(String.format("%s %s", CHANGE_NICKNAME_PREFIX_OK, newNick));
    }

    public void closeConnection() throws IOException {
        myServer.unSubscribe(this);
        myServer.broadcastMessage(String.format(">>> %s вышел из чата", username), this, SERVER_MSG_CMD_PREFIX);
        myServer.saveMessages();
        try {
            in.close();
            out.close();
            clientSocket.close();
            clientSocket2.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
