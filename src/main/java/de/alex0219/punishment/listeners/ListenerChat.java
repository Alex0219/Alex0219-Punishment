package de.alex0219.punishment.listeners;

import de.alex0219.punishment.PunishmentBootstrap;
import de.alex0219.punishment.user.DBUser;
import de.alex0219.punishment.uuid.UUIDFetcher;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Alexander on 05.06.2020 02:59
 * © 2020 Alexander Fiedler
 */


public class ListenerChat implements Listener {

    @EventHandler
    public void onPlayerLogin(final ChatEvent chatEvent) {

        ProxiedPlayer muted = (ProxiedPlayer) chatEvent.getSender();
        final DBUser dbUser = new DBUser(UUIDFetcher.getUUID(muted.getName()), muted.getName());

        if (!chatEvent.getMessage().startsWith("/")) {
            if (PunishmentBootstrap.getInstance().getBanManager().isMuted(dbUser)) {
                final long punishmentEndTime = Long.parseLong(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "muteEnd"));
                final String endDate = new java.text.SimpleDateFormat("dd.MM.yyyy").format(new java.util.Date(punishmentEndTime));
                final String endTime = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(punishmentEndTime));
                final String reason = PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "muteReason");

                if (System.currentTimeMillis() >= punishmentEndTime) {
                    //Unban player
                    PunishmentBootstrap.getInstance().getBanManager().unmutePlayer(dbUser);
                    chatEvent.setCancelled(false);
                }
                chatEvent.setCancelled(true);
                if (punishmentEndTime == -1) {
                    String banMessage = "§7Du wurdest gemutet. \n" + "Grund: §c" + reason + " \n §7Dein Mute läuft am §4niemals §7aus.";
                    dbUser.getPlayer().sendMessage(banMessage);
                } else {
                    String banMessage = "§7Du wurdest gemutet. \n" + "Grund: §c" + reason + " \n §7Dein Mute läuft am §a" + endDate + " §7um §a" + endTime + " §7aus.";
                    dbUser.getPlayer().sendMessage(banMessage);
                }
            }
        }

    }
}
