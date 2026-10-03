# Changelog

## Unofficial Fabric port 0.1.0 - 03/10/2026

- Ported the original 0.7.5 release to Fabric for Minecraft 1.21.11; removed the NeoForge target and integrations tied to 1.21.1.
- Migrated Fabric registration, tracked-data synchronization, recipe ingredients, item-model properties, villager persistence, and tooltip/input hooks to the target APIs.
- Preserved saved payback requests while moving sealed-item tinting and payback-tag selection to item-model properties.
- Corrected lost-mail loot data and legacy pigeon-variant loading; registered complete default attributes for both pigeon variants.
- Restored the unsealing sound through the target consumable component and added required English item-tag names.
- Made JEI mixins conditional on JEI being installed.
- Updated the mod's display name, creative tab, project links, author metadata, and Mod Menu links.
