package de.alex0219.punishment.listeners;

import de.alex0219.punishment.PunishmentBootstrap;
import de.alex0219.punishment.ban.Punishment;
import de.alex0219.punishment.ban.ranks.RankEnum;
import de.alex0219.punishment.ban.reason.PunishmentReason;
import de.alex0219.punishment.user.DBUser;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Alexander on 05.06.2020 02:59
 * © 2020 Alexander Fiedler
 */


public class ListenerChat implements Listener {

    List<String> b = new ArrayList<>();
    private ArrayList<String> insult = new ArrayList<>();
    private ExecutorService service = Executors.newFixedThreadPool(1);

    public ListenerChat() {
        b.add("ficker");
        b.add("fotze");
        b.add("abspritzen");
        b.add("arschficker");
        b.add("anus");
        b.add("arschgefickter");
        b.add("nazi");
        b.add("hitler");
        b.add("fick dich");
        b.add("analspritzer");
        b.add("orgasmus");
        b.add("motherfucker");
        b.add("ich hab deine mutter gefickt");
        b.add("schamhaarasierer");
        b.add("fotzenlecker");
        b.add("hurensohn");
        b.add("analgasfotze");
        b.add("Kommt alle auf");
        b.add("dein vater ist ein spast");
        b.add("spast");
        b.add("Aasgeier");
        b.add("Joint alle auf");
        b.add("hürensöhne");
        b.add("huhrensöhne");
        b.add("hurensoehne");
        b.add("dickpic");
        b.add("schwanzbild");
        b.add("vergewaltigen");
        b.add("vergewaltigt");
        b.add("ihr hurensöhne");
        b.add("du hurensöhne");
        b.add("hässlicher spast");
        b.add("deine mutter ist eine hure");

    }


    @EventHandler
    public void onPlayerLogin(final ChatEvent chatEvent) {

        ProxiedPlayer muted = (ProxiedPlayer) chatEvent.getSender();
        final DBUser dbUser = PunishmentBootstrap.getInstance().getRankManager().getDBUser(muted.getName());

        if (!chatEvent.getMessage().startsWith("/")) {
            if (PunishmentBootstrap.getInstance().getBanManager().isMuted(dbUser)) {
                final long punishmentEndTime = Long.parseLong(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "muteEnd"));
                final String endDate = new java.text.SimpleDateFormat("dd.MM.yyyy").format(new java.util.Date(punishmentEndTime));
                final String endTime = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(punishmentEndTime));
                final String reason = PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "muteReason");

                if (System.currentTimeMillis() >= punishmentEndTime && punishmentEndTime != -1) {
                    //Unban player
                    PunishmentBootstrap.getInstance().getBanManager().unmutePlayer(dbUser,"Auto-Unmute");
                    chatEvent.setCancelled(false);
                }
                chatEvent.setCancelled(true);
                if (punishmentEndTime == -1) {
                    String banMessage = "§7Du wurdest gemutet. \n" + "Grund: §c" + reason + " \n §7Dein Mute läuft am §4niemals §7aus. \n§7Du kannst auf unserem Discord: §cdiscord.gg/pyRj7FBcyN §7einen Entbannungsantrag stellen.";
                    dbUser.getPlayer().sendMessage(banMessage);
                } else {
                    String banMessage = "§7Du wurdest gemutet. \n" + "Grund: §c" + reason + " \n §7Dein Mute läuft am §a" + endDate + " §7um §a" + endTime + " §7aus. \n§7Du kannst auf unserem Discord: §cdiscord.gg/pyRj7FBcyN §7einen Entbannungsantrag stellen.";
                    dbUser.getPlayer().sendMessage(banMessage);
                }
            }
        }

    }



    @EventHandler
    public void onChat(ChatEvent e) {
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();
if(PunishmentBootstrap.getInstance().getRankManager().getDBUser(((ProxiedPlayer) e.getSender()).getName()).getRank() != RankEnum.ADMIN && !PunishmentBootstrap.getInstance().getBanManager().isMuted(PunishmentBootstrap.getInstance().getRankManager().getDBUser(((ProxiedPlayer) e.getSender()).getName()))) {
    String msg = e.getMessage();
    for (String s : b) {

        if (msg.equalsIgnoreCase(s)) {
            if (insult.contains(p.getName())) {
                insult.remove(p.getName());
                service.execute(() -> {
                    Punishment mutePunishment = new Punishment(new DBUser("018f2527-b5f7-49f3-a511-656b542c6366","System"),PunishmentBootstrap.getInstance().getRankManager().getDBUser(p.getName()), PunishmentReason.BELEIDIGUNG);
                    PunishmentBootstrap.getInstance().getBanManager().mutePlayer(mutePunishment);
                });
                p.sendMessage("§bMC-Survival.de §7» §cDu wurdest für §eBeleidigung §cgemutet!");
                e.setMessage(null);
                e.setCancelled(true);

            } else {
                p.sendMessage("§bMC-Survival.de §7» §cBitte verhalte dich respektvoll im Chat");
                p.sendMessage("§bMC-Survival.de §7» Solltest du dies nochmal schreiben, wirst du gemutet!");
                e.setMessage(null);
                e.setCancelled(true);
                insult.add(p.getName());

            }

        }
    }
}

    }

}
