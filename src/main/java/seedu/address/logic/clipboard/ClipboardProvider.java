package seedu.address.logic.clipboard;

/**
 * Abstraction for system clipboard, needed to enable testing.
 */
public interface ClipboardProvider {
    /** Gets the current string content from clipboard. */
    String getString();

    /**
     * Sets the clipboard content to the specified string.
     *
     * @param value The string to set the clipboard to.
    */
    void setString(String value);
}
