package seedu.address.logic.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Used to access the actual filesystem.
 */
public class SystemFileSystemProvider implements FileSystemProvider {
    @Override
    public boolean exists(Path path) {
        return path != null && Files.exists(path);
    }

    @Override
    public String readFile(Path path) throws IOException {
        return seedu.address.commons.util.FileUtil.readFromFile(path);
    }
}
