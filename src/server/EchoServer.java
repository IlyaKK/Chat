package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class EchoServer {
    private static final int  SERVER_PORT = 8888;

    static final Scanner inServer = new Scanner(System.in);

    public static void main(String[] args) {

        try(ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {

            System.out.println("Ожидание подключения...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Соединение установлено!");

                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

                try {
                    while (true) {
                        serverMessage(out);
                        String message = in.readUTF();

                        if (message.equals("/server-stop")) {
                            break;
                        }

                        System.out.println("Сообщение пользователя: " + message);
                        out.writeUTF(message + ": прошло через сервер");
                    }

                    System.out.println("Сервер остановлен");
                } catch (IOException e) {
                    clientSocket.close();
                    System.out.println("Клиент отключился");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void serverMessage(DataOutputStream out) {
        Thread thread = new Thread(() -> {
            try{
                while (true){
                    String message = inServer.nextLine();
                    out.writeUTF("Сервер: " + message);
                }
            }catch (IOException e){
                System.out.println("Ошибка отправки сообщения с сервера");
            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}
