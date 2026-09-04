package shai.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
