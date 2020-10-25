package de.alex0219.punishment.commands;

import de.alex0219.punishment.PunishmentBootstrap;
import de.alex0219.punishment.user.DBUser;
import de.alex0219.punishment.uuid.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Alexander on 09.08.2020
 * © 2020 Alexander Fiedler
 **/
public class CommandClearlookup extends Command {
    public CommandClearlookup(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        if (commandSender.hasPermission("punish.clearlookup")) {
            if (args.length == 1) {
                if (!(commandSender instanceof ProxiedPlayer)) {
                    commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cDieser Befehl kann nicht von der Konsole ausgeführt werden."));
                    return;
                }
                 DBUser target = PunishmentBootstrap.getInstance().getRankManager().getDBUser(args[0]);

                if(target == null) {
                    target = new DBUser(UUIDFetcher.getUUID(args[0]), args[0]);
                }

                if (!target.userExists()) {
                    commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cEs kann kein Lookupclear für einen nicht existenten Spieler durchgeführt werden."));
                    return;
                }

                for (final String entry : PunishmentBootstrap.getInstance().getJedis().keys("punishlookup:*")) {
                    if(PunishmentBootstrap.getInstance().getJedis().hget(entry,"bannedPlayer") !=null) {
                        if(PunishmentBootstrap.getInstance().getJedis().hget(entry,"bannedPlayer").equalsIgnoreCase(target.getUuid())) {
                            PunishmentBootstrap.getInstance().getJedis().del(entry);
                        }
                    }

                }

                commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» Der Lookup für den Spieler §c" + args[0] + " §7wurde erfolgreich gelöscht."));

            } else {
                commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» Bitte verwende /clearlookup <Spieler>"));
            }
        } else {
            commandSender.sendMessage(new TextComponent("§cYou do not have permission to execute this command!"));
        }


    }
}
