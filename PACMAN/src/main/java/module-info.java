module com.example.pacman {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;
    requires java.desktop;

    opens com.example.pacman to javafx.fxml;
    exports com.example.pacman;
}