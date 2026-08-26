import java.util.Scanner;

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
        int taskCount = 0;
        Task[] tasks = new Task[100];

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
                    listTasks(tasks, taskCount);
                } else if (isCommand(command, "mark")) {
                    markTask(command, tasks, taskCount);
                } else if (isCommand(command, "unmark")) {
                    unmarkTask(command, tasks, taskCount);
                } else if (isCommand(command, "todo")) {
                    taskCount = addToDo(command, tasks, taskCount);
                } else if (isCommand(command, "deadline")) {
                    taskCount = addDeadline(command, tasks, taskCount);
                } else if (isCommand(command, "event")) {
                    taskCount = addEvent(command, tasks, taskCount);
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
    private static void listTasks(Task[] tasks, int taskCount) {
        System.out.println("\tHere are the tasks in your list:");
        for (int i = 1; i <= taskCount; i++) {
            System.out.println("\t" + i + "." + tasks[i - 1]);
        }
    }

    /** Marks the task selected by a mark command as done. */
    private static void markTask(String command, Task[] tasks, int taskCount) throws ShaiException {
        int index = parseTaskIndex(command, "mark", taskCount);
        System.out.println("\tNice! I've marked this task as done:");
        Task task = tasks[index];
        task.markAsDone();
        System.out.println("\t  " + task);
    }

    /** Marks the task selected by an unmark command as not done. */
    private static void unmarkTask(String command, Task[] tasks, int taskCount) throws ShaiException {
        int index = parseTaskIndex(command, "unmark", taskCount);
        System.out.println("\tOK, I've marked this task as not done yet:");
        Task task = tasks[index];
        task.unmark();
        System.out.println("\t  " + task);
    }

    /** Adds a ToDo parsed from a command and returns the updated task count. */
    private static int addToDo(String command, Task[] tasks, int taskCount) throws ShaiException {
        String description = command.substring("todo".length()).trim();
        requireNonEmpty(description, "Hold up - I need a description for that todo.");
        return addTask(new ToDo(description), tasks, taskCount);
    }

    /** Adds a Deadline parsed from a command and returns the updated task count. */
    private static int addDeadline(String command, Task[] tasks, int taskCount) throws ShaiException {
        int indexBy = command.indexOf("/by");
        if (indexBy < 0) {
            throw new ShaiException("A deadline needs a date after /by. Try: deadline submit report /by Friday.");
        }
        String description = command.substring("deadline".length(), indexBy).trim();
        String by = command.substring(indexBy + 3).trim();
        requireNonEmpty(description, "Hold up - I need a description for that deadline.");
        requireNonEmpty(by, "Hold up - I need a date after /by.");
        return addTask(new Deadline(description, by), tasks, taskCount);
    }

    /** Adds an Event parsed from a command and returns the updated task count. */
    private static int addEvent(String command, Task[] tasks, int taskCount) throws ShaiException {
        int indexFrom = command.indexOf("/from");
        int indexTo = command.indexOf("/to");
        if (indexFrom < 0 || indexTo < 0 || indexFrom >= indexTo) {
            throw new ShaiException("An event needs /from and /to times. Try: event meeting /from 2pm /to 4pm.");
        }
        String description = command.substring("event".length(), indexFrom).trim();
        String from = command.substring(indexFrom + 5, indexTo).trim();
        String to = command.substring(indexTo + 3).trim();
        requireNonEmpty(description, "Hold up - I need a description for that event.");
        requireNonEmpty(from, "Hold up - I need a starting time after /from.");
        requireNonEmpty(to, "Hold up - I need an ending time after /to.");
        return addTask(new Event(description, from, to), tasks, taskCount);
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

    /** Parses and validates a one-based task number, returning its zero-based array index. */
    private static int parseTaskIndex(String command, String commandName, int taskCount)
            throws ShaiException {
        String argument = command.substring(commandName.length()).trim();
        requireNonEmpty(argument, "Please provide a task number after " + commandName + ".");

        final int oneBasedIndex;
        try {
            oneBasedIndex = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new ShaiException("The task number after " + commandName + " must be a whole number.");
        }

        if (oneBasedIndex < 1 || oneBasedIndex > taskCount) {
            throw new ShaiException("That task number is not in your list yet.");
        }
        return oneBasedIndex - 1;
    }

    /** Stores a task and prints the common confirmation message. */
    private static int addTask(Task task, Task[] tasks, int taskCount) {
        System.out.println("\tGot it. I've added this task:");
        tasks[taskCount++] = task;
        System.out.println("\t  " + task);
        System.out.println("\tNow you have " + taskCount + " tasks in the list.");
        return taskCount;
    }
}
