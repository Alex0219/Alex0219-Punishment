package de.alex0219.punishment.ban;

import de.alex0219.punishment.ban.reason.PunishmentReason;
import de.alex0219.punishment.user.DBUser;

public class Punishment {

    DBUser executor;
    DBUser bannedPlayer;
    PunishmentReason punishmentReason;

    public Punishment(final DBUser executor, final DBUser bannedPlayer, final PunishmentReason punishmentReason) {
        this.executor = executor;
        this.bannedPlayer = bannedPlayer;
        this.punishmentReason = punishmentReason;
    }


    public DBUser getExecutor() {
        return executor;
    }

    public DBUser getBannedPlayer() {
        return bannedPlayer;
    }

    public PunishmentReason getReason() {
        return punishmentReason;
    }
}
