# The Shitposting Update (v0.3-ALPHA)

## Added
* Awesome users!
  * These users get all sorts of useless features :D
  * Commands:
    * `!addmsg <type> <message>` - *pm only*
      * Available types: `PLAYING` `ERROR` `ALIVE`
        * `PLAYING`: Messages for the playing status.
        * `ERROR`: Messages for the error messages.
        * `ALIVE`: The messages that get posted in Twitch Chat upon streams going live.
    * `!setping` - *pm only*
      * This will allow you to set your `!ping` response.
* Super admins!
  * These users are allowed to override ALL permissions within Skuddbot, these users are carefully chosen by me.
* Code documentation!
  * I only documented new stuff and stuff I changed!
* Command: `!ping`
  * **PONG!**
* Settings
  * `VR_MODE` (boolean - true/false)
    * This mode is for VR streamers, this will put a exclamation mark (!) and a space in front of every message Skuddbot posts in Twitch Chat. That way you can exclude those message from being read out by a TTS bot. (Requested by [Melsh](https://www.twitch.tv/melsh87) (Go follow him! <3))
  
## Changed
* Rob has been unbanned from XP gain. LUL
* HikariCP version 2.4.7 -> 2.5.1
* Some refactoring
* `!flip` is now available in PM's and Twitch Chat (Twitch chat has a 30 second cooldown). (Jasch, I got ya covered)
* Shit got more Juicy!
* Playing messages now rotate through the messages pool that can be set by awesome users!
