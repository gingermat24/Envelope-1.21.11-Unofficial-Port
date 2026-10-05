# Community Suggestions

Reviewed on 2026-10-04. This file keeps community ideas and their current decisions
for future updates. Decisions are scope recommendations, not promises or release dates.

## Tag copying recipe

**Decision: YES for Address Tags; MAYBE for Payback Tags.**

- Add a shapeless custom crafting recipe that combines one Address Tag with an address
  and one blank Address Tag, then returns two Address Tags carrying that same address.
- This is useful for bulk mail preparation and avoids repeatedly opening the address
  selection interface.
- A custom recipe is needed to copy the configured data component. A normal shapeless
  recipe would not preserve the address from an arbitrary input tag.
- The current Payback Tag stores a `PaybackRequest`, not an address. Do not make it
  copy an address until its intended address semantics are designed; copying its
  existing payback request could be considered separately.

## Return-to-sender system

**Decision: LATER; consider a smaller return-address feature before the full proposal.**

- Failed deliveries already return to the original sender in several cases. A custom
  return address could be a useful extension, but needs explicit rules for failed
  return delivery, repeat/loop prevention, expired or missing addresses, and the
  distinction between an ordinary return and mail explicitly marked "returned".
- A Return Stamp and an address-bearing Payback Tag depend on those rules and on
  deciding how a return address is stored and preserved on both letters and packages.
- The Postbox, collection activation, mailbox input changes, and package/lost-mail
  outcomes are a broad redesign of delivery and mailbox behavior. Treat those as
  separate proposals rather than prerequisites for basic return addressing.
- Do not delete undeliverable letters or silently replace existing mailbox defaults
  as part of a first implementation. Any defaults change needs a separate compatibility
  and gameplay review.

## Mail-in rebates

**Decision: MAYBE / LATER as a dedicated coupon feature; YES to exploring it through
Mail Service datapack recipes.**

- The current Mail Service crafting flow already accepts mailed package contents,
  matches recipes, creates outputs, and can deliver a reply to the sender. This can
  support curated mail-order or redemption recipes without a new general-purpose
  trading system.
- A datapack recipe can define a coupon-like input and a reward. Multiple redemption
  addresses can use distinct recipes.
- A dedicated reusable coupon/postcard system, randomized reward selection, and
  validation of written instructions would need additional design. Start with the
  existing recipe system and only add mechanics that datapack recipes cannot express.

## "Amazon" / mail-order shopping

**Decision: YES for opt-in datapack-defined Mail Service orders; NO to recreating
villager trading halls as a built-in default system.**

- This is a good use of the Mail Service recipe framework: datapacks can define
  specific package ingredients, costs, and returned goods.
- Keep the catalog intentional and datapack-configurable rather than shipping a broad
  default catalog that duplicates villager trades or changes the mod into a general
  remote-shop system.
- Consider this part of the Mail Service crafting direction, not a separate mail
  economy implementation.

## Pigeonhole and waste improvements

**Decision: NO for adding generic waste/byproducts right now; MAYBE for pigeon
preference/training as a later refinement.**

- Part of this already exists: pigeons use pigeonholes as home/rest locations, and
  released pigeons can record a pigeonhole as home. A pigeon can forget that home
  after three in-game days away, so explicit training or a preferred-hole mechanic
  could be a focused follow-up.
- Pigeonhole waste already accumulates probabilistically when pigeons leave. At its
  maximum level it can be scooped with a suitable shovel; the current loot is usually
  bone meal, with a rare diamond. Bone meal already gives the byproduct a farming use.
- Consider extra seeds or other rewards only if testing shows the current waste loop
  is not useful or engaging; avoid adding redundant drops before then.

## Environmental storytelling and risks

**Decision: PARTLY ALREADY IMPLEMENTED; MAYBE for carefully bounded additions, but
NO to making routine mail loss a core mechanic.**

- Tattered letters already exist: the Fox interaction can mark regular or sealed
  letters as tattered, controlled by the `fox_tattering` server config option. This
  is visual damage caused by a fox picking up and spitting out a letter, not a
  general theft/failed-delivery system.
- Weather already affects pigeons: rain and thunderstorms make them seek pigeonholes,
  and they cannot start deliveries during those conditions. Tired pigeons also fly
  more slowly. The proposed weather shelter/fatigue theme therefore partly overlaps
  existing behavior.
- Charred Pigeons already exist as a distinct fire/Nether-associated entity and have
  their own loot behavior. More Nether-specific behavior is MAYBE if it gives them a
  clear delivery role; extra loot alone is low priority and should not be added just
  to increase drops.
