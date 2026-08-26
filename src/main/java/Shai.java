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
            String command = scanner.nextLine();
            System.out.println(LINE);

            if (command.equals("bye")) {
                System.out.println("\tSay less. Stay blessed, peace!");
                System.out.println(LINE + "\n");
                break;
            }

            if (command.equals("list")) {
                listTasks(tasks, taskCount);
                continue;
            }

            if (command.startsWith("mark ")) {
                markTask(command, tasks);
                continue;
            }

            if (command.startsWith("unmark ")) {
                unmarkTask(command, tasks);
                continue;
            }

            if (command.startsWith("todo ")) {
                taskCount = addToDo(command, tasks, taskCount);
            }

            if (command.startsWith("deadline ")) {
                taskCount = addDeadline(command, tasks, taskCount);
            }

            if (command.startsWith("event ")) {
                taskCount = addEvent(command, tasks, taskCount);
            }
            System.out.println(LINE + "\n");
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
        System.out.println(LINE + "\n");
    }

    /** Marks the task selected by a mark command as done. */
    private static void markTask(String command, Task[] tasks) {
        System.out.println("\tNice! I've marked this task as done:");
        int index = Integer.parseInt(command.substring(5).trim());
        Task task = tasks[index - 1];
        task.markAsDone();
        System.out.println("\t  " + task);
        System.out.println(LINE + "\n");
    }

    /** Marks the task selected by an unmark command as not done. */
    private static void unmarkTask(String command, Task[] tasks) {
        System.out.println("\tOK, I've marked this task as not done yet:");
        int index = Integer.parseInt(command.substring(7).trim());
        Task task = tasks[index - 1];
        task.unmark();
        System.out.println("\t  " + task);
        System.out.println(LINE + "\n");
    }

    /** Adds a ToDo parsed from a command and returns the updated task count. */
    private static int addToDo(String command, Task[] tasks, int taskCount) {
        String description = command.substring(5).trim();
        return addTask(new ToDo(description), tasks, taskCount);
    }

    /** Adds a Deadline parsed from a command and returns the updated task count. */
    private static int addDeadline(String command, Task[] tasks, int taskCount) {
        int indexBy = command.indexOf("/by");
        String description = command.substring(9, indexBy).trim();
        String by = command.substring(indexBy + 3).trim();
        return addTask(new Deadline(description, by), tasks, taskCount);
    }

    /** Adds an Event parsed from a command and returns the updated task count. */
    private static int addEvent(String command, Task[] tasks, int taskCount) {
        int indexFrom = command.indexOf("/from");
        int indexTo = command.indexOf("/to");
        String description = command.substring(6, indexFrom).trim();
        String from = command.substring(indexFrom + 5, indexTo).trim();
        String to = command.substring(indexTo + 3).trim();
        return addTask(new Event(description, from, to), tasks, taskCount);
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
