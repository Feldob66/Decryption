module com.example.decryption {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.decryption to javafx.fxml;

    exports com.example.decryption;
    exports com.example.decryption.model;
    exports com.example.decryption.controller;
    exports com.example.decryption.util;
}
