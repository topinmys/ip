import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * A simple command-line chatbot that echoes commands until the user says goodbye.
 */
public class Shai {
    private static final String LINE = "\t____________________________________________________________";

    /**
     * Starts Shai, displays the greeting, and processes commands from standard input.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        List<Task> tasks;
        try {
            tasks = Storage.loadTasks();
        } catch (ShaiException e) {
            System.out.println("\t" + e.getMessage());
            tasks = new ArrayList<>();
        }

        printBanner();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();
            System.out.println(LINE);
            try {
                if (command.equals("bye")) {
                    System.out.println("\tSay less. Stay blessed, peace!");
                    break;
                } else if (command.equals("list")) {
                    listTasks(tasks);
                } else if (isCommand(command, "mark")) {
                    markTask(command, tasks);
                } else if (isCommand(command, "unmark")) {
                    unmarkTask(command, tasks);
                } else if (isCommand(command, "todo")) {
                    addToDo(command, tasks);
                } else if (isCommand(command, "deadline")) {
                    addDeadline(command, tasks);
                } else if (isCommand(command, "event")) {
                    addEvent(command, tasks);
                } else if (isCommand(command, "delete")) {
                    deleteTask(command, tasks);
                } else {
                    throw new ShaiException("Ayy, I don't know that command yet.");
                }
            } catch (ShaiException e) {
                System.out.println("\t" + e.getMessage());
            } finally {
                System.out.println(LINE + "\n");
            }
        }
    }

    /** Prints the greeting shown when Shai starts. */
    private static void printBanner() {
        String banner = LINE + "\n"
                + "\t  ____  _           _\n"
                + "\t / ___|| |__   __ _(_)\n"
                + "\t \\___ \\| '_ \\ / _` | |\n"
                + "\t  ___) | | | | (_| | |\n"
                + "\t |____/|_| |_|\\__,_|_|\n"
                + "\tYo, what's good. I'm Shai.\n"
                + "\tDrop the word, I gotchu.\n"
                + LINE
                + "\n";

        System.out.println(banner);
    }

    /** Prints every task currently stored in the task list. */
    private static void listTasks(List<Task> tasks) {
        System.out.println("\tHere are the tasks in your list:");
        for (int i = 1; i <= tasks.size(); i++) {
            System.out.println("\t" + i + "." + tasks.get(i - 1));
        }
    }

    /** Marks the task selected by a mark command as done. */
    private static void markTask(String command, List<Task> tasks) throws ShaiException {
        int index = parseTaskIndex(command, "mark", tasks);
        System.out.println("\tNice! I've marked this task as done:");
        Task task = tasks.get(index);
        task.markAsDone();
        System.out.println("\t  " + task);
        Storage.saveTasks(tasks);
    }

    /** Marks the task selected by an unmark command as not done. */
    private static void unmarkTask(String command, List<Task> tasks) throws ShaiException {
        int index = parseTaskIndex(command, "unmark", tasks);
        System.out.println("\tOK, I've marked this task as not done yet:");
        Task task = tasks.get(index);
        task.unmark();
        System.out.println("\t  " + task);
        Storage.saveTasks(tasks);
    }

    /** Deletes the task selected by a delete command and reports the updated task count. */
    private static void deleteTask(String command, List<Task> tasks) throws ShaiException {
        int index = parseTaskIndex(command, "delete", tasks);
        System.out.println("\tNoted. I've removed this task:");
        System.out.println("\t  " + tasks.get(index));
        tasks.remove(index);
        System.out.println("\tNow you have " + tasks.size() + " tasks in the list.");
        Storage.saveTasks(tasks);
    }

    /** Adds a ToDo parsed from a command. */
    private static void addToDo(String command, List<Task> tasks) throws ShaiException {
        String description = command.substring("todo".length()).trim();
        requireNonEmpty(description, "Hold up - I need a description for that todo.");
        addTask(new ToDo(description), tasks);
    }

    /** Adds a Deadline parsed from a command. */
    private static void addDeadline(String command, List<Task> tasks) throws ShaiException {
        int indexBy = command.indexOf("/by");
        if (indexBy < 0) {
            throw new ShaiException("A deadline needs a date after /by. Try: deadline submit report /by 2019-12-01.");
        }
        String description = command.substring("deadline".length(), indexBy).trim();
        String byText = command.substring(indexBy + 3).trim();
        requireNonEmpty(description, "Hold up - I need a description for that deadline.");
        requireNonEmpty(byText, "Hold up - I need a date after /by.");
        LocalDateTime by = parseDateTime(byText);
        addTask(new Deadline(description, by), tasks);
    }

    /** Adds an Event parsed from a command. */
    private static void addEvent(String command, List<Task> tasks) throws ShaiException {
        int indexFrom = command.indexOf("/from");
        int indexTo = command.indexOf("/to");
        if (indexFrom < 0 || indexTo < 0 || indexFrom >= indexTo) {
            throw new ShaiException("An event needs /from and /to times. Try: event meeting /from 2019-12-01 1400 /to 2019-12-01 1600.");
        }
        String description = command.substring("event".length(), indexFrom).trim();
        String fromText = command.substring(indexFrom + 5, indexTo).trim();
        String toText = command.substring(indexTo + 3).trim();
        requireNonEmpty(description, "Hold up - I need a description for that event.");
        requireNonEmpty(fromText, "Hold up - I need a starting time after /from.");
        requireNonEmpty(toText, "Hold up - I need an ending time after /to.");
        LocalDateTime from = parseDateTime(fromText);
        LocalDateTime to = parseDateTime(toText);
        addTask(new Event(description, from, to), tasks);
    }

    /** Parses a task date and converts parser errors into a user-friendly command error. */
    private static LocalDateTime parseDateTime(String value) throws ShaiException {
        try {
            return DateTimeParser.parse(value);
        } catch (DateTimeParseException e) {
            throw new ShaiException("Invalid date/time. Use yyyy-MM-dd HHmm, for example 2019-12-02 1800.");
        }
    }

    /** Returns whether the input is a command or a command followed by arguments. */
    private static boolean isCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    /** Ensures that a required command field contains useful text. */
    private static void requireNonEmpty(String value, String message) throws ShaiException {
        if (value.trim().isEmpty()) {
            throw new ShaiException(message);
        }
    }

    /** Parses and validates a one-based task number, returning its zero-based list index. */
    private static int parseTaskIndex(String command, String commandName, List<Task> tasks)
            throws ShaiException {
        String argument = command.substring(commandName.length()).trim();
        requireNonEmpty(argument, "Please provide a task number after " + commandName + ".");

        final int oneBasedIndex;
        try {
            oneBasedIndex = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new ShaiException("The task number after " + commandName + " must be a whole number.");
        }

        if (oneBasedIndex < 1 || oneBasedIndex > tasks.size()) {
            throw new ShaiException("That task number is not in your list yet.");
        }
        return oneBasedIndex - 1;
    }

    /** Stores a task and prints the common confirmation message. */
    private static void addTask(Task task, List<Task> tasks) throws ShaiException {
        System.out.println("\tGot it. I've added this task:");
        tasks.add(task);
        System.out.println("\t  " + task);
        System.out.println("\tNow you have " + tasks.size() + " tasks in the list.");
        Storage.saveTasks(tasks);
    }
}
