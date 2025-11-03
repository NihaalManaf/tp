package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.FileUtil;
import seedu.address.model.person.Status;

/**
 * Manages storage of email templates in text files.
 */
public class TemplateStorageManager implements TemplateStorage {
    private static final Logger logger = LogsCenter.getLogger(TemplateStorageManager.class);
    private static final String TEMPLATE_FILE_SUFFIX = "Template.txt";

    private final Path templateDirectoryPath;

    /**
     * Creates a TemplateStorageManager with the given directory path.
     *
     * @param templateDirectoryPath The directory where template files are stored.
     */
    public TemplateStorageManager(Path templateDirectoryPath) {
        this.templateDirectoryPath = templateDirectoryPath;
    }

    @Override
    public Path getTemplateDirectoryPath() {
        return templateDirectoryPath;
    }

    /**
     * Returns the file path for a template based on the status.
     *
     * @param status The status to get the file path for.
     * @return The path to the template file for this status.
     */
    private Path getTemplateFilePath(Status status) {
        String fileName = status.name().toLowerCase() + TEMPLATE_FILE_SUFFIX;
        return templateDirectoryPath.resolve(fileName);
    }

    @Override
    public String readTemplate(Status status) throws IOException {
        Path filePath = getTemplateFilePath(status);

        if (!Files.exists(filePath)) {
            logger.info("Template file not found for " + status + ", creating with default content");
            String defaultContent = getDefaultTemplate(status);
            saveTemplate(status, defaultContent);
            return defaultContent;
        }

        String content = FileUtil.readFromFile(filePath);

        // If the file contains only whitespace or is empty, replace it with default template
        if (content == null || content.isBlank()) {
            logger.info("Template file for " + status + " is blank, replacing with default content");
            String defaultContent = getDefaultTemplate(status);
            saveTemplate(status, defaultContent);
            return defaultContent;
        }

        return content;
    }

    @Override
    public void saveTemplate(Status status, String content) throws IOException {
        Path filePath = getTemplateFilePath(status);

        // Ensure directory exists
        FileUtil.createIfMissing(templateDirectoryPath);

        // If content is blank (empty or only whitespace), save the default template instead
        String contentToSave = (content == null || content.isBlank()) ? getDefaultTemplate(status) : content;

        FileUtil.writeToFile(filePath, contentToSave);
        logger.info("Saved template for " + status + " to " + filePath);
    }

    @Override
    public String getDefaultTemplate(Status status) {
        String statusName = formatStatusName(status);
        return "This is the default template for status " + statusName;
    }

    /**
     * Formats the status name for display (e.g., UNCONTACTED -> Uncontacted).
     *
     * @param statusToFormat The status to format.
     * @return A formatted status name with proper capitalization.
     */
    private String formatStatusName(Status statusToFormat) {
        String name = statusToFormat.name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}
