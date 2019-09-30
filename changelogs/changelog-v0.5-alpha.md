# The "yes this is away overdue" update. (v0.5-ALPHA)

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
        
## Changed
- Using commands will now also give XP.
- When using the WIP bot in the Test server everybody has elevated permission by default.

## Fixed
- Backend NPE when adding reactions.