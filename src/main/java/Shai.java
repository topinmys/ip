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
                System.out.println("\tHere are the tasks in your list:");
                for (int i = 1; i <= taskCount; i++){
                    System.out.println("\t" + i + "." + tasks[i-1]);
                }
                System.out.println(LINE + "\n");
                continue;
            }

            if (command.startsWith("mark ")) {
                System.out.println("\tNice! I've marked this task as done:");
                int index = Integer.parseInt(command.substring(5).trim());
                Task t = tasks[index - 1];
                t.markAsDone();
                System.out.println("\t  " + t);
                System.out.println(LINE + "\n");
                continue;
            }

            if (command.startsWith("unmark ")) {
                System.out.println("\tOK, I've marked this task as not done yet:");
                int index = Integer.parseInt(command.substring(7).trim());
                Task t = tasks[index - 1];
                t.unmark();
                System.out.println("\t  " + t);
                System.out.println(LINE + "\n");
                continue;
            }
            System.out.println("\tadded: " + command);
            Task t = new Task(command);
            tasks[taskCount++] = t;
            System.out.println(LINE + "\n");
        }
    }
}
