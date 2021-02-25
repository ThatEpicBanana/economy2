package mods.banana.economy2.itemmodules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModuleConfig {
    private final Path path;
    private JsonObject object;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public ModuleConfig(Path path) {
        this.path = path;
    }

    public void readConfigs() {
        String file = "{}";

        try {
            if(Files.exists(path)) {
                file = Files.readString(path);

                if(file.isEmpty() || !file.startsWith("{")) {
                    file = "{}";
                }
            } else {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                Files.write(path, "{}".getBytes());
                file = "{}";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        object = GSON.fromJson(file, JsonObject.class);
    }

    public void saveConfigs() {
        try {
            if(!Files.exists(path)) Files.createFile(path);
            Files.write(path, GSON.toJson(object).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isActivated(ItemModule module) {
        return isActivated(module.getName());
    }

    public boolean isActivated(String string) {
        if(
                object.has(string) &&
                        object.get(string).isJsonPrimitive() &&
                        object.get(string).getAsJsonPrimitive().isBoolean()
        ) {
            return object.get(string).getAsBoolean();
        } else {
            setValue(string, true);
            return true;
        }
    }

    public void setValue(ItemModule module, boolean bool) {
        setValue(module.getName(), bool);
    }

    public void setValue(String string, boolean bool) {
        object.addProperty(string, bool);
        saveConfigs();
    }
}
