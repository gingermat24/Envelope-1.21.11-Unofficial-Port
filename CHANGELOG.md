# Changelog

## 0.3.1 - 10/10/2026

### Changed

- Added compatibility for the optional JEI integration with JEI 27.3.0.10 and newer; tested with 27.3.0.10, 27.3.0.13, and 27.44.0.105.

### Technical

- Updated the Gradle wrapper, Architectury Loom and plugin, Shadow, Mod Publish, and Wiki Toolkit to current stable releases.
- Verified target-specific dependency pins against current repositories: Fabric Loader 0.19.5, Fabric API 0.141.6+1.21.11, Forge Config API Port 21.11.1, and JEI 27.44.0.105.

## 0.3.0 - 08/10/2026

### Added

- Added the Courier Bat for night deliveries. Mailboxes can employ nearby Bats or summon one when needed; Mail Service deliveries use Bats at night. A Bat switches between courier roles at the virtual hub if the time changes during a delivery.
- Added a Spider Eye food slot for Bats, Bat courier visuals and sounds, and the "The Pigeons and the Bats" advancement. Each Bat can complete up to three deliveries; its delivery count is saved.
- Added the Soulbound Seal Stamp, crafted by combining a Seal Stamp with an Echo Shard. It has eight uses and returns as the original stamp with its wax and die preserved.
- Added persistent seal locks and `/envelope seal_lock create|unlock|list`. Locks can be used by Soulbound Seals or assigned to other seals for custom uses; `create` accepts either an owner name or an existing `owner#uuid` lock ID.
- Added all 16 colored Seal Stamp designs and separate Gold Stamp artwork. Added the `cube`, `letter_and_quill`, and `skull_and_bones` designs and their Mail Service recipes; the cube recipe requires a Grass Block.
- Added JEI notes for broadcast and payback-cancellation recipes, clickable service addresses, and previews for the dye-specific Seal Stamp recipes.
- Added the original recipient to delivered-mail tooltips and the original Halloween spiderweb decoration to empty mailboxes.

### Changed

- Replaced the port's initial custom seal-coloring approach with the original colored wax backgrounds, impression palettes, and stamp artwork from upstream.
- Increased background Pigeon travel speed from 20 to 25 blocks per second; Bat speed defaults to 50 blocks per second.
- Added server settings for Bat delivery limits, Soulbound Stamp availability/uses/locks, seal removal time, delivery phase duration, and Mail Service courier-death notices. Unified courier damage evasion and replaced the old courier spawning-rule option. **Review the regenerated server configuration after updating; renamed settings may need to be reapplied.**
- Moved send and broadcast commands under `/envelope mail`; sending supports optional sender and recipient addresses. Added `/envelope debug terminate_all_deliveries confirm` with explicit confirmation.
- Moved seal artwork to animated GUI sprites and changed seal material/symbol definitions to use sprite references. Legacy texture fields remain readable. Seal data can now include an optional lock.
- Added material-specific seal glint (Gold) and animated Sculk wax, the original stamp sound, and lock overlays. The preview lock symbol reflects lock data on the item and may remain visible after an administrator deactivates the lock.
- Increased the visibility distance of courier arrival/departure particles and made couriers play their ambient sound when a delivery finishes.
- Removed the Slimeball requirement from the Automated Supply Service Letter and Writable Book recipes. The Seal Stamp recipe now uses honeycomb, planks, and an iron ingot.

### Fixed

- Fixed the Mail Service recipes for the cube, letter-and-quill, and skull-and-bones Seal Stamps failing to load.
- Fixed Soulbound Seal Stamp previews, including Sculk animation and Creative-inventory stamps defaulting to Sculk.
- Restored native animated-sprite rendering for Sculk wax so its interpolation metadata is honored; Gold glint keeps its upstream animated sequence and intentional pauses.
- Fixed Soulbound stamps obtained directly in Creative so they can seal mail without a crafting-added material component.
- Fixed the Soulbound Seal death-protection mixin for the target game's inventory list method signature.
- Fixed bulk-expiring payback mail by returning items from a snapshot while removing expired entries.
- Removed an unused service-address tick callback.

This release adapts selected features from the original creators' [Envelope 0.8.0 Snapshot 1 and Snapshot 2](https://www.patreon.com/mortuusars/posts/envelope-0-8-0-2-171460637). See [Credits](README.md#credits) for the original project and creators.

## 0.2.1 - 05/10/2026

- Added the `delivery.ascend_distance` server configuration option; its default is 24 blocks.
- Added 16 dye-specific seal materials and stamp-dye recipes, plus a gold seal material.
- Added the seal creator's UUID to newly applied seals; recoloring retains the existing impression, signature, and creator.
- Changed the Seal Stamp recipe to honeycomb, planks, and an iron ingot; removed slimeballs from the Automated Supply Service Letter and Writable Book recipes.
- Added a custom stamp-dye recipe that accepts exactly one Seal Stamp and one matching dye/material, preserves the stamp's other data, and assigns the selected wax.
- Kept undyed stamps neutral and unable to seal mail; dyed stamps can recolor existing seals.
- Dyeing a stamp preserves its die and other components; mail-service stamp recipes carry forward the selected wax.
- Restored the 53 letter, number, and emblem impression textures with their grayscale detail; material backgrounds remain separate.
- Ported the upstream per-channel seal-shading palette values for every wax color and matched its multiplier conversion from the [1.21.1-dev source revision](https://github.com/mortuusars/Envelope/commit/cb8c3d1fec89b626794c0d4c2a51317c33b5c481).
- Ported the original background sprite setup: each colored wax seal now uses its own source sprite instead of recoloring one neutral background; seal materials select that sprite through their `sprite` field.
- Adapted the upstream stamp-preview shading to Minecraft 1.21.11's GUI draw pipeline by applying the same channel multipliers to source pixels through a bounded, reload-aware generated-texture cache.
- Kept the existing seal-material registry IDs so worlds and saved item components continue resolving their materials.
- Restored adult and baby model scaling for both pigeon variants.
- Corrected Letter and Quill layout: writing-area clipping, wrapping for over-width characters, and 50%-opacity selection highlights.
- Corrected delivered-letter parchment and tattered-overlay sampling for 256x256 textures, and clipped displayed text to its writing area.
- Corrected texture sampling in Address Tag, package, payback, and packing screens, including the Payback Box letter-slot preview.
- Made Mailbox labels render with an opaque text color.
- Consolidated sender, delivery-log, and returned status into `mail_delivery_info`; legacy components remain readable and are removed when delivery data is next written. Retained `mail_id` for mailbox identity and removal.
- Avoided mutating the pending-subject map while returning expired payback requests, preventing a server-side concurrent-modification crash.
- Declared the minimum Fabric Loader version in Fabric mod metadata.
- Corrected publishing tasks to upload the remapped Fabric release JAR instead of resolving the shared common module artifact.

## Beta 0.1.0 - 03/10/2026

- Ported the original 0.7.5 release to Fabric; removed the NeoForge target and integrations tied to 1.21.1.
- Migrated Fabric registration, tracked-data synchronization, recipe ingredients, item-model properties, villager persistence, and tooltip/input hooks to the target APIs.
- Preserved saved payback requests while moving sealed-item tinting and payback-tag selection to item-model properties.
- Corrected lost-mail loot data and legacy pigeon-variant loading; registered complete default attributes for both pigeon variants.
- Restored the unsealing sound through the target consumable component and added required English item-tag names.
- Made JEI mixins conditional on JEI being installed.
- Updated the mod's display name, creative tab, project links, author metadata, and Mod Menu links.