- A package-theft/scavenger feature is distinct from tattering a letter, but risks
  frustrating players and interacting badly with return-to-sender behavior. Revisit
  only with clear recovery, opt-in/configuration, and non-lossy defaults.

## Return-to-sender and tracking mechanics

**Decision: LATER for automatic returns; MAYBE for a simple player-visible receipt;
NO to random content loss.**

- The tattered-letter effect is not tracking or return-to-sender: it is a fox-caused
  visual damage state. The mod already records delivery information and has courier
  death notices, but that is not the same as a player-facing status receipt.
- A receipt item or optional tracking tag could be useful if it exposes existing
  delivery states without creating another address system. Keep the first version
  informational; do not make tracking mandatory or add needless inventory clutter.
- Automatic return after invalid/destroyed destination or a timeout remains LATER.
  Define retries, missing return addresses, loops, offline players, and exactly when
  an item is considered lost before implementing it. Do not silently delete mail.
- Delayed or damaged delivery with reduced contents is NO: taking contents away
  unpredictably would be punitive and difficult to recover from. Flavor-only delay
  notes could be reconsidered if they are rare, cosmetic, and do not block delivery.

## Mailbox upgrades and network features

**Decision: MAYBE for shared access and visible arrival indicators; LATER for a
sorting-office block; NO for prioritizing more decorative variants now.**

- Mailboxes already have an owner UUID, but the current open-menu path does not use
  it to enforce private access. Public/private modes or explicit shared users could
  be useful, including shared/family addresses, but need a clear permissions model
  for changing addresses, sending, withdrawing received mail, and hopper access.
- Mail arrival already drives an internal `has_mail` block-state property and a
  comparator signal. The current mailbox blockstate models do not select a distinct
  visual model for that property, so a visible flag or indicator could make the
  existing signal useful at a glance. This is a strong, focused usability improvement.
- A sorting-office/overflow feature is a larger routing and storage addition.
  Existing mailboxes have large inbox capacity and the project already has a
  Collapsed Mail Hub structure, but neither provides general automatic player-built
  sorting. Defer until a concrete routing/overflow use case is defined.
- Hanging mailboxes already exist, and Dovecote structures already provide some
  post-station-style world decoration. More cosmetic variants are lower priority than
  gameplay and usability gaps.

## More service addresses and mail recipes

**Decision: YES to a small, curated set of useful datapack-defined recipes and
services; MAYBE for the proposed specialist services; do not promise KubeJS
compatibility without a tested integration.**

- The existing service-address definitions and mailing recipes are data-driven, and
  the wiki documents how datapacks can define them. This is a sound way to add
  targeted mail-order or utility recipes without rebuilding villager trading as a
  general shop system.
- Lost & Found / Dead Letter Office is MAYBE, not an immediate new service: Lost Mail
  already exists as a Mail Service purchase. A distinct office should only be added
  if it provides recoverable undelivered mail, rather than duplicating that purchase
  or implying failed mail is currently returned there.
- Cartographer's Bureau is MAYBE and a good candidate for a limited, opt-in recipe
  set. Map-copying, filled-map creation, and exploration-note outputs need a clear
  design and should not imply the recipe system can dynamically generate arbitrary
  maps unless that capability is implemented.
- Scribe's Guild is MAYBE. Fixed-output stationery, stamp, or writing-supply recipes
  fit the existing model; copying arbitrary written text or applying formatting to
  player-authored letters would require preserving and transforming their data, which
  is not established by ordinary fixed-result recipes.
- Dimension-specific services are LATER. Service addresses can be defined with
  locations and paired with mailing recipes, but Nether-specific courier rules,
  Charred Pigeon requirements, fuel, and cross-dimension delivery semantics need to
  be designed together. Avoid creating a service that appears to deliver to a
  dimension but has no distinct gameplay behavior.
- The datapack format is verified by the project documentation; direct KubeJS support
  is not verified. Keep the documented datapack path as the baseline and only claim
  KubeJS compatibility after testing the relevant KubeJS version and recipe flow.


Tier S — Do these
Address Tag copying
Mailbox arrival indicator
Delivery receipt
Postcards
Postal markings/cancellation stamps
More curated datapack Mail Services
Mailbox shared access
Tier A — Very good additions
Stamps/postage
Urgent mail
Fragile/Confidential markings
Mailbox notification sound
Cartographer's Bureau
Scribe's Guild
Better pigeon-home preference/training
Tier B — Later
Return-to-sender
Payback Tag copying
Lost & Found
Dimension-specific services
Sorting office
Tier C — Only if there's a compelling design
Full return-address system
Advanced tracking
Automated sorting/network infrastructure
Tier F — Don't do it
Random item loss
Random package theft
Generic waste items
Built-in Amazon/villager marketplace
Massive postal economy