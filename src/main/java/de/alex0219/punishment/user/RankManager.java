package de.alex0219.punishment.user;

/**
 * Created by Alexander on 05.06.2020 00:36
 * © 2020 Alexander Fiedler
 */
public class RankManager {

    /**
     * Defines whether a user is permitted to ban a specific user.
     *
     * @param executor
     * @param banned
     * @return Boolean
     */
    public boolean isPermittedToBan(DBUser executor, DBUser banned) {
        return executor.getRank().getRankLevel() > banned.getRank().getRankLevel();
    }
}
