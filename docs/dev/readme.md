# Tilapia Network

### Todo
1. Fetch language files from HTTP
2. Fetch libraries and load them from HTTP

### Projects
(By read-only projects, you could still do pull requests on them)


#### Public Projects
Public projects means every developer working in Tilapia has access to 
source of them. They are usually common libraries. Public read-only projects are marked with `[R]`,
read-write projects are marked as `[RW]`

- `[R] tilapia-api` The core of tilapia. Has mini-game system, lobbies system,
events system, commands system, and more.: 
- `[RW] tilapia-common` The common library for both proxy and spigot
- `[RW] tilapia-game-extension` Extensions for games, has game rules and more

#### Closed-Source Projects
Closed-source projects simply means no one except from team working on
it has access to the source. For more information, please 

- `tilapia-core` Implementation of `tilapia-api`. You could ask for a snapshot of this project.
- 

#### Private Projects
Private projects means they are libraries developed by individual developer.
The owner has permission to decide whether or not to make it public to
every developer. Public read-only projects are marked with `[R]`,
read-write projects are marked as `[RW]`, and private projects are marked
with `[P]`.

- `[RW] fan87-plugin-dev-kit` Common library that fan87 uses. Includes Gui
library, serialization-based config API, utilities, and more.