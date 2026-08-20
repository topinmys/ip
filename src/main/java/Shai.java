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
        String[] tasks = new String[100];
        int taskCount = 0;

        String banner = LINE + "\n"
                + "\t  ____  _           _\n"
                + "\t / ___|| |__   __ _(_)\n"
                + "\t \\___ \\| '_ \\ / _` | |\n"
                + "\t  ___) | | | | (_| | |\n"
                + "\t |____/|_| |_|\\__,_|_|\n"
                + "\tYo, what's good. I'm Shai.\n"
                + "\tDrop the word, I gotchu.\n"
                + LINE;

        System.out.println(banner);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(LINE);

            if (command.equals("bye")) {
                System.out.println("\tSay less. Stay blessed, peace!");
                System.out.println(LINE);
                break;
            }

            if (command.equals("list")) {
                for (int i = 1; i <= taskCount; i++){
                    System.out.println("\t" + i + ". " + tasks[i-1]);
                }
                System.out.println(LINE);
                continue;
            }
            System.out.println("\tadded: " + command);
            tasks[taskCount++] = command;
            System.out.println(LINE);
        }
    }
}
