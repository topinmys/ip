package shai.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import shai.exception.ShaiException;
import shai.parser.DateTimeParser;
import shai.task.Deadline;
import shai.task.Event;
import shai.task.Task;
import shai.task.TaskList;
import shai.task.ToDo;

/**
 * Saves and loads the current task list on the hard disk.
 *
 * <p>The file is rewritten after every successful task-list mutation so that it
 * always reflects the current in-memory list.</p>
 */
public class Storage {
    private final Path taskFile;

    /**
     * Creates storage backed by the specified file.
     *
     * @param filePath path to the task file
     */
    public Storage(String filePath) {
        taskFile = Path.of(filePath);
    }

    /**
     * Saves all tasks to the configured task file.
     *
     * @param tasks the current task list
     * @throws ShaiException if the directory or file cannot be written
     */
    public void saveTasks(TaskList tasks) throws ShaiException {
        if (tasks == null) {
            throw new ShaiException("I couldn't save your tasks to disk.");
        }

        Path temporaryFile = taskFile.resolveSibling(taskFile.getFileName() + ".tmp");
        try {
            if (taskFile.getParent() != null) {
                Files.createDirectories(taskFile.getParent());
            }
            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(formatTask(task));
            }
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            replaceTaskFile(temporaryFile);
        } catch (IOException | SecurityException | IllegalArgumentException e) {
            deleteTemporaryFile(temporaryFile);
            throw new ShaiException("I couldn't save your tasks to disk.");
        }
    }

    /**
     * Loads tasks from the configured task file.
     *
     * @return the tasks stored on disk, or an empty list if the file does not exist
     * @throws ShaiException if the file cannot be read or contains invalid data
     */
    public TaskList loadTasks() throws ShaiException {
        try {
            if (!Files.exists(taskFile)) {
                return new TaskList();
            }

            TaskList tasks = new TaskList();
            List<String> lines = Files.readAllLines(taskFile, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (!line.isBlank()) {
                    tasks.add(parseTask(line, i + 1));
                }
            }
            return tasks;
        } catch (IOException | SecurityException e) {
            throw new ShaiException("I couldn't load your tasks from disk.");
        }
    }

    /** Replaces the old task file only after the new contents have been written successfully. */
    private void replaceTaskFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, taskFile,
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, taskFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Removes a failed temporary save without hiding the original save error. */
    private static void deleteTemporaryFile(Path temporaryFile) {
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException | SecurityException ignored) {
            // The original save error is the useful message for the user.
        }
    }

    /** Converts a task into the simple line format used by the data file. */
    private static String formatTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("A task list cannot contain null tasks.");
        }
        String status = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return "D | " + status + " | " + escapeField(task.getDescription())
                    + " | " + escapeField(DateTimeParser.formatForStorage(deadline.getBy()));
        }
        if (task instanceof Event event) {
            return "E | " + status + " | " + escapeField(task.getDescription())
                    + " | " + escapeField(DateTimeParser.formatForStorage(event.getFrom()))
                    + " | " + escapeField(DateTimeParser.formatForStorage(event.getTo()));
        }
        return "T | " + status + " | " + escapeField(task.getDescription());
    }

    /** Escapes characters that have a special meaning in the task file format. */
    private static String escapeField(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Task fields cannot be null.");
        }
        return value.replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /** Parses one persisted task line. */
    private static Task parseTask(String line, int lineNumber) throws ShaiException {
        List<String> fields = splitFields(line, lineNumber);
        if (fields.size() < 3) {
            throw invalidData(lineNumber);
        }

        String type = fields.get(0);
        String status = fields.get(1);
        String description = fields.get(2);
        if (description.isBlank() || !(status.equals("0") || status.equals("1"))) {
            throw invalidData(lineNumber);
        }

        Task task;
        switch (type) {
            case "T":
                if (fields.size() != 3) {
                    throw invalidData(lineNumber);
                }
                task = new ToDo(description);
                break;
            case "D":
                if (fields.size() != 4 || fields.get(3).isBlank()) {
                    throw invalidData(lineNumber);
                }
                task = new Deadline(description, parseDateTime(fields.get(3), lineNumber));
                break;
            case "E":
                if (fields.size() != 5 || fields.get(3).isBlank() || fields.get(4).isBlank()) {
                    throw invalidData(lineNumber);
                }
                task = new Event(description, parseDateTime(fields.get(3), lineNumber),
                        parseDateTime(fields.get(4), lineNumber));
                break;
            default:
                throw invalidData(lineNumber);
        }

        if (status.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Parses a date or time restored from the task file. */
    private static LocalDateTime parseDateTime(String value, int lineNumber) throws ShaiException {
        try {
            return DateTimeParser.parse(value);
        } catch (DateTimeParseException e) {
            throw invalidData(lineNumber);
        }
    }

    /** Splits a persisted line while preserving escaped separators and special characters. */
    private static List<String> splitFields(String line, int lineNumber) throws ShaiException {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else if (character == '\\' && i + 1 < line.length()) {
                char escaped = line.charAt(i + 1);
                if (escaped == '\\' || escaped == '|' || escaped == 'n' || escaped == 'r') {
                    field.append(unescapeCharacter(escaped));
                    i++;
                } else {
                    field.append(character);
                }
            } else if (character == '\\') {
                throw invalidData(lineNumber);
            } else {
                field.append(character);
            }
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /** Converts one escaped character into its stored value. */
    private static char unescapeCharacter(char escaped) {
        switch (escaped) {
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            default:
                return escaped;
        }
    }

    /** Creates a consistent error for a malformed persisted line. */
    private static ShaiException invalidData(int lineNumber) {
        return new ShaiException("I couldn't load your tasks from disk (line " + lineNumber + ").");
    }
}
