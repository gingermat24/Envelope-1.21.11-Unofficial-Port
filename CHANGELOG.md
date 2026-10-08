# Changelog

## 0.3.0 - 08/10/2026

- Ported the Courier Bat for night mail delivery; mailboxes can employ nearby vanilla Bats or summon one when none are available.
- Added the Spider Eye Bat-food slot to mailboxes without moving the existing food and mail slot indices, preserving existing mailbox inventories.
- Mail Service deliveries use Bats at night, and background couriers switch between Bat and Pigeon at the virtual mail hub when the time of day changes.
- Added separate Pigeon and Bat background travel speeds (25 and 50 blocks per second by default) and the "The Pigeons and the Bats" advancement.
- Added Bat entity registration, courier AI, delivery visuals, backpack texture, and Bat settings for mailbox employment and spawning.
- Added the Soulbound Seal Stamp, crafted from a Seal Stamp and Echo Shard. It has eight uses and returns as the original stamp with its wax and die preserved.
- Fixed the Soulbound stamp preview: animated Sculk now cycles through its four frames at the configured one-second frame duration, and stamps obtained in Creative default to the Sculk seal preview.
- Fixed Soulbound Seal Stamps obtained directly from Creative being rejected when sealing unsealed mail: they now use their intrinsic Sculk material even without a crafting-added material component.
- Added persistent seal locks. Soulbound seals prevent other players from opening the item until the sealer dies, and the sealed item is kept through death and transferred into the sealer's respawned inventory.
- Added `/envelope seal_lock create|unlock|list` for managing locks; an active lock must also be assigned in the seal component to protect an item.
- Added the original recipient to delivered-mail tooltips and made service addresses clickable in JEI mailing recipes to show usages.
- Migrated seal art into `textures/gui/sprites/seal`, enabled animated seal sprites, and changed seal material/symbol data to the `sprite` field while retaining support for legacy `texture` data.
- Fixed Soulbound Seal startup crashes by matching both the owner and receiver types of Minecraft 1.21.11's `NonNullList.set` call in its death-protection mixin.
- Implemented the Courier Bat, Soulbound Seal, and related features described in the original creators' [Envelope 0.8.0 Snapshot 2 notes](https://www.patreon.com/mortuusars/posts/envelope-0-8-0-2-171460637); see [README credits](README.md#credits).
- Updated pigeon background delivery speed from 20 to 25 blocks per second.
- Added a unified courier damage-evasion setting and renamed the corresponding damage-type tag.
- Replaced the courier `doMobSpawning` setting with `spawning_ignores_domobspawning_rule`; its default preserves the prior behavior of respecting the game rule.
- Added a server setting to disable Mail Service notices when a courier dies.
- Made courier appearance/disappearance particles visible at greater distances and play courier ambient sounds when deliveries finish.
- Moved administrative send and broadcast commands to `/envelope mail send` and `/envelope mail broadcast`; mail sending accepts an optional `from <address>` sender and `to <address>` recipient override.
- Added `/envelope debug terminate_all_deliveries`, with a separate `confirm` literal to prevent accidental termination.
- Ported the `cube`, `letter_and_quill`, and `skull_and_bones` seal symbols and their Mail Service stamp recipes; the cube recipe requires a Grass Block. Legacy symbol registry entries remain for existing seals, while the obsolete skeleton-stamp recipe was removed.
- Added original color-specific textures and item models for all 16 dyeable Seal Stamps, selected by the stamp's material component.
- Added the original Seal Stamp sound to stamp application and ported the animated Soulbound glint plus lock overlays to seal previews.
- Updated mailbox mail-type icons and added the original Halloween spider/web decoration for empty mailboxes.
- Added a JEI crafting extension that previews the 16 dye-specific Seal Stamp results with their correct material components.
- Deferred Cloud Depository and Letter Presetting. Every Compat remains unported because no compatible Fabric artifact for Minecraft 1.21.11 was available on Modrinth on 2026-10-08.
- Fixed bulk-expiring pending payback mail by iterating over a snapshot while items are removed and returned.
- Removed an unused service-address tick callback and obsolete experimental code from the debug command.
- Renamed server configuration paths. Existing settings may be regenerated and need to be reapplied after updating.
- Adapted selected delivery and command changes from the original creators' [Envelope 0.8.0 Snapshot 2 notes](https://www.patreon.com/mortuusars/posts/envelope-0-8-0-2-171460637).
- Reviewed and linked the community proposals in `COMMUNITY_SUGGESTIONS.md`, including upstream ideas for tag copying, return-to-sender, feather alternatives, Create automation, and mail-in rebates.

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
