package de.alex0219.punishment.commands;

import de.alex0219.punishment.PunishmentBootstrap;
import de.alex0219.punishment.ban.ranks.RankEnum;
import de.alex0219.punishment.user.DBUser;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Alexander on 12.08.2020 23:55
 * © 2020 Alexander Fiedler
 */
public class CommandMSGSpy extends Command {
    public CommandMSGSpy(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer)commandSender;
            final DBUser dbUser = PunishmentBootstrap.getInstance().getRankManager().getDBUser(player.getName());
            if (dbUser.getRank() == RankEnum.ADMIN) {
                if (args.length == 0) {
                    if(PunishmentBootstrap.getInstance().getMsgSpy().contains(player)) {
                        player.sendMessage(new TextComponent("§bMC-Survival.de §7» §cMSG-Spy wurde deaktiviert."));
                        PunishmentBootstrap.getInstance().getMsgSpy().remove(player);
                        return;
                    } else {
                        player.sendMessage(new TextComponent("§bMC-Survival.de §7» §aMSG-Spy wurde aktiviert."));
                        PunishmentBootstrap.getInstance().getMsgSpy().add(player);
                        return;
                    }
                } else {
                    player.sendMessage(new TextComponent("§bMC-Survival.de §7» §7Bitte verwende /msgspy"));
                }
            } else {
                commandSender.sendMessage(new TextComponent("§cYou do not have permission to execute this command!"));
                return;
            }
        }

    }
}