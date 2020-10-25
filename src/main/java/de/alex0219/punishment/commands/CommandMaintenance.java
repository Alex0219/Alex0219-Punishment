package de.alex0219.punishment.commands;

import de.alex0219.punishment.PunishmentBootstrap;
import de.alex0219.punishment.ban.ranks.RankEnum;
import de.alex0219.punishment.user.DBUser;
import de.alex0219.punishment.uuid.UUIDFetcher;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Alexander on 09.08.2020
 * © 2020 Alexander Fiedler
 **/
public class CommandMaintenance extends Command {

    public CommandMaintenance(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        final DBUser executor = new DBUser(UUIDFetcher.getUUID(commandSender.getName()), commandSender.getName());
        if (executor.getRank() == RankEnum.ADMIN) {
            if (args.length == 0) {
                if (!(commandSender instanceof ProxiedPlayer)) {
                    commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cDieser Befehl kann nicht von der Konsole ausgeführt werden."));
                    return;
                }
                if(PunishmentBootstrap.getInstance().getJedis().get("bungeemaintenance").equalsIgnoreCase("true")) {
                    PunishmentBootstrap.getInstance().getJedis().set("bungeemaintenance","false");
                    commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cDer Wartungsmodus wurde deaktiviert."));
                } else if (PunishmentBootstrap.getInstance().getJedis().get("bungeemaintenance").equalsIgnoreCase("false")) {
                    PunishmentBootstrap.getInstance().getJedis().set("bungeemaintenance","true");
                    commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §aDer Wartungsmodus wurde aktiviert."));
                    BungeeCord.getInstance().getPlayers().forEach(players -> {
                        DBUser dbUser = new DBUser(players.getUniqueId().toString(),players.getName());
                        if(dbUser.getRank().getRankLevel() == 1) {
                            players.disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&',PunishmentBootstrap.getInstance().getJedis().get("bungeemaintenancetext"))));
                        }
                    });
                }

            } else {
                commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» Bitte verwende /maintenance"));
            }
        } else {
            commandSender.sendMessage(new TextComponent("§cYou do not have permission to execute this command!"));
        }


    }
}
