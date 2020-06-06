package de.alex0219.punishment.ban.ranks;

/**
 * Created by Alexander on 04.06.2020 20:57
 * © 2020 Alexander Fiedler
 */
public enum RankEnum {

    SUPERADMIN("Superadmin", 4, 3),
    ADMIN("Admin", 3, 2),
    MODERATOR("mod", 2, 1),
    PREMIUM("Premium", 1, 1),
    SPIELER("Spieler", 0, 0);

    String name;
    int id;
    int rankLevel;

    RankEnum(final String name, final int id, final int rankLevel) {
        this.name = name;
        this.id = id;
        this.rankLevel = rankLevel;
    }

    public static RankEnum getRankByName(final String rankName) {
        for (RankEnum ranks : values()) {
            if (ranks.getName().equalsIgnoreCase(rankName)) {
                return ranks;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    //developer
    //DEveLOPer

    public int getRankLevel() {
        return rankLevel;
    }
}
