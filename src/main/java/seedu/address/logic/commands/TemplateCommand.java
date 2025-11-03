package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;

import seedu.address.logic.clipboard.ClipboardProvider;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.TemplateViewState;
import seedu.address.model.person.Status;
import seedu.address.storage.TemplateStorage;

/**
 * Opens a template view for editing email templates for a specific status.
 */
public class TemplateCommand extends Command {

    public static final String COMMAND_WORD = "template";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Opens template editor for a specific status, saves the current template, "
            + "or copies a template to clipboard.\n"
            + "Parameters: s:STATUS (to open template), save (to save current template), "
            + "or copy s:STATUS (to copy template to clipboard)\n"
            + "Allowed STATUS: " + Status.allowedValuesDescription() + "\n"
            + "Examples:\n"
            + COMMAND_WORD + " s:Contacted\n"
            + COMMAND_WORD + " s:Uncontacted\n"
            + COMMAND_WORD + " save\n"
            + COMMAND_WORD + " copy s:Contacted";

    public static final String MESSAGE_OPEN_TEMPLATE_SUCCESS = "Opened template for %s status";
    public static final String MESSAGE_SAVE_TEMPLATE_SUCCESS = "Template saved for %s status";
    public static final String MESSAGE_SAVE_BLANK_TEMPLATE =
            "Detected empty template as input, saving as the default template instead.";
    public static final String MESSAGE_COPY_TEMPLATE_SUCCESS = "Copied template for %s status to clipboard";
    public static final String MESSAGE_NO_TEMPLATE_TO_SAVE = "No template is currently open. "
            + "Use 'template s:STATUS' to open a template first.";
    public static final String MESSAGE_INVALID_STATUS = "Invalid status. Allowed: "
            + Status.allowedValuesDescription();
    public static final String MESSAGE_STORAGE_ERROR = "Error accessing template storage: %s";

    private final Status status;
    private final boolean isSaveAction;
    private final boolean isCopyAction;
    private final TemplateStorage templateStorage;
    private final ClipboardProvider clipboardProvider;

    /**
     * Creates a TemplateCommand to open a template for the specified status.
     *
     * @param status The status for which to open the template.
     * @param templateStorage The storage to read/write templates.
     */
    public TemplateCommand(Status status, TemplateStorage templateStorage) {
        this(status, false, false, templateStorage, null);
    }

    /**
     * Creates a TemplateCommand to save the currently open template.
     *
     * @param templateStorage The storage to read/write templates.
     */
    public TemplateCommand(TemplateStorage templateStorage) {
        this(null, true, false, templateStorage, null);
    }

    /**
     * Creates a TemplateCommand to copy a template for the specified status to clipboard.
     *
     * @param status The status for which to copy the template.
     * @param templateStorage The storage to read templates.
     * @param clipboardProvider The provider to access the system clipboard.
     */
    public TemplateCommand(Status status, TemplateStorage templateStorage, ClipboardProvider clipboardProvider) {
        this(status, false, true, templateStorage, clipboardProvider);
    }

    /**
     * Internal constructor for TemplateCommand.
     *
     * @param status The status (required for open and copy actions, null for save action).
     * @param isSaveAction Whether this is a save action.
     * @param isCopyAction Whether this is a copy action.
     * @param templateStorage The storage to read/write templates.
     * @param clipboardProvider The provider to access the system clipboard (required for copy action).
     */
    private TemplateCommand(Status status, boolean isSaveAction, boolean isCopyAction,
                           TemplateStorage templateStorage, ClipboardProvider clipboardProvider) {
        requireNonNull(templateStorage);
        if (!isSaveAction) {
            requireNonNull(status);
        }
        if (isCopyAction) {
            requireNonNull(clipboardProvider);
        }
        this.status = status;
        this.isSaveAction = isSaveAction;
        this.isCopyAction = isCopyAction;
        this.templateStorage = templateStorage;
        this.clipboardProvider = clipboardProvider;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (isSaveAction) {
            return executeSave(model);
        } else if (isCopyAction) {
            return executeCopy(model);
        } else {
            return executeOpen(model);
        }
    }

