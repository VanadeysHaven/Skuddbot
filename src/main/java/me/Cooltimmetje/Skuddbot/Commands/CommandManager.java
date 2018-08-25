package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Commands.Admin.*;
import me.Cooltimmetje.Skuddbot.Commands.Admin.SuperAdmin.*;
import me.Cooltimmetje.Skuddbot.Commands.Useless.*;
import me.Cooltimmetje.Skuddbot.ServerSpecific.PogoGravesend.SetupCommand;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

import java.io.IOException;

/**
 * This class handles everything commands, and triggers the right bit of code to process the command!
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.32-ALPHA
 * @since v0.1-ALPHA
 */
public class CommandManager {

    /**
     * EVENT: This event gets triggered when a message gets posted, it will check for a command and then run the code to process that command.
     *
     * @param event The event that the message triggered.
     */
    @EventSubscriber
    public void onMessage(MessageReceivedEvent event){
        if(!event.getMessage().getChannel().isPrivate()) {
            switch (event.getMessage().getContent().split(" ")[0].toLowerCase()) {
                case "!game":
                    GameCommand.run(event.getMessage());
                    break;
                case "!xp":
                    XpCommand.run(event.getMessage());
                    break;
                case "!saveprofile":
                    SaveProfile.run(event.getMessage());
                    break;
                case "!twitch":
                    TwitchLinkCommand.run(event.getMessage());
                    break;
                case "!xplb":
                    LeaderboardCommand.run(event.getMessage());
                    break;
                case "!serverinfo":
                    ServerInfoCommand.run(event.getMessage());
                    break;
                case "!serversettings":
                    ServerSettingsCommand.run(event.getMessage());
                    break;
                case "!reloadglobal":
                    ReloadGlobal.run(event.getMessage());
                    break;
                case "!about":
                    AboutCommand.run(event.getMessage());
                    break;
                case "!loadauth":
                    LoadAuth.run(event.getMessage());
                    break;
                case "!flip":
                    FlipTextCommand.run(event.getMessage());
                    break;
                case "!import":
                    try {
                        ImportCommand.run(event.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "!ping":
                    PingCommand.run(event.getMessage());
                    break;
                case "!addawesome":
                    AwesomeManager.add(event.getMessage());
                    break;
                case "!removeawesome":
                    AwesomeManager.remove(event.getMessage());
                    break;
                case "!addadmin":
                    AdminManager.add(event.getMessage());
                    break;
                case "!removeadmin":
                    AdminManager.remove(event.getMessage());
                    break;
                case "!userinfo":
                    UserInfo.run(event.getMessage());
                    break;
                case "!riot":
                    MessagesUtils.sendPlain("(╯°□°）╯︵ ┻━┻", event.getMessage().getChannel(), false);
                    break;
                case "!reverse":
                    ReverseCommand.run(event.getMessage());
                    break;
                case "!usersettings":
                    UserSettingsCommand.run(event.getMessage());
                    break;
                case "o7":
                    SaluteCommand.run(event.getMessage());
                    break;
                case "!panic":
                    PanicCommand.run(event.getMessage());
                    break;
                case "!challenge":
                    ChallengeCommand.run(event.getMessage());
                    break;
                case "!clearcooldowns":
                    ClearCooldownCommand.run(event.getMessage());
                    break;
                case "!randomtest":
                    RandomTestCommand.run(event.getMessage());
                    break;
//                case "!hug":
//                    HugCommand.run(event.getMessage());
//                    break;

                //Pogo Gravesend
                case "!pogo_setup":
                    SetupCommand.run(event.getMessage());
                    break;
            }
        } else {
            switch (event.getMessage().getContent().split(" ")[0].toLowerCase()) {
                case "!confirm":
                    TwitchLinkCommand.confirm(event.getMessage());
                    break;
                case "!cancel":
                    TwitchLinkCommand.cancel(event.getMessage());
                    break;
                case "!setchannel":
                    SayCommand.setChannel(event.getMessage());
                    break;
                case "!say":
                    SayCommand.sayMessage(event.getMessage());
                    break;
                case "!reloadawesome":
                    AwesomeManager.reload(event.getMessage());
                    break;
                case "!flip":
                    FlipTextCommand.run(event.getMessage());
                    break;
                case "!setping":
                    SetPing.run(event.getMessage());
                    break;
                case "!ping":
                    PingCommand.run(event.getMessage());
                    break;
                case "!addmsg":
                    AddMessageCommand.run(event.getMessage());
                    break;
                case "!reverse":
                    ReverseCommand.run(event.getMessage());
                    break;
                case "!rigit":
                    RiggedCommand.run(event.getMessage());
                    break;
            }
        }
    }

}
