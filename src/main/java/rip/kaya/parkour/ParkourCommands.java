package rip.kaya.parkour;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.kaya.parkour.objects.ParkourCheckpoint;
import rip.kaya.parkour.objects.ParkourSession;
import rip.kaya.parkour.objects.Profile;
import rip.kaya.parkour.utils.CC;
import rip.kaya.parkour.utils.LocationUtil;
import rip.kaya.parkour.utils.command.annotation.Command;
import rip.kaya.parkour.utils.command.annotation.Sender;

/*
 * Property of kayalust © 2022
 * Project: Troll
 */
@RequiredArgsConstructor
public class ParkourCommands {

    private final ParkourPlugin plugin;

    @Command(name = "", aliases = "help", desc = "Main command for managing parkour")
    public void mainHelp(@Sender Player player) {
        player.sendMessage(CC.translate("&c/parkour start"));
        player.sendMessage(CC.translate("&c/parkour cancel"));
        player.sendMessage(CC.translate("&c/parkour checkpoint"));

        if (player.hasPermission(plugin.getAdminPermission())) {
            player.sendMessage(CC.translate("&c/parkour setstart"));
            player.sendMessage(CC.translate("&c/parkour setend"));
            player.sendMessage(CC.translate("&c/parkour addcheckpoint"));
            player.sendMessage(CC.translate("&c/parkour removecheckpoint"));
        }
    }

    @Command(name = "start", desc = "Starts a parkour session")
    public void start(@Sender Player player) {
        Profile profile = plugin.getProfileHandler().getByUUID(player.getUniqueId());

        if (profile.isInParkour()) {
            player.sendMessage(CC.translate("&cYou cannot do this right now!"));
            return;
        }

        ParkourSession session = new ParkourSession(player);
        session.begin();
    }

    @Command(name = "cancel", desc = "Cancel a parkour session")
    public void cancel(@Sender Player player) {
        Profile profile = plugin.getProfileHandler().getByUUID(player.getUniqueId());

        if (!profile.isInParkour()) {
            player.sendMessage(CC.translate("&cYou are not in parkour!"));
            return;
        }

        profile.getParkourSession().cancel();
    }

    @Command(name = "addcheckpoint", desc = "Adds a checkpoint to parkour")
    public void addCheckpoint(@Sender Player player) {
        Profile profile = plugin.getProfileHandler().getByUUID(player.getUniqueId());

        if (profile.isInParkour()) {
            player.sendMessage(CC.translate("&cYou cannot do this right now!"));
            return;
        }

        if (player.getLocation().getBlock().getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
            if (LocationUtil.isCheckpoint(player.getLocation())) {
                player.sendMessage(CC.translate("&cThis checkpoint already exists!"));
                return;
            }

            ParkourCheckpoint checkpoint = new ParkourCheckpoint();
            checkpoint.setLocation(player.getLocation());
            plugin.getParkourHandler().getCheckpoints().add(checkpoint);
            plugin.getParkourHandler().save();
            player.sendMessage(CC.translate("&aSuccessfully added a new checkpoint to parkour!"));
            return;
        }

        player.sendMessage(CC.translate("&cYou must be standing on an iron pressure plate!"));
    }

    @Command(name = "removecheckpoint", desc = "Removes a checkpoint from parkour")
    public void removeCheckpoint(@Sender Player player) {
        Profile profile = plugin.getProfileHandler().getByUUID(player.getUniqueId());

        if (profile.isInParkour()) {
            player.sendMessage(CC.translate("&cYou cannot do this right now!"));
            return;
        }

        if (player.getLocation().getBlock().getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
            if (!LocationUtil.isCheckpoint(player.getLocation())) {
                player.sendMessage(CC.translate("&cThis is not a checkpoint!"));
                return;
            }

            plugin.getParkourHandler().getCheckpoints().removeIf(c -> LocationUtil.isCheckpoint(c.getLocation()));
            plugin.getParkourHandler().save();
            player.sendMessage(CC.translate("&aSuccessfully removed a checkpoint from parkour!"));
            return;
        }

        player.sendMessage(CC.translate("&cYou must be standing on an iron pressure plate!"));
    }

    @Command(name = "setstart", desc = "Sets the start point of parkour")
    public void setStart(@Sender Player player) {
        Profile profile = plugin.getProfileHandler().getByUUID(player.getUniqueId());

        if (profile.isInParkour()) {
            player.sendMessage(CC.translate("&cYou cannot do this right now!"));
            return;
        }

        if (player.getLocation().getBlock().getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
            plugin.getParkourHandler().setStartPoint(player.getLocation());
            plugin.getParkourHandler().save();
            player.sendMessage(CC.translate("&aSuccessfully set the start position to your standing position!"));
            return;
        }

        player.sendMessage(CC.translate("&cYou must be standing on a gold pressure plate!"));
    }

    @Command(name = "setend", desc = "Sets the end point of parkour")
    public void setEnd(@Sender Player player) {
        Profile profile = plugin.getProfileHandler().getByUUID(player.getUniqueId());

        if (profile.isInParkour()) {
            player.sendMessage(CC.translate("&cYou cannot do this right now!"));
            return;
        }

        if (player.getLocation().getBlock().getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
            plugin.getParkourHandler().setEndPoint(player.getLocation());
            plugin.getParkourHandler().save();
            player.sendMessage(CC.translate("&aSuccessfully set the end position to your standing position!"));
            return;
        }

        player.sendMessage(CC.translate("&cYou must be standing on a gold pressure plate!"));
    }
}
