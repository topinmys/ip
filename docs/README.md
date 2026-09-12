# Shai User Guide

Shai helps you keep track of ToDos, deadlines, and events. Commands are entered
in the CLI or sent through the graphical interface.

## Adding tasks

Add a ToDo without a date:

```text
todo buy milk
```

Add a deadline:

```text
deadline submit report /by 2026-09-15 1700
```

Add an event:

```text
event team meeting /from 2026-09-18 1400 /to 2026-09-18 1600
```

Deadlines and events receive a reminder one day before their deadline or start
time by default.

Reminders are shown when you run the `remind` command. Shai also displays an
automatic alert at startup and after successful commands when a reminder is due
within the next 24 hours. Each automatic alert is shown only once per Shai
session. This Phase 1 feature does not run a background notification service.

## Managing reminders

List reminders scheduled within the next seven days:

```text
remind
```

Reminders are listed chronologically. Completed, disabled, past, and more
distant reminders are not shown.

Configure a reminder using minutes (`m`), hours (`h`), or days (`d`):

```text
remind 1 /before 30m
remind 2 /before 2h
remind 3 /before 1d
```

Use `0m` to set the reminder at the deadline or event start time:

```text
remind 1 /before 0m
```

Disable a reminder with:

```text
remind 1 /off
```

Only deadlines and events can have reminders. Reminder settings are saved with
the task list and restored the next time Shai starts.

## Other commands

```text
list
find <keyword>
mark <task number>
unmark <task number>
delete <task number>
bye
```
