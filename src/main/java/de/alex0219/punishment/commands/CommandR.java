package de.alex0219.punishment.commands;

import de.alex0219.punishment.PunishmentBootstrap;
import de.alex0219.punishment.user.DBUser;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.logging.Level;

/**
 * Created by Alexander on 12.08.2020
 * © 2020 Alexander Fiedler
 **/
public class CommandR extends Command {
    public CommandR(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        if(commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer)commandSender;

            final DBUser dbUser = new DBUser(player.getUniqueId().toString(),player.getName());

            if (PunishmentBootstrap.getInstance().getBanManager().isMuted(dbUser)) {
                player.sendMessage(new TextComponent("§bMC-Survival.de §7» §cDu darfst diesen Befehl nicht nutzen, während du gemutet bist."));
                return;
            }

            if(args.length > 0) {
                String message = "";
                for (int i = 0; i < args.length; i++) {
                    message = message + args[i] + " ";
                }

                if(PunishmentBootstrap.getInstance().getMsgs().containsKey(player)) {
                    ProxiedPlayer target = PunishmentBootstrap.getInstance().getMsgs().get(player);
                    if(target == null) {
                        player.sendMessage(new TextComponent("§bMC-Survival.de §7» §cDer angegebene Spieler ist nicht mehr online"));
                        PunishmentBootstrap.getInstance().getMsgs().remove(player);
                        return;
                    }

                    player.sendMessage(new TextComponent( "§bMC-Survival.de §7» §3Du §7➥ §3"
                            + target.getName() + " §8» §f" + message));
                    target.sendMessage(new TextComponent("§bMC-Survival.de §7» §3" + player.getName()
                            + " §7§7➥ §3Dir §8» §f" + message));

                    PunishmentBootstrap.getInstance().getRankManager().sendMSGSpyMessage(message,player.getName(),target.getName());

                    BungeeCord.getInstance().getLogger().log(Level.INFO,"§bMC-Survival.de §7» §7[§cMSG-SPY§7] " + player.getName()
                            + " §7§7➥ §3"+target+ "§8» §f" + message);

                    PunishmentBootstrap.getInstance().getMsgs().put(player,target);
                    PunishmentBootstrap.getInstance().getMsgs().put(target,player);

                } else {
                    player.sendMessage(new TextComponent("§bMC-Survival.de §7» §cDu hast noch keine Nachrichten erhalten!"));
                    return;
                }
            } else {
                player.sendMessage(new TextComponent("§bMC-Survival.de §7» §7Bitte verwende /r <Nachricht>"));
            }
        }
    }
}
