package me.Cooltimmetje.Skuddbot.Commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Channel;
import me.Cooltimmetje.Skuddbot.Commands.Admin.ServerSettingsCommand;
import me.Cooltimmetje.Skuddbot.Commands.Admin.SuperAdmin.*;
import me.Cooltimmetje.Skuddbot.Commands.Custom.CommandEditor;
import me.Cooltimmetje.Skuddbot.Commands.Useless.*;
import me.Cooltimmetje.Skuddbot.Minigames.Blackjack.BlackjackManager;
import me.Cooltimmetje.Skuddbot.Minigames.Challenge.ChallengeManager;
import me.Cooltimmetje.Skuddbot.Minigames.FreeForAll.FFAManager;
import me.Cooltimmetje.Skuddbot.Minigames.TeamDeathmatch.TdManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;

/**
 * This class handles everything commands, and triggers the right bit of code to process the command!
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5.1-ALPHA
 * @since v0.1-ALPHA
 */
public class CommandManager {

    /**
     * EVENT: This event gets triggered when a message gets posted, it will check for a command and then run the code to process that command.
     *
     * @param event The event that the message triggered.
     */
    public static void onMessage(MessageCreateEvent event){
        String invoker = event.getMessage().getContent().get().split(" ")[0].toLowerCase();

        if(event.getMessage().getChannel().block().getType() == Channel.Type.GUILD_TEXT) {
            switch (invoker) {
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
                    MessagesUtils.sendPlain("(╯°□°）╯︵ ┻━┻", event.getMessage().getChannel().block(), false);
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
                case "!duel":
                case "!fight":
                case "!1v1":
                    ChallengeManager.run(event.getMessage());
                    break;
                case "!clearcooldowns":
                case "!cc":
                    ClearCooldownCommand.run(event.getMessage());
                    break;
                case "!randomtest":
                    RandomTestCommand.run(event.getMessage());
                    break;
                case "!hug":
                    HugCommand.run(event.getMessage());
                    break;
                case "!punch":
                    PunchCommand.run(event.getMessage());
                    break;
                case "!puppy":
                case "!emergencypuppy":
                case "!wuff":
                case "!dogger":
                case "!doggo":
                case "!dog":
                case "!pupper":
                case "!riit":
                case "!rogged":
                case "!help":
                case "!woowoo":
                case "!dogo":
                case "!dogggo":
                case "!doogo":
                case "!dogoo":
                case "!owo":
                case "!doggerino":
                case "!addit":
                case "!doggy":
                case "!defectius":
                    PuppyCommand.run(event.getMessage());
                    break;
                case "!ffa":
                case "!freeforall":
                    FFAManager.run(event.getMessage());
                    break;
                case "!stats":
                    StatsCommand.run(event.getMessage());
                    break;
                case "!bacon":
                    BaconCommand.run(event.getMessage());
                    break;
                case "!blackjack":
                case "!21":
                case "!bj":
                case "!deal":
                    BlackjackManager.run(event.getMessage());
                    break;
                case "!kitty":
                case "!cat":
                case "!pussy":
                case "!kitten":
                    KittyCommand.run(event.getMessage());
                    break;
                case "!statlb":
                    StatLeaderboardCommand.run(event.getMessage());
                    break;
                case "!teamdeathmatch":
                case "!td":
                    TdManager.run(event.getMessage());
                    break;
                case "!command":
                case "!commands":
                case "!cmd":
                    CommandEditor.run(event.getMessage());
                    break;
                case "!cake":
                    CakeCommand.run(event.getMessage());
                    break;
                default:
                    ServerManager.getServer(event.getGuild().block()).runCommand(invoker, event.getMessage());
                    break;
            }
        } else if (event.getMessage().getChannel().block().getType() == Channel.Type.DM){
            switch (event.getMessage().getContent().get().split(" ")[0].toLowerCase()) {
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
