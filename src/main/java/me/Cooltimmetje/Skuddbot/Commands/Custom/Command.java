package me.Cooltimmetje.Skuddbot.Commands.Custom;

import lombok.Getter;
import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;

import java.text.MessageFormat;
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
    @Getter @Setter private String output;
    private HashMap<MetaData,String> metaData;
    private HashMap<Properties,String> properties;

    public Command(String serverId, String invoker, String output){
        this.serverId = serverId;
        this.invoker = invoker;
        this.output = output;

        this.metaData = new HashMap<>();
        this.properties = new HashMap<>();

        MySqlManager.createNewCommand(serverId, invoker, output, metaDataJSON());
        Logger.info(MessageFormat.format("Created new command for server id {0} with invoker {1} and output {2}", serverId, invoker, output));
    }

    public Command(String serverId, String invoker, String output, String metaData, String properties){
        this.serverId = serverId;
        this.invoker = invoker;
        this.output = output;

        this.metaData = new HashMap<>();
        this.properties = new HashMap<>();
        parseMetaData(metaData);
        parseProperties(properties);
        Logger.info(MessageFormat.format("Loaded command for server id {0} with invoker {1}, output {2}, metadata {3} and properties {4}", serverId, invoker, output, metaData, properties));
    }

    public void run(IMessage message) {
        MessagesUtils.sendPlain(output, message.getChannel());
    }

    private String metaDataJSON(){
        return "{}";
    }

    private String propertiesJSON(){
        return "{}";
    }

    private void parseMetaData(String metaData){

    }

    private void parseProperties(String properties){

    }

    public void save(){
        MySqlManager.saveCommand(serverId, invoker, output, metaDataJSON(), propertiesJSON());
        Logger.info(MessageFormat.format("Saved command for server id {0} with invoker {1}, output {2}, metadata {3} and properties {4}", serverId, invoker, output, metaDataJSON(), propertiesJSON()));
    }
}
