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
        boolean[] done = new boolean[100];

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
                    char mark = ' ';
                    if (done[i-1]) {
                        mark = 'X';
                    }
                    System.out.println("\t" + i + ".[" + mark + "] " + tasks[i-1]);
                }
                System.out.println(LINE + "\n");
                continue;
            }

            if (command.length() >= 4) {
                String cmd = command.substring(0, 4);
                if (cmd.equals("mark")) {
                    System.out.println("\tNice! I've marked this task as done:");
                    int index = Integer.valueOf(command.substring(5, 6));
                    done[index - 1] = true;
                    System.out.println("\t  [X] " + tasks[index - 1]);
                    System.out.println(LINE + "\n");
                    continue;
                }
            }
            System.out.println("\tadded: " + command);
            tasks[taskCount++] = command;
            System.out.println(LINE + "\n");
        }
    }
}
