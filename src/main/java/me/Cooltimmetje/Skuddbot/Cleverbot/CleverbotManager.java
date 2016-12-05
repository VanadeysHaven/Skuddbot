package me.Cooltimmetje.Skuddbot.Cleverbot;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotType;
import me.Cooltimmetje.Skuddbot.Profiles.Server;

/**
 * Created by Tim on 8/2/2016.
 */
public class CleverbotManager {

    private ChatterBotFactory factory;
    private ChatterBot bot;

    public CleverbotManager(){
        factory = new ChatterBotFactory();
        try {
            bot = factory.create(ChatterBotType.CLEVERBOT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getOutput(String input, Server server){
        if(server.getSession() == null){
            server.setSession(bot.createSession());
        }
        try {
            return server.getSession().think(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
