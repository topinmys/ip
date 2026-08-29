package shai.ui;

import java.util.Scanner;
import shai.exception.ShaiException;
import shai.task.Task;
import shai.task.TaskList;

/**
 * Handles Shai's interactions with the user through the console.
 *
 * <p>The command-processing logic remains in {@link Shai}; this class keeps
 * input handling and presentation details out of that logic.</p>
 */
public class Ui {
    private static final String LINE = "\t____________________________________________________________";
    private final Scanner scanner;

    /** Creates a user interface that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Returns whether another command is available from standard input. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads and trims the next command from standard input. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Prints the greeting shown when Shai starts. */
    public void showBanner() {
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

    /** Prints the separator before processing a command. */
    public void showCommandStart() {
        System.out.println(LINE);
    }

    /** Prints the separator after processing a command. */
    public void showCommandEnd() {
        System.out.println(LINE + "\n");
    }

    /** Prints an error encountered while loading tasks. */
    public void showLoadingError(ShaiException exception) {
        showError(exception);
    }

    /** Prints an error produced while processing a command. */
    public void showError(ShaiException exception) {
        System.out.println("\t" + exception.getMessage());
    }

    /** Prints the response for the {@code bye} command. */
    public void showGoodbye() {
        System.out.println("\tSay less. Stay blessed, peace!");
    }

    /** Prints every task currently stored in the task list. */
    public void showTasks(TaskList tasks) {
        System.out.println("\tHere are the tasks in your list:");
        for (int i = 1; i <= tasks.size(); i++) {
            System.out.println("\t" + i + "." + tasks.get(i - 1));
        }
    }

    /** Prints every task whose description contains the requested keyword. */
    public void showMatchingTasks(TaskList tasks) {
        System.out.println("\tHere are the matching tasks in your list:");
        for (int i = 1; i <= tasks.size(); i++) {
            System.out.println("\t" + i + "." + tasks.get(i - 1));
        }
    }

    /** Prints the response for marking a task as done. */
    public void showMarked(Task task) {
        System.out.println("\tNice! I've marked this task as done:");
        System.out.println("\t  " + task);
    }

    /** Prints the response for marking a task as not done. */
    public void showUnmarked(Task task) {
        System.out.println("\tOK, I've marked this task as not done yet:");
        System.out.println("\t  " + task);
    }

    /** Prints the response for deleting a task. */
    public void showDeleted(Task task, int remainingTaskCount) {
        System.out.println("\tNoted. I've removed this task:");
        System.out.println("\t  " + task);
        System.out.println("\tNow you have " + remainingTaskCount + " tasks in the list.");
    }

    /** Prints the response for adding a task. */
    public void showAdded(Task task, int taskCount) {
        System.out.println("\tGot it. I've added this task:");
        System.out.println("\t  " + task);
        System.out.println("\tNow you have " + taskCount + " tasks in the list.");
    }
}
