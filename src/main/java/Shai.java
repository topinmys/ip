/**
 * A simple command-line chatbot that echoes commands until the user says goodbye.
 */
public class Shai {
    /** Handles all console input and output. */
    private final Ui ui;

    /** Converts raw user input into executable commands. */
    private final Parser parser;

    /** Loads and saves the task data. */
    private final Storage storage;

    /** Stores the tasks managed during this run. */
    private final TaskList tasks;

    /**
     * Creates Shai using the specified task-data file.
     *
     * @param filePath path to the file used for loading and saving tasks
     */
    public Shai(String filePath) {
        ui = new Ui();
        parser = new Parser();
        storage = new Storage(filePath);
        TaskList loadedTasks;
        try {
            loadedTasks = storage.loadTasks();
        } catch (ShaiException e) {
            ui.showLoadingError(e);
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

    /** Starts the greeting and processes commands until the user says goodbye. */
    public void run() {
        ui.showBanner();

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showCommandStart();
            try {
                Command command = parser.parse(input, tasks.size());
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (ShaiException e) {
                ui.showError(e);
            } finally {
                ui.showCommandEnd();
            }
        }
    }

    /** Starts Shai with its default task-data file. */
    public static void main(String[] args) {
        new Shai("data/shai.txt").run();
    }
}
