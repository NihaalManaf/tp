package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes one or more persons identified using their displayed indices from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
                                                       + ": Deletes the person(s) identified by the index number(s) "
                                                       + "used in the displayed person list.\n"
                                                       + "Parameters: INDEX [MORE_INDICES]... (must be positive "
                                                       + "integers)\n"
                                                       + "Example: "
                                                       + COMMAND_WORD
                                                       + " 1\n"
                                                       + "Example: "
                                                       + COMMAND_WORD
                                                       + " 1 2 3";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    public static final String MESSAGE_DELETE_PERSONS_SUCCESS = "Deleted %1$d Person(s):\n%2$s";
    public static final String MESSAGE_INVALID_INDICES = "Invalid index(es) detected: %1$s\n"
            + "Please ensure all indices are valid before deleting.";
    public static final String MESSAGE_DUPLICATE_INDICES = "Duplicate indices were detected!\n"
            + "Please ensure all indices are unique.";

    private final List<Index> targetIndices;

    public DeleteCommand(List<Index> targetIndices) {
        this.targetIndices = targetIndices;
    }

    /**
     * Executes the delete command to remove one or more persons from the address book.
     *
     * @param model The model containing the address book data.
     * @return A CommandResult with a success message.
     * @throws CommandException If any of the target indices are invalid.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        validateAllIndices(lastShownList);
        List<Person> personsToDelete = collectPersonsToDelete(lastShownList);
        deletePersonsFromModel(model, personsToDelete);
        String successMessage = formatSuccessMessage(personsToDelete);

        return new CommandResult(successMessage);
    }

    /**
     * Validates that all target indices are within the bounds of the list and are unique.
     *
     * @param personList The list of persons to check against.
     * @throws CommandException If any index is out of bounds or if there are duplicate indices.
     */
    private void validateAllIndices(List<Person> personList) throws CommandException {
        // Check for duplicates first
        Set<Integer> seenIndices = new HashSet<>();
        for (Index index : targetIndices) {
            if (!seenIndices.add(index.getZeroBased())) {
                throw new CommandException(MESSAGE_DUPLICATE_INDICES);
            }
        }
        
        // Then check for invalid indices
        List<Integer> invalidIndices = new ArrayList<>();
        for (Index index : targetIndices) {
            if (index.getZeroBased() >= personList.size()) {
                invalidIndices.add(index.getOneBased());
            }
        }
        if (!invalidIndices.isEmpty()) {
            String invalidIndicesString = invalidIndices.stream()
                    .map(String::valueOf)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            throw new CommandException(String.format(MESSAGE_INVALID_INDICES, invalidIndicesString));
        }
    }

    /**
     * Collects the persons to be deleted based on the target indices.
     * Indices are processed in descending order to avoid shifting issues.
     *
     * @param personList The current filtered list of persons.
     * @return A list of persons to delete.
     */
    private List<Person> collectPersonsToDelete(List<Person> personList) {
        List<Index> sortedIndices = new ArrayList<>(targetIndices);
        sortedIndices.sort(Comparator.comparingInt(Index::getZeroBased).reversed());

        List<Person> personsToDelete = new ArrayList<>();
        for (Index index : sortedIndices) {
            personsToDelete.add(personList.get(index.getZeroBased()));
        }
        return personsToDelete;
    }

    /**
     * Deletes all persons in the list from the model.
     *
     * @param model The model to delete from.
     * @param personsToDelete The list of persons to delete.
     */
    private void deletePersonsFromModel(Model model, List<Person> personsToDelete) {
        for (Person person : personsToDelete) {
            model.deletePerson(person);
        }
    }

    /**
     * Formats the success message based on the number of persons deleted.
     *
     * @param deletedPersons The list of persons that were deleted.
     * @return A formatted success message.
     */
    private String formatSuccessMessage(List<Person> deletedPersons) {
        if (deletedPersons.size() == 1) {
            return String.format(MESSAGE_DELETE_PERSON_SUCCESS,
                    Messages.format(deletedPersons.get(0)));
        } else {
            return formatMultipleDeleteMessage(deletedPersons);
        }
    }

    /**
     * Formats the success message for multiple person deletions.
     *
     * @param deletedPersons The list of persons that were deleted.
     * @return A formatted success message listing all deleted persons.
     */
    private String formatMultipleDeleteMessage(List<Person> deletedPersons) {
        StringBuilder deletedPersonsList = new StringBuilder();
        for (Person person : deletedPersons) {
            deletedPersonsList.append("- ").append(Messages.format(person)).append("\n");
        }
        return String.format(MESSAGE_DELETE_PERSONS_SUCCESS,
                deletedPersons.size(), deletedPersonsList.toString().trim());
    }

    /**
     * Checks if this DeleteCommand is equal to another object.
     * Two DeleteCommands are equal if they target the same indices.
     *
     * @param other The object to compare with.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (other
                    == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return targetIndices.equals(otherDeleteCommand.targetIndices);
    }

    /**
     * Returns a string representation of this DeleteCommand.
     *
     * @return A string describing this command with its target indices.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                       .add("targetIndices", targetIndices)
                       .toString();
    }
}
