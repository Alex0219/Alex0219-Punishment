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
public class CommandMSG extends Command {

    public CommandMSG(String name) {
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

            if(args.length > 1) {

                 String message = "";
                for (int i = 1; i < args.length; i++) {
                    message = message + args[i] + " ";
                }

                ProxiedPlayer target = BungeeCord.getInstance().getPlayer(args[0]);
                if(target == null) {
                    player.sendMessage(new TextComponent("§bMC-Survival.de §7» §cDer angegebene Spieler wurde nicht gefunden!"));
                    return;
                }

                if(target.getName().equalsIgnoreCase(player.getName())) {
                    player.sendMessage(new TextComponent("§bMC-Survival.de §7» §7Führst du gerne Unterhaltungen mit dir selbst?"));
                    return;
                }

                //player is online

                player.sendMessage(new TextComponent( "§bMC-Survival.de §7» §3Du §7➥ §3"
                        + target.getName() + " §8» §f" + message));
                target.sendMessage(new TextComponent("§bMC-Survival.de §7» §3" + player.getName()
                        + " §7§7➥ §3Dir §8» §f" + message));

                PunishmentBootstrap.getInstance().getRankManager().sendMSGSpyMessage(message,player.getName(),target.getName());


                BungeeCord.getInstance().getLogger().log(Level.INFO,"§bMC-Survival.de §7» §7[§cMSG-SPY§7] " + player.getName()
                        + " §7§7➥ §3"+target+ "§8» §f" + message);

                if(PunishmentBootstrap.getInstance().getMsgs().containsKey(player)) {
                    PunishmentBootstrap.getInstance().getMsgs().remove(player);
                }

                PunishmentBootstrap.getInstance().getMsgs().put(target,player);


            } else {
                player.sendMessage(new TextComponent("§bMC-Survival.de §7» §7Bitte verwende /msg <Name> <Nachricht>"));
            }
        }
    }
}
