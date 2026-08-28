/**
 * A command that displays all tasks currently in the task list.
 */
public class ListCommand extends Command {
    /** Displays the current tasks without changing application state. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasks(tasks);
    }
}
