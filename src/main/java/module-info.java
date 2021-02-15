module client {
    requires javafx.controls;
    requires javafx.fxml;

    opens client.controllers to javafx.fxml;
    exports client;
}