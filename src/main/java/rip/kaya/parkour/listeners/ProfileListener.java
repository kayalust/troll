package rip.kaya.parkour.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import rip.kaya.parkour.ParkourPlugin;
import rip.kaya.parkour.objects.Profile;

/*
 * Property of kayalust © 2022
 * Project: Troll
 */
@RequiredArgsConstructor
public class ProfileListener implements Listener {

    private final ParkourPlugin plugin;

    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        Profile profile = new Profile(event.getUniqueId());

        plugin.getProfileHandler().loadProfile(profile);
    }
}
