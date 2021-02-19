module client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires sqlite.jdbc;

    opens client.controllers to javafx.fxml;
    exports client;
}