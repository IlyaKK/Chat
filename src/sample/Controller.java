package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Controller {
    @FXML
    private ListView<String> listPerson;

    @FXML
    private ListView<String> listMessage;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendBtn;

    private final ObservableList<String> messageList = FXCollections.observableArrayList("привет!", "hello", "hey");

    private final ObservableList<String> personList = FXCollections.observableArrayList("Марк", "Коля", "Настя");

    @FXML
    public void initialize(){
        listMessage.setItems(messageList);
        listPerson.setItems(personList);
    }

    @FXML
    public void sendMessage() {
        String message = messageField.getText();
        if(!message.isBlank()){
            addMessageToListMessage(message);
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка ввода данных");
            alert.setContentText("Не отправляйте пустое сообщение");
            alert.show();
        }
        messageField.clear();
    }

    private void addMessageToListMessage(String message) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        ObservableList<String> name = listPerson.getSelectionModel().getSelectedItems();
        if(name.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Не выбран участник");
            alert.setContentText("Выберите имя пишущего из списка участников");
            alert.show();
        }else {
            listMessage.getItems().add(name.toString() + " " + message + " : " + dateFormat.format(date));
        }
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
}