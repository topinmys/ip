package shai.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Owns the tasks currently managed by Shai.
 *
 * <p>This class provides the task-list operations used by command handling so
 * callers do not need to manage the underlying collection directly.</p>
 */
public class TaskList implements Iterable<Task> {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks tasks to copy into this list
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Gets a task by its zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Removes and returns a task by its zero-based index. */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a read-only iterator over the current tasks.
     *
     * @return an iterator that cannot modify this task list
     */
    @Override
    public Iterator<Task> iterator() {
        return Collections.unmodifiableList(tasks).iterator();
    }
}
