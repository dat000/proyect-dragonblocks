# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Proyect Dragon Blocks: a Minecraft **Forge 1.20.1** mod (Forge 47.2.0, Java 17, official Mojang mappings), mod id `dragonblocks`, base package `com.devdat.dragonblocks`. Built from the Forge MDK, using ForgeGradle 6. GeckoLib 4.4.4 is a dependency (for animated models). Versions and mod metadata live in `gradle.properties` and are expanded into `META-INF/mods.toml` / `pack.mcmeta` by `processResources`.

Code comments, log messages, and in-game debug chat are written in **Spanish**; follow that convention. Lang files exist for `en_us` and `es_es`, so add new translation keys to both.

## Commands

Windows: use `gradlew.bat` (or `./gradlew` in Git Bash).

- Build the mod jar (reobfuscated, in `build/libs/`): `./gradlew build`
- Launch the dev client: `./gradlew runClient` (working dir `run/`)
- Launch a dev dedicated server: `./gradlew runServer` (`--nogui`)
- Run GameTests: `./gradlew runGameTestServer` (none exist yet; the server crashes if no gametests are registered)
- Data generation: `./gradlew runData` (outputs to `src/generated/resources/`)
- Regenerate IDE run configs: `./gradlew genIntellijRuns`
- Fix dependency issues: `./gradlew --refresh-dependencies`, or `./gradlew clean`

There are no unit tests and no linter. To verify a change, compile with `./gradlew build` and then test in game with `runClient`. Check client/server separation with `runServer`.

## Architecture

All event handlers self-register through `@Mod.EventBusSubscriber`, so there is no central registration list. When adding a handler, pick the bus and side carefully:
- `bus = MOD` for lifecycle and registration events (e.g. `RegisterKeyMappingsEvent`, `RegisterCapabilitiesEvent`)
- default/`FORGE` bus for gameplay events (ticks, rendering, input, capability attach)
- `value = Dist.CLIENT` for anything in `client/`, which must never be loaded on a dedicated server

`DragonBlocks.java` is the `@Mod` entry point. It still has MDK example registrations (example block/item/tab, which are placeholders) and calls `ModMessages.register()` in `commonSetup`. `Config.java` is also the MDK example config.

### Player stats capability (`capability/`)
`PlayerStats` holds the per-player state: ki, maxKi, stamina, maxStamina, race (0 = Human, 1 = Saiyan, 2 = Namekian), lifeStage (1–3), and isCharging. `PlayerStatsProvider` exposes it as the `PLAYER_STATS` capability and serializes it to NBT. `PlayerStatsAttachHandler` attaches it to every `Player`. Access it with `player.getCapability(PlayerStatsProvider.PLAYER_STATS).ifPresent(...)`.
- The capability is not copied on `PlayerEvent.Clone` yet, so stats reset on death or dimension change.

### Networking (`network/`)
`ModMessages` owns a single `SimpleChannel` (`dragonblocks:messages`). Packets get sequential IDs in registration order, so append new packets at the end. Each packet class follows the same pattern: a constructor that reads from `FriendlyByteBuf`, `toBytes`, and `handle(Supplier<NetworkEvent.Context>)` with `enqueueWork`. Use the helpers `sendToServer`, `sendToPlayer`, and `sendToAllPlayers`.
- `ChargeKiPacket` (C→S): sets `isCharging`.
- `SyncPlayerStatsPacket` (S→C): writes ki/stamina values into the client-side capability. It syncs only ki, maxKi, stamina, and maxStamina. If you need new fields on the client, extend this packet.

### Server is authoritative: the ki charging flow
1. The client's `KeyInputHandler` watches `CHARGE_KI_KEY` (C) and sends `ChargeKiPacket` only when the key state changes.
2. On the server, `core/event/ServerTickHandler` runs on `PlayerTickEvent` END. While charging, it adds ki and sends `SyncPlayerStatsPacket` to that player.
3. The client's `DebugHudOverlay` draws the synced values.

New stat mechanics should follow the same loop: client input → C→S packet → server mutates the capability → S→C sync → client renders.

### Client-only systems (`client/`)
- `ClientKeybinds`: key mappings, in category `key.categories.dragonblocks`. Lock-on is the middle mouse button; charge ki is C.
- `LockOnManager`: client-side target lock. It picks the nearest `LivingEntity` within 16 blocks and breaks the lock beyond 20 blocks. The scroll wheel cycles targets (and cancels the hotbar scroll). It smoothly overrides camera yaw/pitch in `ViewportEvent.ComputeCameraAngles`. `getLockedTarget()` is used by `LockOnRenderer` to draw `textures/gui/lock_icon.png` at the target's torso.

### Race models (GeckoLib player replacement)
`capability/Race` maps race ids (`-1` vanilla, the default; `0` humano; `1` saiyajin; `2` namekiano). Change a race in game with `/dragonblocks race <name> [targets]` (`command/RaceCommand`, op level 2).
- **Sync:** `core/event/PlayerSyncHandler` sends `SyncRacePacket` to the player and everyone tracking them. It sends on login, respawn, dimension change and when the command runs. On `StartTracking` it sends only to the new tracker. `PlayerEvent.Clone` copies the capability so stats survive death.
- **Rendering (client):** `PlayerModelReplacer` cancels `RenderPlayerEvent.Pre` and draws the player with a `RacePlayerRenderer` (`GeoReplacedEntityRenderer`). It uses one singleton `RaceAnimatable` per race; GeckoLib keeps animation state per entity id. The animation names are the raw keys from the `.animation.json`: `idle` while standing, `walk` while moving and `run` while sprinting. Every race's animation file must define these three. Head look rotation is applied at render time in `RacePlayerRenderer.renderRecursively` (bone `Head`), not by changing the bone's rotation, because that would accumulate across frames.
- **Adding a race model:** put the `.geo.json`, texture and `.animation.json` under `assets/dragonblocks/`, then point that race's entry in `client/renderer/RaceRenderers` at them. Saiyan and Namekian currently reuse the human files. The first-person hand is still vanilla.
