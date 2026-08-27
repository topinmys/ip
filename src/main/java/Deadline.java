/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /** The date or time by which the task should be completed. */
    protected String by;

    /**
     * Creates a Deadline that is initially not done.
     *
     * @param description the text describing the Deadline
     * @param by the date or time by which the task should be completed
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Gets the date or time by which this task should be completed.
     *
     * @return the deadline value
     */
    public String getBy() {
        return by;
    }

    /**
     * Returns the Deadline with its type, completion status, and due time.
     *
     * @return the formatted Deadline description
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
