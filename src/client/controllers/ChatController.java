package client.controllers;

import client.models.Network;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatController {
    @FXML
    private ListView<String> listPerson;

    @FXML
    private ListView<String> listMessage;

    @FXML
    private TextField messageField;

    private Network network;

    private final ObservableList<String> messageList = FXCollections.observableArrayList();
    private String selectedRecipient;

    @FXML
    public void initialize(){
        listPerson.setCellFactory(lv -> {
            MultipleSelectionModel<String> selectionModel = listPerson.getSelectionModel();
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                listPerson.requestFocus();
                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (selectionModel.getSelectedIndices().contains(index)) {
                        selectionModel.clearSelection(index);
                        selectedRecipient = null;
                    } else {
                        selectionModel.select(index);
                        selectedRecipient = cell.getItem();
                    }
                    event.consume();
                }
            });
            return cell;
        });
    }

    @FXML
    public void sendMessage() {
        String message = messageField.getText();
        if (!message.isBlank()) {
            try {
                network.getOut().writeUTF(message);
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
        messageField.clear();
        try {
            if (selectedRecipient != null) {
                network.sendPrivateMessage(message, selectedRecipient);
            } else {
                network.sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("!!Ошибка при отправке сообщения");
        }
    }

    public void addMessageToListMessage(String message) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        listMessage.getItems().add(message + " : " + dateFormat.format(date));
    }

    public void addPersonToListPerson(String name){
        listPerson.getItems().add(name);
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
