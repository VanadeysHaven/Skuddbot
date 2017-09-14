# The ... Update (v0.5-ALPHA)

## Added
* ...

## Changed
* Dashes (`-`) are now allowed to be used in `!usersettings` and `!serversettings`.
* Discord4J updated: `2.8.3` -> `2.8.4`

## Fixed
* [Twitch Specific] A security issue, if the bot was modded people could ban other people, using the `!reverse` command, without the need of being a mod themselves.
  * `!reverse` now uses a whitelist of commands that may be triggered by the bot via reversing text.