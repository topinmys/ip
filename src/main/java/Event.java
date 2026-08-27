/**
 * Represents a task that takes place between a start and end date or time.
 */
public class Event extends Task {
    /** The date or time when the event starts. */
    protected String from;

    /** The date or time when the event ends. */
    protected String to;

    /**
     * Creates an Event that is initially not done.
     *
     * @param description the text describing the Event
     * @param from the date or time when the event starts
     * @param to the date or time when the event ends
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Gets the date or time when this event starts.
     *
     * @return the event start value
     */
    public String getFrom() {
        return from;
    }

    /**
     * Gets the date or time when this event ends.
     *
     * @return the event end value
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns the Event with its type, completion status, and time range.
     *
     * @return the formatted Event description
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
