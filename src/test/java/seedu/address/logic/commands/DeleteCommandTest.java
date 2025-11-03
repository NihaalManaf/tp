package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(List.of(INDEX_FIRST_PERSON));

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(List.of(outOfBoundIndex));

        String expectedMessage = String.format(DeleteCommand.MESSAGE_INVALID_INDICES,
                outOfBoundIndex.getOneBased());
        assertCommandFailure(deleteCommand, model, expectedMessage);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(List.of(INDEX_FIRST_PERSON));

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased()
                           < model.getAddressBook().getPersonList().size());

        DeleteCommand deleteCommand = new DeleteCommand(List.of(outOfBoundIndex));

        String expectedMessage = String.format(DeleteCommand.MESSAGE_INVALID_INDICES,
                outOfBoundIndex.getOneBased());
        assertCommandFailure(deleteCommand, model, expectedMessage);
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(List.of(INDEX_FIRST_PERSON));
        DeleteCommand deleteSecondCommand = new DeleteCommand(List.of(INDEX_SECOND_PERSON));

        // same object -> returns true
        assertEquals(deleteFirstCommand, deleteFirstCommand);

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(List.of(INDEX_FIRST_PERSON));
        assertEquals(deleteFirstCommand, deleteFirstCommandCopy);

        // different types -> returns false
        assertNotEquals(1, deleteFirstCommand);

        // null -> returns false
        assertNotEquals(null, deleteFirstCommand);

        // different person -> returns false
        assertNotEquals(deleteFirstCommand, deleteSecondCommand);
    }

    @Test
    public void execute_multipleValidIndices_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person secondPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());

        DeleteCommand deleteCommand = new DeleteCommand(Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(firstPerson);
        expectedModel.deletePerson(secondPerson);

        // Persons are displayed in descending order of indices (index 2 before index 1)
        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSONS_SUCCESS, 2,
                "- "
                        + Messages.format(secondPerson)
                        + "\n- "
                        + Messages.format(firstPerson));

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void toStringMethod() {
        List<Index> targetIndices = List.of(Index.fromOneBased(1));
        DeleteCommand deleteCommand = new DeleteCommand(targetIndices);
        String expected = DeleteCommand.class.getCanonicalName()
                                  + "{targetIndices="
                                  + targetIndices
                                  + "}";
        assertEquals(expected, deleteCommand.toString());
    }

    @Test
    public void execute_multipleInvalidIndices_throwsCommandException() {
        Index firstInvalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        Index secondInvalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 5);
        DeleteCommand deleteCommand = new DeleteCommand(Arrays.asList(firstInvalidIndex, secondInvalidIndex));

        String expectedMessage = String.format(DeleteCommand.MESSAGE_INVALID_INDICES,
                firstInvalidIndex.getOneBased() + ", " + secondInvalidIndex.getOneBased());
        assertCommandFailure(deleteCommand, model, expectedMessage);
    }

    @Test
    public void execute_mixedValidAndInvalidIndices_throwsCommandException() {
        Index validIndex = INDEX_FIRST_PERSON;
        Index invalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(Arrays.asList(validIndex, invalidIndex));

        String expectedMessage = String.format(DeleteCommand.MESSAGE_INVALID_INDICES,
                invalidIndex.getOneBased());
        assertCommandFailure(deleteCommand, model, expectedMessage);

        // Verify that no persons were deleted (all-or-nothing behavior)
        assertEquals(model.getFilteredPersonList().size(),
                new ModelManager(getTypicalAddressBook(), new UserPrefs()).getFilteredPersonList().size());
    }

    @Test
    public void execute_duplicateIndices_throwsCommandException() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        // Delete same person twice using duplicate indices
        // This should throw CommandException with duplicate indices message
        DeleteCommand deleteCommand = new DeleteCommand(
                Arrays.asList(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON));

        assertThrows(CommandException.class, DeleteCommand.MESSAGE_DUPLICATE_INDICES, ()
                -> deleteCommand.execute(model));
    }

    @Test
    public void execute_multipleValidIndicesInFilteredList_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(List.of(INDEX_FIRST_PERSON));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_allIndicesInvalid_throwsCommandException() {
        Index firstInvalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        Index secondInvalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 2);
        Index thirdInvalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 3);

        DeleteCommand deleteCommand = new DeleteCommand(
                Arrays.asList(firstInvalidIndex, secondInvalidIndex, thirdInvalidIndex));

        String expectedMessage = String.format(DeleteCommand.MESSAGE_INVALID_INDICES,
                firstInvalidIndex.getOneBased() + ", " + secondInvalidIndex.getOneBased()
                + ", " + thirdInvalidIndex.getOneBased());
        assertCommandFailure(deleteCommand, model, expectedMessage);
    }

    @Test
    public void execute_indicesInDescendingOrder_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Index thirdIndex = Index.fromOneBased(3);
        Index firstIndex = INDEX_FIRST_PERSON;

        Person firstPerson = model.getFilteredPersonList().get(firstIndex.getZeroBased());
        Person thirdPerson = model.getFilteredPersonList().get(thirdIndex.getZeroBased());

        // Provide indices in descending order
        DeleteCommand deleteCommand = new DeleteCommand(Arrays.asList(thirdIndex, firstIndex));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(firstPerson);
        expectedModel.deletePerson(thirdPerson);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSONS_SUCCESS, 2,
                "- " + Messages.format(thirdPerson) + "\n- " + Messages.format(firstPerson));

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_singleIndexBoundary_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Index lastIndex = Index.fromOneBased(model.getFilteredPersonList().size());
        Person personToDelete = model.getFilteredPersonList().get(lastIndex.getZeroBased());

        DeleteCommand deleteCommand = new DeleteCommand(List.of(lastIndex));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void equals_multipleIndices_correctComparison() {
        DeleteCommand deleteFirstAndSecond = new DeleteCommand(
                Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));
        DeleteCommand deleteSecondAndFirst = new DeleteCommand(
                Arrays.asList(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON));
        DeleteCommand deleteFirstAndSecondCopy = new DeleteCommand(
                Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));

        // same object -> returns true
        assertEquals(deleteFirstAndSecond, deleteFirstAndSecond);

        // same values in same order -> returns true
        assertEquals(deleteFirstAndSecond, deleteFirstAndSecondCopy);

        // different order -> returns false (order matters for equals)
        assertNotEquals(deleteFirstAndSecond, deleteSecondAndFirst);
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
