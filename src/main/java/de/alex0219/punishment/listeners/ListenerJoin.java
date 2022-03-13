package de.alex0219.punishment.listeners;

import de.alex0219.punishment.PunishmentBootstrap;
import de.alex0219.punishment.user.DBUser;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ListenerJoin implements Listener {

    @EventHandler
    public void onLogin(LoginEvent loginEvent) {
        final String strippedIP = loginEvent.getConnection().getAddress().getAddress().toString().replace("/","");


        if(PunishmentBootstrap.getInstance().getJedis().exists("ipban:"+strippedIP)) {
            String ipbanReason = PunishmentBootstrap.getInstance().getJedis().hget("ipban:"+strippedIP,"reason");
            String ipbanMessage = "§bMC-Survival.de §7» Deine IP-Adresse wurde gesperrt. \n" + "Grund: §c" + ipbanReason  + " \n §7Dies ist eine finale Entscheidung.";
            loginEvent.setCancelled(true);
            loginEvent.setCancelReason(ipbanMessage);
        }
    }

    @EventHandler
    public void onPlayerLogin(final PostLoginEvent loginEvent) {
        long millisNow = System.currentTimeMillis();
        final ProxiedPlayer player = loginEvent.getPlayer();
        final DBUser dbUser = new DBUser(player.getUniqueId().toString(), player.getName());
        if(!PunishmentBootstrap.getInstance().getRankManager().getDbusers().contains(dbUser)) {
            PunishmentBootstrap.getInstance().getRankManager().getDbusers().add(dbUser);
        }

        if (!dbUser.userExists()) {
            dbUser.createUser();
        } else {
            System.out.println("Backend -> User already exists!");
            //update player name in case player has changed his name
            PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "name", dbUser.getName());
        }
        if(PunishmentBootstrap.getInstance().getJedis().exists("uuid:")) {
            PunishmentBootstrap.getInstance().getJedis().del("uuid:");
        }

        if (PunishmentBootstrap.getInstance().getBanManager().isBanned(dbUser)) {
            final long punishmentEndTime = Long.parseLong(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "banEnd"));
            final String endDate = new java.text.SimpleDateFormat("dd.MM.yyyy").format(new java.util.Date(punishmentEndTime));
            final String endTime = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(punishmentEndTime));
            final String reason = PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "banReason");
            System.out.println(punishmentEndTime);
            if (System.currentTimeMillis() >= punishmentEndTime && punishmentEndTime != -1) {
                //Unban player
                PunishmentBootstrap.getInstance().getBanManager().unbanPlayer(dbUser,"Auto-Unban");
            }

            if (punishmentEndTime == -1) {
                String banMessage = "§bMC-Survival.de §7» Du wurdest gebannt. \n" + "Grund: §c" + reason + " \n §7Dein Bann läuft §4niemals §7aus. \n §7Du kannst auf unserem Discord: §cdiscord.gg/pyRj7FBcyN §7einen Entbannungsantrag stellen.";
                player.disconnect(banMessage);
            } else {
                String banMessage = "§bMC-Survival.de §7» Du wurdest gebannt. \n" + "Grund: §c" + reason + " \n §7Dein Bann läuft am §a" + endDate + " §7um §a" + endTime + " §7aus. \n §7Du kannst auf unserem Discord: §cdiscord.gg/pyRj7FBcyN §7einen Entbannungsantrag stellen.";
                player.disconnect(banMessage);
            }

        }

        if(PunishmentBootstrap.getInstance().getJedis().get("bungeemaintenance").equalsIgnoreCase("true")) {
            if(dbUser.getRank().getRankLevel() == 1 ) {
                player.disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&',PunishmentBootstrap.getInstance().getJedis().get("bungeemaintenancetext"))));
            }
        }


        String millis = String.valueOf(System.currentTimeMillis() - millisNow);
        System.out.println("Backend -> Player Join took " + millis + " milliseconds");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "lastip", player.getAddress().getAddress().toString());
        dbUser.updateLoginCount();

    }

    @EventHandler
    public void onServerSwitch(final ServerConnectEvent event) {
        if(PunishmentBootstrap.getInstance().getMsgs().containsKey(event.getPlayer())) {
            PunishmentBootstrap.getInstance().getMsgs().remove(event.getPlayer());
        }
        BungeeCord.getInstance().getPlayers().forEach(players -> {
            players.setTabHeader(new TextComponent("§7» §bMC-Survival.de §7« \n §7Dein Classic Minecraft Survival Server \nSpieler online: §c"+ BungeeCord.getInstance().getPlayers().size()+"/"+BungeeCord.getInstance().getConfig().getPlayerLimit()), new TextComponent("§7Du möchtest uns unterstützen? §e/vote \n§7Unser Discord: §cdiscord.gg/pyRj7FBcyN"));
        });
        event.getPlayer().setTabHeader(new TextComponent("§7» §bMC-Survival.de §7« \n §7Dein Classic Minecraft Survival Server \nSpieler online: §c"+ BungeeCord.getInstance().getPlayers().size()+"/"+BungeeCord.getInstance().getConfig().getPlayerLimit()), new TextComponent("§7Du möchtest uns unterstützen? §e/vote \n§7Unser Discord: §cdiscord.gg/pyRj7FBcyN"));

    }

    @EventHandler
    public void onServerDisconnect(final ServerDisconnectEvent event) {
        if(PunishmentBootstrap.getInstance().getMsgs().containsKey(event.getPlayer())) {
            PunishmentBootstrap.getInstance().getMsgs().remove(event.getPlayer());
        }
        BungeeCord.getInstance().getPlayers().forEach(players -> {
            players.setTabHeader(new TextComponent("§7» §bMC-Survival.de §7« \n §7Dein Classic Minecraft Survival Server \nSpieler online: §c"+ BungeeCord.getInstance().getPlayers().size()+"/"+BungeeCord.getInstance().getConfig().getPlayerLimit()), new TextComponent("§7Du möchtest uns unterstützen? §e/vote \n§7Unser Discord: §cdiscord.gg/pyRj7FBcyN"));
        });
    }
    @EventHandler
    public void onServerDisconnect(final ServerKickEvent event) {
        if(PunishmentBootstrap.getInstance().getMsgs().containsKey(event.getPlayer())) {
            PunishmentBootstrap.getInstance().getMsgs().remove(event.getPlayer());
        }
        BungeeCord.getInstance().getPlayers().forEach(players -> {
            players.setTabHeader(new TextComponent("§7» §bMC-Survival.de §7« \n §7Dein Classic Minecraft Survival Server \nSpieler online: §c"+ BungeeCord.getInstance().getPlayers().size()+"/"+BungeeCord.getInstance().getConfig().getPlayerLimit()), new TextComponent("§7Du möchtest uns unterstützen? §e/vote \n§7Unser Discord: §cdiscord.gg/pyRj7FBcyN"));
        });
    }
}
