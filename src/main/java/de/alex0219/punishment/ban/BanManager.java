package de.alex0219.punishment.ban;

import de.alex0219.punishment.PunishmentBootstrap;
import de.alex0219.punishment.ban.reason.PunishmentReason;
import de.alex0219.punishment.user.DBUser;
import de.alex0219.punishment.uuid.UUIDFetcher;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;

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
                String banMessage = "§bMC-Survival.de §7» Du wurdest gebannt. \n" + "Grund: §c" + punishment.getReason().getName() + " \n §7Dein Bann läuft §4niemals §7aus.";
                bannedPlayer.getPlayer().disconnect(banMessage);
            } else {
                final String endDate = new java.text.SimpleDateFormat("dd.MM.yyyy").format(new java.util.Date(punishmentEndTime));
                final String endTime = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(punishmentEndTime));
                String banMessage = "§bMC-Survival.de §7» Du wurdest gebannt. \n" + "Grund: §c" + punishment.getReason().getName() + " \n §7Dein Bann läuft am §a" + endDate + " §7um §a" + endTime + " §7aus.";
                bannedPlayer.getPlayer().disconnect(banMessage);
            }
        }

        BungeeCord.getInstance().getPlayers().forEach(players -> {
            if (players.hasPermission("punish.notify")) {
                final String banStartDate = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(new java.util.Date(Long.valueOf(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + bannedPlayer.getUuid(), "banStart"))));

                String banEndDate = "";
                if (PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + bannedPlayer.getUuid(), "banEnd").equalsIgnoreCase("-1")) {
                    banEndDate = "permanent";
                } else {
                    banEndDate = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(new java.util.Date(Long.valueOf(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + bannedPlayer.getUuid(), "banEnd"))));
                }

                final String banExecutor = UUIDFetcher.getName(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + bannedPlayer.getUuid(), "banExecutor"));

                final String reason = PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + bannedPlayer.getUuid(), "banReason");

                players.sendMessage(new TextComponent("§bMC-Survival.de §7» BAN " + bannedPlayer.getName() + reason + " -> §c" + banStartDate + " §7- §c" + banEndDate + " §7von §c" + banExecutor));
            }
        });

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
                String banMessage = "§bMC-Survival.de §7» Du wurdest gebannt. \n" + "Grund: §c" + punishmentReason + " \n §7Dein Bann läuft §4niemals §7aus.";
                bannedPlayer.getPlayer().disconnect(banMessage);
            } else {
                final String endDate = new java.text.SimpleDateFormat("dd.MM.yyyy").format(new java.util.Date(punishmentEndTime));
                final String endTime = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(punishmentEndTime));
                String banMessage = "§bMC-Survival.de §7» Du wurdest gebannt. \n" + "Grund: §c" + punishmentReason + " \n §7Dein Bann läuft am §a" + endDate + " §7um §a" + endTime + " §7aus.";
                bannedPlayer.getPlayer().disconnect(banMessage);
            }
        }
        //send message to all permitted users
        BungeeCord.getInstance().getPlayers().forEach(players -> {
            if (players.hasPermission("punish.notify")) {
                final String banStartDate = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(new java.util.Date(Long.valueOf(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + bannedPlayer.getUuid(), "banStart"))));

                String banEndDate = "";
                if (PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + bannedPlayer.getUuid(), "banEnd").equalsIgnoreCase("-1")) {
                    banEndDate = "permanent";
                } else {
                    banEndDate = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(new java.util.Date(Long.valueOf(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + bannedPlayer.getUuid(), "banEnd"))));
                }

                final String banExecutor = UUIDFetcher.getName(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + bannedPlayer.getUuid(), "banExecutor"));

                final String reason = PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + bannedPlayer.getUuid(), "banReason");

                players.sendMessage(new TextComponent("§bMC-Survival.de §7» BAN " + bannedPlayer.getName() + reason + " -> §c" + banStartDate + " §7- §c" + banEndDate + " §7von §c" + banExecutor));
            }
        });
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
        BungeeCord.getInstance().getPlayers().forEach(players -> {
            if (players.hasPermission("punish.notify")) {
                final String banStartDate = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(new java.util.Date(Long.valueOf(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + bannedPlayer.getUuid(), "muteStart"))));

                String banEndDate = "";
                if (PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + bannedPlayer.getUuid(), "muteEnd").equalsIgnoreCase("-1")) {
                    banEndDate = "permanent";
                } else {
                    banEndDate = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(new java.util.Date(Long.valueOf(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + bannedPlayer.getUuid(), "muteEnd"))));
                }

                final String banExecutor = UUIDFetcher.getName(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + bannedPlayer.getUuid(), "muteExecutor"));

                final String reason = PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + bannedPlayer.getUuid(), "muteReason");

                players.sendMessage(new TextComponent("§bMC-Survival.de §7» §cMUTE §7-> §c" + bannedPlayer.getName() + reason + " -> §c" + banStartDate + " §7- §c" + banEndDate + " §7von §c" + banExecutor));
            }
        });

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

        BungeeCord.getInstance().getPlayers().forEach(players -> {
            if (players.hasPermission("punish.notify")) {
                final String banStartDate = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(new java.util.Date(Long.valueOf(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + bannedPlayer.getUuid(), "banStart"))));

                String banEndDate = "";
                if (PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + bannedPlayer.getUuid(), "muteEnd").equalsIgnoreCase("-1")) {
                    banEndDate = "permanent";
                } else {
                    banEndDate = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(new java.util.Date(Long.valueOf(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + bannedPlayer.getUuid(), "banEnd"))));
                }

                final String banExecutor = UUIDFetcher.getName(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + bannedPlayer.getUuid(), "muteExecutor"));

                final String reason = PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + bannedPlayer.getUuid(), "muteReason");

                players.sendMessage(new TextComponent("§bMC-Survival.de §7» §cMUTE §7-> " + bannedPlayer.getName() + reason + " -> §c" + banStartDate + " §7- §c" + banEndDate + " §7von §c" + banExecutor));
            }
        });
    }

    public void kickPlayer(final DBUser kickedPlayer, DBUser executor, final String reason) {
        String banMessage = "§7Du wurdest gekickt. \n" + "Grund: §c" + reason;
        kickedPlayer.getPlayer().disconnect(banMessage);

        BungeeCord.getInstance().getPlayers().forEach(players -> {
            if (players.hasPermission("punish.notify")) {
                players.sendMessage(new TextComponent("§bMC-Survival.de §7» §cKICK §7-> " + kickedPlayer.getName() + " §7von §c" + executor.getName() + " §7Grund:§c" + reason));
            }
        });
    }

    public void unbanPlayer(DBUser dbUser) {
        final String executor = UUIDFetcher.getName(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "banExecutor"));
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "banned", "false");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "banReason", "null");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "banStart", "null");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "banEnd", "null");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "banExecutor", "null");

        BungeeCord.getInstance().getPlayers().forEach(players -> {
            if (players.hasPermission("punish.notify")) {
                players.sendMessage(new TextComponent("§bMC-Survival.de §7» §cUNBAN §7-> " + dbUser.getName() + " §7von §c" + executor));
            }
        });

    }

    public void unmutePlayer(final DBUser dbUser) {
        final String executor = UUIDFetcher.getName(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + dbUser.getUuid(), "muteExecutor"));
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "muted", "false");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "muteReason", "null");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "muteStart", "null");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "muteEnd", "null");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + dbUser.getUuid(), "muteExecutor", "null");

        BungeeCord.getInstance().getPlayers().forEach(players -> {
            if (players.hasPermission("punish.notify")) {
                players.sendMessage(new TextComponent("§bMC-Survival.de §7» §cUNMUTE §7-> " + dbUser.getName() + " §7von §c" + executor));
            }
        });
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
