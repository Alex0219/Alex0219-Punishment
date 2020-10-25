package de.alex0219.punishment;

import de.alex0219.punishment.ban.BanManager;
import de.alex0219.punishment.commands.*;
import de.alex0219.punishment.db.RedisConnector;
import de.alex0219.punishment.listeners.ListenerChat;
import de.alex0219.punishment.listeners.ListenerJoin;
import de.alex0219.punishment.telegram.TelegramUtils;
import de.alex0219.punishment.user.DBUser;
import de.alex0219.punishment.user.RankManager;
import de.alex0219.punishment.uuid.UUIDFetcher;
import de.alex0219.punishment.rest.RestServer;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PunishmentBootstrap extends Plugin {

    public static PunishmentBootstrap instance;
    public Jedis jedis;
    public RedisConnector redisConnector;
    public BanManager banManager;
    RankManager rankManager;
    boolean maintenance;
    public ArrayList<ProxiedPlayer> msgSpy = new ArrayList<>();
    HashMap<ProxiedPlayer, ProxiedPlayer> msgs = new HashMap<>();
    TelegramUtils telegramUtils;
    RestServer restServer;

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
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandKick("kick"));
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandPBan("pban"));
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandPMute("pmute"));
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandBan("ban"));
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandMute("mute"));
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandUnban("unban"));
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandUnmute("unmute"));
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandLookup("lookup"));
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandPlayerInfo("playerinfo"));
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandRank("rank"));
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandClearlookup("clearlookup"));
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandMaintenance("maintenance"));
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandMSG("msg"));
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandR("r"));
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandMSGSpy("msgspy"));
        maintenance = Boolean.valueOf(getJedis().get("bungeemaintenance"));
        telegramUtils = new TelegramUtils();
        restServer = new RestServer(8080);
        restServer.start();


        BungeeCord.getInstance().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {

                BungeeCord.getInstance().getPlayers().forEach(players -> {
                    players.setTabHeader(new TextComponent("§7» §bMC-Survival.de §7« \n §7Dein Classic Minecraft Survival Server \nSpieler online: §c"+ BungeeCord.getInstance().getPlayers().size()+"/"+BungeeCord.getInstance().getConfig().getPlayerLimit()), new TextComponent("§7Du möchtest uns unterstützen? §e/vote \n§7Unser Discord: §cdiscord.gg/Jye3Cut"));
                });

                try {
                    PunishmentBootstrap.getInstance().getJedis().set("timeoutkey","yes");
                    if(PunishmentBootstrap.getInstance().getJedis().exists("uuid:")) {
                        PunishmentBootstrap.getInstance().getJedis().del("uuid:");
                    }
                } catch(JedisConnectionException exception) {
                    PunishmentBootstrap.getInstance().redisConnector.connectToRedis("127.0.0.1", 6379);
                    PunishmentBootstrap.getInstance().jedis = PunishmentBootstrap.getInstance().redisConnector.getJedis();
                    System.out.println("Reconnected to redis!");
                }
                int unbannedPlayers = 0;
                int unmutedPlayers = 0;
                for (final String entry : PunishmentBootstrap.getInstance().getJedis().keys("uuid:*")) {
                    if (PunishmentBootstrap.getInstance().getJedis().hget(entry, "banned").equalsIgnoreCase("true")) {
                        if (!PunishmentBootstrap.getInstance().getJedis().hget(entry, "banEnd").equalsIgnoreCase("-1")) {
                            final long banEndTime = Long.valueOf(PunishmentBootstrap.getInstance().getJedis().hget(entry, "banEnd"));
                            if (System.currentTimeMillis() >= banEndTime) {
                                //auto unban player
                                final String bannedUUID = entry.replace("uuid:", "");
                                final DBUser dbUser = new DBUser(bannedUUID, UUIDFetcher.getName(bannedUUID));
                                PunishmentBootstrap.getInstance().getBanManager().unbanPlayer(dbUser,"Auto-Unban");
                                unbannedPlayers++;
                            }
                        }

                    } else if (PunishmentBootstrap.getInstance().getJedis().hget(entry, "muted").equalsIgnoreCase("true")) {

                        if (!PunishmentBootstrap.getInstance().getJedis().hget(entry, "muteEnd").equalsIgnoreCase("-1")) {
                            final long muteEndTime = Long.valueOf(PunishmentBootstrap.getInstance().getJedis().hget(entry, "muteEnd"));
                            if (System.currentTimeMillis() >= muteEndTime) {
                                //auto unmute player
                                final String mutedUUID = entry.replace("uuid:", "");
                                final DBUser dbUser = new DBUser(mutedUUID, UUIDFetcher.getName(mutedUUID));
                                PunishmentBootstrap.getInstance().getBanManager().unmutePlayer(dbUser,"Auto-Unmute");
                                unmutedPlayers++;
                            }
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

    public HashMap<ProxiedPlayer, ProxiedPlayer> getMsgs() {
        return msgs;
    }

    public ArrayList<ProxiedPlayer> getMsgSpy() {
        return msgSpy;
    }

    public TelegramUtils getTelegramUtils() {
        return telegramUtils;
    }

    public RedisConnector getRedisConnector() {
        return redisConnector;
    }
}
