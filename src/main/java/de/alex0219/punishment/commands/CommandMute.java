package de.alex0219.punishment.commands;

import de.alex0219.punishment.PunishmentBootstrap;
import de.alex0219.punishment.ban.Punishment;
import de.alex0219.punishment.ban.reason.PunishmentReason;
import de.alex0219.punishment.ban.types.PunishmentType;
import de.alex0219.punishment.user.DBUser;
import de.alex0219.punishment.uuid.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandMute extends Command {


    public CommandMute(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        if (commandSender.hasPermission("punish.mute")) {
            if (args.length == 2) {
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
                final String reason = args[1];
                final PunishmentReason punishmentReason = PunishmentReason.getPunishmentReasonByName(reason);

                if (punishmentReason == null) {
                    String availableBanReasons = PunishmentReason.getAllMuteReasons().toString();
                    availableBanReasons = availableBanReasons.replace("[", "").replace("]", "");
                    commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cBitte gebe einen Grund aus dieser Liste an: §a" + availableBanReasons));
                    return;
                }
                if(punishmentReason.getPunishmentType() != PunishmentType.CHATBAN) {
                    String availableBanReasons = PunishmentReason.getAllBanReasons().toString();
                    availableBanReasons = availableBanReasons.replace("[", "").replace("]", "");
                    commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cBitte gebe einen Grund aus dieser Liste an: §a" + availableBanReasons));
                    return;
                }
                //we are able to punish the player

                //check if player is permitted to mute the target player
                if (PunishmentBootstrap.getInstance().getRankManager().isPermittedToBan(executor, bannedPlayer)) {
                    if (PunishmentBootstrap.getInstance().getBanManager().isMuted(bannedPlayer)) {
                        commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cDieser Spieler ist bereits gebannt!"));
                        return;
                    }
                    PunishmentBootstrap.getInstance().getBanManager().mutePlayer(new Punishment(executor, bannedPlayer, punishmentReason));
                    commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» Der Spieler §a" + bannedPlayer.getName() + " §7wurde erfolgreich gemutet."));

                } else {
                    commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» §cDu darfst diesen Spieler nicht muten!"));
                    return;
                }
            } else {
                commandSender.sendMessage(new TextComponent("§bMC-Survival.de §7» Bitte verwende /mute <Spieler> <Grund>"));
            }
        } else {
            commandSender.sendMessage(new TextComponent("§cYou do not have permission to execute this command!"));
        }
    }
}
