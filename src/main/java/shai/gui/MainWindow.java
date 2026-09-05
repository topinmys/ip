package shai.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shai.Shai;

/** Controller for the main Shai GUI window. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Shai shai;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/gj.jpg"));
    private Image shaiImage = new Image(this.getClass().getResourceAsStream("/images/shai.jpg"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Shai instance used to process GUI commands. */
    public void setShai(Shai s) {
        shai = s;
    }

    /** Adds Shai's introductory message to the conversation. */
    public void showGreeting() {
        dialogContainer.getChildren().add(DialogBox.getShaiDialog(shai.getGreeting(), shaiImage));
    }

    /**
     * Displays the user's input and Shai's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = shai.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getShaiDialog(response, shaiImage)
        );
        userInput.clear();

        if (input.trim().equals("bye")) {
            Stage stage = (Stage) userInput.getScene().getWindow();
            stage.close();
        }
    }
}
