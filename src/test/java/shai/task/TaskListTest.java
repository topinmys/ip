package shai.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task-list storage and collection operations. */
class TaskListTest {
    @Test
    void add_task_appendsTaskAndUpdatesSize() {
        TaskList taskList = new TaskList();
        Task task = new ToDo("buy milk");

        taskList.add(task);

        assertEquals(1, taskList.size());
        assertSame(task, taskList.get(0));
    }

    @Test
    void taskList_nullTasks_reportBrokenCollectionContract() {
        assertThrows(AssertionError.class, () -> new TaskList((List<Task>) null));

        TaskList taskList = new TaskList();
        assertThrows(AssertionError.class, () -> taskList.add(null));
        assertThrows(AssertionError.class, () -> taskList.find(null));
    }

    @Test
    void remove_taskAtIndex_returnsTaskAndShiftsRemainingTasks() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        TaskList taskList = new TaskList(List.of(first, second));

        Task removed = taskList.remove(0);

        assertSame(first, removed);
        assertEquals(1, taskList.size());
        assertSame(second, taskList.get(0));
    }

    @Test
    void constructor_sourceListChanges_doesNotChangeTaskList() {
        List<Task> source = new ArrayList<>();
        source.add(new ToDo("buy milk"));

        TaskList taskList = new TaskList(source);
        source.clear();

        assertEquals(1, taskList.size());
    }

    @Test
    void iterator_removeCalled_cannotModifyTaskList() {
        TaskList taskList = new TaskList(List.of(new ToDo("buy milk")));
        Iterator<Task> iterator = taskList.iterator();
        iterator.next();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals(1, taskList.size());
    }

    @Test
    void find_keywordMatchesIgnoringCase_returnsMatchingTasksInOrder() {
        TaskList taskList = new TaskList(List.of(
                new ToDo("read book"),
                new ToDo("buy milk"),
                new ToDo("return BOOK")));

        TaskList matchingTasks = taskList.find("book");

        assertEquals(2, matchingTasks.size());
        assertEquals("read book", matchingTasks.get(0).getDescription());
        assertEquals("return BOOK", matchingTasks.get(1).getDescription());
        assertEquals(3, taskList.size());
    }

    @Test
    void find_noDescriptionMatches_returnsEmptyTaskList() {
        TaskList taskList = new TaskList(List.of(new ToDo("buy milk")));

        assertEquals(0, taskList.find("book").size());
    }

    @Test
    void findUpcomingReminders_filtersAndSortsPendingTimedTasks() {
        LocalDateTime now = LocalDateTime.of(2026, 9, 11, 12, 0);
        Deadline first = new Deadline("first", LocalDateTime.of(2026, 9, 12, 12, 0));
        Deadline outsideWindow = new Deadline("outside", LocalDateTime.of(2026, 9, 20, 12, 0));
        Event second = new Event("second", LocalDateTime.of(2026, 9, 14, 10, 0),
                LocalDateTime.of(2026, 9, 14, 11, 0));
        Deadline disabled = new Deadline("disabled", LocalDateTime.of(2026, 9, 12, 12, 0));
        disabled.setReminderMinutesBefore(Reminder.DISABLED);
        Deadline completed = new Deadline("completed", LocalDateTime.of(2026, 9, 12, 12, 0));
        completed.markAsDone();
        TaskList taskList = new TaskList(List.of(first, outsideWindow, second, disabled, completed));

        List<Reminder> reminders = taskList.findUpcomingReminders(now);

        assertEquals(2, reminders.size());
        assertEquals(0, reminders.get(0).getTaskIndex());
        assertEquals(2, reminders.get(1).getTaskIndex());
    }

    @Test
    void findRemindersDueSoon_includesMissedReminderForFutureTask() {
        LocalDateTime now = LocalDateTime.of(2026, 9, 11, 12, 0);
        Deadline missedReminder = new Deadline("missed", now.plusHours(12));
        Deadline atWindowEnd = new Deadline("at window end", now.plusDays(2));
        Deadline outsideWindow = new Deadline("outside", now.plusDays(3));
        Deadline overdue = new Deadline("overdue", now.minusMinutes(1));
        Deadline completed = new Deadline("completed", now.plusHours(6));
        completed.markAsDone();
        TaskList taskList = new TaskList(List.of(missedReminder, atWindowEnd, outsideWindow, overdue, completed));

        List<Reminder> reminders = taskList.findRemindersDueSoon(now);

        assertEquals(2, reminders.size());
        assertEquals(0, reminders.get(0).getTaskIndex());
        assertEquals(1, reminders.get(1).getTaskIndex());
    }
}
