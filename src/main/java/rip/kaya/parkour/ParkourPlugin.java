package rip.kaya.parkour;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.*;
import rip.kaya.parkour.handlers.*;
import rip.kaya.parkour.board.ScoreboardAdapter;
import rip.kaya.parkour.utils.JsonConfigFile;
import rip.kaya.parkour.utils.board.Assemble;
import rip.kaya.parkour.utils.board.AssembleStyle;
import rip.kaya.parkour.utils.command.CommandService;
import rip.kaya.parkour.utils.command.Drink;
/*
 * Property of kayalust © 2022
 * Project: Troll
 */

@Getter
public final class ParkourPlugin extends JavaPlugin {

    @Getter private static ParkourPlugin instance;

    public static Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private ParkourHandler parkourHandler;
    private ProfileHandler profileHandler;
    private MongoHandler mongoHandler;

    private JsonConfigFile checkpointsFile;

    private String regionName;
    private String adminPermission;
    private String scoreboardTitle;
    private String serverIP;

    @Override
    public void onLoad() {
        instance = this;

        this.saveDefaultConfig();

        checkpointsFile = new JsonConfigFile(getDataFolder(), "checkpoints");
        checkpointsFile.save();
    }

    @Override
    public void onEnable() {
        this.regionName = this.getConfig().getString("parkour.region-name");
        this.adminPermission = this.getConfig().getString("parkour.permission");
        this.scoreboardTitle = this.getConfig().getString("parkour.title");
        this.serverIP = this.getConfig().getString("parkour.server-ip");

        mongoHandler = new MongoHandler(this);
        mongoHandler.init();

        profileHandler = new ProfileHandler(this);
        profileHandler.init();

        parkourHandler = new ParkourHandler(this);
        parkourHandler.init();

        CommandService drink = Drink.get(this);
        drink.register(new ParkourCommands(this), "parkour");
        drink.registerCommands();

        Assemble assemble = new Assemble(this, new ScoreboardAdapter());
        assemble.setAssembleStyle(AssembleStyle.MODERN);
        assemble.setTicks(1L);
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("Server is restarting!");
        }

        parkourHandler.save();
        profileHandler.saveAll();
        mongoHandler.shutdown();

        instance = null;
    }


}
