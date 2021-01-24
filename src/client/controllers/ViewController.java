package client.controllers;

import client.EchoClient;
import client.models.Network;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewController {
    @FXML
    private ListView<String> listPerson;

    @FXML
    private ListView<String> listMessage;

    @FXML
    private TextField messageField;

    private Network network;

    private final ObservableList<String> messageList = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        listMessage.setItems(messageList);
        listPerson.setItems(FXCollections.observableArrayList(EchoClient.USERS_TEST_DATA));
    }

    @FXML
    public void sendMessage() {
        ObservableList<String> name = listPerson.getSelectionModel().getSelectedItems();
        if(name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Не выбран участник");
            alert.setContentText("Выберите имя пишущего из списка участников");
            alert.show();
        }else {
            String message = messageField.getText();
            if (!message.isBlank()) {
                try {
                    network.getOut().writeUTF(name + " " + message);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Ошибка при отправке сообщения");
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Ошибка ввода данных");
                alert.setContentText("Не отправляйте пустое сообщение");
                alert.show();
            }
        }
        messageField.clear();
    }

    public void addMessageToListMessage(String message) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        listMessage.getItems().add(message + " : " + dateFormat.format(date));
    }

    @FXML
    void addPerson() {
        TextField newPerson = new TextField();
        StackPane secondaryLayout = new StackPane();

        Button button = new Button();
        button.setText("Добавить");

        button.setOnAction(event -> {
            String message = newPerson.getText();
            if(!message.isBlank()){
                listPerson.getItems().add(newPerson.getText());
            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Ошибка ввода данных");
                alert.setContentText("У участника должно быть имя");
                alert.show();
            }
            newPerson.clear();
        });

        StackPane.setAlignment(newPerson, Pos.TOP_CENTER);
        StackPane.setAlignment(button, Pos.BOTTOM_CENTER);

        secondaryLayout.getChildren().add(newPerson);
        secondaryLayout.getChildren().add(button);

        Scene secondScene = new Scene(secondaryLayout, 300, 100);

        Stage newWindow = new Stage();
        newWindow.setTitle("Добавить участника");
        newWindow.setScene(secondScene);

        newWindow.initModality(Modality.APPLICATION_MODAL);
        newWindow.show();
    }

    @FXML
    void exit() {
        System.exit(0);
    }

    @FXML
    void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Homework 4");
        alert.setContentText("Chat");
        alert.show();
    }

    public void setNetwork(Network network) {
        this.network = network;
    }
}
