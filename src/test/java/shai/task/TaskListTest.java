package shai.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
