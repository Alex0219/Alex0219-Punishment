package de.alex0219.punishment.commands;

import de.alex0219.punishment.PunishmentBootstrap;
import de.alex0219.punishment.user.DBUser;
import de.alex0219.punishment.uuid.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Alexander on 05.06.2020 18:11
 * © 2020 Alexander Fiedler
 */
public class CommandPMute extends Command {

    public CommandPMute(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        PunishmentBootstrap.getInstance().getExecutorService().execute(() -> {
            if (commandSender.hasPermission("punish.pmute")) {
                if (!(args.length < 2)) {
                    if (!(commandSender instanceof ProxiedPlayer)) {
                        commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cDieser Befehl kann nicht von der Konsole ausgeführt werden."));
                        return;
                    }
                    final DBUser executor = new DBUser(UUIDFetcher.getUUID(commandSender.getName()), commandSender.getName());
                    final DBUser bannedPlayer = new DBUser(UUIDFetcher.getUUID(args[0]), args[0]);

                    if (!bannedPlayer.userExists()) {
                        commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cEs kann kein Mute für einen §cnicht existenten §cSpieler erstellt werden."));
                        return;
                    }
                    String reason = "";
                    for (int i = 1; i < args.length; i++) {
                        reason = reason + args[i] + " ";
                    }
                    //we are able to punish the player

                    //check if player is permitted to ban the target player
                    if (PunishmentBootstrap.getInstance().getRankManager().isPermittedToBan(executor, bannedPlayer)) {
                        if (PunishmentBootstrap.getInstance().getBanManager().isBanned(bannedPlayer)) {
                            commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cDieser Spieler ist bereits gemutet!"));
                            return;
                        }
                        PunishmentBootstrap.getInstance().getBanManager().mutePlayerPermanently(executor, bannedPlayer, reason);
                        commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» Der Spieler §a" + bannedPlayer.getName() + " §7wurde erfolgreich §7gemutet."));
                    } else {
                        commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cDu darfst diesen Spieler nicht muten!"));
                        return;
                    }
                } else {
                    commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» Bitte verwende /pmute <Spieler> <Grund>"));
                }
            } else {
                commandSender.sendMessage(new TextComponent("§cYou do not have permission to execute this command!"));
            }
        });



    }
}
