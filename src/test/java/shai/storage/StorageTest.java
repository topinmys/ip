package shai.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import shai.exception.ShaiException;
import shai.task.Deadline;
import shai.task.Event;
import shai.task.TaskList;
import shai.task.ToDo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests persistence, escaping, restoration, and malformed-data handling. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void loadTasks_missingFile_returnsEmptyTaskList() throws ShaiException {
        Storage storage = storage();

        TaskList tasks = storage.loadTasks();

        assertEquals(0, tasks.size());
    }

    @Test
    void saveAndLoadTasks_allTaskTypes_roundTripsFieldsAndStatus() throws ShaiException {
        TaskList original = new TaskList();
        ToDo toDo = new ToDo("review | draft \\ backup");
        toDo.markAsDone();
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        Event event = new Event("team meeting", LocalDateTime.of(2019, 12, 3, 14, 0),
                LocalDateTime.of(2019, 12, 3, 16, 0));
        original.add(toDo);
        original.add(deadline);
        original.add(event);

        Storage storage = storage();
        storage.saveTasks(original);
        TaskList loaded = storage.loadTasks();

        assertEquals(3, loaded.size());
        assertEquals(toDo.toString(), loaded.get(0).toString());
        assertTrue(loaded.get(0).isDone());
        assertEquals(deadline.toString(), loaded.get(1).toString());
        assertEquals(event.toString(), loaded.get(2).toString());
        assertFalse(loaded.get(1).isDone());
        assertFalse(loaded.get(2).isDone());
    }

    @Test
    void loadTasks_malformedLine_throwsLineSpecificError() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "X | 0 | invalid task\n");

        ShaiException exception = assertThrows(ShaiException.class,
                () -> new Storage(file.toString()).loadTasks());

        assertEquals("I couldn't load your tasks from disk (line 1).", exception.getMessage());
    }

    @Test
    void saveTasks_nullTaskList_throwsUsefulError() {
        ShaiException exception = assertThrows(ShaiException.class,
                () -> storage().saveTasks(null));

        assertEquals("I couldn't save your tasks to disk.", exception.getMessage());
    }

    private Storage storage() {
        return new Storage(temporaryDirectory.resolve("tasks.txt").toString());
    }
}
