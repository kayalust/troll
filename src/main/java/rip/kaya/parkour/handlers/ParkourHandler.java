package rip.kaya.parkour.handlers;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import rip.kaya.parkour.ParkourPlugin;
import rip.kaya.parkour.listeners.ParkourListener;
import rip.kaya.parkour.objects.ParkourCheckpoint;
import rip.kaya.parkour.utils.LocationUtil;

import java.util.LinkedList;
import java.util.Map;
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

        JsonObject object = plugin.getCheckpointsFile().getElement().getAsJsonObject();

        for (ParkourCheckpoint checkpoint : checkpoints) {
            JsonObject obj = new JsonObject();

            obj.addProperty("worldName", Objects.requireNonNull(checkpoint.getLocation().getWorld()).getName());
            obj.addProperty("x", checkpoint.getLocation().getBlockX());
            obj.addProperty("y", checkpoint.getLocation().getBlockY());
            obj.addProperty("z", checkpoint.getLocation().getBlockZ());

            object.add("checkpointsData", obj);
        }

        plugin.getCheckpointsFile().setElement(object);
        plugin.getCheckpointsFile().save();
    }

    public void load() {
        this.startPoint = plugin.getConfig().getString("parkour.start") != null ? LocationUtil.deserialize(plugin.getConfig().getString("parkour.start")) : null;
        this.endPoint = plugin.getConfig().getString("parkour.end") != null ? LocationUtil.deserialize(plugin.getConfig().getString("parkour.end")) : null;

        JsonObject object = plugin.getCheckpointsFile().getElement() == null ? new JsonObject() : plugin.getCheckpointsFile().getElement().getAsJsonObject();

        JsonObject data = object.get("checkpointsData").getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
            ParkourCheckpoint checkpoint = new ParkourCheckpoint();
            JsonObject obj = entry.getValue().getAsJsonObject();

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
