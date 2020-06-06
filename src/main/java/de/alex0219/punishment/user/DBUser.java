package de.alex0219.punishment.user;

import de.alex0219.punishment.PunishmentBootstrap;
import de.alex0219.punishment.ban.ranks.RankEnum;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: Alexander<br/>
 * Date: 04.02.2018<br/>
 * Time: 17:36<br/>
 * MIT License
 * <p>
 * Copyright (c) 2017 Alexander Fiedler
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use and modify without distributing the software to anybody else,
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * <p>
 * <p>
 * MIT Lizenz
 * Copyright (c) 2017 Alexander Fiedler
 * Hiermit wird unentgeltlich jeder Person, die eine Kopie der Software und der zugehörigen Dokumentationen (die "Software") erhält, die Erlaubnis erteilt, sie uneingeschränkt zu nutzen, inklusive und ohne Ausnahme mit dem Recht, sie zu verwenden, zu verändern und Personen, denen diese Software überlassen wird, diese Rechte zu verschaffen, außer sie zu verteilen unter den folgenden Bedingungen:
 * <p>
 * Der obige Urheberrechtsvermerk und dieser Erlaubnisvermerk sind in allen Kopien oder Teilkopien der Software beizulegen.
 * <p>
 * DIE SOFTWARE WIRD OHNE JEDE AUSDRÜCKLICHE ODER IMPLIZIERTE GARANTIE BEREITGESTELLT, EINSCHLIEßLICH DER GARANTIE ZUR BENUTZUNG FÜR DEN VORGESEHENEN ODER EINEM BESTIMMTEN ZWECK SOWIE JEGLICHER RECHTSVERLETZUNG, JEDOCH NICHT DARAUF BESCHRÄNKT. IN KEINEM FALL SIND DIE AUTOREN ODER COPYRIGHTINHABER FÜR JEGLICHEN SCHADEN ODER SONSTIGE ANSPRÜCHE HAFTBAR ZU MACHEN, OB INFOLGE DER ERFÜLLUNG EINES VERTRAGES, EINES DELIKTES ODER ANDERS IM ZUSAMMENHANG MIT DER SOFTWARE ODER SONSTIGER VERWENDUNG DER SOFTWARE ENTSTANDEN.
 */
public class DBUser {
    /**
     * Diese Klasse verwaltet den @DBUser, auch bekannt als Spieler.
     * Sämtliche Abfragen werden durch diese Klasse verwaltet.
     * Auch der Spieler wird in dieser Klasse erstellt.
     * Auch der {@link ProxiedPlayer} wird in dieser Klasse instanziert.
     */

    String uuid;
    String name;
    ProxiedPlayer player;

    public DBUser(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        if (BungeeCord.getInstance().getPlayer(name) != null) {
            this.player = BungeeCord.getInstance().getPlayer(name);
        }
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public boolean userExists() {
        return PunishmentBootstrap.getInstance().getJedis().exists("uuid:" + getUuid());
    }

    public void createUser() {

        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + getUuid(), "name", getName());
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + getUuid(), "registertimestamp", new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(new Date()));
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + getUuid(), "logins", "1");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + getUuid(), "rank", "spieler");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + getUuid(), "muted", "false");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + getUuid(), "banned", "false");
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + getUuid(), "hasworld", "false");
        System.out.println("Backend -> Created user with uuid:" + uuid);

    }

    /**
     * If the player object is not null(which means that the player is online) this method will return the player object.
     *
     * @return
     */
    public ProxiedPlayer getPlayer() {
        return player;
    }

    /**
     * Updates the online time of a user by 1L(1 minute)
     *
     * @param time
     */
    public void updateOntime(long time) {
        final long currentOntime = Long.parseLong(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + getUuid(), "ontime"));
        final long ontimeNow = currentOntime + 1L;
        PunishmentBootstrap.getInstance().getJedis().hset("uuid:" + getUuid(), "ontime", String.valueOf(ontimeNow));
    }

    public RankEnum getRank() {
        return RankEnum.getRankByName(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + getUuid(), "rank"));
    }

    public Integer getPunishmentLevel() {
        return Integer.valueOf(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + getUuid(), "punishmentLevel"));
    }

    public String getOnlinetime() {

        long seconds = Long.parseLong(PunishmentBootstrap.getInstance().getJedis().hget("uuid:" + getUuid(), "ontime"));
        long minutes = 0L;
        long hours = 0L;
        long days = 0L;
        long weeks = 0L;
        while (seconds > 60L) {
            seconds -= 60L;
            ++minutes;
        }
        while (minutes > 60L) {
            minutes -= 60L;
            ++hours;
        }
        while (hours > 24L) {
            hours -= 24L;
            ++days;
        }
        while (days > 7L) {
            days -= 7L;
            ++weeks;
        }
        while (weeks > 7L) {
            days -= 7L;
        }
        if (weeks != 0L) {
            return "§c" + weeks + " §7Woche(n) §c" + "§c" + days + " §7Tag(e) §c" + hours + " §7Stunde(n) §c" + minutes + " §7Minute(n) §c" + seconds + " §7Sekunde(n)";
        }
        if (days != 0L) {
            return "§c" + days + " §7Tag(e) §c" + hours + " §7Stunde(n) §c" + minutes + " §7Minute(n) §c" + seconds + " §7Sekunde(n)";
        }
        if (hours != 0L) {
            return "§c" + hours + " §7Stunde(n) §c" + minutes + " §7Minute(n) §c" + seconds + " §7Sekunde(n)";
        }
        if (minutes != 0L) {
            return "§c" + minutes + " §7Minute(n) §c" + seconds + " §7Sekunde(n)";
        }
        if (seconds != 0L) {
            return "§c" + seconds + " §7Sekunde(n)";
        }
        return "§c" + weeks + " §7Woche(n) §c" + "§c" + days + " §7Tag(e) §c" + hours + " §7Stunde(n) §c" + minutes + " §7Minute(n) §6" + seconds + " §7Sekunde(n)";

    }


}

