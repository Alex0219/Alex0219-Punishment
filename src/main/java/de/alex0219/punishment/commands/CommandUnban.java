package de.alex0219.punishment.commands;

import de.alex0219.punishment.PunishmentBootstrap;
import de.alex0219.punishment.user.DBUser;
import de.alex0219.punishment.uuid.UUIDFetcher;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandUnban extends Command {


    public CommandUnban(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        PunishmentBootstrap.getInstance().getExecutorService().execute(() -> {
            if (commandSender.hasPermission("punish.unban")) {
                if (args.length == 1) {
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
                        commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cEs kann kein Unban für einen §cnicht existenten Spieler erstellt werden."));
                        return;
                    }
                    if (PunishmentBootstrap.getInstance().getRankManager().isPermittedToBan(executor, bannedPlayer)) {
                        if (!PunishmentBootstrap.getInstance().getBanManager().isBanned(bannedPlayer)) {
                            commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cDieser Spieler ist nicht gebannt."));
                            return;
                        }
                        PunishmentBootstrap.getInstance().getBanManager().unbanPlayer(bannedPlayer,commandSender.getName());
                        commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» Der Spieler §a" + bannedPlayer.getName() + " §7wurde erfolgreich entbannt."));
                    } else {
                        commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cDu darfst diesen Spieler nicht entbannen!"));
                        return;
                    }
                } else {
                    commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» Bitte verwende /unban <Spieler>"));
                }
            } else {
                commandSender.sendMessage(new TextComponent("§cYou do not have permission to execute this command!"));
            }
        });

    }
}
