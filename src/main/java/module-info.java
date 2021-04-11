module client {
    requires java.sql;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;

    opens client.controllers to javafx.fxml;
    exports client;
}