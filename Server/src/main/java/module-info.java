module com.main.server {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.main.server to javafx.fxml;
    exports com.main.server;
}