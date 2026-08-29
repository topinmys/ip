package shai.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import shai.command.AddCommand;
import shai.command.Command;
import shai.command.DeleteCommand;
import shai.command.ExitCommand;
import shai.command.ListCommand;
import shai.command.MarkCommand;
import shai.command.UnmarkCommand;
import shai.exception.ShaiException;
import shai.task.Deadline;
import shai.task.Event;
import shai.task.ToDo;

/**
 * Converts raw user input into executable commands for Shai.
 *
 * <p>Parsing and validation errors are reported as {@link ShaiException}s so
 * that the caller can display them without knowing the parsing details.</p>
 */
public class Parser {
    /**
     * Parses and validates a raw command.
     *
     * @param input raw user input
     * @param taskCount current number of tasks, used to validate task numbers
     * @return an executable command
     * @throws ShaiException if the command is unknown or malformed
     */
    public Command parse(String input, int taskCount) throws ShaiException {
        String command = input.trim();
        if (command.equals("bye")) {
            return new ExitCommand();
        } else if (command.equals("list")) {
            return new ListCommand();
        } else if (isCommand(command, "mark")) {
            return new MarkCommand(parseTaskIndex(command, "mark", taskCount));
        } else if (isCommand(command, "unmark")) {
            return new UnmarkCommand(parseTaskIndex(command, "unmark", taskCount));
        } else if (isCommand(command, "todo")) {
            return parseToDo(command);
        } else if (isCommand(command, "deadline")) {
            return parseDeadline(command);
        } else if (isCommand(command, "event")) {
            return parseEvent(command);
        } else if (isCommand(command, "delete")) {
            return new DeleteCommand(parseTaskIndex(command, "delete", taskCount));
        }
        throw new ShaiException("Ayy, I don't know that command yet.");
    }

    /** Parses a ToDo command and extracts its description. */
    private static Command parseToDo(String command) throws ShaiException {
        String description = command.substring("todo".length()).trim();
        requireNonEmpty(description, "Hold up - I need a description for that todo.");
        return new AddCommand(new ToDo(description));
    }

    /** Parses a Deadline command and extracts its description and due date. */
    private static Command parseDeadline(String command) throws ShaiException {
        int indexBy = command.indexOf("/by");
        if (indexBy < 0) {
            throw new ShaiException("A deadline needs a date after /by. Try: deadline submit report /by 2019-12-01.");
        }
        String description = command.substring("deadline".length(), indexBy).trim();
        String byText = command.substring(indexBy + 3).trim();
        requireNonEmpty(description, "Hold up - I need a description for that deadline.");
        requireNonEmpty(byText, "Hold up - I need a date after /by.");
        return new AddCommand(new Deadline(description, parseDateTime(byText)));
    }

    /** Parses an Event command and extracts its description and time range. */
    private static Command parseEvent(String command) throws ShaiException {
        int indexFrom = command.indexOf("/from");
        int indexTo = command.indexOf("/to");
        if (indexFrom < 0 || indexTo < 0 || indexFrom >= indexTo) {
            throw new ShaiException(
                    "An event needs /from and /to times. Try: event meeting /from 2019-12-01 1400 "
                            + "/to 2019-12-01 1600.");
        }
        String description = command.substring("event".length(), indexFrom).trim();
        String fromText = command.substring(indexFrom + 5, indexTo).trim();
        String toText = command.substring(indexTo + 3).trim();
        requireNonEmpty(description, "Hold up - I need a description for that event.");
        requireNonEmpty(fromText, "Hold up - I need a starting time after /from.");
        requireNonEmpty(toText, "Hold up - I need an ending time after /to.");
        return new AddCommand(new Event(description, parseDateTime(fromText), parseDateTime(toText)));
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

    /** Parses and validates a one-based task number into a zero-based index. */
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
}
