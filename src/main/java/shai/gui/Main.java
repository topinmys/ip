package shai.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import shai.Shai;

/** A JavaFX GUI for Shai using FXML. */
public class Main extends Application {
    private Shai shai = new Shai("data/shai.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setShai(shai);
            mainWindow.showGreeting();
            stage.setMinHeight(420);
            stage.setMinWidth(640);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
