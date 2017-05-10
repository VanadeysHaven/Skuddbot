# Skuddbot Analytics (v0.4-ALPHA)

## Added
* **ANALYTICS**
  * Yes, the long awaited analytics are here!
  * The analytics will work automatically with MuxyBot, otherwise you'll have to manually tell the bot you go live and when you head offline. - Later, we'll automate this without MuxyBot, but this is what you have to deal with for now.
  * The following stats are tracked:
    * Quotes added.
    * Riots.
    * Top 5 messages posted per user. (Grant's rewards)
    * Top 5 XP gain per user. (Grant's rewards)
    * Top 5 riots started per user. (Grant's rewards)
    * Average XP gain per user.
    * Total XP gain.
    * Unique users.
    * Messages seen.
    * Longest Chatwall (Grant's rewards)
  * Users will get rewards for placing high in analytics.
  * Notes:
    * Chat logs are being kept for the duration of the stream, and are deleted after.
      * However, we are saving the **FULL** Analytic results to JSON file, this is for use in later updates. These do not contain actual message content.
    * Users that have tracking turned off have no influence on analytics **AT ALL**.
* User settings!
  * Access with `!usersettings`
  * Works the same as Server Settings.
  * Settings available:
    * `LEVEL_UP_NOTIFY` - This specifies how you see level up notifications.
      * 0: Notifies you by a reaction, reacting to that message too posts the message like normal.
      * 1: Notifies you by a PM.
      * 2: Does not notify you at all, but you will still continue to gain XP and show up on leaderboard and such.
    * `TRACK_ME` - This will disable **ALL** user tracking. XP and Analytics only for now (as there is no other forms of tracking currently). This is change is necessary to make Skuddbot compliant with the Twitch TOS! *However* Skuddbot will still respond to all your commands.
      * This setting can be changed via the `!usersettings` command on Discord, or by typing `!toggletracking` (30 second cooldown) in Twitch Chat.
      * If you have linked your Twitch account, then the setting will change retroactively on both Twitch and Discord (regardless of where you execute the command).
      * Turning this off will **NOT** delete your progress, it will simply pause it. You will still continue to show up on the leaderboard and such, turning it back on will resume your progress.
      * In the event you link your Twitch Account to Discord in Skuddbot, the setting on Discord is from that point onwards your `TRACK_ME` setting
        * Example: If you have tracking turned off on Twitch but not on Discord and you link. Tracking will then resume on both platforms, you can obviously turn it back off if you like.
    * `ANALYTICS_MENTION` - This will define if the bot will mention you if you appear on the analytics leaderboard.
* New server settings:
  * `STREAM_LIVE` - This specifies if the stream is live and logs should be kept for analytics (if enabled).
    * If you have MuxyBot, you do not need to change this value, it will happen automatically.
  * `ALLOW_ANALYTICS` - This defines if you want analytics to be generated upon the end of the stream.
  * `ALLOW_REWARDS` - This defines if you want rewards to be granted to users when they did well in the analytics.
* Attempts to reduce messages. REACTIONS FTW!
* Lots of documentation added. Still not everything has been documented.

## Changed
* Level up notification's are now shown with reactions. You can also alter how you see your level up's with the newly added User Settings!
* `!settings` is now `!serversettings`
  * Classes regarding server settings have also been refactored.
* Database Structure changed to have room for the new user settings and is now much more efficient.
* Server settings are now much easier to handle in the code. (Doesn't matter for the end user... but we got it!)
* Server settings can now be saved without the profiles in that server.
* Added some warning suppression's because we want neat looking code.
* Moved some constants to the server class and also removed some.
* Min/Max values are now ignored when loading settings.
* Changed how we detect streams going live. (Technically also a bug fix. - Thanks [Smooth_Knight](https://www.twitch.tv/smooth_knight)!)
* Discord4J version bump.  2.7.0 -> 2.8.1

## Fixed
* Several typo's have been addressed. I can English.
* `!reverse` Twitch Chat cooldown now applies properly.
* Fixed a instance where server settings would not save, server settings now save every 10 minutes too.
* Fixed a bug where if the `ADMIN_ROLE` role wasn't found, the `!serversettings` command wouldn't work. (Fuck you [Jess](https://www.twitch.tv/valkyrien27).)
* Fixed a instance where the bot would cut off the first character when using `!flip` and `!reverse`.

## Removed
* Cleverbot