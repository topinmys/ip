import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import shai.Shai;
/**
 * Controller for the main GUI.
 */
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

    /** Injects the Shai instance */
    public void setShai(Shai s) {
        shai = s;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Shai's reply and then appends them to
     * the dialog container. Clears the user input after processing.
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
    }
}