    /**
     * Opens the template view for the specified status.
     *
     * @param model The model containing the application data.
     * @return A CommandResult indicating success with the template view flag set.
     * @throws CommandException If there's an error accessing template storage.
     */
    private CommandResult executeOpen(Model model) throws CommandException {
        try {
            String content = templateStorage.readTemplate(status);
            TemplateViewState state = new TemplateViewState(status, content);
            model.setTemplateViewState(state);

            String successMessage = createOpenSuccessMessage();
            return new CommandResult(successMessage, false, false, true); // isShowTemplate = true
        } catch (IOException e) {
            throw new CommandException(String.format(MESSAGE_STORAGE_ERROR, e.getMessage()));
        }
    }

    /**
     * Saves the currently displayed template.
     *
     * @param model The model containing the current template state.
     * @return A CommandResult indicating success.
     * @throws CommandException If no template is open or there's an error saving.
     */
    private CommandResult executeSave(Model model) throws CommandException {
        TemplateViewState currentState = model.getTemplateViewState();

        if (currentState == null) {
            throw new CommandException(MESSAGE_NO_TEMPLATE_TO_SAVE);
        }

        try {
            String content = currentState.getContent();
            boolean isBlankContent = content == null || content.isBlank();

            templateStorage.saveTemplate(currentState.getStatus(), content);

            // Use different message if content was blank
            String successMessage = isBlankContent
                    ? MESSAGE_SAVE_BLANK_TEMPLATE
                    : createSaveSuccessMessage(currentState.getStatus());
            return new CommandResult(successMessage);
        } catch (IOException e) {
            throw new CommandException(String.format(MESSAGE_STORAGE_ERROR, e.getMessage()));
        }
    }

    /**
     * Copies the template content for the specified status to the clipboard.
     *
     * @param model The model (not used for copy action but required by interface).
     * @return A CommandResult indicating success.
     * @throws CommandException If there's an error reading the template.
     */
    private CommandResult executeCopy(Model model) throws CommandException {
        try {
            String content = templateStorage.readTemplate(status);
            clipboardProvider.setString(content);
            String successMessage = createCopySuccessMessage();
            return new CommandResult(successMessage);
        } catch (IOException e) {
            throw new CommandException(String.format(MESSAGE_STORAGE_ERROR, e.getMessage()));
        }
    }

    /**
     * Creates a success message for opening a template.
     *
     * @return The formatted success message.
     */
    private String createOpenSuccessMessage() {
        return String.format(MESSAGE_OPEN_TEMPLATE_SUCCESS, formatStatusName(status));
    }

    /**
     * Creates a success message for saving a template.
     *
     * @param statusToSave The status of the template being saved.
     * @return The formatted success message.
     */
    private String createSaveSuccessMessage(Status statusToSave) {
        return String.format(MESSAGE_SAVE_TEMPLATE_SUCCESS, formatStatusName(statusToSave));
    }

    /**
     * Creates a success message for copying a template.
     *
     * @return The formatted success message.
     */
    private String createCopySuccessMessage() {
        return String.format(MESSAGE_COPY_TEMPLATE_SUCCESS, formatStatusName(status));
    }

    /**
     * Formats the status name for display.
     *
     * @param statusToFormat The status to format.
     * @return A formatted status name (e.g., "Contacted" instead of "CONTACTED").
     */
    private String formatStatusName(Status statusToFormat) {
        String name = statusToFormat.name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }

    /**
     * Checks if this TemplateCommand is equal to another object.
     * Two TemplateCommands are equal if they are both save actions, or if they
     * open templates for the same status.
     *
     * @param other The object to compare with.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TemplateCommand)) {
            return false;
        }

        TemplateCommand otherCommand = (TemplateCommand) other;
        if (isSaveAction != otherCommand.isSaveAction || isCopyAction != otherCommand.isCopyAction) {
            return false;
        }

        if (isSaveAction) {
            return true; // Both are actions to save
        }

        return status.equals(otherCommand.status);
    }

    /**
     * Returns a string representation of this TemplateCommand.
     *
     * @return A string describing this command.
     */
    @Override
    public String toString() {
        if (isSaveAction) {
            return "TemplateCommand{save}";
        } else if (isCopyAction) {
            return "TemplateCommand{copy, status=" + status + "}";
        } else {
            return "TemplateCommand{status=" + status + "}";
        }
    }
}
