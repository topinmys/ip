import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

/** Parses task dates and times and formats them for users and storage. */
public final class DateTimeParser {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);

    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private static final DateTimeFormatter STORAGE_FORMAT =
            strictFormatter("uuuu-MM-dd HHmm");

    private static final List<DateTimeFormatter> DATE_TIME_FORMATS = List.of(
            strictFormatter("uuuu-MM-dd HHmm"),
            strictFormatter("uuuu-MM-dd HH:mm"),
            strictFormatter("d/M/uuuu HHmm"),
            strictFormatter("d/M/uuuu HH:mm"));

    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            strictFormatter("uuuu-MM-dd"),
            strictFormatter("d/M/uuuu"));

    private DateTimeParser() {
        // Utility class; do not instantiate.
    }

    /**
     * Parses a date or date-time supplied by a user.
     *
     * <p>Date-only values are represented at the start of that date because
     * task deadlines and event boundaries use {@link LocalDateTime}.</p>
     *
     * @param value the user-supplied date or date-time
     * @return the parsed date-time
     * @throws DateTimeParseException if the value does not use a supported format
     */
    public static LocalDateTime parse(String value) throws DateTimeParseException {
        String trimmed = value == null ? "" : value.trim();
        DateTimeParseException lastException = null;

        for (DateTimeFormatter formatter : DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(trimmed, formatter);
            } catch (DateTimeParseException e) {
                lastException = e;
            }
        }

        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(trimmed, formatter).atStartOfDay();
            } catch (DateTimeParseException e) {
                lastException = e;
            }
        }

        throw new DateTimeParseException(
                "Unsupported date/time format", trimmed, 0, lastException);
    }

    /** Formats a task date or time for display in the command-line interface. */
    public static String formatForDisplay(LocalDateTime value) {
        if (value.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return value.format(DATE_DISPLAY_FORMAT);
        }
        return value.format(DISPLAY_FORMAT);
    }

    /** Formats a task date or time in the stable format used by storage. */
    public static String formatForStorage(LocalDateTime value) {
        return value.format(STORAGE_FORMAT);
    }

    private static DateTimeFormatter strictFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }
}
