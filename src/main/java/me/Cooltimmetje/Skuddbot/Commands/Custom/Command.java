package me.Cooltimmetje.Skuddbot.Commands.Custom;

import lombok.Getter;
import lombok.Setter;
import me.Cooltimmetje.Skuddbot.Commands.Custom.MetaData.MetaDataContainer;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Utilities.Logger;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import org.json.simple.parser.ParseException;
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
    private MetaDataContainer metaData;
    private HashMap<Properties,String> properties;

    public Command(String serverId, String invoker, String output, long createdBy){
        this.serverId = serverId;
        this.invoker = invoker;
        this.output = output;

        try {
            this.metaData = new MetaDataContainer("{}");
            this.metaData.setTimeCreated(System.currentTimeMillis());
            this.metaData.setLastUpdated(System.currentTimeMillis());
            this.metaData.setLastUpdatedBy(createdBy);
            this.metaData.setCreatedBy(createdBy);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.properties = new HashMap<>();

        MySqlManager.createNewCommand(serverId, invoker, output, metaData.getJSON());
        Logger.info(MessageFormat.format("Created new command for server id {0} with invoker {1}, output {2} and metadata {3}", serverId, invoker, output, metaData.getJSON()));
    }

    public Command(String serverId, String invoker, String output, String metaData, String properties){
        this.serverId = serverId;
        this.invoker = invoker;
        this.output = output;

        try {
            this.metaData = new MetaDataContainer(metaData);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.properties = new HashMap<>();
        parseProperties(properties);
        Logger.info(MessageFormat.format("Loaded command for server id {0} with invoker {1}, output {2}, metadata {3} and properties {4}", serverId, invoker, output, metaData, properties));
    }

    public void run(IMessage message) {
        MessagesUtils.sendPlain(output, message.getChannel());
        metaData.incrementCount();
    }

    private String propertiesJSON(){
        return "{}";
    }

    private void parseProperties(String properties){

    }

    public void save(){
        MySqlManager.saveCommand(serverId, invoker, output, metaData.getJSON(), propertiesJSON());
        Logger.info(MessageFormat.format("Saved command for server id {0} with invoker {1}, output {2}, metadata {3} and properties {4}", serverId, invoker, output,  metaData.getJSON(), propertiesJSON()));
    }

    public void setInvoker(String newInvoker){
        String oldInvoker = this.invoker;
        this.invoker = newInvoker;
        MySqlManager.editInvoker(serverId, oldInvoker, newInvoker);
    }

    public void update(long userId){
        metaData.setLastUpdated(System.currentTimeMillis());
        metaData.setLastUpdatedBy(userId);
    }
}
