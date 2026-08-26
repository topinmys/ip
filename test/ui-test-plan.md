# Shai UI Test Plan

This plan describes black-box tests for the `Shai` command-line interface.

## Test-session setup

- Java version: 25
- Entry point: `src/main/java/Shai.java`
- Compile command:

  ```powershell
  New-Item -ItemType Directory -Force _temp\test-ui\classes | Out-Null
  javac -d _temp\test-ui\classes src\main\java\*.java
  ```

- Test command for each case:

  ```powershell
  java -cp _temp\test-ui\classes Shai
  ```

- Output comparison: exact, after normalizing CRLF/LF line endings only.
- Isolation: run every test case in a fresh process.
- Failure policy: stop immediately at the first failed test case.

## Test Case 1: Exit immediately

- Aim: Verify that Shai displays its greeting and exits cleanly when the user enters `bye`.
- Command: `java -cp _temp\test-ui\classes Shai`
- Inputs:

  ```text
  bye
  ```

- Expected output:

  ```text
  	____________________________________________________________
  	  ____  _           _
  	 / ___|| |__   __ _(_)
  	 \___ \| '_ \ / _` | |
  	  ___) | | | | (_| | |
  	 |____/|_| |_|\__,_|_|
  	Yo, what's good. I'm Shai.
  	Drop the word, I gotchu.
  	____________________________________________________________

  	____________________________________________________________
  	Say less. Stay blessed, peace!
  	____________________________________________________________

  ```

## Test Case 2: Add and list a ToDo

- Aim: Verify that a `todo` command adds an incomplete ToDo and that it appears in the task list.
- Command: `java -cp _temp\test-ui\classes Shai`
- Inputs:

  ```text
  todo buy milk
  list
  bye
  ```

- Expected output:

  ```text
  	____________________________________________________________
  	  ____  _           _
  	 / ___|| |__   __ _(_)
  	 \___ \| '_ \ / _` | |
  	  ___) | | | | (_| | |
  	 |____/|_| |_|\__,_|_|
  	Yo, what's good. I'm Shai.
	Drop the word, I gotchu.
	____________________________________________________________

	____________________________________________________________
	Got it. I've added this task:
	  [T][ ] buy milk
	Now you have 1 tasks in the list.
	____________________________________________________________

	____________________________________________________________
	Here are the tasks in your list:
	1.[T][ ] buy milk
	____________________________________________________________

  	____________________________________________________________
  	Say less. Stay blessed, peace!
  	____________________________________________________________

  ```

## Test Case 3: Mark and unmark a ToDo

- Aim: Verify that `mark 1` changes a ToDo to done and `unmark 1` changes it back to not done.
- Command: `java -cp _temp\test-ui\classes Shai`
- Inputs:

  ```text
  todo submit report
  mark 1
  unmark 1
  list
  bye
  ```

- Expected output:

  ```text
  	____________________________________________________________
  	  ____  _           _
  	 / ___|| |__   __ _(_)
  	 \___ \| '_ \ / _` | |
  	  ___) | | | | (_| | |
  	 |____/|_| |_|\__,_|_|
  	Yo, what's good. I'm Shai.
	Drop the word, I gotchu.
	____________________________________________________________

	____________________________________________________________
	Got it. I've added this task:
	  [T][ ] submit report
	Now you have 1 tasks in the list.
	____________________________________________________________

  	____________________________________________________________
  	Nice! I've marked this task as done:
	  [T][X] submit report
  	____________________________________________________________

  	____________________________________________________________
  	OK, I've marked this task as not done yet:
	  [T][ ] submit report
  	____________________________________________________________

  	____________________________________________________________
  	Here are the tasks in your list:
	1.[T][ ] submit report
  	____________________________________________________________

  	____________________________________________________________
  	Say less. Stay blessed, peace!
	____________________________________________________________

  ```

## Test Case 4: Add a Deadline

- Aim: Verify that a `deadline` command stores and displays its description and `by` value.
- Command: `java -cp _temp\test-ui\classes Shai`
- Inputs:

  ```text
  deadline return book /by Sunday
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, what's good. I'm Shai.
	Drop the word, I gotchu.
	____________________________________________________________

	____________________________________________________________
	Got it. I've added this task:
	  [D][ ] return book (by: Sunday)
	Now you have 1 tasks in the list.
	____________________________________________________________

	____________________________________________________________
	Say less. Stay blessed, peace!
	____________________________________________________________

  ```

## Test Case 5: Add an Event

- Aim: Verify that an `event` command stores and displays its description, `from` value, and `to` value.
- Command: `java -cp _temp\test-ui\classes Shai`
- Inputs:

  ```text
  event project meeting /from Mon 2pm /to 4pm
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, what's good. I'm Shai.
	Drop the word, I gotchu.
	____________________________________________________________

	____________________________________________________________
	Got it. I've added this task:
	  [E][ ] project meeting (from: Mon 2pm to: 4pm)
	Now you have 1 tasks in the list.
	____________________________________________________________

	____________________________________________________________
	Say less. Stay blessed, peace!
	____________________________________________________________

  ```

## Test Case 6: Reject an empty ToDo description

- Aim: Verify that Shai reports an error for a `todo` command without a description and continues accepting commands.
- Command: `java -cp _temp\test-ui\classes Shai`
- Inputs:

  ```text
  todo
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, what's good. I'm Shai.
	Drop the word, I gotchu.
	____________________________________________________________

	____________________________________________________________
	Hold up - I need a description for that todo.
	____________________________________________________________

	____________________________________________________________
	Say less. Stay blessed, peace!
	____________________________________________________________

  ```

## Test Case 7: Reject malformed commands

- Aim: Verify that Shai reports specific errors for malformed deadlines and events, unknown commands, and invalid task numbers.
- Command: `java -cp _temp\test-ui\classes Shai`
- Inputs:

  ```text
  deadline submit report
  deadline /by Friday
  event team sync /from 2pm
  event /from 2pm /to 4pm
  blah
  mark abc
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, what's good. I'm Shai.
	Drop the word, I gotchu.
	____________________________________________________________

	____________________________________________________________
	A deadline needs a date after /by. Try: deadline submit report /by Friday.
	____________________________________________________________

	____________________________________________________________
	Hold up - I need a description for that deadline.
	____________________________________________________________

	____________________________________________________________
	An event needs /from and /to times. Try: event meeting /from 2pm /to 4pm.
	____________________________________________________________

	____________________________________________________________
	Hold up - I need a description for that event.
	____________________________________________________________

	____________________________________________________________
	Ayy, I don't know that command yet.
	____________________________________________________________

	____________________________________________________________
	The task number after mark must be a whole number.
	____________________________________________________________

	____________________________________________________________
	Say less. Stay blessed, peace!
	____________________________________________________________

  ```

## Test Case 8: Delete a middle task

- Aim: Verify that deleting a task removes the selected task and re-numbers the remaining tasks.
- Command: `java -cp _temp\test-ui\classes Shai`
- Inputs:

  ```text
  todo alpha
  todo beta
  todo gamma
  delete 2
  list
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, what's good. I'm Shai.
	Drop the word, I gotchu.
	____________________________________________________________

	____________________________________________________________
	Got it. I've added this task:
	  [T][ ] alpha
	Now you have 1 tasks in the list.
	____________________________________________________________

	____________________________________________________________
	Got it. I've added this task:
	  [T][ ] beta
	Now you have 2 tasks in the list.
	____________________________________________________________

	____________________________________________________________
	Got it. I've added this task:
	  [T][ ] gamma
	Now you have 3 tasks in the list.
	____________________________________________________________

	____________________________________________________________
	Noted. I've removed this task:
	  [T][ ] beta
	Now you have 2 tasks in the list.
	____________________________________________________________

	____________________________________________________________
	Here are the tasks in your list:
	1.[T][ ] alpha
	2.[T][ ] gamma
	____________________________________________________________

	____________________________________________________________
	Say less. Stay blessed, peace!
	____________________________________________________________

  ```

## Test Case 9: Reject invalid delete commands

- Aim: Verify that delete reports errors for a missing number, a non-numeric number, and an out-of-range number without changing the list.
- Command: `java -cp _temp\test-ui\classes Shai`
- Inputs:

  ```text
  todo alpha
  delete
  delete abc
  delete 2
  list
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, what's good. I'm Shai.
	Drop the word, I gotchu.
	____________________________________________________________

	____________________________________________________________
	Got it. I've added this task:
	  [T][ ] alpha
	Now you have 1 tasks in the list.
	____________________________________________________________

	____________________________________________________________
	Please provide a task number after delete.
	____________________________________________________________

	____________________________________________________________
	The task number after delete must be a whole number.
	____________________________________________________________

	____________________________________________________________
	That task number is not in your list yet.
	____________________________________________________________

	____________________________________________________________
	Here are the tasks in your list:
	1.[T][ ] alpha
	____________________________________________________________

	____________________________________________________________
	Say less. Stay blessed, peace!
	____________________________________________________________

  ```

## Test Case 10: Valid additions survive invalid input

- Aim: Verify that rejected ToDo, deadline, and event commands do not alter tasks added by valid commands.
- Command: `java -cp _temp\test-ui\classes Shai`
- Inputs:

  ```text
  todo alpha
  todo
  deadline beta /by Friday
  deadline report /by
  event planning /from 2pm /to 3pm
  event review /from /to 4pm
  list
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, what's good. I'm Shai.
	Drop the word, I gotchu.
	____________________________________________________________

	____________________________________________________________
	Got it. I've added this task:
	  [T][ ] alpha
	Now you have 1 tasks in the list.
	____________________________________________________________

	____________________________________________________________
	Hold up - I need a description for that todo.
	____________________________________________________________

	____________________________________________________________
	Got it. I've added this task:
	  [D][ ] beta (by: Friday)
	Now you have 2 tasks in the list.
	____________________________________________________________

	____________________________________________________________
	Hold up - I need a date after /by.
	____________________________________________________________

	____________________________________________________________
	Got it. I've added this task:
	  [E][ ] planning (from: 2pm to: 3pm)
	Now you have 3 tasks in the list.
	____________________________________________________________

	____________________________________________________________
	Hold up - I need a starting time after /from.
	____________________________________________________________

	____________________________________________________________
	Here are the tasks in your list:
	1.[T][ ] alpha
	2.[D][ ] beta (by: Friday)
	3.[E][ ] planning (from: 2pm to: 3pm)
	____________________________________________________________

	____________________________________________________________
	Say less. Stay blessed, peace!
	____________________________________________________________

  ```

## Test Case 11: Task actions preserve state after invalid indexes

- Aim: Verify that an invalid mark or delete number does not change a task's status or remove it.
- Command: `java -cp _temp\test-ui\classes Shai`
- Inputs:

  ```text
  todo alpha
  mark 1
  mark 2
  list
  delete 0
  list
  delete 1
  list
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, what's good. I'm Shai.
	Drop the word, I gotchu.
	____________________________________________________________

	____________________________________________________________
	Got it. I've added this task:
	  [T][ ] alpha
	Now you have 1 tasks in the list.
	____________________________________________________________

	____________________________________________________________
	Nice! I've marked this task as done:
	  [T][X] alpha
	____________________________________________________________

	____________________________________________________________
	That task number is not in your list yet.
	____________________________________________________________

	____________________________________________________________
	Here are the tasks in your list:
	1.[T][X] alpha
	____________________________________________________________

	____________________________________________________________
	That task number is not in your list yet.
	____________________________________________________________

	____________________________________________________________
	Here are the tasks in your list:
	1.[T][X] alpha
	____________________________________________________________

	____________________________________________________________
	Noted. I've removed this task:
	  [T][X] alpha
	Now you have 0 tasks in the list.
	____________________________________________________________

	____________________________________________________________
	Here are the tasks in your list:
	____________________________________________________________

	____________________________________________________________
	Say less. Stay blessed, peace!
	____________________________________________________________

  ```
