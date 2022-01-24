package rip.kaya.parkour.board;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import rip.kaya.parkour.ParkourPlugin;
import rip.kaya.parkour.objects.Profile;
import rip.kaya.parkour.utils.CC;
import rip.kaya.parkour.utils.LocationUtil;
import rip.kaya.parkour.utils.TimeUtil;
import rip.kaya.parkour.utils.board.AssembleAdapter;

import java.util.List;

/*
 * Property of kayalust © 2022
 * Project: Troll
 */
public class ScoreboardAdapter implements AssembleAdapter {

    private final ParkourPlugin plugin = ParkourPlugin.getInstance();

    @Override
    public String getTitle(Player player) {
        return CC.translate(plugin.getScoreboardTitle());
    }

    @Override
    public List<String> getLines(Player player) {
        final List<String> toReturn = Lists.newArrayList();
        Profile profile = plugin.getProfileHandler().getByUUID(player.getUniqueId());

        toReturn.add("&7&o----------------------");
        if (profile.isInParkour() || LocationUtil.isInRegion(player)) {
            toReturn.add("&bBest Attempt: &f" + (profile.getBestAttempt() == null ? "None" : TimeUtil.millisToTimer(profile.getBestAttempt().getTimeElapsed())));
            toReturn.add(" ");
            toReturn.add("&b&lLeaderboards");

            for (int i = 1; i <= 5; i++) {
                for (Profile pf : plugin.getProfileHandler().getPlayersTop(5)) {
                    if (pf.getBestAttempt() == null) { // right here could be done better, but that requires effort and brain usage
                        toReturn.add("&b#" + i + " &7- " + "&f" + Bukkit.getOfflinePlayer(pf.getUuid()).getName() + " &7- " + "&bNone");
                    } else {
                        toReturn.add("&b#" + i + " &7- " + "&f" + Bukkit.getOfflinePlayer(pf.getUuid()).getName() + " &7- " + "&b" + TimeUtil.millisToTimer(pf.getBestAttempt().getTimeElapsed()));
                    }
                }
            }
        } else {
            toReturn.add("Online: &b" + Bukkit.getOnlinePlayers().size());
            toReturn.add("In Parkour: &b" + plugin.getProfileHandler().getPlayersInParkour().size());
            toReturn.add(" ");
            toReturn.add("Best Attempt: &b" + TimeUtil.millisToTimer(profile.getBestAttempt().getTimeElapsed()));
        }
        toReturn.add(" ");
        toReturn.add("&7&o" + plugin.getServerIP());
        toReturn.add("&7&o----------------------");

        return toReturn;
    }
}
