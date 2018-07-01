# The "Make it slightly better" update. (v0.4.31-ALPHA)
This update brings various changes and fixes, also some minor additions are added in this version.

## Added
- `!serverinfo`
   - Shows useful information about the server.
- More logging.
- Support for custom Emoji.
   - This is basic for now, better support to be added.
- Aliases for standard Emoji's.
   - This is to be in conformity with the new Discord4J update.

## Removed
- `!initialize`
- `!dumpdata`


## Changed
- [Discord4J](https://github.com/Discord4J/Discord4J) updated: `2.9.2 -> 2.10.1`
  - And the code has been updated accordingly.
- Initialization now happens automatically upon new server join.
- Initialization happens way faster.
- Debug strings with reactions are no longer mandatory.
- These commands now use reactions:
   - `!game`
   - `@mention logout`
   - `!twitch`
   - `[redacted]`

## Fixed
- Initializations should no longer bug out.
- Salutes should now work better.

## Coming soon
- Commands... *I promise*.