module com.main.client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.main.client to javafx.fxml;
    exports com.main.client;
}