/**
 * A simple command-line chatbot that echoes commands until the user says goodbye.
 */
public class Shai {
    /** Handles all console input and output. */
    private final Ui ui;

    /** Converts raw user input into structured commands. */
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

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showCommandStart();
            try {
                Parser.ParsedCommand command = parser.parse(input, tasks.size());
                switch (command.getType()) {
                case BYE:
                    ui.showGoodbye();
                    return;
                case LIST:
                    ui.showTasks(tasks);
                    break;
                case MARK:
                    markTask(command);
                    break;
                case UNMARK:
                    unmarkTask(command);
                    break;
                case TODO:
                    addTask(new ToDo(command.getDescription()));
                    break;
                case DEADLINE:
                    addTask(new Deadline(command.getDescription(), command.getFirstDate()));
                    break;
                case EVENT:
                    addTask(new Event(command.getDescription(), command.getFirstDate(),
                            command.getSecondDate()));
                    break;
                case DELETE:
                    deleteTask(command);
                    break;
                default:
                    throw new ShaiException("Ayy, I don't know that command yet.");
                }
            } catch (ShaiException e) {
                ui.showError(e);
            } finally {
                ui.showCommandEnd();
            }
        }
    }

    /** Marks the selected task as done. */
    private void markTask(Parser.ParsedCommand command) throws ShaiException {
        Task task = tasks.get(command.getTaskIndex());
        task.markAsDone();
        ui.showMarked(task);
        storage.saveTasks(tasks);
    }

    /** Marks the selected task as not done. */
    private void unmarkTask(Parser.ParsedCommand command) throws ShaiException {
        Task task = tasks.get(command.getTaskIndex());
        task.unmark();
        ui.showUnmarked(task);
        storage.saveTasks(tasks);
    }

    /** Deletes the selected task and reports the updated task count. */
    private void deleteTask(Parser.ParsedCommand command) throws ShaiException {
        Task task = tasks.remove(command.getTaskIndex());
        ui.showDeleted(task, tasks.size());
        storage.saveTasks(tasks);
    }

    /** Adds a task, reports the updated task count, and persists the list. */
    private void addTask(Task task) throws ShaiException {
        tasks.add(task);
        ui.showAdded(task, tasks.size());
        storage.saveTasks(tasks);
    }

    /** Starts Shai with its default task-data file. */
    public static void main(String[] args) {
        new Shai("data/shai.txt").run();
    }
}
