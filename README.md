<div align="center">
  <img src="https://cdn.modrinth.com/data/cached_images/1e5ca1695c736651de431c425bf9d0293811d101_0.webp" width="256" alt="Envelope [Unofficial Port] mod icon">
  <br>

  <img src="https://img.shields.io/badge/Fabric-1.21.11-e04e14" alt="Fabric 1.21.11">

  <br>

  <h2>Envelope [Unofficial Port]</h2>
  <h4><i>Letters, Packages, Deliveries and more...</i></h4>
  <p>A Minecraft mod that adds a classy mailing system.</p>

  <br>

  <h3>Original Mod</h3>

  <a href="https://curseforge.com/minecraft/mc-mods/envelope">
    <img src="https://cf.way2muchnoise.eu/1281170.svg" alt="CurseForge downloads for Envelope">
  </a>

  <a href="https://modrinth.com/mod/envelope">
    <img src="https://img.shields.io/modrinth/dt/bINSbhYK?logo=modrinth&label=&suffix=%20&style=flat&color=242629&labelColor=5ca424&logoColor=1c1c1c" alt="Modrinth downloads for Envelope">
  </a>

<br><br>

  <h3>Unofficial Port</h3>

  <a href="https://modrinth.com/project/envelope-unofficial-port">
    <img src="https://img.shields.io/badge/Modrinth-Unofficial%20Port-5ca424?logo=modrinth&logoColor=white" alt="Unofficial Port on Modrinth">
  </a>

  <br>

  <a href="https://github.com/gingermat24/Envelope-1.21.11-Unofficial-Port">
    <img src="https://img.shields.io/badge/GitHub-Source-181717?logo=github&logoColor=white" alt="GitHub source code">
  </a>

<br><br>

<b>Modrinth is the recommended source for the Unofficial Port mod, as releases are published there first and more frequently.</b>

</b>Dowload from the link Above</b>

</div>

<br>

## About

