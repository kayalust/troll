package rip.kaya.parkour.handlers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bukkit.Bukkit;
import rip.kaya.parkour.ParkourPlugin;
import rip.kaya.parkour.listeners.ProfileListener;
import rip.kaya.parkour.objects.ParkourAttempt;
import rip.kaya.parkour.objects.Profile;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/*
 * Property of kayalust © 2022
 * Project: Troll
 */

@Getter
@RequiredArgsConstructor
public class ProfileHandler {

    protected final ParkourPlugin plugin;

    private final ConcurrentMap<UUID, Profile> profiles = Maps.newConcurrentMap();

    public void init() {
        Bukkit.getPluginManager().registerEvents(new ProfileListener(plugin), plugin);
    }

    public void saveAll() {
        profiles.values().forEach(this::saveProfile);
    }

    public void loadProfile(Profile profile) {
        MongoCollection<Document> collection = plugin.getMongoHandler().getProfiles();
        Document document = collection.find(Filters.eq("_id", profile.getUuid().toString())).first();

        if (document == null) {
            plugin.getLogger().info(profile.getUuid() + " is null!");
            this.saveProfile(profile);
            return;
        }

        Document attemptsDocument = (Document) document.get("attempts");

        for (String key : attemptsDocument.keySet()) {
            Document attempt = (Document) attemptsDocument.get(key);
            ParkourAttempt parkourAttempt = new ParkourAttempt();

            parkourAttempt.setAttemptId(UUID.fromString(key));
            parkourAttempt.setTimeStarted(attempt.getLong("timeStarted"));
            parkourAttempt.setTimeEnded(attempt.getLong("timeEnded"));
            parkourAttempt.setTimeElapsed(attempt.getLong("timeElapsed"));
            parkourAttempt.setBest(attempt.getBoolean("best"));

            profile.getAttempts().put(UUID.fromString(key), parkourAttempt);
        }

        plugin.getProfileHandler().getProfiles().put(profile.getUuid(), profile);
    }

    public void saveProfile(Profile profile) {
        MongoCollection<Document> collection = plugin.getMongoHandler().getProfiles();
        Document document = new Document();

        document.put("_id", profile.getUuid().toString());

        Document attemptsDocument = new Document();

        for (ParkourAttempt attempt : profile.getAttempts().values()) {
            Document attemptDocument = new Document();

            attemptDocument.put("timeStarted", attempt.getTimeStarted());
            attemptDocument.put("timeEnded", attempt.getTimeEnded());
            attemptDocument.put("timeElapsed", attempt.getTimeElapsed());
            attemptDocument.put("best", attempt.isBest());

            attemptsDocument.put(attempt.getAttemptId().toString(), attemptDocument);
        }

        document.put("attempts", attemptsDocument);
        collection.replaceOne(Filters.eq("_id", profile.getUuid().toString()), document, new ReplaceOptions().upsert(true));
        plugin.getProfileHandler().getProfiles().replace(profile.getUuid(), profile);
    }

    public Profile getByUUID(UUID uuid) {
        return profiles.get(uuid);
    }

    public List<Profile> getPlayersInParkour() {
        return profiles.values().stream().filter(Profile::isInParkour).collect(Collectors.toList());
    }

    public List<Profile> getPlayersTop(int count) {
        LinkedList<Profile> top = Lists.newLinkedList(profiles.values());
        top.sort(new ProfileComparator());

        return top.stream().limit(count).collect(Collectors.toList());
    }

    private static class ProfileComparator implements Comparator<Profile> {

        @Override
        public int compare(Profile profile1, Profile profile2) {
            if (profile1.getBestAttempt() == null || profile2.getBestAttempt() == null) {
                return 0;
            }

            return (int) (profile1.getBestAttempt().getTimeElapsed() - profile2.getBestAttempt().getTimeElapsed());
        }
    }
}
