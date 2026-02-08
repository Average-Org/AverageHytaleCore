package util;

import com.hypixel.hytale.server.core.util.io.BlockingDiskFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Facilitates pathing for plugin configuration.
 */
public final class PathUtils {
    private PathUtils() {}
    private static final String DEFAULT_NAME = "UndefinedPlugin";

    /**
     * The name of the plugin directory.
     */
    private static String modDirectoryName = DEFAULT_NAME;

    /**
     * Sets the name of the plugin directory. Should be called on plugin load/setup.
     *
     * @param name The name of the plugin directory.
     */
    public static void setModDirectoryName(String name) {
        modDirectoryName = name;
    }

    /**
     * Gets the name of the plugin directory.
     *
     * @return The name of the plugin directory.
     */
    public static String getModDirectoryName() {
        if (modDirectoryName.equals(DEFAULT_NAME)) {
            // get caller's package and attempt to use that name
            modDirectoryName = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                    .walk(frames -> frames
                            .skip(1)
                            .findFirst()
                            .map(f -> f.getDeclaringClass().getPackageName())
                            .orElse(DEFAULT_NAME));
        }

        return modDirectoryName;
    }

    /**
     * Gets the path for a config file based on a magic string.
     *
     * @param configFile Config file name
     * @return Path to config file
     */
    public static Path getPathForConfig(String configFile) {
        return Paths.get("mods", modDirectoryName, configFile);
    }

    /**
     * Attempts to initialize the pathing for a config file, creating directories if necessary.
     *
     * @param path     Path to config file
     * @param provider Provider for the config file
     */
    public static void initializeAndEnsurePathing(Path path, BlockingDiskFile provider) {
        try {
            Files.createDirectories(path.getParent());
            provider.syncLoad();
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize pathing for config file at path: " + path, e);
        }
    }
}
