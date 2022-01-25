package rip.kaya.parkour.objects;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.UUID;

/*
 * Property of kayalust © 2022
 * Project: Troll
 */

@Getter @Setter
public class Profile {

    private UUID uuid;

    private ParkourAttempt bestAttempt;
    private ParkourSession parkourSession;

    private final LinkedHashMap<UUID, ParkourAttempt> attempts = Maps.newLinkedHashMap();

    public Profile(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean isInParkour() {
        return parkourSession != null;
    }

    public boolean isNewBest(ParkourAttempt attempt) {
        return bestAttempt == null || bestAttempt.getTimeElapsed() <= attempt.getTimeElapsed();
    }
}
