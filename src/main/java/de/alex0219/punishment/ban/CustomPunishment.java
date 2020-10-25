package de.alex0219.punishment.ban;


import de.alex0219.punishment.ban.reason.CustomPunishmentReason;
import de.alex0219.punishment.user.DBUser;

/**
 * Created by Alexander on 19.08.2020 04:13
 * © 2020 Alexander Fiedler
 */
public class CustomPunishment {

    String executor;
    DBUser bannedPlayer;
    CustomPunishmentReason punishmentReason;

    public CustomPunishment(final String executor, final DBUser bannedPlayer, final CustomPunishmentReason punishmentReason) {
        this.executor = executor;
        this.bannedPlayer = bannedPlayer;
        this.punishmentReason = punishmentReason;
    }

    public String getExecutor() {
        return executor;
    }

    public DBUser getBannedPlayer() {
        return bannedPlayer;
    }

    public CustomPunishmentReason getReason() {
        return punishmentReason;
    }
}
