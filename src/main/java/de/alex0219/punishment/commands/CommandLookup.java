package de.alex0219.punishment.commands;

import de.alex0219.punishment.PunishmentBootstrap;
import de.alex0219.punishment.user.DBUser;
import de.alex0219.punishment.uuid.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandLookup extends Command {


    public CommandLookup(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        if (commandSender.hasPermission("punish.lookup")) {
            if (args.length == 1) {
                if (!(commandSender instanceof ProxiedPlayer)) {
                    commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cDieser Befehl kann nicht von der Konsole ausgeführt werden."));
                    return;
                }
                DBUser lookupPlayer = PunishmentBootstrap.getInstance().getRankManager().getDBUser(args[0]);

                if(lookupPlayer == null) {
                   lookupPlayer = new DBUser(UUIDFetcher.getUUID(args[0]), args[0]);
                }

                if (!lookupPlayer.userExists()) {
                    commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cEs kann kein Lookup für einen §cnicht-existenten §cSpieler ausgeführt werden."));
                    return;
                }

                commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» Gebannt: " + PunishmentBootstrap.getInstance().getBanManager().isBanned(lookupPlayer)));
                commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» Gemutet: " + PunishmentBootstrap.getInstance().getBanManager().isMuted(lookupPlayer)));
                commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» Bans: "));
                for (final String entry : PunishmentBootstrap.getInstance().getJedis().keys("punishlookup:*")) {
                    if (PunishmentBootstrap.getInstance().getJedis().hget(entry, "punishType").equalsIgnoreCase("BAN")) {
                        if (PunishmentBootstrap.getInstance().getJedis().hget(entry, "bannedPlayer").equalsIgnoreCase(UUIDFetcher.getUUID(lookupPlayer.getName()))) {

                            final String banStartDate = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(new java.util.Date(Long.valueOf(PunishmentBootstrap.getInstance().getJedis().hget(entry, "banStart"))));

                            String banEndDate = "";

                            if (PunishmentBootstrap.getInstance().getJedis().hget(entry, "banEnd").equalsIgnoreCase("-1")) {
                                banEndDate = "permanent";
                            } else {
                                banEndDate = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(new java.util.Date(Long.valueOf(PunishmentBootstrap.getInstance().getJedis().hget(entry, "banEnd"))));
                            }
                            final String banExecutor;
                            if(PunishmentBootstrap.getInstance().getJedis().hget(entry, "Executor").startsWith("Webinterface")) {
                                banExecutor = PunishmentBootstrap.getInstance().getJedis().hget(entry, "Executor");
                            } else {
                                banExecutor = UUIDFetcher.getName(PunishmentBootstrap.getInstance().getJedis().hget(entry, "Executor"));
                            }

                            final String reason = PunishmentBootstrap.getInstance().getJedis().hget(entry, "banReason");

                            commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» " + reason + " -> §c" + banStartDate + " §7- §c" + banEndDate + " §7von §c" + banExecutor));
                        }
                    }
                }
                commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» Mutes: "));
                for (final String entry : PunishmentBootstrap.getInstance().getJedis().keys("punishlookup:*")) {
                    if (PunishmentBootstrap.getInstance().getJedis().hget(entry, "punishType").equalsIgnoreCase("MUTE")) {
                        if (PunishmentBootstrap.getInstance().getJedis().hget(entry, "mutedPlayer").equalsIgnoreCase(UUIDFetcher.getUUID(lookupPlayer.getName()))) {

                            final String banStartDate = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(new java.util.Date(Long.valueOf(PunishmentBootstrap.getInstance().getJedis().hget(entry, "banStart"))));

                            String banEndDate = "";
                            if (PunishmentBootstrap.getInstance().getJedis().hget(entry, "banEnd").equalsIgnoreCase("-1")) {
                                banEndDate = "permanent";
                            } else {
                                banEndDate = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(new java.util.Date(Long.valueOf(PunishmentBootstrap.getInstance().getJedis().hget(entry, "banEnd"))));
                            }

                            final String banExecutor;

                            if(PunishmentBootstrap.getInstance().getJedis().hget(entry, "banExecutor").startsWith("Webinterface")) {
                                banExecutor = PunishmentBootstrap.getInstance().getJedis().hget(entry, "banExecutor");
                            } else {
                                banExecutor = UUIDFetcher.getName(PunishmentBootstrap.getInstance().getJedis().hget(entry, "banExecutor"));
                            }

                            final String reason = PunishmentBootstrap.getInstance().getJedis().hget(entry, "banReason");

                            commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» " + reason + " -> §c " + banStartDate + " §7- §c" + banEndDate + " §7von §c" + banExecutor));
                        }

                    }
                }

            } else {
                commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» Bitte verwende /lookup <Spieler>"));
            }
        } else {
            commandSender.sendMessage(new TextComponent("§cYou do not have permission to execute this command!"));
        }

    }
}
