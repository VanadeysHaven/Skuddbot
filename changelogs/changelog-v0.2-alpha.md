# MultiServer Support (v0.2-ALPHA)

## Added
* Settings menu. Change how Skuddbot behaves on your server. Type `!settings` to get started.
* Commands:  
    * `!about`  
    * `!reloadglobal`  
    * `!loadauth`
    * `!flip`
* Server object, this will handle most of the per server stuff.
* Multi server profiles! Woo! :tada:
* Server Authorization.
* XP Banning, to ban twitch bots from gaining XP (and Rob ofc).

## Changed
* Changed some error messages.
* Logout command changed to `<mention bot> logout`, this way we don't have to worry about multiple bots (Due to having a Skuddbot and Skuddbot WIP bot now).
* To save all profiles from one guild, you must now save the server.
* Refactored Server class.
* On initialization we'll now create a role called "Linked".
* Added server name to xp leaderboard.
* Added a way for the Twitch Linker to keep track of what server the link originates form.
* You can now have only one link active globally, you'll need to complete that one first before you start another one.
* Twitch link now shows the server origin.
* Profiles now live within the server class.
* Saving servers in a different way, so we can also request a server from a Twitch channel instead of only from a Discord server id.
* Profiles now also hold the Server ID they belong to.
* Updated Discord4J to 2.6.1
* Cleverbots are now per server.
* Fixed role checking system, server owners should now be able to bypass.
* `!saveprofile` command is now only available to Timmy.
* Profiles will now save and unload when the user is inactive for too long.
* The HashMaps that hold the profiles are now public.
* Changed some messages.
* Linked role now only gets assigned when it does actually exist.

## Fixes
* Fixed several Typos.
* Game command not working properly when command isn't properly capitalized. (Thanks Jasch)
* When user left server it could be causing some NullPointers. (Thanks Jasch)

## Removed
* Item system (might return later)
* Brainpower
* No Man's Sky command
* JSON Dumps, will return later though.
* No permission messages -> moved to log.

# SoonTM
* Basic command system, just the framework, so I can expand on that later on.
* A `!help` command.
* Twitch Chat Tracking (after stream stats)
* ???