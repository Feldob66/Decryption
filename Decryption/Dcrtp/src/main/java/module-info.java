module com.example.dcrtp {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.dcrtp to javafx.fxml;
    exports com.example.dcrtp;
}