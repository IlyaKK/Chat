package client;

import client.controllers.ViewController;
import client.models.Network;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.chat.User;

import java.util.List;

public class EchoClient extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(EchoClient.class.getResource("views/chat-view.fxml"));
        Parent root = loader.load();
        stage.setTitle("Chat");
        stage.setScene(new Scene(root));
        stage.show();

        Network network = new Network();
        network.connect();

        ViewController viewController = loader.getController();
        viewController.setNetwork(network);

        network.waitMessage(viewController);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
