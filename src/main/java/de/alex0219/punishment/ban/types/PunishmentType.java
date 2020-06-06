package de.alex0219.punishment.ban.types;

/**
 * Created by Alexander on 04.06.2020 23:00
 * © 2020 Alexander Fiedler
 */
public enum PunishmentType {

    BAN("Ban"),
    CHATBAN("Chatban");

    String name;

    PunishmentType(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