**Envelope [Unofficial Port]** is an unofficial Fabric port of [Envelope](https://modrinth.com/mod/envelope) for **Minecraft 1.21.11**.

The original mod was not available for this Minecraft version, so I ported it to 1.21.11 to make it usable on newer Fabric installations.

This project is **not the original Envelope mod**. It is a community-made port intended to bring the original mod to a newer Minecraft version.

## How this port differs

The port is based on the original 0.7.5 release. Its core gameplay (pigeons, mailboxes, letters, packages, seals, and delivery) comes from upstream. This project adapts it for Fabric on Minecraft 1.21.11; it is not a new implementation of those features.

Changes added in this port:

* **Seal Stamp customization:** 16 dye colors plus gold; crafting preserves the selected die, and colored stamps can recolor existing seals. Each colored stamp now displays its original color-specific artwork. Undyed stamps cannot seal mail.
* **Original seal sprites and impression coloring:** each wax color uses its own original background sprite selected by the seal material's `sprite` field, rather than tinting a neutral wax image. Impression palettes use the original [1.21.1-dev values](https://github.com/mortuusars/Envelope/commit/cb8c3d1fec89b626794c0d4c2a51317c33b5c481); the 1.21.11 GUI adaptation preserves their multiplier behavior without relying on the older global shader-color state.
* **Seal symbols and presentation:** added the `cube`, `letter_and_quill`, and `skull_and_bones` seal dies and Mail Service recipes, including the Grass Block requirement for the cube die. Older symbol registry entries remain available for saved seals, while removed legacy dies are no longer offered in the current special-symbol list. Soulbound seals show the animated glint, and seals carrying a lock show the lock overlay.
* **Courier ascent configuration:** `delivery.ascend_distance` controls how far couriers ascend; the default is 24 blocks.
* **Recipe updates:** removed the Slimeball from the Automated Supply Service Letter and Writable Book recipes, and revised the Seal Stamp recipe to use honeycomb, planks, and an iron ingot.
* **Courier delivery updates:** pigeon background travel speed now defaults to 25 blocks per second; courier damage evasion, spawning-rule behavior, and Mail Service courier-death notices have dedicated server settings. Couriers play their ambient sound when completing a delivery, and their appear/disappear particles are visible at greater distances.
* **Mail commands:** administrative send and broadcast commands are grouped under `/envelope mail`; send supports an explicit `to <address>` recipient override and optional `from <address>` sender. `/envelope debug terminate_all_deliveries confirm` provides an explicit confirmation step before stopping deliveries.
* **Soulbound Seals:** combine a Seal Stamp with an Echo Shard to make an 8-use Soulbound Seal Stamp. Its seals lock mail to the sealer, prevent other players from opening it while the sealer lives, and keep the sealed item through the sealer's death; the stamp returns to its original type and wax when depleted. The four-frame Sculk preview animates at its configured frame rate, and stamps obtained directly in Creative can seal unsealed mail without a crafting-added material component. The death-protection hook targets Minecraft 1.21.11's inventory list implementation.
* **Seal locks:** `/envelope seal_lock create|unlock|list` manages persistent locks for Soulbound Seals and other custom uses. A lock must also be stored in the seal component to apply to an item.
* **Mail and JEI:** delivered mail remembers its original recipient for the `To:` tooltip. JEI shows the 16 dye outputs for the stamp-dyeing recipe, service addresses in mailing recipes are clickable to show usages, and mailbox address icons use the upstream icon presentation. Mailboxes also use the original seasonal spider/web decoration when empty on Halloween.
* **Maintenance fixes:** bulk-expiring payback mail now safely returns all pending items; removed obsolete no-op service ticking and disabled experimental debug-command code.

This 0.3.0 release adapts selected features and assets from upstream Envelope 0.8.0 Snapshot 1 and Snapshot 2. The seal artwork is moved to GUI sprites to support animation; existing `texture` data fields remain readable for compatibility with older saved data. Cloud Depository and Letter Presetting are intentionally deferred. The upstream Every Compat integration is also deferred: no matching Fabric build for Minecraft 1.21.11 was available on Modrinth when this port was updated (2026-10-08), so an incompatible 1.21.1 API dependency was not added.

Some server configuration paths were renamed in 0.3.0. Back up and review the existing Envelope server config after updating; renamed settings may be regenerated with defaults and need to be reapplied.

Future ideas and their current scope recommendations are listed in [COMMUNITY_SUGGESTIONS.md](COMMUNITY_SUGGESTIONS.md). Upstream issue links there refer to community proposals for the original mod, not requests made specifically to this port.

## Dependencies and compatibility

For Minecraft 1.21.11, install Fabric Loader and the following required mods:

* [Fabric API](https://modrinth.com/mod/fabric-api)
* [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port)

[JEI](https://modrinth.com/mod/jei) is optional and adds recipe-viewer integration. It is not required on a server for the core mod.

No incompatible mods are currently declared. This does not guarantee compatibility with every mod combination; known conflicts should be reported in the issue tracker.

## Credits

Full credit goes to the original creators of **Envelope**.

The original creators retain credit for the original code, textures, models, images, assets, and overall mod concept. This port adapts that work for **Fabric 1.21.11** and includes the port-specific changes listed above.

Please support the original project and its creators:

* [Original Envelope source code](https://github.com/mortuusars/Envelope) (GPL-3.0)
* [Original Envelope on Modrinth](https://modrinth.com/mod/envelope)
* [Original Envelope on CurseForge](https://curseforge.com/minecraft/mc-mods/envelope)
* [Support the creators on Patreon](https://www.patreon.com/mortuusars?utm_medium=unknown&utm_source=join_link&utm_campaign=creatorshare_creator&utm_content=copyLink)
* [Donate via PayPal](https://www.paypal.com/donate/?hosted_button_id=YTSFJQ8XTXZBW)
* [Join their Discord](https://discord.com/invite/FzHKGDW2et)
* [Original Wiki](https://moddedmc.wiki/en/project/envelope/latest)

The Snapshot 2 notes are a feature reference, not a source for image files. The Soulbound stamp texture, Sculk seal sprite, and Sculk animation metadata in this port were verified byte-for-byte against the corresponding files in the original mod's public [`1.21.1-dev` branch](https://github.com/mortuusars/Envelope/tree/1.21.1-dev).

## Disclaimer

This is an **unofficial port** and is not affiliated with or endorsed by the original Envelope developers unless explicitly stated otherwise.

If you encounter an issue that is specific to the 1.21.11 port, please report it on this project's issue tracker.
For issues with the original mod, please contact the original developers through the official project pages.

If you want to contact me directly, you can reach me on Discord at **matteo_sb**.

---

> As much as i am against AI in a lot of contexts, like daily life, school, art, and more, since i'm still a beginner i used it a little to ask questions, understand things, and check my code while learning. i still wrote and worked on the mod myself, and i mainly used AI as a learning tool. hope yall understand, especially since this is my first time porting a more complicated mod.
