# Tilapia Network

## Module System
Every plugin is currently included in this main repository, including all our 
forks. It will be cleaned up once the scale gets larger.

### Code outside of this repository
#### Client-side mod
We have a client-side mod. The repository is currently being maintained and
developed by fan87

## Issue/Bug Tracker
Please refer to our [Issues](https://github.com/Tilapia-Network/Tilapia/issues)
page on this GitHub repository. Issues or features should be fixed or implemented
by the group of person assigned to that issue.

### For users
For bug reports, please report them to our [#快照版bug回報](https://discord.com/channels/1082381511296307301/1098132968482013206)
discord channel. Issue will be opened by developer.

## Codestyle
We are following the Kotlin & Java's official coding convention, with English as
primary language, and Chinese for texts. 

### Language
All Chinese/User-friendly texts should be put inside `LanguageDelegation` wrapper 
that generates a `LanguageKey` object with the text as default value.

For commands, they should be put inside `getCommandLanguageKey`; and for games, they
should be in `getGameLanguageKey`.


## Repositories We Forked
- `PacketWrapper` included in `tilapia-api`
  - Modified for our use
- `FastAsyncWorldEdit-Legacy` included in `fastasyncworldedit` module
  - Added feature that builders are requesting

