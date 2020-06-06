package de.alex0219.punishment.commands;

import de.alex0219.punishment.PunishmentBootstrap;
import de.alex0219.punishment.user.DBUser;
import de.alex0219.punishment.uuid.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandUnmute extends Command {


    public CommandUnmute(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        if (commandSender.hasPermission("punish.unmute")) {
            if (args.length == 1) {
                if (!(commandSender instanceof ProxiedPlayer)) {
                    commandSender.sendMessage(new TextComponent("§bAlex0219.de §7» §cDieser Befehl kann nicht von der Konsole ausgeführt werden."));
                    return;
                }
                final DBUser executor = new DBUser(UUIDFetcher.getUUID(commandSender.getName()), commandSender.getName());
                final DBUser bannedPlayer = new DBUser(UUIDFetcher.getUUID(args[0]), args[0]);

                if (!bannedPlayer.userExists()) {
                    commandSender.sendMessage(new TextComponent("§bAlex0219.de §7» §cEs kann kein Unmute für einen §cnicht-existenten Spieler erstellt werden."));
                    return;
                }
                if (PunishmentBootstrap.getInstance().getRankManager().isPermittedToBan(executor, bannedPlayer)) {
                    if (!PunishmentBootstrap.getInstance().getBanManager().isMuted(bannedPlayer)) {
                        commandSender.sendMessage(new TextComponent("§bAlex0219.de §7» §cDieser Spieler ist nicht gemutet."));
                        return;
                    }
                    PunishmentBootstrap.getInstance().getBanManager().unmutePlayer(bannedPlayer);
                    commandSender.sendMessage(new TextComponent("§bAlex0219.de §7» Der Spieler §a" + bannedPlayer.getName() + " §7wurde erfolgreich entmutet."));
                } else {
                    commandSender.sendMessage(new TextComponent("§bAlex0219.de §7» §cDu darfst diesen Spieler nicht entmuten!"));
                    return;
                }
            } else {
                commandSender.sendMessage(new TextComponent("§bAlex0219.de §7» Bitte verwende /unmute <Spieler>"));
            }
        } else {
            commandSender.sendMessage(new TextComponent("§cYou do not have permission to execute this command!"));
        }
    }
}
