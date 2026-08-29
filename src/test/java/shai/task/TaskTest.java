package shai.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests the core state and formatting behavior shared by all task types. */
class TaskTest {
    @Test
    void constructor_descriptionProvided_startsIncompleteWithDescription() {
        Task task = new Task("submit report");

        assertEquals("submit report", task.getDescription());
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void markAsDone_incompleteTask_becomesCompleted() {
        Task task = new Task("submit report");

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] submit report", task.toString());
    }

    @Test
    void unmark_completedTask_becomesIncomplete() {
        Task task = new Task("submit report");
        task.markAsDone();

        task.unmark();

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] submit report", task.toString());
    }
}
