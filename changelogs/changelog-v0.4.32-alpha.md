# v0.4.32-ALPHA

## Added
- Commands:
  - `!hug`
  - `!punch`
- A way for the bot to see when a user has last typed.
  - This list is not persistent.
- User Setting: `MENTION_ME`
  - This will define if you are mentioned in useless commands.
     - This applies to the following commands:
        - `!hug`
        - `!punch`
- Server setting: `ARENA_NAME`
  - This will define the arena name in `!challenge`.
- `!challenge` is now available on Twitch!
  - Use `s!challenge` to start a challenge on Twitch!

## Changed
- Removed the command way of accepting a challenge from the initial message.
  - Typing the command **WILL STILL** accept the challenge.
- WIP bot will no longer spit out a startup message in the bot log channel.
## Fixed
- Admin ID's not being recognized.
- Welcome messages not working properly.