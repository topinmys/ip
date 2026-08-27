import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Saves the current task list to the hard disk.
 *
 * <p>Loading is intentionally not implemented yet. The file is rewritten after
 * every successful task-list mutation so that it always reflects the current
 * in-memory list.</p>
 */
public class Storage {
    private static final Path TASK_FILE = Path.of("data", "shai.txt");

    /**
     * Saves all tasks to the configured task file.
     *
     * @param tasks the current task list
     * @throws ShaiException if the directory or file cannot be written
     */
    public static void saveTasks(List<Task> tasks) throws ShaiException {
        try {
            Files.createDirectories(TASK_FILE.getParent());
            List<String> lines = tasks.stream()
                    .map(Storage::formatTask)
                    .collect(Collectors.toList());
            Files.write(TASK_FILE, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ShaiException("I couldn't save your tasks to disk.");
        }
    }

    /** Converts a task into the simple line format used by the data file. */
    private static String formatTask(Task task) {
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return "D | " + status + " | " + task.getDescription() + " | " + deadline.getBy();
        }
        if (task instanceof Event event) {
            return "E | " + status + " | " + task.getDescription()
                    + " | " + event.getFrom() + " | " + event.getTo();
        }
        return "T | " + status + " | " + task.getDescription();
    }
}
