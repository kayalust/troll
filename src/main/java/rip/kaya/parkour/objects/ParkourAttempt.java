package rip.kaya.parkour.objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.UUID;

/*
 * Property of kayalust © 2022
 * Project: Troll
 */

@Getter @Setter
public class ParkourAttempt {

    private UUID attemptId;
    private Player player;

    private boolean best = false;
    private long timeStarted;
    private long timeEnded;
    private long timeElapsed;

    public ParkourAttempt(ParkourSession session) {
        this.attemptId = UUID.randomUUID();
        this.player = session.getPlayer();

        this.timeStarted = session.getTimeStarted();
        this.timeEnded = session.getTimeEnded();
        this.timeElapsed = session.getTimeElapsed();
    }

    public ParkourAttempt() {}
}
