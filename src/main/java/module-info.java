module com.example.decryption {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.decryption to javafx.fxml;
    exports com.example.decryption;
}