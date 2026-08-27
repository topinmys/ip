import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Saves and loads the current task list on the hard disk.
 *
 * <p>The file is rewritten after every successful task-list mutation so that it
 * always reflects the current in-memory list.</p>
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

    /**
     * Loads tasks from the configured task file.
     *
     * @return the tasks stored on disk, or an empty list if the file does not exist
     * @throws ShaiException if the file cannot be read or contains invalid data
     */
    public static List<Task> loadTasks() throws ShaiException {
        if (!Files.exists(TASK_FILE)) {
            return new ArrayList<>();
        }

        try {
            List<Task> tasks = new ArrayList<>();
            for (String line : Files.readAllLines(TASK_FILE, StandardCharsets.UTF_8)) {
                if (!line.isBlank()) {
                    tasks.add(parseTask(line));
                }
            }
            return tasks;
        } catch (IOException e) {
            throw new ShaiException("I couldn't load your tasks from disk.");
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

    /** Parses one persisted task line. */
    private static Task parseTask(String line) throws ShaiException {
        String[] fields = line.split("\\s*\\|\\s*", -1);
        if (fields.length < 3) {
            throw new ShaiException("I couldn't load your tasks from disk.");
        }

        String type = fields[0];
        String status = fields[1];
        String description = fields[2];
        if (description.isBlank() || !(status.equals("0") || status.equals("1"))) {
            throw new ShaiException("I couldn't load your tasks from disk.");
        }

        Task task;
        switch (type) {
            case "T":
                if (fields.length != 3) {
                    throw new ShaiException("I couldn't load your tasks from disk.");
                }
                task = new ToDo(description);
                break;
            case "D":
                if (fields.length != 4 || fields[3].isBlank()) {
                    throw new ShaiException("I couldn't load your tasks from disk.");
                }
                task = new Deadline(description, fields[3]);
                break;
            case "E":
                if (fields.length != 5 || fields[3].isBlank() || fields[4].isBlank()) {
                    throw new ShaiException("I couldn't load your tasks from disk.");
                }
                task = new Event(description, fields[3], fields[4]);
                break;
            default:
                throw new ShaiException("I couldn't load your tasks from disk.");
        }

        if (status.equals("1")) {
            task.markAsDone();
        }
        return task;
    }
}
