package shai.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import shai.command.AddCommand;
import shai.command.Command;
import shai.command.DeleteCommand;
import shai.command.ExitCommand;
import shai.command.FindCommand;
import shai.command.ListCommand;
import shai.command.MarkCommand;
import shai.command.UnmarkCommand;
import shai.exception.ShaiException;
import shai.storage.Storage;
import shai.task.Deadline;
import shai.task.Event;
import shai.task.TaskList;
import shai.task.ToDo;
import shai.ui.Ui;

/** Tests command selection, argument extraction, and parser validation. */
class ParserTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void parse_knownCommands_returnsMatchingCommandTypes() throws ShaiException {
        Parser parser = new Parser();

        assertInstanceOf(ExitCommand.class, parser.parse("bye", 0));
        assertInstanceOf(ListCommand.class, parser.parse("list", 0));
        assertInstanceOf(FindCommand.class, parser.parse("find book", 0));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1", 1));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 1", 1));
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1", 1));
        assertInstanceOf(AddCommand.class, parser.parse("todo buy milk", 0));
        assertInstanceOf(AddCommand.class, parser.parse("deadline return book /by 2019-12-01", 0));
        assertInstanceOf(AddCommand.class,
                parser.parse("event meeting /from 2019-12-01 1400 /to 2019-12-01 1600", 0));
    }

    @Test
    void parse_deadlineCommand_createsDeadlineWithParsedDate() throws ShaiException {
        Parser parser = new Parser();
        TaskList tasks = new TaskList();
        Command command = parser.parse("deadline return book /by 2019-12-01", 0);

        command.execute(tasks, new Ui(), storage());

        Deadline deadline = assertInstanceOf(Deadline.class, tasks.get(0));
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 1, 0, 0), deadline.getBy());
    }

    @Test
    void parse_eventCommand_createsEventWithParsedTimes() throws ShaiException {
        Parser parser = new Parser();
        TaskList tasks = new TaskList();
        Command command = parser.parse(
                "event team meeting /from 2019-12-01 1400 /to 2019-12-01 1600", 0);

        command.execute(tasks, new Ui(), storage());

        Event event = assertInstanceOf(Event.class, tasks.get(0));
        assertEquals("team meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 1, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 1, 16, 0), event.getTo());
    }

    @Test
    void parse_markCommand_usesOneBasedTaskNumber() throws ShaiException {
        Parser parser = new Parser();
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("first"));
        tasks.add(new ToDo("second"));
        Command command = parser.parse("mark 2", tasks.size());

        command.execute(tasks, new Ui(), storage());

        assertFalse(tasks.get(0).isDone());
        assertTrue(tasks.get(1).isDone());
    }

    @Test
    void parse_invalidCommands_throwsUsefulErrors() {
        Parser parser = new Parser();

        assertParseError(parser, "blah", 0, "Ayy, I don't know that command yet.");
        assertParseError(parser, "find", 0, "Please provide a keyword after find.");
        assertParseError(parser, "todo", 0, "Hold up - I need a description for that todo.");
        assertParseError(parser, "mark 2", 1, "That task number is not in your list yet.");
        assertParseError(parser, "deadline report /by 2019-02-30", 0,
                "Invalid date/time. Use yyyy-MM-dd HHmm, for example 2019-12-02 1800.");
    }

    private Storage storage() {
        return new Storage(temporaryDirectory.resolve("tasks.txt").toString());
    }

    private static void assertParseError(Parser parser, String input, int taskCount,
                                         String expectedMessage) {
        ShaiException exception = assertThrows(ShaiException.class, () -> parser.parse(input, taskCount));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
