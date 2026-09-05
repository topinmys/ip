package shai;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
}
