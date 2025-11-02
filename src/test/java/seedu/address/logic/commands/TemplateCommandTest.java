package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.clipboard.ClipboardProvider;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Status;
import seedu.address.storage.TemplateStorageManagerTest.StorageStub;

public class TemplateCommandTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    public void constructor_nullStatus_throwsNullPointerException() {
        StorageStub storageStub = new StorageStub();
        assertThrows(NullPointerException.class, () -> new TemplateCommand(null, storageStub));
    }

    @Test
    public void constructor_nullStorage_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TemplateCommand(Status.CONTACTED, null));
    }

    @Test
    public void constructor_saveMode_doesNotThrowException() {
        StorageStub storageStub = new StorageStub();
        // This should not throw an exception
        new TemplateCommand(storageStub);
    }

    @Test
    public void execute_templateContactedStatus_success() throws Exception {
        StorageStub storageStub = new StorageStub();
        ModelManager model = new ModelManager();
        TemplateCommand command = new TemplateCommand(Status.CONTACTED, storageStub);

        CommandResult result = command.execute(model);

        assertEquals(String.format(TemplateCommand.MESSAGE_OPEN_TEMPLATE_SUCCESS, "Contacted"),
                result.getFeedbackToUser());
        assertTrue(result.isShowTemplate());
        assertEquals(Status.CONTACTED, model.getTemplateViewStateProperty().getValue().getStatus());
    }

    @Test
    public void execute_templateUncontactedStatus_success() throws Exception {
        StorageStub storageStub = new StorageStub();
        ModelManager model = new ModelManager();
        TemplateCommand command = new TemplateCommand(Status.UNCONTACTED, storageStub);

        CommandResult result = command.execute(model);

        assertEquals(String.format(TemplateCommand.MESSAGE_OPEN_TEMPLATE_SUCCESS, "Uncontacted"),
                result.getFeedbackToUser());
        assertTrue(result.isShowTemplate());
        assertEquals(Status.UNCONTACTED, model.getTemplateViewStateProperty().getValue().getStatus());
    }

    @Test
    public void execute_saveTemplate_success() throws Exception {
        StorageStub storageStub = new StorageStub();
        ModelManager model = new ModelManager();

        // First open a template
        model.setTemplateViewState(new seedu.address.model.TemplateViewState(Status.CONTACTED, "Test content"));

        // Then save it
        TemplateCommand saveCommand = new TemplateCommand(storageStub);
        CommandResult result = saveCommand.execute(model);

        assertEquals(String.format(TemplateCommand.MESSAGE_SAVE_TEMPLATE_SUCCESS, "Contacted"),
                result.getFeedbackToUser());
        assertFalse(result.isShowTemplate());
        assertEquals("Test content", storageStub.getSavedTemplate(Status.CONTACTED));
    }

    @Test
    public void execute_saveWithoutOpenTemplate_throwsCommandException() {
        StorageStub storageStub = new StorageStub();
        ModelManager model = new ModelManager();
        TemplateCommand saveCommand = new TemplateCommand(storageStub);

        assertThrows(CommandException.class,
                TemplateCommand.MESSAGE_NO_TEMPLATE_TO_SAVE, () -> saveCommand.execute(model));
    }

    @Test
    public void execute_saveBlankTemplate_savesDefaultTemplate() throws Exception {
        StorageStub storageStub = new StorageStub();
        ModelManager model = new ModelManager();

        // Test with empty string
        model.setTemplateViewState(new seedu.address.model.TemplateViewState(Status.CONTACTED, ""));
        TemplateCommand saveCommand1 = new TemplateCommand(storageStub);
        CommandResult result1 = saveCommand1.execute(model);

        // Should show blank template message
        assertEquals(TemplateCommand.MESSAGE_SAVE_BLANK_TEMPLATE, result1.getFeedbackToUser());
        assertEquals("This is the default template for status Contacted",
                storageStub.getSavedTemplate(Status.CONTACTED));

        // Test with whitespace only
        model.setTemplateViewState(new seedu.address.model.TemplateViewState(Status.UNCONTACTED, "   \t\n  "));
        TemplateCommand saveCommand2 = new TemplateCommand(storageStub);
        CommandResult result2 = saveCommand2.execute(model);

        // Should show blank template message
        assertEquals(TemplateCommand.MESSAGE_SAVE_BLANK_TEMPLATE, result2.getFeedbackToUser());
        assertEquals("This is the default template for status Uncontacted",
                storageStub.getSavedTemplate(Status.UNCONTACTED));
    }

    @Test
    public void execute_allStatuses_success() throws Exception {
        StorageStub storageStub = new StorageStub();
        ModelManager model = new ModelManager();

        Status[] allStatuses = {Status.UNCONTACTED, Status.CONTACTED, Status.REJECTED,
                                Status.ACCEPTED, Status.UNREACHABLE, Status.BUSY};
        String[] statusNames = {"Uncontacted", "Contacted", "Rejected", "Accepted", "Unreachable", "Busy"};

        for (int i = 0; i < allStatuses.length; i++) {
            Status status = allStatuses[i];
            String statusName = statusNames[i];
            TemplateCommand command = new TemplateCommand(status, storageStub);
            CommandResult result = command.execute(model);

            assertEquals(String.format(TemplateCommand.MESSAGE_OPEN_TEMPLATE_SUCCESS, statusName),
                    result.getFeedbackToUser());
            assertTrue(result.isShowTemplate());
            assertEquals(status, model.getTemplateViewStateProperty().getValue().getStatus());
        }
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        StorageStub storageStub = new StorageStub();
        TemplateCommand command = new TemplateCommand(Status.CONTACTED, storageStub);
        assertTrue(command.equals(command));
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        StorageStub storageStub1 = new StorageStub();
        StorageStub storageStub2 = new StorageStub();
        TemplateCommand command1 = new TemplateCommand(Status.CONTACTED, storageStub1);
        TemplateCommand command2 = new TemplateCommand(Status.CONTACTED, storageStub2);
        assertTrue(command1.equals(command2));
    }

    @Test
    public void equals_differentStatus_returnsFalse() {
        StorageStub storageStub = new StorageStub();
        TemplateCommand command1 = new TemplateCommand(Status.CONTACTED, storageStub);
        TemplateCommand command2 = new TemplateCommand(Status.UNCONTACTED, storageStub);
        assertFalse(command1.equals(command2));
    }

    @Test
    public void equals_differentSaveMode_returnsFalse() {
        StorageStub storageStub = new StorageStub();
        TemplateCommand command1 = new TemplateCommand(Status.CONTACTED, storageStub);
        TemplateCommand command2 = new TemplateCommand(storageStub);
        assertFalse(command1.equals(command2));
    }

    @Test
    public void equals_saveModeCommands_returnsTrue() {
        StorageStub storageStub1 = new StorageStub();
        StorageStub storageStub2 = new StorageStub();
        TemplateCommand command1 = new TemplateCommand(storageStub1);
        TemplateCommand command2 = new TemplateCommand(storageStub2);
        assertTrue(command1.equals(command2));
    }

    @Test
    public void equals_null_returnsFalse() {
        StorageStub storageStub = new StorageStub();
        TemplateCommand command = new TemplateCommand(Status.CONTACTED, storageStub);
        assertFalse(command.equals(null));
    }

    @Test
    public void equals_differentType_returnsFalse() {
        StorageStub storageStub = new StorageStub();
        TemplateCommand command = new TemplateCommand(Status.CONTACTED, storageStub);
        assertFalse(command.equals("string"));
    }

    @Test
    public void hashCode_sameValues_returnsSameHashCode() {
        // TemplateCommand doesn't override hashCode, so it uses Object.hashCode()
        // which is based on object identity, not values.
        // This test should verify that the same object has the same hash code.
        StorageStub storageStub = new StorageStub();
        TemplateCommand command = new TemplateCommand(Status.CONTACTED, storageStub);
        assertEquals(command.hashCode(), command.hashCode());
    }

    @Test
    public void hashCode_differentValues_returnsDifferentHashCode() {
        StorageStub storageStub = new StorageStub();
        TemplateCommand command1 = new TemplateCommand(Status.CONTACTED, storageStub);
        TemplateCommand command2 = new TemplateCommand(Status.UNCONTACTED, storageStub);
        assertNotEquals(command1.hashCode(), command2.hashCode());
    }

    @Test
    public void toString_validCommand_correctFormat() {
        StorageStub storageStub = new StorageStub();
        TemplateCommand command = new TemplateCommand(Status.CONTACTED, storageStub);
        String expected = "TemplateCommand{status=CONTACTED}";
        assertEquals(expected, command.toString());
    }

    @Test
    public void toString_saveCommand_correctFormat() {
        StorageStub storageStub = new StorageStub();
        TemplateCommand command = new TemplateCommand(storageStub);
        String expected = "TemplateCommand{save}";
        assertEquals(expected, command.toString());
    }

    // ==================== Copy Mode Tests ====================

    @Test
    public void constructor_copyNullStatus_throwsNullPointerException() {
        StorageStub storageStub = new StorageStub();
        ClipboardStub clipboardStub = new ClipboardStub();
        assertThrows(NullPointerException.class, () -> new TemplateCommand(null, storageStub, clipboardStub));
    }

    @Test
    public void constructor_copyNullStorage_throwsNullPointerException() {
        ClipboardStub clipboardStub = new ClipboardStub();
        assertThrows(NullPointerException.class, () -> new TemplateCommand(Status.CONTACTED, null, clipboardStub));
    }

    @Test
    public void constructor_copyNullClipboard_throwsNullPointerException() {
        StorageStub storageStub = new StorageStub();
        assertThrows(NullPointerException.class, () -> new TemplateCommand(Status.CONTACTED, storageStub, null));
    }

    @Test
    public void execute_copyTemplate_success() throws Exception {
        StorageStub storageStub = new StorageStub();
        ClipboardStub clipboardStub = new ClipboardStub();
        ModelManager model = new ModelManager();

        TemplateCommand command = new TemplateCommand(Status.CONTACTED, storageStub, clipboardStub);
        CommandResult result = command.execute(model);

        assertEquals(String.format(TemplateCommand.MESSAGE_COPY_TEMPLATE_SUCCESS, "Contacted"),
                result.getFeedbackToUser());
        assertFalse(result.isShowTemplate());
        assertEquals("This is the default template for status Contacted", clipboardStub.getString());
    }

    @Test
    public void execute_copyTemplateAllStatuses_success() throws Exception {
        StorageStub storageStub = new StorageStub();
        ClipboardStub clipboardStub = new ClipboardStub();
        ModelManager model = new ModelManager();

        Status[] allStatuses = {Status.UNCONTACTED, Status.CONTACTED, Status.REJECTED,
                                Status.ACCEPTED, Status.UNREACHABLE, Status.BUSY};
        String[] statusNames = {"Uncontacted", "Contacted", "Rejected", "Accepted", "Unreachable", "Busy"};

        for (int i = 0; i < allStatuses.length; i++) {
            Status status = allStatuses[i];
            String statusName = statusNames[i];
            TemplateCommand command = new TemplateCommand(status, storageStub, clipboardStub);
            CommandResult result = command.execute(model);

            assertEquals(String.format(TemplateCommand.MESSAGE_COPY_TEMPLATE_SUCCESS, statusName),
                    result.getFeedbackToUser());
            assertFalse(result.isShowTemplate());

            // Format status name (e.g., CONTACTED -> Contacted)
            String formattedStatus = status.name().charAt(0) + status.name().substring(1).toLowerCase();
            assertEquals("This is the default template for status " + formattedStatus, clipboardStub.getString());
        }
    }

    @Test
    public void execute_copySavedTemplate_success() throws Exception {
        StorageStub storageStub = new StorageStub();
        ClipboardStub clipboardStub = new ClipboardStub();
        ModelManager model = new ModelManager();

        // Save a custom template first
        storageStub.saveTemplate(Status.CONTACTED, "Custom template content");

        // Copy the saved template
        TemplateCommand command = new TemplateCommand(Status.CONTACTED, storageStub, clipboardStub);
        CommandResult result = command.execute(model);

        assertEquals(String.format(TemplateCommand.MESSAGE_COPY_TEMPLATE_SUCCESS, "Contacted"),
                result.getFeedbackToUser());
        assertEquals("Custom template content", clipboardStub.getString());
    }

    @Test
    public void execute_copyBlankTemplate_copiesDefaultTemplate() throws Exception {
        StorageStub storageStub = new StorageStub();
        ClipboardStub clipboardStub = new ClipboardStub();
        ModelManager model = new ModelManager();

        // Save a blank template (which should become default)
        storageStub.saveTemplate(Status.BUSY, "   \t\n  ");

        // Copy the template - should get default, not whitespace
        TemplateCommand command = new TemplateCommand(Status.BUSY, storageStub, clipboardStub);
        CommandResult result = command.execute(model);

        assertEquals(String.format(TemplateCommand.MESSAGE_COPY_TEMPLATE_SUCCESS, "Busy"),
                result.getFeedbackToUser());
        // Should copy the default template, not the whitespace
        assertEquals("This is the default template for status Busy", clipboardStub.getString());
    }

    @Test
    public void equals_copyCommandsSameStatus_returnsTrue() {
        StorageStub storageStub1 = new StorageStub();
        StorageStub storageStub2 = new StorageStub();
        ClipboardStub clipboardStub1 = new ClipboardStub();
        ClipboardStub clipboardStub2 = new ClipboardStub();

        TemplateCommand command1 = new TemplateCommand(Status.CONTACTED, storageStub1, clipboardStub1);
        TemplateCommand command2 = new TemplateCommand(Status.CONTACTED, storageStub2, clipboardStub2);
        assertTrue(command1.equals(command2));
    }

    @Test
    public void equals_copyCommandsDifferentStatus_returnsFalse() {
        StorageStub storageStub = new StorageStub();
        ClipboardStub clipboardStub = new ClipboardStub();

        TemplateCommand command1 = new TemplateCommand(Status.CONTACTED, storageStub, clipboardStub);
        TemplateCommand command2 = new TemplateCommand(Status.UNCONTACTED, storageStub, clipboardStub);
        assertFalse(command1.equals(command2));
    }

    @Test
    public void equals_copyAndOpenCommand_returnsFalse() {
        StorageStub storageStub = new StorageStub();
        ClipboardStub clipboardStub = new ClipboardStub();

        TemplateCommand copyCommand = new TemplateCommand(Status.CONTACTED, storageStub, clipboardStub);
        TemplateCommand openCommand = new TemplateCommand(Status.CONTACTED, storageStub);
        assertFalse(copyCommand.equals(openCommand));
    }

    @Test
    public void equals_copyAndSaveCommand_returnsFalse() {
        StorageStub storageStub = new StorageStub();
        ClipboardStub clipboardStub = new ClipboardStub();

        TemplateCommand copyCommand = new TemplateCommand(Status.CONTACTED, storageStub, clipboardStub);
        TemplateCommand saveCommand = new TemplateCommand(storageStub);
        assertFalse(copyCommand.equals(saveCommand));
    }

    @Test
    public void toString_copyCommand_correctFormat() {
        StorageStub storageStub = new StorageStub();
        ClipboardStub clipboardStub = new ClipboardStub();
        TemplateCommand command = new TemplateCommand(Status.CONTACTED, storageStub, clipboardStub);
        String expected = "TemplateCommand{copy, status=CONTACTED}";
        assertEquals(expected, command.toString());
    }

    /**
     * A stub implementation of ClipboardProvider for testing.
     */
    private static class ClipboardStub implements ClipboardProvider {
        private String value;

        @Override
        public String getString() {
            return value;
        }

        @Override
        public void setString(String v) {
            value = v;
        }
    }
}
