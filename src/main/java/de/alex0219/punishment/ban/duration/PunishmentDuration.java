package de.alex0219.punishment.ban.duration;

/**
 * Created by Alexander on 04.06.2020 20:45
 * © 2020 Alexander Fiedler
 */
public enum PunishmentDuration {

    TEMPORARILY("Temporarily"),
    PERMANENTLY("Permanently");

    String name;

    PunishmentDuration(final String name) {
        this.name = name;
    }

    public static PunishmentDuration getBanDurationFromString(final String banDuration) {
        for (PunishmentDuration banDurations : values()) {
            if (banDurations.getName().equalsIgnoreCase(banDuration)) {
                return banDurations;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
}
