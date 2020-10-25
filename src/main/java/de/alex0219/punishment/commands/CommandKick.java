package de.alex0219.punishment.commands;

import de.alex0219.punishment.PunishmentBootstrap;
import de.alex0219.punishment.user.DBUser;
import de.alex0219.punishment.uuid.UUIDFetcher;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Alexander on 07.06.2020 20:26
 * © 2020 Alexander Fiedler
 */
public class CommandKick extends Command {
    public CommandKick(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        if (commandSender.hasPermission("punish.kick")) {
            if (!(args.length < 2)) {
                if (!(commandSender instanceof ProxiedPlayer)) {
                    commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cDieser Befehl kann nicht von der Konsole ausgeführt werden."));
                    return;
                }
                final DBUser executor = PunishmentBootstrap.getInstance().getRankManager().getDBUser(commandSender.getName());
                DBUser bannedPlayer;

                if(BungeeCord.getInstance().getPlayer(args[0]) !=null) {
                    bannedPlayer = PunishmentBootstrap.getInstance().getRankManager().getDBUser(args[0]);
                } else {
                    bannedPlayer = new DBUser(UUIDFetcher.getUUID(args[0]), args[0]);
                }

                if (!bannedPlayer.userExists()) {
                    commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cDer angegebene Spieler wurde §cnicht gefunden."));
                    return;
                }

                String reason = "";
                for (int i = 1; i < args.length; i++) {
                    reason = reason + args[i] + " ";
                }
                //we are able to punish the player

                //check if player is permitted to mute the target player
                if (PunishmentBootstrap.getInstance().getRankManager().isPermittedToBan(executor, bannedPlayer)) {
                    if (BungeeCord.getInstance().getPlayer(bannedPlayer.getName()) == null) {
                        commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cDer angegebene Spieler wurde §cnicht gefunden."));
                        return;
                    }
                    PunishmentBootstrap.getInstance().getBanManager().kickPlayer(bannedPlayer, executor, reason);
                    commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» Der Spieler §a" + bannedPlayer.getName() + " §7wurde erfolgreich gekickt"));

                } else {
                    commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cDu darfst diesen Spieler nicht kicken!"));
                    return;
                }
            } else {
                commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» Bitte verwende /kick <Spieler> <Grund>"));
            }
        } else {
            commandSender.sendMessage(new TextComponent("§cYou do not have permission to execute this command!"));
        }
    }
}
