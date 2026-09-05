package shai.ui;

import java.io.InputStream;
import java.io.PrintStream;
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
    /** Destination for user-facing messages. */
    private final PrintStream output;

    /** Source of commands for the console interface. */
    private final Scanner scanner;

    /** Creates a user interface that reads commands from standard input. */
    public Ui() {
        this(System.out, System.in);
    }

    /**
     * Creates an output-only user interface for non-console clients.
     *
     * @param output destination for user-facing messages
     */
    public Ui(PrintStream output) {
        this(output, InputStream.nullInputStream());
    }

    private Ui(PrintStream output, InputStream input) {
        this.output = output;
        scanner = new Scanner(input);
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

        output.println(banner);
    }

    /** Prints the separator before processing a command. */
    public void showCommandStart() {
        output.println(LINE);
    }

    /** Prints the separator after processing a command. */
    public void showCommandEnd() {
        output.println(LINE + "\n");
    }

    /** Prints an error encountered while loading tasks. */
    public void showLoadingError(ShaiException exception) {
        showError(exception);
    }

    /** Prints an error produced while processing a command. */
    public void showError(ShaiException exception) {
        output.println("\t" + exception.getMessage());
    }

    /** Prints the response for the {@code bye} command. */
    public void showGoodbye() {
        output.println("\tSay less. Stay blessed, peace!");
    }

    /** Prints every task currently stored in the task list. */
    public void showTasks(TaskList tasks) {
        output.println("\tHere are the tasks in your list:");
        for (int i = 1; i <= tasks.size(); i++) {
            output.println("\t" + i + "." + tasks.get(i - 1));
        }
    }

    /** Prints every task whose description contains the requested keyword. */
    public void showMatchingTasks(TaskList tasks) {
        output.println("\tHere are the matching tasks in your list:");
        for (int i = 1; i <= tasks.size(); i++) {
            output.println("\t" + i + "." + tasks.get(i - 1));
        }
    }

    /** Prints the response for marking a task as done. */
    public void showMarked(Task task) {
        output.println("\tNice! I've marked this task as done:");
        output.println("\t  " + task);
    }

    /** Prints the response for marking a task as not done. */
    public void showUnmarked(Task task) {
        output.println("\tOK, I've marked this task as not done yet:");
        output.println("\t  " + task);
    }

    /** Prints the response for deleting a task. */
    public void showDeleted(Task task, int remainingTaskCount) {
        output.println("\tNoted. I've removed this task:");
        output.println("\t  " + task);
        output.println("\tNow you have " + remainingTaskCount + " tasks in the list.");
    }

    /** Prints the response for adding a task. */
    public void showAdded(Task task, int taskCount) {
        output.println("\tGot it. I've added this task:");
        output.println("\t  " + task);
        output.println("\tNow you have " + taskCount + " tasks in the list.");
    }
}
