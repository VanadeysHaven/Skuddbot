# The "yes this is way overdue" update. (v0.5-ALPHA)

## Added
- **CUSTOM COMMANDS!!**
  - Yes, you can now create your own commands!
  - Command: `!command`
    - Alias: `!cmd`
    - Command requires elevated permissions.
  - Functionality:
    - Adding of commands:
      - `!command add <invoker> <output...>`
        - This will create a new command with the given invoker and output.
    - Editing of commands:
      - `!command edit <invoker> <newOutput...>`
        - This will edit the output of the command with the given invoker to the new specified output.
      - `!command edit -invoker <oldInvoker> <newInvoker>`
        - This will edit the command with the given old invoker to the new invoker.
        
## Changed
- Using commands will now also give XP.
- When using the WIP bot in the Test server everybody has elevated permission by default.

## Improved
- Less bot spam!

## Fixed
- Backend NPE when adding reactions.