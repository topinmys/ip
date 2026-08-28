/**
 * A command that removes one task from the task list.
 */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /** Creates a delete command for a zero-based task index. */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /** Removes the selected task, reports the new count, and persists the list. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ShaiException {
        Task task = tasks.remove(taskIndex);
        ui.showDeleted(task, tasks.size());
        storage.saveTasks(tasks);
    }
}
