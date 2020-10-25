package de.alex0219.punishment.commands;

import de.alex0219.punishment.PunishmentBootstrap;
import de.alex0219.punishment.ban.ranks.RankEnum;
import de.alex0219.punishment.user.DBUser;
import de.alex0219.punishment.uuid.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Alexander on 08.08.2020
 * © 2020 Alexander Fiedler
 **/
public class CommandPlayerInfo extends Command {
    public CommandPlayerInfo(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("bungee.playerinfo")) {
            if(args.length == 1) {
                 DBUser dbUser = PunishmentBootstrap.getInstance().getRankManager().getDBUser(args[0]);;

                if(dbUser == null) {
                    dbUser = new DBUser(UUIDFetcher.getUUID(args[0]), args[0]);
                }

                if (!dbUser.userExists()) {
                    commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cEs kann keine Info für einen nicht existenten Spieler angezeigt werden."));
                    return;
                }

                //player exists

                Instant instantFirst = Instant.ofEpochMilli(Long.valueOf(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "firstLogin")));
                ZonedDateTime firstLoginTime = instantFirst.atZone(ZoneId.of("Europe/Berlin"));

                DateTimeFormatter firstLoginFormatter = DateTimeFormatter.ofPattern(" dd.MM.yyyy kk:mm:ss");

                Instant instLast = Instant.ofEpochMilli(Long.valueOf(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "lastlogin")));
                ZonedDateTime lastLoginTime = instLast.atZone(ZoneId.of("Europe/Berlin"));

                DateTimeFormatter lastLoginFormatter = DateTimeFormatter.ofPattern(" dd.MM.yyyy kk:mm:ss");

                commandSender.sendMessage(new TextComponent("§7Info über: " + dbUser.getName()));
                commandSender.sendMessage(new TextComponent("§7Erster Login:" + firstLoginFormatter.format(firstLoginTime)));
                commandSender.sendMessage(new TextComponent("§7Letzter Login:" + lastLoginFormatter.format(lastLoginTime)));
                if(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "worlds") !=null) {
                    commandSender.sendMessage(new TextComponent("§7Welten: " + PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "worlds").replace("[","").replace("]","")));
                }
                commandSender.sendMessage(new TextComponent("§7Anzahl der Logins: " + PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "logins")));
                commandSender.sendMessage(new TextComponent("§7Anzahl der Votes: " + PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "votes")));
                commandSender.sendMessage(new TextComponent("§7Rang: " + PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "rank")));
                commandSender.sendMessage(new TextComponent("§7Ontime: " + PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "ontime") + " Minuten"));
                DBUser executor = PunishmentBootstrap.getInstance().getRankManager().getDBUser(commandSender.getName());
                if(executor.getRank() == RankEnum.ADMIN) {
                    commandSender.sendMessage(new TextComponent("§7Letzte IP: " + PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "lastip").replace("/","")));
                }

            } else {
                commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» Bitte verwende /playerinfo <Spieler>"));
            }
        } else {
            commandSender.sendMessage(new TextComponent("§cYou do not have permission to execute this command!"));
        }
    }
}
