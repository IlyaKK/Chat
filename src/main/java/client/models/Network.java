package client.models;

import client.controllers.ChatController;
import client.messages.Message;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class Network {
    private Socket socket;
    private Socket socket2;
    private DataInputStream in;
    private DataOutputStream out;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private static final int DEFAULT_SERVER_SOCKET = 8888;
    private static final int DEFAULT_SERVER_SOCKET2 = 8889;
    private static final String DEFAULT_SERVER_HOST = "localhost";
    private static final String CHANGE_NICKNAME_PREFIX = "/changeNickname"; // + newNickname
    private static final String CHANGE_NICKNAME_PREFIX_OK = "/changeNicknameOk";
    private static final String AUTH_CMD_PREFIX = "/auth"; // + login + pass
    private static final String AUTHOK_CMD_PREFIX = "/authok"; // + username
    private static final String AUTHERR_CMD_PREFIX = "/autherr"; // + error message
    private static final String CLIENT_MSG_CMD_PREFIX = "/clientMsg"; // + msg
    private static final String SERVER_MSG_CMD_PREFIX = "/serverMsg"; // + msg
    private static final String PRIVATE_MSG_CMD_PREFIX = "/w"; //sender + p + msg
    private static final String CLIENT_ADD_CMD_PREFIX = "/addedClient"; // + clients
    private static final String ADD_LAST_MESSAGES_CMD_PREFIX = "/addMessages";
    private static final String ADD_LAST_MESSAGES_OK_CMD_PREFIX = "/addMessagesOk";
    private static final String END_CMD_PREFIX = "/end"; //

    private final int port;
    private final int port2;
    private final String host;
    private String userName;
    private String nickName;

    private Message messages = new Message();

    public Network() {
        this.host = DEFAULT_SERVER_HOST;
        this.port = DEFAULT_SERVER_SOCKET;
        this.port2 = DEFAULT_SERVER_SOCKET2;
    }

    public DataOutput getOut() {
        return out;
    }

    public void connect() {
        try {
            socket = new Socket(host, port);
            socket2 = new Socket(host, port2);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket2.getInputStream());
            oos = new ObjectOutputStream(socket2.getOutputStream());

        } catch (IOException e) {
            System.out.println("Соединение не установлено");
            e.printStackTrace();
        }
    }

    public void waitMessage(ChatController chatController) {
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    String message = in.readUTF();
                    if (message.startsWith(CLIENT_MSG_CMD_PREFIX)) {
                        String[] parts = message.split("\\s+", 3);
                        String sender = parts[1];
                        String messageFromUser = parts[2];

                        Platform.runLater(() -> chatController.addMessageToListMessage(String.format("%s: %s", sender, messageFromUser)));
                    } else if (message.startsWith(SERVER_MSG_CMD_PREFIX)) {
                        String[] parts = message.split("\\s+", 2);
                        String messageFromServer = parts[1];
                        Platform.runLater(() -> chatController.addMessageToListMessage(messageFromServer));
                    } else if (message.startsWith(CLIENT_ADD_CMD_PREFIX)) {
                        String[] parts1 = message.split("\\s+", 2);
                        String[] listPersons = parts1[1].split("\\s+");
                        List<String> newListPersons = List.of(listPersons);
                        addLastMessages(chatController);
                        Platform.runLater(() -> chatController.updatePersonsInList(newListPersons));
                    } else if (message.startsWith(CHANGE_NICKNAME_PREFIX_OK)) {
                        String[] parts2 = message.split("\\s+", 2);
                        nickName = parts2[1];
                    } else {
                        Platform.runLater(() -> System.out.println("!!Неизвестная ошибка сервера"));
                    }
                }
            } catch (IOException e) {
                System.out.println("Ошибка подключения");
            }

        });

        thread.setDaemon(true);
        thread.start();
    }

    public String sendAuthCommand(String login, String password) {
        try {
            out.writeUTF(String.format("%s %s %s", AUTH_CMD_PREFIX, login, password));
            String response = in.readUTF();

            if (response.startsWith(AUTHOK_CMD_PREFIX)) {
                this.userName = response.split("\\s+", 2)[1];
                return null;
            } else {
                return response.split("\\s+", 2)[1];
            }
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String getUsername() {
        return userName;
    }

    public void sendPrivateMessage(String message, String selectedRecipient) throws IOException {
        String command = String.format("%s %s %s", PRIVATE_MSG_CMD_PREFIX, selectedRecipient, message);
        sendMessage(command);
    }

    public void sendMessage(String message) throws IOException {
        out.writeUTF(message);
    }

    public void sendChangeNickCommand(String newNick) throws IOException {
        String command = String.format("%s %s", CHANGE_NICKNAME_PREFIX, newNick);
        sendMessage(command);
    }

    public void addLastMessages(ChatController chatController) {
        try {
            messages.setMessages(((Message) ois.readObject()).getMessages());
            for(String message: messages.getMessages()){
                Platform.runLater(() -> chatController.addMessageToListMessage(message));
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
