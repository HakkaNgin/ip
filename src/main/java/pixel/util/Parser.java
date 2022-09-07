package pixel.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import pixel.Pixel;
import pixel.task.Task;

/**
 * Deals with making sense of the user command
 */
public class Parser { // inner class

    private final TaskList taskList;
    private final String filePath;

    public Parser(String filePath) {
        this.filePath = filePath;
        this.taskList = new TaskList(filePath);
    }

    enum Marking {
        MARK,
        UNMARK
    }

    private int getMarkOrUnmarkIndex(String strippedInput, Marking instruction) throws IncorrectFormatException {
        switch (instruction) {
            case MARK: {
                String indexString = strippedInput.substring(5).strip();
                int indexToChange = Character.getNumericValue(indexString.charAt(0));
                return indexToChange;
            }
            case UNMARK: {
                String temp = strippedInput.substring(7).strip();
                int indexToChange = Character.getNumericValue(temp.charAt(0));
                return indexToChange;
            }
            default: //shouldn't reach here
                throw new IncorrectFormatException("Not a case of mark or unmark!"); // programme breaks
        }
    }

    public String parse(String userInput) {

        String strippedInput = userInput.strip();

        try {
            if (userInput.strip().startsWith("bye")) {
                return UserInterface.GOODBYE_MESSAGE;
                // System.exit(0);

            } else if (userInput.strip().startsWith("todo ")) {
                return taskList.handleNewTask(userInput, "T");

            } else if (userInput.strip().startsWith("deadline ")) {
                return taskList.handleNewTask(userInput, "D");

            } else if (userInput.strip().startsWith("event ")) {
                return taskList.handleNewTask(userInput, "E");

            } else if (strippedInput.startsWith("mark ")) {
                int indexToChange = getMarkOrUnmarkIndex(strippedInput, Marking.MARK);
                if ((indexToChange > 0) && (indexToChange < 100)) {
                    Storage.INPUT_TASKS.get(indexToChange - 1).markAsDone();
                }

                Storage.resetFile(this.filePath);
                for (Task task : Storage.INPUT_TASKS) {
                    Storage.appendToFile(task, this.filePath);
                }

                return (" Nice! I've marked this task as done: \n"
                    + Storage.INPUT_TASKS.get(indexToChange - 1) + "\n"
                    + UserInterface.AFTER_VALID_INPUT);

            } else if (strippedInput.startsWith("unmark ")) {
                int indexToChange = getMarkOrUnmarkIndex(strippedInput, Marking.UNMARK);
                if ((indexToChange > 0) && (indexToChange < 100)) {
                    Storage.INPUT_TASKS.get(indexToChange - 1).markAsNotDone();
                }

                Storage.resetFile(this.filePath);
                for (Task task : Storage.INPUT_TASKS) {
                    Storage.appendToFile(task, this.filePath);
                }

                return ("OK, I've marked this task as not done yet: \n"
                    + Storage.INPUT_TASKS.get(indexToChange - 1) + "\n"
                    + UserInterface.AFTER_VALID_INPUT);

            } else if (userInput.strip().equals("list")) {
                String output = "Here are the tasks in your list: \n";

                for (int i = 0; i < Pixel.count; i++) {
                    Task currentTask = Storage.INPUT_TASKS.get(i);
                    output += ((i + 1) + ". " + currentTask + "\n");
                }
                return output + UserInterface.AFTER_VALID_INPUT;

            } else if (userInput.strip().startsWith("delete ")) {
                String output = Storage.deleteEntry(userInput, filePath);
                return output + "\n" + UserInterface.AFTER_VALID_INPUT;

            } else if (userInput.strip().startsWith("find ")) {
                ArrayList<Task> results = Storage.findEntry(userInput);
                String output = "Here are the matching tasks in your list: \n";
                for (int i = 0; i < results.size(); i++) {
                    Task currentTask = results.get(i);
                    output += ((i + 1) + ". " + currentTask + "\n");
                }
                return output + UserInterface.AFTER_VALID_INPUT;

            } else {
                throw new IncorrectFormatException("Input should be a task or command!"); // programme breaks
            }

        } catch (IndexOutOfBoundsException e) {
            return ("caught Index Out of Bounds Exception \n"
                + UserInterface.AFTER_INVALID_INPUT + "\n"
                + UserInterface.PROMPT_MESSAGE);

        } catch (StackOverflowError e) {
            return ("caught Stack Overflow Error \n"
                + UserInterface.AFTER_INVALID_INPUT + "\n"
                + UserInterface.PROMPT_MESSAGE);

        } catch (NullPointerException e) {
            return ("caught Null pointer exception \n"
                + UserInterface.AFTER_INVALID_INPUT + "\n"
                + UserInterface.PROMPT_MESSAGE);

        } catch (IncorrectFormatException e) {
            return (e + "\n"
                + "Incorrect format exception! \n"
                + UserInterface.AFTER_INVALID_INPUT + "\n"
                + UserInterface.PROMPT_MESSAGE);

        } catch (IOException e) {

            if (e instanceof FileNotFoundException) {
                File tempFile = new File("./data/pixel", "pixel.txt");
                return ("Caught FileNotFound exception! \n"
                    + "New file is created for you \n"
                    + UserInterface.PROMPT_MESSAGE);
            } else {
                return ("Caught IO exception! \n"
                    + UserInterface.PROMPT_MESSAGE);
            }

        }
    }

}
