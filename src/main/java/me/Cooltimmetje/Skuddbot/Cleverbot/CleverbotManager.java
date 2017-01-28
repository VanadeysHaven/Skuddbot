package me.Cooltimmetje.Skuddbot.Cleverbot;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotType;
import me.Cooltimmetje.Skuddbot.Profiles.Server;

/**
 * This class manages everything cleverbots.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.2-ALPHA
 * @since v0.1-ALPHA
 */
public class CleverbotManager {

    private ChatterBotFactory factory;
    private ChatterBot bot;

    /**
     * Constructor for this class. Runs upon bot boot.
     */
    public CleverbotManager(){
        factory = new ChatterBotFactory();
        try {
            bot = factory.create(ChatterBotType.CLEVERBOT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Let the cleverbot think what it should respond
     *
     * @param input What the user said.
     * @param server The server this input originates from.
     * @return The output.
     */
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
