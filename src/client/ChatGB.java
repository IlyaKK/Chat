package client;

import client.controllers.AuthController;
import client.controllers.ChatController;
import client.models.Network;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatGB extends Application {

    private Stage primaryStage;
    private Network network;
    private Stage authStage;
    private ChatController chatController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        network = new Network();
        network.connect();

        openAuthDialog();
        createChatDialog();
    }

    private void createChatDialog() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ChatGB.class.getResource("views/chat-view.fxml"));

        Parent root = loader.load();

        primaryStage.setTitle("Chat");
        primaryStage.setScene(new Scene(root));

        chatController = loader.getController();
        chatController.setNetwork(network);
    }

    private void openAuthDialog() throws IOException {
        FXMLLoader authLoader = new FXMLLoader();
        authLoader.setLocation(ChatGB.class.getResource("views/auth-view.fxml"));

        Parent root = authLoader.load();
        authStage = new Stage();

        authStage.setTitle("Аутентификация");
        authStage.setScene(new Scene(root));
        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.initOwner(primaryStage);
        authStage.show();

        AuthController authLoaderController = authLoader.getController();
        authLoaderController.setNetwork(network);
        authLoaderController.setChatGB(this);
    }


    public void openChat() {
        authStage.close();
        primaryStage.show();
        primaryStage.setTitle(network.getUsername());
        primaryStage.setAlwaysOnTop(true);
        network.waitMessage(chatController);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
