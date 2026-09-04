/**
 * Temporary response provider used by the copied JavaFX demonstration.
 *
 * <p>This class is intentionally separate from {@code shai.Shai}, which is
 * the application's command-line entry point. It can be replaced with an
 * adapter to the real command engine once the GUI layout is working.</p>
 */
public class GuiShai {
    /**
     * Generates a response for the user's chat message.
     *
     * @param input message entered in the GUI
     * @return demonstration response containing the input
     */
    public String getResponse(String input) {
        return "Shai heard: " + input;
    }
}
