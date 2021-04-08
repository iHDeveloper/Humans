# Humans
Multiplayer game with open world that follows a certain timeline and events in Minecraft.
The story starts with **Wither King** putting all humans in one prison. And the more the time goes on the complex the situation is.

The game architecture provides you the ability to run multiple servers with different behaviour in the same infrastructure.
The design of the game systems are flexible to allow adding more content while safely maintaining the game.

**Note:** The game is not complete at all. The game is unstable and is under the **ALPHA** state.

Check out the [Youtube Video](https://youtu.be/mdQTy7q6s0k) for the showcase of the game world.

## Technical Implementations
Implementations that change the Minecraft behaviour completely.
- Scene System
- Teleportation System
- NPC System
- Boss Bar System
- Cross-Server Time System
- Custom Item System
- Custom GUI System
- Custom Entity System

## Game Modules
The goal of modules is to separate the codebase of the game world. And it lets you focus on what matters.
| Name | Path | Description |
|-------|-------|------------|
| Core | core/ | The main infrastructure to run different parts of the game world |
| Game Service | game-service/ | HTTP Server that provides shared info for all parts of the game world |
| Hub | hub/ | Includes functionality to run the **Hub** of the game world |
| Kotlin | kotlin/ | Empty project that's used to include shared dependencies in order to decrease the size of the plugins |
| Mine | mine/ | Includes functionality to run the **Mine** of the game world |
| Simple API | simple-api/ | In-Memory API to fetch/store data about the player from game service |

## Compile the game
This step will generate all of the jars to run the game world in the `build/` folder.
```shell
# You can use ./gradlew
gradle kotlin:prepare core:prepare game-service:prepare simple-api:prepare hub:prepare mine:prepare
```

## Run the game world
In order to run the game world it consists 3 sections
- [Run the game service](#run-the-game-service)
- [Run part of the game world](#run-part-of-the-game-world)
- [Connect the player to all parts of the world](#connect-the-player-to-all-parts-of-the-world)

### Run the game service
The game service is important to connect parts of the game world together. It runs a HTTP server on port **80**.
It's critical part of the game world. Without it the game will never work.
```shell
java -jar humans-game-service-0.x.jar
```

### Run part of the game world
You need to run it on a Spigot server on `1.8.8` (aka `1.8-R3`).

The plugins folder should include these plugin jars:
- `humans-kotlin-0.x.jar` (Shared dependencies to run the plugins)
- `humans-core-0.x.jar` (Shared infrastructure to provide flexibility in building the game world)
- `humans-simple-api-0.x.jar` (API to contact the game service to fetch/store information about the game world)
- One jar to run part of the game world
-- `humans-hub-0.x.jar` (**Hub** Place)
-- `humans-mine-0.x.jar` (**Mine** Place)

### Connect the player to all parts of the world
You need [BungeeCord](https://github.com/SpigotMC/BungeeCord) to connect the player to all parts of the game world.
This is important in order for the teleportation system work.

