package de.alex0219.punishment.ban.reason;

import de.alex0219.punishment.ban.duration.PunishmentDuration;
import de.alex0219.punishment.ban.types.PunishmentType;

import java.util.ArrayList;

/**
 * Created by Alexander on 04.06.2020 22:48
 * © 2020 Alexander Fiedler
 */
public enum PunishmentReason {

    HACKING("Hacking", 604800000L, PunishmentDuration.TEMPORARILY, PunishmentType.BAN),
    BELEIDIGUNG("Beleidigung", 21600000L, PunishmentDuration.TEMPORARILY, PunishmentType.CHATBAN),
    SPAM("Spam", 600000L, PunishmentDuration.TEMPORARILY, PunishmentType.CHATBAN),
    WERBUNG("Werbung", -1, PunishmentDuration.PERMANENTLY, PunishmentType.BAN),
    GRIEFING("Griefing", 604800000L, PunishmentDuration.TEMPORARILY, PunishmentType.BAN),
    BUGUSING("Bugusing", -1, PunishmentDuration.PERMANENTLY, PunishmentType.BAN);

    String name;
    long banTime;
    PunishmentDuration punishmentDuration;
    PunishmentType punishmentType;

    PunishmentReason(final String name, final long banTime, final PunishmentDuration punishmentDuration, final PunishmentType punishmentType) {
        this.name = name;
        this.banTime = banTime;
        this.punishmentDuration = punishmentDuration;
        this.punishmentType = punishmentType;
    }

    public static PunishmentReason getPunishmentReasonByName(final String name) {
        for (final PunishmentReason punishmentReasons : values()) {
            if (punishmentReasons.getName().equalsIgnoreCase(name)) {
                return punishmentReasons;
            }
        }
        return null;
    }

    public static ArrayList<PunishmentReason> getAllMuteReasons() {
        final ArrayList<PunishmentReason> tempReasons = new ArrayList<>();
        for (PunishmentReason reasons : values()) {
            if (reasons.getPunishmentType() == PunishmentType.CHATBAN) {
                tempReasons.add(reasons);
            }
        }
        return tempReasons;
    }

    public static ArrayList<PunishmentReason> getAllBanReasons() {
        final ArrayList<PunishmentReason> tempReasons = new ArrayList<>();
        for (PunishmentReason reasons : values()) {
            if (reasons.getPunishmentType() == PunishmentType.BAN) {
                tempReasons.add(reasons);
            }
        }
        return tempReasons;
    }

    public String getName() {
        return name;
    }

    public long getBanTime() {
        return banTime;
    }

    public PunishmentDuration getPunishmentDuration() {
        return punishmentDuration;
    }

    public PunishmentType getPunishmentType() {
        return punishmentType;
    }

    // Hacking 1d 1mo p TEMPORARILY
}
