package de.alex0219.punishment.listeners;

import de.alex0219.punishment.PunishmentBootstrap;
import de.alex0219.punishment.user.DBUser;
import de.alex0219.punishment.uuid.UUIDFetcher;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ListenerJoin implements Listener {

    @EventHandler
    public void onPlayerLogin(final LoginEvent loginEvent) {
        final PendingConnection pendingConnection = loginEvent.getConnection();
        final DBUser dbUser = new DBUser(UUIDFetcher.getUUID(pendingConnection.getName()), pendingConnection.getName());

        long millisNow = System.currentTimeMillis();

        if (!dbUser.userExists()) {
            dbUser.createUser();
            String millis = String.valueOf(System.currentTimeMillis() - millisNow);
            System.out.println("Backend -> Player Join took " + millis + " milliseconds");
        } else {
            System.out.println("Backend -> User already exists!");
            //update player name in case player has changed his name
            PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "name", dbUser.getName());
            String millis = String.valueOf(System.currentTimeMillis() - millisNow);
            System.out.println("Backend -> Player Join took " + millis + " milliseconds");
        }

        if (PunishmentBootstrap.getInstance().getBanManager().isBanned(dbUser)) {
            final long punishmentEndTime = Long.parseLong(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "banEnd"));
            final String endDate = new java.text.SimpleDateFormat("dd.MM.yyyy").format(new java.util.Date(punishmentEndTime));
            final String endTime = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(punishmentEndTime));
            final String reason = PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "banReason");
            System.out.println(punishmentEndTime);
            if (System.currentTimeMillis() >= punishmentEndTime && punishmentEndTime != -1) {
                //Unban player
                PunishmentBootstrap.getInstance().getBanManager().unbanPlayer(dbUser);
                loginEvent.setCancelled(false);
            }
            loginEvent.setCancelled(true);
            if (punishmentEndTime == -1) {
                String banMessage = "§bAlex0219.de §7» Du wurdest gebannt. \n" + "Grund: §c" + reason + " \n §7Dein Bann läuft §4niemals §7aus.";
                loginEvent.setCancelReason(banMessage);
            } else {
                String banMessage = "§bAlex0219.de §7» Du wurdest gebannt. \n" + "Grund: §c" + reason + " \n §7Dein Bann läuft am §a" + endDate + " §7um §a" + endTime + " §7aus.";
                loginEvent.setCancelReason(banMessage);
            }

        }
    }
}
