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

## Test Case 2: Add and list a task

- Aim: Verify that a normal command is added as an incomplete task and appears in the task list.
- Command: `java -cp _temp\test-ui\classes Shai`
- Inputs:

  ```text
  buy milk
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
  	added: buy milk
  	____________________________________________________________

  	____________________________________________________________
  	Here are the tasks in your list:
  	1.[ ] buy milk
  	____________________________________________________________

  	____________________________________________________________
  	Say less. Stay blessed, peace!
  	____________________________________________________________

  ```

## Test Case 3: Mark and unmark a task

- Aim: Verify that `mark 1` changes the first task to done and `unmark 1` changes it back to not done.
- Command: `java -cp _temp\test-ui\classes Shai`
- Inputs:

  ```text
  submit report
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
  	added: submit report
  	____________________________________________________________

  	____________________________________________________________
  	Nice! I've marked this task as done:
  	  [X] submit report
  	____________________________________________________________

  	____________________________________________________________
  	OK, I've marked this task as not done yet:
  	  [ ] submit report
  	____________________________________________________________

  	____________________________________________________________
  	Here are the tasks in your list:
  	1.[ ] submit report
  	____________________________________________________________

  	____________________________________________________________
  	Say less. Stay blessed, peace!
  	____________________________________________________________

  ```
