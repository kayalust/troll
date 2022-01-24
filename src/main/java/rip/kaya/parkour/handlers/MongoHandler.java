package rip.kaya.parkour.handlers;

/*
 * Property of kayalust © 2022
 * Project: Troll
 */

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import rip.kaya.parkour.ParkourPlugin;

@Getter
@RequiredArgsConstructor
public class MongoHandler {

    protected final ParkourPlugin plugin;

    private MongoDatabase database;
    private MongoClient client;

    private MongoCollection<Document> profiles;

    public void init() {
        String uri = plugin.getConfig().getString("mongo.uri");
        String db = plugin.getConfig().getString("mongo.database");
        if (uri == null || db == null) {
            plugin.getLogger().severe("Mongo URI or database credentials is null!");
            return;
        }

        this.client = MongoClients.create(uri);
        this.database = client.getDatabase(db);

        this.loadCollection();
    }

    private void loadCollection() {
        profiles = this.database.getCollection("profiles");
    }

    public void shutdown() {
        this.client.close();
    }
}
