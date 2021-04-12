package client.controllers;

import client.ChatGB;
import client.models.Network;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AuthController {
    @FXML
    public TextField loginField;

    @FXML
    public PasswordField passwordField;

    private client.models.Network network;
    private ChatGB mainChatGB;

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setChatGB(ChatGB chatGB) {
        this.mainChatGB = chatGB;
    }

    public void checkAuth() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if(login.length() == 0 || password.length() == 0) {
            System.out.println("!!Поля не должны быть пустыми");
            return;
        }

        String authErrorMessage = network.sendAuthCommand(login, password);

        if (authErrorMessage == null) {
            mainChatGB.openChat();
        }
        else {
            System.out.println("!!Ошибка аутентификации");
        }
    }
}
