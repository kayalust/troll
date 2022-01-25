package rip.kaya.parkour.objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import rip.kaya.parkour.ParkourPlugin;
import rip.kaya.parkour.utils.CC;
import rip.kaya.parkour.utils.DurationFormatter;
import rip.kaya.parkour.utils.TimeUtil;

/*
 * Property of kayalust © 2022
 * Project: Troll
 */
@Getter @Setter
public class ParkourSession {

    private Player player;

    private long timeStarted;
    private long timeEnded;

    private ParkourCheckpoint lastCheckpoint;

    private final ParkourPlugin plugin = ParkourPlugin.getInstance();

    public ParkourSession(Player player) {
        this.player = player;
    }

    public void begin() {
        timeStarted = System.currentTimeMillis();

        Profile profile = plugin.getProfileHandler().getByUUID(player.getUniqueId());
        profile.setParkourSession(this);
    }

    // This is not used anywhere, made just in case
    public void goToLastCheckpoint() {
        if (lastCheckpoint == null) {
            player.teleport(plugin.getParkourHandler().getStartPoint());
            return;
        }

        player.teleport(lastCheckpoint.getLocation());
    }

    public void finish() {
        timeEnded = System.currentTimeMillis();
        ParkourAttempt attempt = new ParkourAttempt(this);
        Profile profile = plugin.getProfileHandler().getByUUID(player.getUniqueId());

        player.sendMessage(CC.translate("&7Parkour finished! Time elapsed: &a"
                + DurationFormatter.getRemaining(this.getTimeElapsed(), true) + (profile.isNewBest(attempt) ? " &6&lNEW BEST" : "")));
        if (profile.getBestAttempt() == null || profile.isNewBest(attempt)) profile.setBestAttempt(attempt);
        profile.getAttempts().put(attempt.getAttemptId(), attempt);
        plugin.getProfileHandler().saveProfile(profile);

        profile.setParkourSession(null);
    }

    public void cancel() {
        Profile profile = plugin.getProfileHandler().getByUUID(player.getUniqueId());
        profile.setParkourSession(null);
    }

    public long getTimeElapsed() {
        return timeEnded - timeStarted;
    }
}
