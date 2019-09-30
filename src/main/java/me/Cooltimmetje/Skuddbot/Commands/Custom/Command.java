package me.Cooltimmetje.Skuddbot.Commands.Custom;

import lombok.Getter;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;

import java.util.HashMap;

/**
 * This class represents a custom command.
 * Holds things like the invoker, output, metadata and properties.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5-ALPHA
 * @since v0.5-ALPHA
 */
public class Command {

    private String serverId;
    @Getter private String invoker;
    private String output;
    private HashMap<MetaData,String> metaData;
    private HashMap<Properties,String> properties;

    public Command(String serverId, String invoker, String output){
        this.serverId = serverId;
        this.invoker = invoker;
        this.output = output;

        this.metaData = new HashMap<>();
        this.properties = new HashMap<>();
    }

    public void run(IMessage message) {
        MessagesUtils.sendPlain(output, message.getChannel());
    }
}
