# The update we've been waiting for... (v0.4.2-ALPHA)

## Added
* You can now attach images to welcome and goodbye messages!
  * You can do this using the `WELCOME_MSG_ATTACH` and `GOODBYE_MSG_ATTACH` settings under `!serversettings`.
* You can now make your XP private.
  * You can do this by setting `XP_PRIVATE` to `true` using the `!usersettings` command.
* New avatar: `MEME`

## Changed
* Dashes (`-`) are now allowed to be used in `!usersettings` and `!serversettings`.
* Discord4J updated: `2.8.3` -> `2.9.2`
* Welcome and goodbye messages are now embeds.
* Restructured the `!xp` command in the backend.
  * This comes with increased clarity when a error occurs.

## Fixed

#### Twitch Issues
* A security issue, if the bot was modded people could ban other people, using the `!reverse` command, without the need of being a mod themselves.
  * `!reverse` now uses a whitelist of commands that may be triggered by the bot via reversing text.
  * Commands that are not whitelisted may still be reversed, but won't trigger other bots.
* `!riot` is no longer case sensitive.