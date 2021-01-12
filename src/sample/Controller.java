package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

    @FXML
    private MenuItem addMenu;

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
    void addPerson() {
        TextField newPerson = new TextField();
        StackPane secondaryLayout = new StackPane();

        Button button = new Button();
        button.setText("Добавить");

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
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
            }
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
}
