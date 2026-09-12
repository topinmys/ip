package shai.command;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import shai.exception.ShaiException;
import shai.storage.Storage;
import shai.task.Deadline;
import shai.task.Event;
import shai.task.Reminder;
import shai.task.Task;
import shai.task.TaskList;
import shai.ui.Ui;

/** Lists, configures, or disables reminders for deadlines and events. */
public class ReminderCommand extends Command {
    private final int taskIndex;
    private final long reminderMinutesBefore;
    private final boolean isList;
    private final boolean isDisable;
    private final Clock clock;

    /** Creates a command that lists upcoming reminders. */
    public ReminderCommand(Clock clock) {
        assert clock != null : "The reminder clock must not be null.";
        taskIndex = -1;
        reminderMinutesBefore = Reminder.DISABLED;
        isList = true;
        isDisable = false;
        this.clock = clock;
    }

    /** Creates a command that configures one task's reminder. */
    public ReminderCommand(int taskIndex, long reminderMinutesBefore, Clock clock) {
        assert taskIndex >= 0 : "The reminder task index must not be negative.";
        assert Reminder.isValidMinutesBefore(reminderMinutesBefore)
                && reminderMinutesBefore != Reminder.DISABLED
                : "The reminder duration must be valid and enabled.";
        assert clock != null : "The reminder clock must not be null.";
        this.taskIndex = taskIndex;
        this.reminderMinutesBefore = reminderMinutesBefore;
        isList = false;
        isDisable = false;
        this.clock = clock;
    }

    /** Creates a command that disables one task's reminder. */
    public ReminderCommand(int taskIndex, Clock clock) {
        assert taskIndex >= 0 : "The reminder task index must not be negative.";
        assert clock != null : "The reminder clock must not be null.";
        this.taskIndex = taskIndex;
        reminderMinutesBefore = Reminder.DISABLED;
        isList = false;
        isDisable = true;
        this.clock = clock;
    }

    /** Executes the requested reminder operation and persists configuration changes. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ShaiException {
        if (isList) {
            ui.showReminders(tasks, currentTime());
            return;
        }

        Task task = tasks.get(taskIndex);
        if (!(task instanceof Deadline) && !(task instanceof Event)) {
            throw new ShaiException("Only deadlines and events can have reminders.");
        }

        if (isDisable) {
            setReminderMinutesBefore(task, Reminder.DISABLED);
            ui.showReminderDisabled(taskIndex + 1, task);
            storage.saveTasks(tasks);
            return;
        }

        LocalDateTime reminderTime = getTargetTime(task).minusMinutes(reminderMinutesBefore);
        if (reminderTime.isBefore(currentTime())) {
            throw new ShaiException("The reminder time must not be in the past.");
        }
        setReminderMinutesBefore(task, reminderMinutesBefore);
        ui.showReminderUpdated(taskIndex + 1, task, reminderTime);
        storage.saveTasks(tasks);
    }

    /** Returns the current clock value rounded to the supported minute precision. */
    private LocalDateTime currentTime() {
        return LocalDateTime.now(clock).truncatedTo(ChronoUnit.MINUTES);
    }

    /** Returns the target time used to calculate a task's reminder. */
    private static LocalDateTime getTargetTime(Task task) {
        if (task instanceof Deadline deadline) {
            return deadline.getBy();
        }
        return ((Event) task).getFrom();
    }

    /** Sets the reminder lead time on a supported task. */
    private static void setReminderMinutesBefore(Task task, long minutesBefore) {
        if (task instanceof Deadline deadline) {
            deadline.setReminderMinutesBefore(minutesBefore);
        } else {
            Event event = (Event) task;
            event.setReminderMinutesBefore(minutesBefore);
        }
    }
}
