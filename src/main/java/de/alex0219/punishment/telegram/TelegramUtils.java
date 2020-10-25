package de.alex0219.punishment.telegram;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Alexander on 14.08.2020 04:42
 * © 2020 Alexander Fiedler
 */
public class TelegramUtils {

    public void sendTelegramMessage(final String chatID, final String message, final String token) {
        String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
        urlString = String.format(urlString, token, chatID, message);
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            InputStream is = new BufferedInputStream(conn.getInputStream());
            System.out.println("[Notify] Telegram message successfully sent!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
