package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hypixel.hytale.server.core.util.io.BlockingDiskFile;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class ConfigObjectProvider<T> extends BlockingDiskFile {
    @Nonnull
    public T config;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    public ConfigObjectProvider(String configFileName, Class<T> clazz) {
        super(PathUtils.getPathForConfig(configFileName));

        try {
            this.config = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        PathUtils.initializeAndEnsurePathing(PathUtils.getPathForConfig(configFileName), this);
    }

    @Override
    protected void read(BufferedReader bufferedReader) throws IOException {
        JsonObject root = JsonParser.parseReader(bufferedReader).getAsJsonObject();

        if (root.has("config")) {
            this.config = (T) GSON.fromJson(root.get("config"), config.getClass());
        }
    }

    @Override
    protected void write(BufferedWriter bufferedWriter) throws IOException {
        JsonObject root = new JsonObject();
        root.add("config", GSON.toJsonTree(this.config));
        bufferedWriter.write(GSON.toJson(root));
    }

    @Override
    protected void create(@Nonnull BufferedWriter fileWriter) throws IOException {
        JsonObject root = new JsonObject();
        root.add("config", GSON.toJsonTree(this.config));
        fileWriter.write(GSON.toJson(root));
    }
}
