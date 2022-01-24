package rip.kaya.parkour.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import rip.kaya.parkour.ParkourPlugin;
import rip.kaya.parkour.objects.ParkourCheckpoint;
import rip.kaya.parkour.objects.ParkourSession;
import rip.kaya.parkour.objects.Profile;
import rip.kaya.parkour.utils.CC;
import rip.kaya.parkour.utils.LocationUtil;
import rip.kaya.parkour.utils.TimeUtil;

/*
 * Property of kayalust © 2022
 * Project: Troll
 */
@RequiredArgsConstructor
public class ParkourListener implements Listener {

    private final ParkourPlugin plugin;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileHandler().getByUUID(player.getUniqueId());

        if (event.getAction() != Action.PHYSICAL) return;
        if (plugin.getParkourHandler().getStartPoint() == null || plugin.getParkourHandler().getEndPoint() == null) return;
        if (profile.isInParkour()) return;
        Material blockType = player.getLocation().getBlock().getType();

        if (blockType == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
            if (!profile.isInParkour()) {
                if (!LocationUtil.isValidStart(player.getLocation())) return;
                ParkourSession session = new ParkourSession(player);
                session.begin();
                player.sendMessage(CC.translate("&aYou have started the parkour!"));
                return;
            }

            if (!LocationUtil.isValidEnd(player.getLocation())) return;

            ParkourSession session = profile.getParkourSession();
            session.finish();
        } else if (blockType == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
            if (!LocationUtil.isCheckpoint(player.getLocation())) return;
            if (!profile.isInParkour()) return;

            ParkourSession session = profile.getParkourSession();
            ParkourCheckpoint checkpoint = plugin.getParkourHandler().getCheckpointByLocation(player.getLocation());

            session.setLastCheckpoint(checkpoint);
            player.sendMessage(CC.translate("&aYou have reached this checkpoint in " + TimeUtil.millisToTimer(session.getTimeElapsed()) + "!"));
        }
    }
}