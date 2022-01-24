package rip.kaya.parkour.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import rip.kaya.parkour.ParkourPlugin;
import rip.kaya.parkour.objects.ParkourCheckpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@UtilityClass
public class LocationUtil {

    public Location[] getFaces(Location start) {
        Location[] faces = new Location[4];
        faces[0] = new Location(start.getWorld(), start.getX() + 1, start.getY(), start.getZ());
        faces[1] = new Location(start.getWorld(), start.getX() - 1, start.getY(), start.getZ());
        faces[2] = new Location(start.getWorld(), start.getX(), start.getY() + 1, start.getZ());
        faces[3] = new Location(start.getWorld(), start.getX(), start.getY() - 1, start.getZ());
        return faces;
    }

    public String serialize(Location location) {
        return location == null ? "null" : Objects.requireNonNull(location.getWorld()).getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ() +
                ":" + location.getYaw() + ":" + location.getPitch();
    }

    public Location deserialize(String source) {
        if (source == null || source.equalsIgnoreCase("null")) {
            return null;
        }

        String[] split = source.split(":");
        World world = Bukkit.getServer().getWorld(split[0]);

        if (world == null) {
            return null;
        }

        return new Location(world, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }

    public List<Location> getCircle(Location center, float radius, int amount) {
        List<Location> list = new ArrayList<>();
        for(int i = 0; i < amount; i++) {
            double a = 2 * Math.PI / amount * i;
            double x = Math.cos(a) * radius;
            double z = Math.sin(a) * radius;
            list.add(center.clone().add(x, 0, z));
        }
        return list;
    }

    public boolean isCheckpoint(Location location) {
        if (location.getBlock().getType() == Material.LEGACY_IRON_PLATE || location.getBlock().getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
            for (ParkourCheckpoint checkpoint : ParkourPlugin.getInstance().getParkourHandler().getCheckpoints()) {
                if (checkpoint.getLocation().equals(location)) return true;
            }
        }

        return false;
    }

    public boolean isValidPoint(Location location) {
        if (location.getBlock().getType() == Material.LEGACY_GOLD_PLATE || location.getBlock().getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
            return ParkourPlugin.getInstance().getParkourHandler().getEndPoint().equals(location) || ParkourPlugin.getInstance().getParkourHandler().getStartPoint().equals(location);
        }

        return false;
    }

    public boolean isInRegion(Player player) {
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(player.getLocation());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(loc);

        for (ProtectedRegion region : set) {
            return region.getId().equals(ParkourPlugin.getInstance().getRegionName()) || region.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        }

        return false;
    }
}