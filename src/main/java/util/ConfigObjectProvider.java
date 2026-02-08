package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hypixel.hytale.server.core.util.io.BlockingDiskFile;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * Abstract class to provide easy setup for configuration files based on POJOs.
 * @param <T> A class to create a configuration object from
 */
public abstract class ConfigObjectProvider<T> extends BlockingDiskFile {
    private static final String CONFIG_PROPERTY = "config";

    @Nonnull
    private T config;
    private final Class<T> configClass;

    private static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();


    public ConfigObjectProvider(String configFileName, Class<T> clazz) {
        super(PathUtils.getPathForConfig(configFileName));

        this.configClass = Objects.requireNonNull(clazz);
        try {
            this.config = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate config class: " + clazz.getName(), e);
        }

        PathUtils.initializeAndEnsurePathing(PathUtils.getPathForConfig(configFileName), this);
    }

    @Override
    protected void read(BufferedReader bufferedReader) throws IOException {
        JsonObject rootObject = JsonParser.parseReader(bufferedReader).getAsJsonObject();

        if (!rootObject.has(CONFIG_PROPERTY)) {
            throw new IOException("Config file is missing required root property: " + CONFIG_PROPERTY);
        }

        var configElement = rootObject.get(CONFIG_PROPERTY);
        this.config = GSON.fromJson(configElement, configClass);
    }

    @Override
    protected void write(BufferedWriter writer) throws IOException {
        JsonObject root = new JsonObject();
        root.add(CONFIG_PROPERTY, GSON.toJsonTree(this.config));
        writer.write(GSON.toJson(root));
    }

    @Override
    protected void create(@Nonnull BufferedWriter fileWriter) throws IOException {
        write(fileWriter);
    }

    @NonNullDecl
    public T getConfig() {
        return config;
    }
}
