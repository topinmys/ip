/**
 * A simple command-line chatbot that echoes commands until the user says goodbye.
 */
public class Shai {
    /**
     * Starts Shai, displays the greeting, and processes commands from standard input.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Parser parser = new Parser();
        Storage storage = new Storage("data/shai.txt");
        TaskList tasks;
        try {
            tasks = storage.loadTasks();
        } catch (ShaiException e) {
            ui.showLoadingError(e);
            tasks = new TaskList();
        }

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
                    markTask(command, tasks, ui, storage);
                        break;
                case UNMARK:
                    unmarkTask(command, tasks, ui, storage);
                        break;
                case TODO:
                    addTask(new ToDo(command.getDescription()), tasks, ui, storage);
                        break;
                case DEADLINE:
                    addTask(new Deadline(command.getDescription(), command.getFirstDate()), tasks, ui, storage);
                        break;
                case EVENT:
                    addTask(new Event(command.getDescription(), command.getFirstDate(),
                            command.getSecondDate()), tasks, ui, storage);
                        break;
                case DELETE:
                    deleteTask(command, tasks, ui, storage);
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
    private static void markTask(Parser.ParsedCommand command, TaskList tasks, Ui ui,
            Storage storage)
            throws ShaiException {
        Task task = tasks.get(command.getTaskIndex());
        task.markAsDone();
        ui.showMarked(task);
        storage.saveTasks(tasks);
    }

    /** Marks the selected task as not done. */
    private static void unmarkTask(Parser.ParsedCommand command, TaskList tasks, Ui ui,
            Storage storage)
            throws ShaiException {
        Task task = tasks.get(command.getTaskIndex());
        task.unmark();
        ui.showUnmarked(task);
        storage.saveTasks(tasks);
    }

    /** Deletes the selected task and reports the updated task count. */
    private static void deleteTask(Parser.ParsedCommand command, TaskList tasks, Ui ui,
            Storage storage)
            throws ShaiException {
        Task task = tasks.remove(command.getTaskIndex());
        ui.showDeleted(task, tasks.size());
        storage.saveTasks(tasks);
    }

    /** Adds a task, reports the updated task count, and persists the list. */
    private static void addTask(Task task, TaskList tasks, Ui ui, Storage storage)
            throws ShaiException {
        tasks.add(task);
        ui.showAdded(task, tasks.size());
        storage.saveTasks(tasks);
    }
}
