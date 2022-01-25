package rip.kaya.parkour.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
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
import rip.kaya.parkour.utils.DurationFormatter;
import rip.kaya.parkour.utils.LocationUtil;
import rip.kaya.parkour.utils.TimeUtil;

import java.util.Objects;

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

        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (event.getAction() != Action.PHYSICAL) return;
        if (plugin.getParkourHandler().getStartPoint() == null || plugin.getParkourHandler().getEndPoint() == null) return;
        Material blockType = Objects.requireNonNull(event.getClickedBlock()).getType();

        if (blockType == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
            if (!profile.isInParkour() && LocationUtil.isValidStart(event.getClickedBlock().getLocation())) {
                ParkourSession session = new ParkourSession(player);
                session.begin();
                player.sendMessage(CC.translate("&aYou have started the parkour!"));
            } else if (profile.isInParkour() || !LocationUtil.isValidEnd(event.getClickedBlock().getLocation())){
                ParkourSession session = profile.getParkourSession();
                session.finish();
            }
        } else if (blockType == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
            if (!LocationUtil.isCheckpoint(player.getLocation().getBlock().getLocation())) return;
            if (!profile.isInParkour()) return;

            ParkourSession session = profile.getParkourSession();
            ParkourCheckpoint checkpoint = plugin.getParkourHandler().getCheckpointByLocation(event.getClickedBlock().getLocation());

            session.setLastCheckpoint(checkpoint);
            player.sendMessage(CC.translate("&aYou have reached this checkpoint in " + DurationFormatter.getRemaining(session.getTimeElapsed(), true) + "!"));
        }
    }
}