# The CMDR Update (v0.5-ALPHA)

## Added
* You can now attach images to welcome and goodbye messages!
  * You can do this using the `WELCOME_MSG_ATTACH` and `GOODBYE_MSG_ATTACH` settings under `!serversettings`.
* You can now make your XP private.
  * You can do this by setting `XP_PRIVATE` to `true` using the `!usersettings` command.

## Changed
* Dashes (`-`) are now allowed to be used in `!usersettings` and `!serversettings`.
* Discord4J updated: `2.8.3` -> `2.8.4`
* Welcome and goodbye messages are now embeds.
* Restructured the `!xp` command in the backend.
  * This comes with increased clarity when a error occurs.

## Fixed
* [Twitch Specific] A security issue, if the bot was modded people could ban other people, using the `!reverse` command, without the need of being a mod themselves.
  * `!reverse` now uses a whitelist of commands that may be triggered by the bot via reversing text.