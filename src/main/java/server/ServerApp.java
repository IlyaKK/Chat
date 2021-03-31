package server;

import server.chat.MyServer;

import java.io.IOException;

public class ServerApp {

    private static final int DEFAULT_PORT = 8888;
    private static final int DEFAULT_PORT2 = 8889;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        int port2 = DEFAULT_PORT2;

        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
            port2 = Integer.parseInt(args[1]);
        }

        try {
            new MyServer(port, port2).start();
        } catch (IOException e) {
            System.out.println("Ошибка");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
