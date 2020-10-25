package de.alex0219.punishment.commands;

import de.alex0219.punishment.PunishmentBootstrap;
import de.alex0219.punishment.ban.ranks.RankEnum;
import de.alex0219.punishment.user.DBUser;
import de.alex0219.punishment.user.RankManager;
import de.alex0219.punishment.uuid.UUIDFetcher;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Alexander on 08.08.2020
 * © 2020 Alexander Fiedler
 **/
public class CommandRank extends Command {
    public CommandRank(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("bungee.setrank")) {
            if(args.length == 2) {
                final DBUser dbUser = new DBUser(UUIDFetcher.getUUID(args[0]), args[0]);

                if (!dbUser.userExists()) {
                    commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cEs kann keine Rangänderung für einen nicht existenten Spieler vorgenommen werden."));
                    return;
                }
                final String newrank = args[1];
                final RankEnum rank = RankEnum.getRankByName(newrank);
                if(rank ==null) {
                    String availableRanks = RankEnum.getAllRanks().toString();
                    availableRanks = availableRanks.replace("[", "").replace("]", "");
                    commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cUngültige Eingabe. Valide Ränge sind: " + availableRanks));
                    return;
                }
                final String oldRank = dbUser.getRank().getName();
                RankManager.setRank(dbUser,rank.getName().toLowerCase());
                ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), "lpb user "+dbUser.getName()+" parent remove "+oldRank);
                if(newrank.equalsIgnoreCase("spieler") || newrank.equalsIgnoreCase("stammspieler")) {
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), "lpb user "+dbUser.getName()+" parent set default");
                } else {
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), "lpb user "+dbUser.getName()+" parent set "+newrank);
                }
                commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cRangänderung wurde erfolgreich durchgeführt."));
                PunishmentBootstrap.getInstance().getTelegramUtils().sendTelegramMessage("830927975",
                        commandSender.getName() + " hat den Rang von " + dbUser.getName() + " auf " + rank.getName().toUpperCase() + " geändert.","1168440344:AAEjXf7cX56huGacxQu4hgcqTR0-6GHSOow");
                if(BungeeCord.getInstance().getPlayer(args[0]) !=null) {
                    BungeeCord.getInstance().getPlayer(args[0]).disconnect(new TextComponent("§aDein Rang wurde soeben geändert. Bitte joine neu."));
                }
                return;


            } else {
                commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» Bitte verwende /rank <Spieler> <Rank>"));
            }
        } else {
            commandSender.sendMessage(new TextComponent("§cYou do not have permission to execute this command!"));
        }
    }
}
