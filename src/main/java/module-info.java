module client {
    requires java.sql;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;
    requires org.apache.logging.log4j;

    opens client.controllers to javafx.fxml;
    exports client;
}