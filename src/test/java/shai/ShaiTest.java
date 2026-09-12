package shai;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import shai.storage.Storage;
import shai.task.Deadline;
import shai.task.TaskList;

/** Tests command processing through the programmatic interface used by the GUI. */
class ShaiTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getGreeting_returnsGuiFriendlyIntroduction() {
        Shai shai = new Shai(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("Yo, what's good. I'm Shai.\nDrop the word, I gotchu.", shai.getGreeting());
    }

    @Test
    void getResponse_addTodo_returnsResponseAndPersistsTask() {
        Shai shai = new Shai(temporaryDirectory.resolve("tasks.txt").toString());

        String response = shai.getResponse("todo buy milk");

        String lineSeparator = System.lineSeparator();
        assertEquals(String.join(lineSeparator,
                "Got it. I've added this task:",
                "  [T][ ] buy milk",
                "Now you have 1 tasks in the list."), response);
        assertEquals(String.join(lineSeparator,
                "Here are the tasks in your list:",
                "1.[T][ ] buy milk"), shai.getResponse("list"));
    }

    @Test
    void getResponse_findKeyword_returnsMatchingTasksOnly() {
        Shai shai = new Shai(temporaryDirectory.resolve("tasks.txt").toString());
        shai.getResponse("todo read book");
        shai.getResponse("todo buy milk");

        assertEquals(String.join(System.lineSeparator(),
                "Here are the matching tasks in your list:",
                "1.[T][ ] read book"), shai.getResponse("find BOOK"));
    }

    @Test
    void getResponse_invalidCommand_returnsUserFriendlyError() {
        Shai shai = new Shai(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("Ayy, I don't know that command yet.", shai.getResponse("unknown"));
    }

    @Test
    void getResponse_remind_listsUpcomingRemindersChronologically() {
        Clock clock = Clock.fixed(Instant.parse("2026-09-11T12:00:00Z"), ZoneId.of("UTC"));
        Shai shai = new Shai(temporaryDirectory.resolve("tasks.txt").toString(), clock);
        shai.getResponse("deadline submit report /by 2026-09-15 1700");
        shai.getResponse("event team meeting /from 2026-09-18 1400 /to 2026-09-18 1600");

        assertEquals(String.join(System.lineSeparator(),
                "Here are your upcoming reminders:",
                "1.[D][ ] submit report (by: Sep 15 2026, 5:00 PM) (reminder: Sep 14 2026, 5:00 PM)",
                "2.[E][ ] team meeting (from: Sep 18 2026, 2:00 PM to: Sep 18 2026, 4:00 PM) "
                        + "(reminder: Sep 17 2026, 2:00 PM)"), shai.getResponse("remind"));
    }

    @Test
    void getResponse_remindConfiguration_updatesAndPersistsReminder() {
        Clock clock = Clock.fixed(Instant.parse("2026-09-11T12:00:00Z"), ZoneId.of("UTC"));
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Shai shai = new Shai(taskFile.toString(), clock);
        shai.getResponse("deadline submit report /by 2026-09-15 1700");

        assertEquals(String.join(System.lineSeparator(),
                "Reminder updated for task 1:",
                "  [D][ ] submit report (by: Sep 15 2026, 5:00 PM)",
                "  I'll remind you at Sep 15 2026, 2:00 PM."),
                shai.getResponse("remind 1 /before 3h"));
        assertEquals(String.join(System.lineSeparator(),
                "Reminder disabled for task 1:",
                "  [D][ ] submit report (by: Sep 15 2026, 5:00 PM)"),
                shai.getResponse("remind 1 /off"));
        assertEquals("No reminders on the board - stay ahead of the game!", shai.getResponse("remind"));
    }

    @Test
    void getStartupReminderResponse_showsEachDueSoonReminderOnlyOnce() throws Exception {
        Clock clock = Clock.fixed(Instant.parse("2026-09-11T12:00:00Z"), ZoneId.of("UTC"));
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 9, 12, 12, 0));
        TaskList tasks = new TaskList();
        tasks.add(deadline);
        new Storage(taskFile.toString()).saveTasks(tasks);
        Shai shai = new Shai(taskFile.toString(), clock);

        assertEquals(String.join(System.lineSeparator(),
                "Reminder alert:",
                "1.[D][ ] submit report (by: Sep 12 2026, 12:00 PM) "
                        + "(reminder: Sep 11 2026, 12:00 PM)"), shai.getStartupReminderResponse());
        assertEquals("", shai.getStartupReminderResponse());
    }

    @Test
    void getResponse_dueSoonReminder_isShownAfterFirstSuccessfulCommandOnly() {
        Clock clock = Clock.fixed(Instant.parse("2026-09-11T12:00:00Z"), ZoneId.of("UTC"));
        Shai shai = new Shai(temporaryDirectory.resolve("tasks.txt").toString(), clock);

        String firstResponse = shai.getResponse("deadline submit report /by 2026-09-12 1200");

        assertEquals(String.join(System.lineSeparator(),
                "Got it. I've added this task:",
                "  [D][ ] submit report (by: Sep 12 2026, 12:00 PM)",
                "Now you have 1 tasks in the list.",
                "Reminder alert:",
                "1.[D][ ] submit report (by: Sep 12 2026, 12:00 PM) "
                        + "(reminder: Sep 11 2026, 12:00 PM)"), firstResponse);
        assertEquals(String.join(System.lineSeparator(),
                "Here are the tasks in your list:",
                "1.[D][ ] submit report (by: Sep 12 2026, 12:00 PM)"), shai.getResponse("list"));
    }
}
