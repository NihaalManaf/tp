package seedu.address.ui;

import java.io.IOException;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;

/**
 * Controller for the import data page
 */
public class ImportWindow extends UiPart<Stage> {
    private static final Logger logger = LogsCenter.getLogger(ImportWindow.class);
    private static final String FXML_FILE = "ImportWindow.fxml";
    private static final String DEFAULT_PREVIEW_TEXT = "No data yet.\nPaste JSON data here";

    // Success
    private static final String ALERT_SUCCESS_TITLE = "Success";
    private static final String ALERT_SUCCESS_HEADER = "Successfully imported data!";
    private static final String ALERT_SUCCESS_CONTENT = "Address book data imported successfully!";

    // Failed
    private static final String ALERT_IMPORT_FAILED_TITLE = "Import Failed";
    private static final String ALERT_IMPORT_FAILED_HEADER = "Failed to import data";
    private static final String ALERT_IMPORT_FAILED_TEXT =
            "Please ensure that you only paste the content of the valid address book JSON file.";

    private static final String ALERT_NO_CLIPBOARD_TITLE = "Error";
    private static final String ALERT_NO_CLIPBOARD_HEADER = "No Clipboard Data";
    private static final String ALERT_NO_CLIPBOARD_TEXT = "No valid text found in clipboard.";

    private static final String ALERT_NO_DATA_TITLE = "No Data";
    private static final String ALERT_NO_DATA_HEADER = "No Data Found";
    private static final String ALERT_NO_DATA_TEXT = "Please paste JSON data before saving.";

    private final Logic logic;

    @FXML
    private Text jsonPreview;

    /**
     * Creates a new ImportWindow.
     *
     * @param root  Stage to use as the root of the ImportWindow.
     * @param logic Logic instance to handle saving data.
     */
    public ImportWindow(Stage root, Logic logic) {
        super(FXML_FILE, root);
        this.logic = logic;
        jsonPreview.setText(DEFAULT_PREVIEW_TEXT);
        setupKeyboardShortcuts();
    }

    /**
     * Creates a new ImportWindow.
     */
    public ImportWindow(Logic logic) {
        this(new Stage(), logic);
    }

    /**
     * Sets up keyboard shortcuts for the import window.
     * Ctrl+V: Paste JSON from clipboard
     * Ctrl+S: Save and import data
     */
    private void setupKeyboardShortcuts() {
        KeyCombination pasteKeyCombination = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);
        KeyCombination saveKeyCombination = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);

        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (pasteKeyCombination.match(event)) {
                pasteJson();
                event.consume();
            } else if (saveKeyCombination.match(event)) {
                saveJson();
                event.consume();
            }
        });
    }

    /**
     * Shows the import window.
     *
     * @throws IllegalStateException <ul>
     *                               <li>
     *                               if this method is called on a thread other than the JavaFX
     *                               Application Thread.
     *                               </li>
     *                               <li>
     *                               if this method is called during animation or layout processing.
     *                               </li>
     *                               <li>
     *                               if this method is called on the primary stage.
     *                               </li>
     *                               <li>
     *                               if {@code dialogStage} is already showing.
     *                               </li>
     *                               </ul>
     */
    public void show() {
        logger.fine("Showing import window.");
        jsonPreview.setText(DEFAULT_PREVIEW_TEXT);
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the import window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the import window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the import window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Pastes JSON data from the system clipboard.
     */
    @FXML
    public void pasteJson() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasString()) {
            jsonPreview.setText(clipboard.getString());
        } else {
            ShowAlert.showAlertDialogAndWait(getRoot(), AlertType.WARNING, ALERT_NO_CLIPBOARD_TITLE,
                    ALERT_NO_CLIPBOARD_HEADER,
                    ALERT_NO_CLIPBOARD_TEXT);
        }
    }

    /**
     * Saves the JSON text from the preview to the address book.
     * Validates the JSON and imports it through the Logic layer.
     */
    @FXML
    public void saveJson() {
        String text = jsonPreview.getText();

        if (this.isEmptyText(text)) {
            this.showEmptyTextAlertDialog();
            return;
        }

        try {
            this.importData(text);
        } catch (IOException e) {
            this.handleImportError(e);
        }
    }

    /**
     * Imports the JSON data using the logic layer.
     */
    private void importData(String jsonData) throws IOException {
        logic.importJsonString(jsonData);
        this.showSuccessAlertAndClose();
    }

    /**
     * Shows success alert and closes the import window.
     */
    private void showSuccessAlertAndClose() {
        ShowAlert.showAlertDialogAndWait(getRoot(), AlertType.INFORMATION, ALERT_SUCCESS_TITLE,
                ALERT_SUCCESS_HEADER, ALERT_SUCCESS_CONTENT);
        hide();
    }

    /**
     * Handles import error by logging and showing error alert.
     */
    private void handleImportError(IOException e) {
        logger.warning("Failed to import JSON: " + e.getMessage());
        this.showImportFailedAlertDialog();
    }

    /**
     * Checks if the provided text is empty or default preview text.
     */
    private boolean isEmptyText(String text) {
        return StringUtil.isNullOrEmpty(text) || text.equals(DEFAULT_PREVIEW_TEXT);
    }

    /**
     * Displays alert dialog when no data is pasted.
     */
    private void showEmptyTextAlertDialog() {
        ShowAlert.showAlertDialogAndWait(getRoot(), AlertType.WARNING, ALERT_NO_DATA_TITLE, ALERT_NO_DATA_HEADER,
                ALERT_NO_DATA_TEXT);
    }

    /**
     * Displays alert dialog when import fails due to IOException.
     */
    private void showImportFailedAlertDialog() {
        ShowAlert.showAlertDialogAndWait(getRoot(), AlertType.ERROR, ALERT_IMPORT_FAILED_TITLE,
                ALERT_IMPORT_FAILED_HEADER,
                ALERT_IMPORT_FAILED_TEXT);
    }
}
