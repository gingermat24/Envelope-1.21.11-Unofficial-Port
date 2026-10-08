<div align="center">
  <img src="https://github.com/gingermat24/Envelope-1.21.11-Unofficial-Port/blob/1.21.11/common/bin/main/icon.png?raw=true" width="256" alt="Envelope [Unofficial Port] mod icon">
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
    <img src="https://img.shields.io/modrinth/dt/envelope-unofficial-port?logo=modrinth&label=&suffix=%20&style=flat&color=242629&labelColor=5ca424&logoColor=1c1c1c" alt="Modrinth downloads for Envelope [Unofficial Port]">
  </a>

  <a href="https://www.curseforge.com/minecraft/mc-mods/envelope-unofficial-port">
    <img src="https://cf.way2muchnoise.eu/total_downloads_1733451.svg" alt="CurseForge downloads for Envelope [Unofficial Port]">
  </a>

  <br>

  <a href="https://github.com/gingermat24/Envelope-1.21.11-Unofficial-Port">
    <img src="https://img.shields.io/badge/GitHub-Source-181717?logo=github&logoColor=white" alt="GitHub source code">
  </a>

<br><br>

<b>Modrinth is the recommended source for the Unofficial Port mod, as releases are published there first and more frequently.</b>

</div>

<br>

## About

**Envelope [Unofficial Port]** is an unofficial Fabric port of [Envelope](https://modrinth.com/mod/envelope) for **Minecraft 1.21.11**.

The original mod was not available for this Minecraft version, so I ported it to 1.21.11 to make it usable on newer Fabric installations.

This project is **not the original Envelope mod**. It is a community-made port intended to bring the original mod to a newer Minecraft version.

## How this port differs

The port is based on the original 0.7.5 release. Its core gameplay (pigeons, mailboxes, letters, packages, seals, and delivery) comes from upstream. This project adapts it for Fabric on Minecraft 1.21.11; it is not a new implementation of those features.

Changes added in this port:

* **Colored Seal Stamps:** craft stamps in 16 dye colors or Gold. Their artwork matches the wax color, and colored stamps can recolor existing seals. Undyed stamps cannot seal mail.
* **Seal designs and effects:** added the newer cube, letter-and-quill, and skull-and-bones designs and their Mail Service recipes. Soulbound seals use animated Sculk wax; Gold wax has an animated glint. Seals with a lock display a lock symbol.
* **Updated recipes:** the Automated Supply Service Letter and Writable Book no longer require a Slimeball. The Seal Stamp recipe now uses honeycomb, planks, and an iron ingot.
* **Courier improvements:** long-distance Pigeon deliveries are faster, and Bats can deliver mail at night at twice the Pigeon speed. Each Bat completes at most three deliveries before leaving. Couriers play a sound when they finish, and their arrival and departure particles can be seen from farther away. Server options let you adjust delivery timing, courier damage protection, spawning behavior, and Mail Service notices when a courier dies.
* **Soulbound Seals:** combine a Seal Stamp with an Echo Shard to make an 8-use Soulbound Seal Stamp. By default, it prevents other players from opening the sealed item while its owner is alive and keeps the item safe when that owner dies. When used up, the stamp returns to its original type and wax. Server options let you disable the stamp, its durability, or its locking effect, and adjust how long it takes to remove a seal.
* **Seal locks:** administrators can use `/envelope seal_lock create`, `/envelope seal_lock unlock`, and `/envelope seal_lock list` to manage locks. Locks can also be assigned to seals for quests or other custom uses. An existing lock can be reactivated by providing its ID; by default, owners can remove their own locks.
* **Mail and recipe viewer:** delivered mail shows its original recipient. JEI displays helpful notes for certain mailing recipes and lets you click service addresses to find related recipes. Mailboxes also have seasonal spiderweb decorations on Halloween.
* **Other fixes:** improved handling of returned payback mail and cleaned up unused internal code.

This 0.3.0 release adapts selected features from Envelope 0.8.0 Snapshot 1 and Snapshot 2. Some newer features are not included: Cloud Depository and Letter Presetting are intentionally saved for later, and the optional Every Compat integration is unavailable for this Minecraft version.

Some server settings changed in 0.3.0. Back up your existing Envelope server settings before updating, then review them afterward in case any need to be set again.

The seal preview may continue to show a lock symbol after an administrator deactivates that lock. The item can still be opened according to the current lock status.

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

## Disclaimer

This is an **unofficial port** and is not affiliated with or endorsed by the original Envelope developers unless explicitly stated otherwise.

If you encounter an issue that is specific to the 1.21.11 port, please report it on this project's issue tracker.
For issues with the original mod, please contact the original developers through the official project pages.

If you want to contact me directly, you can reach me on Discord at **matteo.sb**.

---

> As much as i am against AI in a lot of contexts, like daily life, school, art, and more, since i'm still a beginner i used it a little to ask questions, understand things, and check my code while learning. i still wrote and worked on the mod myself, and i mainly used AI as a learning tool. hope yall understand, especially since this is my first time porting a more complicated mod.
