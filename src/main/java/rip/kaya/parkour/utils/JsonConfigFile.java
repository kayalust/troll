package rip.kaya.parkour.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import rip.kaya.parkour.ParkourPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/*
 * Property of kayalust © 2022
 * Project: Troll
 */

@Getter @Setter
public class JsonConfigFile {

    private final File path, file;
    private JsonElement element;

    @SneakyThrows
    public JsonConfigFile(File path, String name) {
        this.path = path;

        if (!path.exists())
            path.mkdirs();

        file = new File(path, name + ".json");

        if (!file.exists())
            file.createNewFile();

        element = JsonParser.parseReader(new FileReader(file));

        if (element.isJsonNull()) {
            element = new JsonObject();
        }
    }

    @SneakyThrows
    public void save() {
        FileWriter fileWriter = new FileWriter(file);

        fileWriter.write(ParkourPlugin.GSON.toJson(element));

        fileWriter.flush();
        fileWriter.close();
    }
}
