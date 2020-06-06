package de.alex0219.punishment;

import de.alex0219.punishment.ban.BanManager;
import de.alex0219.punishment.commands.*;
import de.alex0219.punishment.db.RedisConnector;
import de.alex0219.punishment.listeners.ListenerChat;
import de.alex0219.punishment.listeners.ListenerJoin;
import de.alex0219.punishment.user.DBUser;
import de.alex0219.punishment.user.RankManager;
import de.alex0219.punishment.uuid.UUIDFetcher;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

public class PunishmentBootstrap extends Plugin {

    public static PunishmentBootstrap instance;
    public Jedis jedis;
    public RedisConnector redisConnector;

    public BanManager banManager;

    RankManager rankManager;

    public static PunishmentBootstrap getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        redisConnector = new RedisConnector();
        redisConnector.connectToRedis("127.0.0.1", 6379);
        jedis = redisConnector.getJedis();
        rankManager = new RankManager();
        banManager = new BanManager();
        BungeeCord.getInstance().getPluginManager().registerListener(this, new ListenerJoin());
        BungeeCord.getInstance().getPluginManager().registerListener(this, new ListenerChat());

        if (!getJedis().exists("punishmentHistoryCount")) {
            getJedis().set("punishmentHistoryCount", "1");
        }
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandBan("ban"));
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandMute("mute"));
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandUnban("unban"));
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandUnmute("unmute"));
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandLookup("lookup"));

        BungeeCord.getInstance().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                int unbannedPlayers = 0;
                int unmutedPlayers = 0;
                for (final String entry : PunishmentBootstrap.getInstance().getJedis().keys("uuid:*")) {
                    if (PunishmentBootstrap.getInstance().getJedis().hget(entry, "banned").equalsIgnoreCase("true")) {
                        final long banEndTime = Long.valueOf(PunishmentBootstrap.getInstance().getJedis().hget(entry, "banEnd"));
                        if (System.currentTimeMillis() >= banEndTime) {
                            //auto unban player
                            final String bannedUUID = entry.replace("uuid:", "");
                            final DBUser dbUser = new DBUser(bannedUUID, UUIDFetcher.getName(bannedUUID));
                            PunishmentBootstrap.getInstance().getBanManager().unbanPlayer(dbUser);
                            unbannedPlayers++;
                        }
                    } else if (PunishmentBootstrap.getInstance().getJedis().hget(entry, "muted").equalsIgnoreCase("true")) {
                        final long muteEndTime = Long.valueOf(PunishmentBootstrap.getInstance().getJedis().hget(entry, "muteEnd"));
                        if (System.currentTimeMillis() >= muteEndTime) {
                            //auto unmute player
                            final String mutedUUID = entry.replace("uuid:", "");
                            final DBUser dbUser = new DBUser(mutedUUID, UUIDFetcher.getName(mutedUUID));
                            PunishmentBootstrap.getInstance().getBanManager().unmutePlayer(dbUser);
                            unmutedPlayers++;
                        }
                    }
                }
                System.out.println("Backend ->" + unbannedPlayers + " players were unbanned and " + unmutedPlayers + " players were unmuted in the last minute.");
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public void onDisable() {
        jedis.disconnect();
    }

    public Jedis getJedis() {
        return jedis;
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public BanManager getBanManager() {
        return banManager;
    }
}
