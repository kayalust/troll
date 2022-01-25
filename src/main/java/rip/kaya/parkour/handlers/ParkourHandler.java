package rip.kaya.parkour.handlers;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import rip.kaya.parkour.ParkourPlugin;
import rip.kaya.parkour.listeners.ParkourListener;
import rip.kaya.parkour.objects.ParkourCheckpoint;
import rip.kaya.parkour.utils.LocationUtil;

import java.util.LinkedList;
import java.util.Objects;

/*
 * Property of kayalust © 2022
 * Project: Troll
 */

@Getter @Setter
@RequiredArgsConstructor
public class ParkourHandler {

    protected final ParkourPlugin plugin;

    private Location startPoint;
    private Location endPoint;

    private final LinkedList<ParkourCheckpoint> checkpoints = Lists.newLinkedList();

    public void init() {
        this.load();

        Bukkit.getPluginManager().registerEvents(new ParkourListener(plugin), plugin);
    }

    public void save() {
        plugin.getConfig().set("parkour.start", LocationUtil.serialize(startPoint));
        plugin.getConfig().set("parkour.end", LocationUtil.serialize(endPoint));
        plugin.saveConfig();

        JsonObject object = plugin.getCheckpointsFile().getElement().getAsJsonObject();

        JsonArray array = new JsonArray();

        for (ParkourCheckpoint checkpoint : checkpoints) {
            JsonObject obj = new JsonObject();

            obj.addProperty("worldName", Objects.requireNonNull(checkpoint.getLocation().getWorld()).getName());
            obj.addProperty("x", checkpoint.getLocation().getBlockX());
            obj.addProperty("y", checkpoint.getLocation().getBlockY());
            obj.addProperty("z", checkpoint.getLocation().getBlockZ());

            array.add(obj);
        }

        object.add("checkpointsData", array);

        plugin.getCheckpointsFile().setElement(object);
        plugin.getCheckpointsFile().save();
    }

    public void load() {
        this.startPoint = plugin.getConfig().getString("parkour.start") != null ? LocationUtil.deserialize(plugin.getConfig().getString("parkour.start")) : null;
        this.endPoint = plugin.getConfig().getString("parkour.end") != null ? LocationUtil.deserialize(plugin.getConfig().getString("parkour.end")) : null;

        JsonObject object = plugin.getCheckpointsFile().getElement().getAsJsonObject();

        JsonArray data = object.get("checkpointsData") == null ? new JsonArray() : object.get("checkpointsData").getAsJsonArray();
        if (data.size() == 0) return; // dont load

        for (JsonElement element : data) {
            ParkourCheckpoint checkpoint = new ParkourCheckpoint();
            JsonObject obj = element.getAsJsonObject();

            checkpoint.setLocation(new Location(
                    Bukkit.getWorld(obj.get("worldName").getAsString()),
                    obj.get("x").getAsDouble(),
                    obj.get("y").getAsDouble(),
                    obj.get("z").getAsDouble())
            );
            checkpoints.add(checkpoint);
        }
    }

    public ParkourCheckpoint getCheckpointByLocation(Location location) {
        return checkpoints.stream().filter(c -> c.getLocation().equals(location)).findFirst().orElse(null);
    }
}
