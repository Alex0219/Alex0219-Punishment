package de.alex0219.punishment.ban;

import de.alex0219.punishment.PunishmentBootstrap;
import de.alex0219.punishment.ban.reason.PunishmentReason;
import de.alex0219.punishment.user.DBUser;

public class BanManager {

    public void banPlayer(Punishment punishment) {

        final long punishmentTime = System.currentTimeMillis();

        final PunishmentReason punishmentReason = punishment.getReason();

        final DBUser executor = punishment.getExecutor();
        final DBUser bannedPlayer = punishment.getBannedPlayer();
        long punishmentEndTime;
        if (punishmentReason.getBanTime() == -1) {
            punishmentEndTime = -1;
        } else {
            punishmentEndTime = System.currentTimeMillis() + punishmentReason.getBanTime();
        }

        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + bannedPlayer.getUuid(), "banned", "true");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + bannedPlayer.getUuid(), "banReason", punishmentReason.getName());
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + bannedPlayer.getUuid(), "banStart", String.valueOf(punishmentTime));
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + bannedPlayer.getUuid(), "banEnd", String.valueOf(punishmentEndTime));
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + bannedPlayer.getUuid(), "banExecutor", String.valueOf(executor.getUuid()));
        addBanLookupEntry(bannedPlayer, punishmentReason.getName(), String.valueOf(punishmentTime), String.valueOf(punishmentEndTime), executor.getUuid());
        if (bannedPlayer.getPlayer() != null) {
            //MM/dd/yyyy HH:mm:ss

            if (punishmentEndTime == -1) {
                String banMessage = "§bAlex0219.de §7» Du wurdest gebannt. \n" + "Grund: §c" + punishment.getReason().getName() + " \n §7Dein Bann läuft §4niemals §7aus.";
                bannedPlayer.getPlayer().disconnect(banMessage);
            } else {
                final String endDate = new java.text.SimpleDateFormat("dd.MM.yyyy").format(new java.util.Date(punishmentEndTime));
                final String endTime = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(punishmentEndTime));
                String banMessage = "§bAlex0219.de §7» Du wurdest gebannt. \n" + "Grund: §c" + punishment.getReason().getName() + " \n §7Dein Bann läuft am §a" + endDate + " §7um §a" + endTime + " §7aus.";
                bannedPlayer.getPlayer().disconnect(banMessage);
            }
        }
    }

    public void banPlayerPermanently(final DBUser executor, final DBUser bannedPlayer, final String punishmentReason) {

        final long punishmentTime = System.currentTimeMillis();


        long punishmentEndTime = -1;


        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + bannedPlayer.getUuid(), "banned", "true");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + bannedPlayer.getUuid(), "banReason", punishmentReason);
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + bannedPlayer.getUuid(), "banStart", String.valueOf(punishmentTime));
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + bannedPlayer.getUuid(), "banEnd", String.valueOf(punishmentEndTime));
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + bannedPlayer.getUuid(), "banExecutor", String.valueOf(executor.getUuid()));
        addBanLookupEntry(bannedPlayer, punishmentReason, String.valueOf(punishmentTime), String.valueOf(punishmentEndTime), executor.getUuid());
        if (bannedPlayer.getPlayer() != null) {
            //MM/dd/yyyy HH:mm:ss

            if (punishmentEndTime == -1) {
                String banMessage = "§bAlex0219.de §7» Du wurdest gebannt. \n" + "Grund: §c" + punishmentReason + " \n §7Dein Bann läuft §4niemals §7aus.";
                bannedPlayer.getPlayer().disconnect(banMessage);
            } else {
                final String endDate = new java.text.SimpleDateFormat("dd.MM.yyyy").format(new java.util.Date(punishmentEndTime));
                final String endTime = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(punishmentEndTime));
                String banMessage = "§bAlex0219.de §7» Du wurdest gebannt. \n" + "Grund: §c" + punishmentReason + " \n §7Dein Bann läuft am §a" + endDate + " §7um §a" + endTime + " §7aus.";
                bannedPlayer.getPlayer().disconnect(banMessage);
            }
        }
    }

    public void mutePlayer(Punishment punishment) {
        final long punishmentTime = System.currentTimeMillis();

        final PunishmentReason punishmentReason = punishment.getReason();

        final DBUser executor = punishment.getExecutor();
        final DBUser bannedPlayer = punishment.getBannedPlayer();

        long punishmentEndTime;
        if (punishmentReason.getBanTime() == -1) {
            punishmentEndTime = -1;
        } else {
            punishmentEndTime = System.currentTimeMillis() + punishmentReason.getBanTime();
        }

        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + bannedPlayer.getUuid(), "muted", "true");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + bannedPlayer.getUuid(), "muteReason", punishmentReason.getName());
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + bannedPlayer.getUuid(), "muteStart", String.valueOf(punishmentTime));
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + bannedPlayer.getUuid(), "muteEnd", String.valueOf(punishmentEndTime));
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + bannedPlayer.getUuid(), "muteExecutor", String.valueOf(executor.getUuid()));
        addMuteLookupEntry(bannedPlayer, punishmentReason.getName(), String.valueOf(punishmentTime), String.valueOf(punishmentEndTime), executor.getUuid());
        if (bannedPlayer.getPlayer() != null) {
            //MM/dd/yyyy HH:mm:ss


            if (punishmentEndTime == -1) {
                String banMessage = "§7Du wurdest gemutet. \n" + "Grund: §c" + punishmentReason.getName() + " \n §7Dein Mute läuft am §4niemals §7aus.";
                bannedPlayer.getPlayer().sendMessage(banMessage);
            } else {
                final String endDate = new java.text.SimpleDateFormat("dd.MM.yyyy").format(new java.util.Date(punishmentEndTime));
                final String endTime = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(punishmentEndTime));
                String banMessage = "§7Du wurdest gemutet. \n" + "Grund: §c" + punishmentReason.getName() + " \n §7Dein Mute läuft am §a" + endDate + " §7um §a" + endTime + " §7aus.";
                bannedPlayer.getPlayer().sendMessage(banMessage);
            }
        }
    }

    public void mutePlayerPermanently(final DBUser executor, final DBUser bannedPlayer, final String punishmentReason) {
        final long punishmentTime = System.currentTimeMillis();


        long punishmentEndTime = -1;

        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + bannedPlayer.getUuid(), "muted", "true");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + bannedPlayer.getUuid(), "muteReason", punishmentReason);
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + bannedPlayer.getUuid(), "muteStart", String.valueOf(punishmentTime));
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + bannedPlayer.getUuid(), "muteEnd", String.valueOf(punishmentEndTime));
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + bannedPlayer.getUuid(), "muteExecutor", String.valueOf(executor.getUuid()));
        addMuteLookupEntry(bannedPlayer, punishmentReason, String.valueOf(punishmentTime), String.valueOf(punishmentEndTime), executor.getUuid());
        if (bannedPlayer.getPlayer() != null) {
            //MM/dd/yyyy HH:mm:ss


            if (punishmentEndTime == -1) {
                String banMessage = "§7Du wurdest gemutet. \n" + "Grund: §c" + punishmentReason + " \n §7Dein Mute läuft am §4niemals §7aus.";
                bannedPlayer.getPlayer().sendMessage(banMessage);
            } else {
                final String endDate = new java.text.SimpleDateFormat("dd.MM.yyyy").format(new java.util.Date(punishmentEndTime));
                final String endTime = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(punishmentEndTime));
                String banMessage = "§7Du wurdest gemutet. \n" + "Grund: §c" + punishmentReason + " \n §7Dein Mute läuft am §a" + endDate + " §7um §a" + endTime + " §7aus.";
                bannedPlayer.getPlayer().sendMessage(banMessage);
            }
        }
    }

    public void unbanPlayer(DBUser dbUser) {
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "banned", "false");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "banReason", "null");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "banStart", "null");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "banEnd", "null");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "banExecutor", "null");
    }

    public void unmutePlayer(final DBUser dbUser) {
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "muted", "false");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "muteReason", "null");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "muteStart", "null");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "muteEnd", "null");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "muteExecutor", "null");
    }

    public boolean isBanned(DBUser dbUser) {
        return PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "banned").equalsIgnoreCase("true");
    }

    public boolean isMuted(DBUser dbUser) {
        return PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "muted").equalsIgnoreCase("true");
    }

    public void addBanLookupEntry(final DBUser banned, final String reason, final String banStart, final String banEnd, final String banExecutor) {
        int newID = Math.toIntExact(PunishmentBootstrap.getInstance().getJedis().incr("punishmentHistoryCount"));
        PunishmentBootstrap.getInstance().getJedis().hset("punishlookup:" + newID, "punishType", "BAN");
        PunishmentBootstrap.getInstance().getJedis().hset("punishlookup:" + newID, "banReason", reason);
        PunishmentBootstrap.getInstance().getJedis().hset("punishlookup:" + newID, "banStart", banStart);
        PunishmentBootstrap.getInstance().getJedis().hset("punishlookup:" + newID, "banEnd", banEnd);
        PunishmentBootstrap.getInstance().getJedis().hset("punishlookup:" + newID, "bannedPlayer", banned.getUuid());
        PunishmentBootstrap.getInstance().getJedis().hset("punishlookup:" + newID, "Executor", banExecutor);
    }

    public void addMuteLookupEntry(final DBUser banned, final String reason, final String banStart, final String banEnd, final String banExecutor) {
        int newID = Math.toIntExact(PunishmentBootstrap.getInstance().getJedis().incr("punishmentHistoryCount"));
        PunishmentBootstrap.getInstance().getJedis().hset("punishlookup:" + newID, "punishType", "MUTE");
        PunishmentBootstrap.getInstance().getJedis().hset("punishlookup:" + newID, "banReason", reason);
        PunishmentBootstrap.getInstance().getJedis().hset("punishlookup:" + newID, "banStart", banStart);
        PunishmentBootstrap.getInstance().getJedis().hset("punishlookup:" + newID, "banEnd", banEnd);
        PunishmentBootstrap.getInstance().getJedis().hset("punishlookup:" + newID, "mutedPlayer", banned.getUuid());
        PunishmentBootstrap.getInstance().getJedis().hset("punishlookup:" + newID, "banExecutor", banExecutor);
    }
    // String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (epoch*1000));
}
