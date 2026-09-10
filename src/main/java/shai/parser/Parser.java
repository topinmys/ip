package shai.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import shai.command.AddCommand;
import shai.command.Command;
import shai.command.DeleteCommand;
import shai.command.ExitCommand;
import shai.command.FindCommand;
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
    private static final String BYE_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String FIND_COMMAND = "find";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String DELETE_COMMAND = "delete";
    private static final String BY_MARKER = "/by";
    private static final String FROM_MARKER = "/from";
    private static final String TO_MARKER = "/to";

    /**
     * Parses and validates a raw command.
     *
     * @param input raw user input
     * @param taskCount current number of tasks, used to validate task numbers
     * @return an executable command
     * @throws ShaiException if the command is unknown or malformed
     */
    public Command parse(String input, int taskCount) throws ShaiException {
        assert input != null : "The command input must not be null.";
        assert taskCount >= 0 : "The task count must not be negative.";
        String command = input.trim();
        if (command.equals(BYE_COMMAND)) {
            return new ExitCommand();
        } else if (command.equals(LIST_COMMAND)) {
            return new ListCommand();
        } else if (isCommand(command, FIND_COMMAND)) {
            return parseFind(command);
        } else if (isCommand(command, MARK_COMMAND)) {
            return new MarkCommand(parseTaskIndex(command, MARK_COMMAND, taskCount));
        } else if (isCommand(command, UNMARK_COMMAND)) {
            return new UnmarkCommand(parseTaskIndex(command, UNMARK_COMMAND, taskCount));
        } else if (isCommand(command, TODO_COMMAND)) {
            return parseToDo(command);
        } else if (isCommand(command, DEADLINE_COMMAND)) {
            return parseDeadline(command);
        } else if (isCommand(command, EVENT_COMMAND)) {
            return parseEvent(command);
        } else if (isCommand(command, DELETE_COMMAND)) {
            return new DeleteCommand(parseTaskIndex(command, DELETE_COMMAND, taskCount));
        }
        throw new ShaiException("Ayy, I don't know that command yet.");
    }

    /** Parses a find command and extracts its keyword. */
    private static Command parseFind(String command) throws ShaiException {
        String keyword = command.substring(FIND_COMMAND.length()).trim();
        requireNonEmpty(keyword, "Please provide a keyword after " + FIND_COMMAND + ".");
        return new FindCommand(keyword);
    }

    /** Parses a ToDo command and extracts its description. */
    private static Command parseToDo(String command) throws ShaiException {
        String description = command.substring(TODO_COMMAND.length()).trim();
        requireNonEmpty(description, "Hold up - I need a description for that " + TODO_COMMAND + ".");
        return new AddCommand(new ToDo(description));
    }

    /** Parses a Deadline command and extracts its description and due date. */
    private static Command parseDeadline(String command) throws ShaiException {
        int indexBy = command.indexOf(BY_MARKER);
        if (indexBy < 0) {
            throw new ShaiException("A deadline needs a date after " + BY_MARKER + ". Try: "
                    + DEADLINE_COMMAND + " submit report " + BY_MARKER + " 2019-12-01.");
        }
        String description = command.substring(DEADLINE_COMMAND.length(), indexBy).trim();
        String byText = command.substring(indexBy + BY_MARKER.length()).trim();
        requireNonEmpty(description, "Hold up - I need a description for that " + DEADLINE_COMMAND + ".");
        requireNonEmpty(byText, "Hold up - I need a date after " + BY_MARKER + ".");
        return new AddCommand(new Deadline(description, parseDateTime(byText)));
    }

    /** Parses an Event command and extracts its description and time range. */
    private static Command parseEvent(String command) throws ShaiException {
        int indexFrom = command.indexOf(FROM_MARKER);
        int indexTo = command.indexOf(TO_MARKER);
        if (indexFrom < 0 || indexTo < 0 || indexFrom >= indexTo) {
            throw new ShaiException("An event needs " + FROM_MARKER + " and " + TO_MARKER
                    + " times. Try: " + EVENT_COMMAND + " meeting " + FROM_MARKER
                    + " 2019-12-01 1400 " + TO_MARKER + " 2019-12-01 1600.");
        }
        String description = command.substring(EVENT_COMMAND.length(), indexFrom).trim();
        String fromText = command.substring(indexFrom + FROM_MARKER.length(), indexTo).trim();
        String toText = command.substring(indexTo + TO_MARKER.length()).trim();
        requireNonEmpty(description, "Hold up - I need a description for that " + EVENT_COMMAND + ".");
        requireNonEmpty(fromText, "Hold up - I need a starting time after " + FROM_MARKER + ".");
        requireNonEmpty(toText, "Hold up - I need an ending time after " + TO_MARKER + ".");
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
        int zeroBasedIndex = oneBasedIndex - 1;
        assert zeroBasedIndex >= 0 && zeroBasedIndex < taskCount
                : "A validated task number must convert to a valid zero-based index.";
        return zeroBasedIndex;
    }
}
