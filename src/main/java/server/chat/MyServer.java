package server.chat;

import server.chat.auth.BaseAuthService;
import server.chat.handler.ClientHandler;
import server.messages.Message;
import server.messages.ObjectReader;
import server.messages.ObjectWriter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.*;

public class MyServer {
    private final ServerSocket serverSocket;
    private final ServerSocket serverSocket2;
    private final BaseAuthService authService;
    private final List<ClientHandler> clients = new ArrayList<>();

    private final Message messages = new Message();

    public List<String> getMessages() {
        return messages.getMessages();
    }

    public void setMessages(String message) {
        this.messages.addMessage(message);
    }
    public static final Logger LOGGER = Logger.getLogger("");

    public MyServer(int port, int port2) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.serverSocket2 = new ServerSocket(port2);
        this.authService = new BaseAuthService();
    }

    public BaseAuthService getAuthService() {
        return authService;
    }

    public void start() {
        LOGGER.info("Сервер запущен!");

        try {
            while (true) {
                waitAndProcessNewClientConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void waitAndProcessNewClientConnection() throws IOException {
        LOGGER.info("Ожидание пользователя...");
        Socket socket = serverSocket.accept();
        Socket socket2 = serverSocket2.accept();
        LOGGER.info("Клиент подключился");

        processClientConnection(socket, socket2);
    }

    private void processClientConnection(Socket socket, Socket socket2) throws IOException {
        ClientHandler clientHandler = new ClientHandler(this, socket, socket2);
        clientHandler.handle();
    }

    public synchronized void subscribe(ClientHandler clientHandler) throws IOException {
        clients.add(clientHandler);
        updateListClients();
    }

    public synchronized void unSubscribe(ClientHandler clientHandler) throws IOException {
        clients.remove(clientHandler);
        updateListClients();
    }

    public synchronized boolean isUsernameBusy(String username) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender, String isServerMessage) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        if(isServerMessage.equals("/serverMsg")){
            setMessages(message + " : " + dateFormat.format(date));
        }else{
            setMessages(String.format("%s: %s : %s", sender.getUsername(), message, dateFormat.format(date)));
        }
        for (ClientHandler client : clients) {
            if (client == sender) {
                client.sendMessage("Я ", message, sender);
                continue;
            }
            if(isServerMessage.equals("/serverMsg")){
                client.sendServerMessage(message + " : " + dateFormat.format(date));
            }else {
                client.sendMessage(sender.getUsername(), message, sender);
            }
        }
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender) throws IOException {
        broadcastMessage(message, sender, "/clientMsg");
    }

    public synchronized void sendPrivatMessage(String userName, String privMessage, ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(userName)) {
                client.sendMessage(sender.getUsername(), privMessage, true);
            }
        }
    }

    private synchronized void updateListClients() throws IOException {
        StringBuilder clientsString = new StringBuilder();
        for(ClientHandler client:clients){
            clientsString.append(client.getUsername()).append(" ");
        }
        for(ClientHandler client:clients) {
            client.sendUpdateListClients(clientsString);
        }
    }

    public synchronized void changeNickname(ClientHandler clientHandler, String newNick) throws SQLException, IOException {
        for (ClientHandler client:clients) {
            if(client.equals(clientHandler)){
                authService.changeNickname(client.getLogin(), newNick);
                client.sendUpdateNickname(newNick);
            }
        }
    }

    public void extractMessages(){
        Message messages2 = (((Message)new ObjectReader().extractMessages()));
        if(messages2.getMessages() != null){
            messages.setMessages(messages2.getMessages());
        }
    }

    public void saveMessages() {
        new ObjectWriter().saveMessages(messages);
    }
}
