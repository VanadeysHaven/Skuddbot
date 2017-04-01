# Skuddbot Analytics (v0.4-ALPHA)

## Added
* User settings!
  * Access with `!usersettings`
  * Works the same as Server Settings.
  * Settings available:
    * `LEVEL_UP_NOTIFY` - This specifies how you see level up notifications.
      * 0: Notifies you by a reaction, reacting to that message too posts the message like normal.
      * 1: Notifies you by a PM.
      * 2: Does not notify you at all, but you will still continue to gain XP and show up on leaderboards and such.
    * `TRACK_ME` - This will disable **ALL** user tracking. XP and Analytics only for now (as there is no other forms of tracking currently). This is change is necessary to make Skuddbot compliant with the Twitch TOS! *However* Skuddbot will still respond to all your commands.
      * This setting can be changed via the `!usersettings` command on Discord, or by typing `!toggletracking` (30 second cooldown) in Twitch Chat.
      * If you have linked your Twitch account, then the setting will change retroactively on both Twitch and Discord (regardless of where you execute the command).
      * Turning this off will **NOT** delete your progress, it will simply pause it. You will still continue to show up on the leaderboards and such, turning it back on will resume your progress.
      * In the event you link your Twitch Account to Discord in Skuddbot, the setting on Discord is from that point onwards your `TRACK_ME` setting
        * Example: If you have tracking turned off on Twitch but not on Discord and you link. Tracking will then resume on both platforms, you can obviously turn it back off if you like.
* Attempts to reduce messages. REACTIONS FTW!

## Changed
* Level up notification's are now shown with reactions. You can also alter how you see your level up's with the newly added User Settings!
* `!settings` is now `!serversettings`
  * Classes regarding server settings have also been refactored.
* Database Structure changed to have room for the new user settings.

## Fixed
* Several typo's have been addressed. I can English.
* `!reverse` Twitch Chat cooldown now applies properly.

## Removed
* Cleverbot