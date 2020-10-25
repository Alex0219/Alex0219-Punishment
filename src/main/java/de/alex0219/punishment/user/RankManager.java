package de.alex0219.punishment.user;

import de.alex0219.punishment.PunishmentBootstrap;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;

/**
 * Created by Alexander on 05.06.2020 00:36
 * © 2020 Alexander Fiedler
 */
public class RankManager {

    public ArrayList<DBUser> dbusers = new ArrayList<>();

    /**
     * Defines whether a user is permitted to ban a specific user.
     *
     * @param executor
     * @param banned
     * @return Boolean
     */
    public boolean isPermittedToBan(DBUser executor, DBUser banned) {
        return executor.getRank().getRankLevel() > banned.getRank().getRankLevel();
    }

    public static void setRank(DBUser dbuser, String rank) {
        if (dbuser.userExists()) {
            PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbuser.getUuid(), "rank", rank);

        }
    }

    public DBUser getDBUser(final String name) {
        for(DBUser dbusers : PunishmentBootstrap.getInstance().getRankManager().getDbusers()) {
            if(dbusers.getName().equalsIgnoreCase(name)) {
                return dbusers;
            }
        }
        return null;
    }

    public void sendMSGSpyMessage(final String message, final String playerFrom, final String playerTo) {
        BungeeCord.getInstance().getPlayers().forEach(players -> {
            if(PunishmentBootstrap.getInstance().getMsgSpy().contains(players)) {
                players.sendMessage(new TextComponent("§bMC-Survival.de §7» §7[§cMSG-SPY§7] " + playerFrom
                        + " §7§7➥ §3"+playerTo+ "§8» §f" + message));
            }
        });
    }

    public ArrayList<DBUser> getDbusers() {
        return dbusers;
    }
}
