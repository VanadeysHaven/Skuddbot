package me.Cooltimmetje.Skuddbot.Minigames.Trivia;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class TriviaUtilities {

    public static final String apiURL = "https://opentdb.com/api.php?amount=1";

    public static String getQuestion() throws IOException {
        URL url = new URL(apiURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.flush();
        out.close();

        return "test";
    }



}
