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
