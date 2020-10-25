package de.alex0219.punishment.uuid;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Alexander on 19.08.2020
 * © 2020 Alexander Fiedler
 **/
public class PlayerDBFetcher {

    public ExecutorService service = Executors.newCachedThreadPool();


    public static String
    getUUID(String username) {
        try {
            URL url = new URL("https://playerdb.co/api/player/minecraft/" + username);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String line = reader.readLine();

            final JSONParser parser = new JSONParser();

            try {
                final JSONObject json = (JSONObject) parser.parse(new InputStreamReader(url.openStream()));

                System.out.println(json);
                if (json.get("id") != null) {
                    String uuid = json.get("id").toString();
                    return uuid;
                } else {


                }


            } catch (ParseException e) {

            }


        } catch (IOException e) {
        }

        return "";


    }

    public static String getName(String uuid) {
        try {
            URL url = new URL("https://playerdb.co/api/player/minecraft/" + uuid);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String line = reader.readLine();

            final JSONParser parser = new JSONParser();

            try {
                final JSONObject json = (JSONObject) parser.parse(new InputStreamReader(url.openStream()));

                if (json.get("username") != null) {
                    String username = json.get("username").toString();

                    return username;
                }


            } catch (ParseException e) {

            }


        } catch (IOException e) {
        }
        return "";
    }

    public static String insertDashUUID(String uuid) {
        StringBuffer sb = new StringBuffer(uuid);
        sb.insert(8, "-");

        sb = new StringBuffer(sb.toString());
        sb.insert(13, "-");

        sb = new StringBuffer(sb.toString());
        sb.insert(18, "-");

        sb = new StringBuffer(sb.toString());
        sb.insert(23, "-");

        return sb.toString();
    }
}
