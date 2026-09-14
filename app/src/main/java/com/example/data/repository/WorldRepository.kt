package com.example.data.repository

import androidx.compose.ui.graphics.Color
import com.example.data.model.*

object WorldRepository {

  val districts = listOf(
    DistrictId.BAZAAR,
    DistrictId.HIGH_CUT,
    DistrictId.UNDERTOW_DEN,
    DistrictId.SAILCLOTH_ROW,
    DistrictId.STORM_ANCHOR_SHRINE,
    DistrictId.LOWER_SALT_GALLEON
  )

  fun getInitialPlatforms(): List<Platform2D> = listOf(
    // Lantern Bazaar (Night market)
    Platform2D("plat_bazaar_main", "Black Lacquer Avenue", DistrictId.BAZAAR, 250f, 400f, 420f, 75f, PlatformType.BLACK_LACQUER),
    Platform2D("plat_broth_stall", "Pepper Broth Awning", DistrictId.BAZAAR, 280f, 310f, 130f, 65f, PlatformType.ROPE_PONTOON),
    Platform2D("plat_spice_quarter", "Spice Quarter Platform", DistrictId.BAZAAR, 520f, 310f, 150f, 70f, PlatformType.BLACK_LACQUER),
    Platform2D("plat_book_dealers", "Book Dealer's Nook", DistrictId.BAZAAR, 200f, 480f, 110f, 65f, PlatformType.ROPE_PONTOON),

    // The Undertow Den (Lower grain hauler)
    Platform2D("plat_den_entrance", "Crate Hatch & Gangway", DistrictId.UNDERTOW_DEN, 220f, 850f, 140f, 55f, PlatformType.ROPE_PONTOON),
    Platform2D("plat_den_hold", "The Undertow Den Tavern", DistrictId.UNDERTOW_DEN, 140f, 920f, 290f, 170f, PlatformType.BARGE_HOLD),

    // The High Cut (Aerial span)
    Platform2D("plat_highcut_start", "High Cut West Pier", DistrictId.HIGH_CUT, 720f, 390f, 130f, 65f, PlatformType.ROPE_PONTOON),
    Platform2D("plat_highcut_mid", "High Cut Suspended Span", DistrictId.HIGH_CUT, 920f, 350f, 150f, 60f, PlatformType.ROPE_PONTOON),
    Platform2D("plat_registry_loft", "Lantern Registry Loft", DistrictId.HIGH_CUT, 1080f, 270f, 140f, 70f, PlatformType.BLACK_LACQUER),
    Platform2D("plat_highcut_east", "High Cut East Approach", DistrictId.HIGH_CUT, 1180f, 390f, 140f, 65f, PlatformType.ROPE_PONTOON),

    // Sailcloth Row & Chain Road
    Platform2D("plat_sailcloth_entry", "Timber Plaza & Winches", DistrictId.SAILCLOTH_ROW, 500f, 1180f, 160f, 70f, PlatformType.ROPE_PONTOON),
    Platform2D("plat_service_ledge", "Chain Road Service Ledge", DistrictId.SAILCLOTH_ROW, 680f, 1260f, 180f, 65f, PlatformType.IRON_COLLAR),
    Platform2D("plat_slack_collar", "Slack Collar & Ugly Brace", DistrictId.SAILCLOTH_ROW, 900f, 1340f, 190f, 75f, PlatformType.IRON_COLLAR),

    // Storm Anchor Shrine
    Platform2D("plat_shrine_steps", "Colonnade & Shrine Steps", DistrictId.STORM_ANCHOR_SHRINE, 1420f, 620f, 320f, 110f, PlatformType.PRAYER_STONE),
    Platform2D("plat_outer_gallery", "Pell's Outer Chain Gallery", DistrictId.STORM_ANCHOR_SHRINE, 1500f, 760f, 240f, 80f, PlatformType.IRON_COLLAR),
    Platform2D("plat_inner_well", "The Inner Moon Well", DistrictId.STORM_ANCHOR_SHRINE, 1720f, 520f, 160f, 90f, PlatformType.PRAYER_STONE),
    Platform2D("plat_dragon_sanctuary", "Coil's Cloud Summit", DistrictId.STORM_ANCHOR_SHRINE, 1620f, 380f, 200f, 80f, PlatformType.PRAYER_STONE),

    // Letter of Descent (Lower Salt)
    Platform2D("plat_galleon_deck", "Letter of Descent Deck", DistrictId.LOWER_SALT_GALLEON, 1320f, 1580f, 340f, 120f, PlatformType.BARGE_HOLD),
    Platform2D("plat_galleon_hold", "Descending Weather Hold", DistrictId.LOWER_SALT_GALLEON, 1400f, 1720f, 200f, 80f, PlatformType.BARGE_HOLD)
  )

  fun getInitialChains(): List<ChainBridge> = listOf(
    // Connections from Bazaar to High Cut & Den
    ChainBridge("chain_bazaar_highcut", 670f, 410f, 720f, 410f, linkCount = 4),
    ChainBridge("chain_highcut_1_2", 850f, 400f, 920f, 370f, linkCount = 6),
    ChainBridge("chain_highcut_2_east", 1070f, 360f, 1180f, 400f, linkCount = 8),
    ChainBridge("chain_highcut_loft", 980f, 350f, 1080f, 300f, linkCount = 6),

    // Big Chain from High Cut to Shrine
    ChainBridge("chain_highcut_to_shrine", 1320f, 410f, 1420f, 640f, linkCount = 14),

    // Bazaar to Sailcloth Row
    ChainBridge("chain_bazaar_to_sailcloth", 420f, 475f, 500f, 1180f, linkCount = 18),
    ChainBridge("chain_den_stairs", 320f, 850f, 280f, 920f, linkCount = 5),
    ChainBridge("chain_sailcloth_ledge", 660f, 1210f, 680f, 1270f, linkCount = 4),
    ChainBridge("chain_slack_section", 860f, 1290f, 900f, 1350f, isSlack = true, linkCount = 8),

    // Great Chain from Sailcloth to Shrine Outer Gallery
    ChainBridge("chain_sailcloth_to_shrine", 1090f, 1360f, 1500f, 790f, linkCount = 20),

    // Shrine to Galleon
    ChainBridge("chain_shrine_to_galleon", 1580f, 840f, 1480f, 1580f, linkCount = 22)
  )

  fun getInitialLanterns(): List<Lantern2D> = listOf(
    // Bazaar Lanterns (Blue = path, Amber = shelter)
    Lantern2D("lan_bazaar_entry", 260f, 395f, LanternType.BLUE_GLASS, label = "Lacquer Start"),
    Lantern2D("lan_broth_amber", 330f, 305f, LanternType.AMBER_SHELTER, label = "Broth Awning"),
    Lantern2D("lan_bazaar_mid", 450f, 395f, LanternType.BLUE_GLASS, label = "Avenue Blue"),
    Lantern2D("lan_spice_amber", 580f, 305f, LanternType.AMBER_SHELTER, label = "Spice Shelter"),
    Lantern2D("lan_bazaar_end", 640f, 395f, LanternType.BLUE_GLASS, label = "High Cut Junction"),

    // Undertow Den Lanterns
    Lantern2D("lan_den_porthole", 180f, 950f, LanternType.AMBER_SHELTER, label = "Porthole Amber"),
    Lantern2D("lan_den_bar", 290f, 940f, LanternType.AMBER_SHELTER, label = "Olla's Bar"),

    // High Cut Lanterns (Two unlit / dimmed initially, awaiting Sera to relight!)
    Lantern2D("lan_highcut_1", 760f, 385f, LanternType.BLUE_GLASS, isLit = true, label = "High Cut Post 1", district = DistrictId.HIGH_CUT),
    Lantern2D("lan_highcut_2", 950f, 345f, LanternType.BLUE_GLASS, isLit = false, label = "Dimmed Lamp (Tap to Light)", district = DistrictId.HIGH_CUT),
    Lantern2D("lan_highcut_3", 1220f, 385f, LanternType.BLUE_GLASS, isLit = false, label = "Dimmed Lamp (Tap to Light)", district = DistrictId.HIGH_CUT),

    // Sailcloth Row
    Lantern2D("lan_sailcloth_amber", 540f, 1175f, LanternType.AMBER_SHELTER, label = "Winch House"),
    Lantern2D("lan_collar_blue", 720f, 1255f, LanternType.BLUE_GLASS, label = "Service Ring"),
    Lantern2D("lan_slack_warning", 940f, 1335f, LanternType.VIOLET_STORM, label = "Slack Anomaly"),

    // Storm Anchor Shrine
    Lantern2D("lan_shrine_colonnade", 1460f, 615f, LanternType.BLUE_GLASS, label = "Sacred Colonnade"),
    Lantern2D("lan_shrine_tea", 1580f, 615f, LanternType.AMBER_SHELTER, label = "Warden's Tea"),
    Lantern2D("lan_pell_lock", 1600f, 755f, LanternType.BLUE_GLASS, label = "Pell's Lock"),
    Lantern2D("lan_moon_well", 1780f, 515f, LanternType.BLUE_GLASS, glowRadius = 120f, label = "Moon Well Radiance"),

    // Galleon
    Lantern2D("lan_galleon_amber", 1370f, 1575f, LanternType.AMBER_SHELTER, label = "Captain's Lamp"),
    Lantern2D("lan_galleon_violet", 1460f, 1715f, LanternType.VIOLET_STORM, label = "Bottled Weather")
  )

  fun getInitialNpcs(): List<Npc2D> = listOf(
    Npc2D(
      id = "npc_clerk",
      name = "Bazaar Clerk",
      role = "Bazaar Ward Courier Desk",
      district = DistrictId.BAZAAR,
      x = 380f,
      y = 420f,
      greeting = "\"Packet for the spice quarter and a glass seal for Warden Pell.\"",
      dialogue = listOf(
        "\"The High Cut is lying tonight, Venn. Someone paid to have routes marked missing.\"",
        "\"Here is the amber-waxed packet for the Spice Quarter. And a second one... sealed in warm blue glass.\"",
        "\"Don't let the weather eat paper. Moon-koi find what clerks deny.\""
      ),
      accentColor = 0xFF00E5FF
    ),
    Npc2D(
      id = "npc_auntie",
      name = "Broth Auntie",
      role = "Pepper Broth & Candied Peel Vendor",
      district = DistrictId.BAZAAR,
      x = 310f,
      y = 330f,
      greeting = "\"Drink the pepper broth, Courier. Your sash is tied like a woman who expects to climb.\"",
      dialogue = listOf(
        "\"Ten minutes of trustworthy passage. Amber light is a contract.\"",
        "\"Buy peel anyway. Sugar keeps the mouth from telling the wind too much.\"",
        "\"Nami looks hungry. Here's a flake of dried silver-fish for the little moon-koi.\""
      ),
      accentColor = 0xFFF59E0B
    ),
    Npc2D(
      id = "npc_olla",
      name = "Olla of the Hold",
      role = "Undertow Den Keeper",
      district = DistrictId.UNDERTOW_DEN,
      x = 240f,
      y = 960f,
      greeting = "\"Plum night, Sera. Safety rented by the cup; the drop preaching through glass.\"",
      dialogue = listOf(
        "\"Storm jars have been moving. Marked as pickled citrus through my hold.\"",
        "\"The Office wants the shrine's outer chains to take a new strain. You loosen a city's ankle, the city kneels.\"",
        "\"Don't let anyone cage Nami. Moon-koi that accept a cage stop telling the truth.\"",
        "\"Take the violet jar from behind the bar to the Shrine Moon Well. Unmake it before the strain-bell rings.\""
      ),
      accentColor = 0xFFD97706
    ),
    Npc2D(
      id = "npc_tavi",
      name = "Tavi Quill",
      role = "Registry Scribe",
      district = DistrictId.HIGH_CUT,
      x = 1120f,
      y = 290f,
      greeting = "\"You're the courier with the unlicensed koi. The loft has opinions.\"",
      dialogue = listOf(
        "\"I copy true routes for the shrine, and obedient ones for the Ward.\"",
        "\"Someone has been erasing Venn-water from the charts for years.\"",
        "\"Here is the true parchment of Lower Salt. Residual moonlight in the ink. Nami knows the way.\""
      ),
      accentColor = 0xFF38BDF8
    ),
    Npc2D(
      id = "npc_len",
      name = "Len Vale",
      role = "Master Chain-Rigger",
      district = DistrictId.SAILCLOTH_ROW,
      x = 750f,
      y = 1270f,
      greeting = "\"Three broken fingers on the left hand and a rigger's swear. You brought the spare pin?\"",
      dialogue = listOf(
        "\"Ward paid for a lecture on my posture. Didn't pay for the fingers.\"",
        "\"This chain is slack. Slack means weight below changed. Someone wanted this platform to think about falling.\"",
        "\"Use your grappling hook to seat the brace into the collar. Make the demonstration expensive!\""
      ),
      accentColor = 0xFF94A3B8
    ),
    Npc2D(
      id = "npc_warden",
      name = "The Older Warden",
      role = "Storm Anchor Shrine Keeper",
      district = DistrictId.STORM_ANCHOR_SHRINE,
      x = 1520f,
      y = 650f,
      greeting = "\"Official tea for two. You followed residual moonlight through the closed current.\"",
      dialogue = listOf(
        "\"Pell was a lock. Pell's collar still believes in him.\"",
        "\"Drop the weather-craft into the Inner Moon Well. Let moonlight unmake what was compressed in malice.\"",
        "\"Look up quietly. The Coil sleeps with only one ear listening. Do not point at the dragon.\""
      ),
      accentColor = 0xFF14B8A6
    ),
    Npc2D(
      id = "npc_irix",
      name = "Captain Irix Halder",
      role = "Barge Syndicate Captain",
      district = DistrictId.HIGH_CUT,
      x = 790f,
      y = 410f,
      greeting = "\"I can make the High Cut honest for an hour, Venn. For a friend.\"",
      dialogue = listOf(
        "\"False paths are a market, Courier. Don't perform purity at me on a slack chain.\"",
        "\"My crews can relight the lanterns in twenty minutes if we make a deal.\"",
        "\"The Office is buying time. Time is the only honest contraband left.\""
      ),
      accentColor = 0xFF64748B
    )
  )

  fun getInitialItems(): List<InteractiveItem2D> = listOf(
    InteractiveItem2D(
      "item_spice_packet",
      "Spice Quarter Packet",
      InteractiveType.DELIVERY_PACKET,
      570f,
      330f,
      "Amber wax seal. Destination: Three platforms down, left of the rope bridge."
    ),
    InteractiveItem2D(
      "item_pell_packet",
      "Blue Glass Packet for Pell",
      InteractiveType.DELIVERY_PACKET,
      390f,
      415f,
      "Warm blue-glass cylinder addressed to Warden Pell at the outer chain gallery."
    ),
    InteractiveItem2D(
      "item_den_jar",
      "Pickled Citrus (Storm Jar)",
      InteractiveType.WEATHER_JAR,
      170f,
      980f,
      "Compressed violet weather humming inside a brass-capped jar. Needs moonlight unmaking!"
    ),
    InteractiveItem2D(
      "item_slack_pin",
      "Spare Gallery Pin & Brace",
      InteractiveType.STORM_BRACE,
      940f,
      1350f,
      "Seat the pin with the grappling hook to secure the slack chain before the strain-bell rings."
    ),
    InteractiveItem2D(
      "item_pell_lock",
      "Pell's Iron Collar",
      InteractiveType.PELL_LOCK,
      1610f,
      770f,
      "Lock-plate engraved with 'PELL'. Press the warm glass packet here to release the lock."
    ),
    InteractiveItem2D(
      "item_moon_well",
      "The Inner Moon Well",
      InteractiveType.MOON_WELL,
      1770f,
      530f,
      "A sacred well bathed in residual moonlight where compressed jar-weather is unmade."
    ),
    InteractiveItem2D(
      "item_tavi_chart",
      "Chart of Lower Salt",
      InteractiveType.SECRET_CHART,
      1140f,
      280f,
      "Un-erased chart showing Venn-water, barge graves, and routes to the sea floor."
    )
  )

  fun getQuests(): List<CourierQuest> = listOf(
    CourierQuest(
      id = "quest_spice",
      title = "Spice Quarter Delivery",
      giver = "Bazaar Clerk",
      description = "Deliver the amber-waxed spice tube to the spice stall across the lacquer avenue.",
      targetDistrict = DistrictId.BAZAAR,
      targetX = 570f,
      targetY = 330f,
      stepDescription = "Walk east along the lacquer avenue to the Spice Platform."
    ),
    CourierQuest(
      id = "quest_highcut",
      title = "Relight The High Cut",
      giver = "Sera's Instinct",
      description = "Captain Irix's syndicate dimmed the blue-glass lanterns. Relight the 2 extinguished lamps with your staff-lantern to restore the true aerial route.",
      targetDistrict = DistrictId.HIGH_CUT,
      targetX = 950f,
      targetY = 345f,
      stepDescription = "Cross the rope bridges and tap the dimmed lanterns to ignite blue flame."
    ),
    CourierQuest(
      id = "quest_pell",
      title = "The Packet That Should Not Exist",
      giver = "Warm Blue Glass Seal",
      description = "Deliver the warm glass cylinder to Pell's Collar at the Storm Anchor Shrine outer gallery.",
      targetDistrict = DistrictId.STORM_ANCHOR_SHRINE,
      targetX = 1610f,
      targetY = 770f,
      stepDescription = "Navigate to the Storm Anchor Shrine and seat the packet into Pell's lock."
    ),
    CourierQuest(
      id = "quest_brace",
      title = "Brace the Slack Collar",
      giver = "Len Vale",
      description = "The Office loosened a city's ankle. Use the grappling hook and spare pin to tighten the slack chain on Sailcloth Row.",
      targetDistrict = DistrictId.SAILCLOTH_ROW,
      targetX = 940f,
      targetY = 1350f,
      stepDescription = "Reach Sailcloth Row's outer ledge and brace the loose collar."
    ),
    CourierQuest(
      id = "quest_unmake",
      title = "Moonlight Unmaking",
      giver = "Olla of the Hold",
      description = "Retrieve the illicit violet storm jar from the Undertow Den and take it to the Shrine's Inner Moon Well to be safely unmade.",
      targetDistrict = DistrictId.STORM_ANCHOR_SHRINE,
      targetX = 1770f,
      targetY = 530f,
      stepDescription = "Carry the violet jar to the Moon Well and release it into the silver light."
    ),
    CourierQuest(
      id = "quest_coil",
      title = "Communion with The Coil",
      giver = "Nami the Moon-Koi",
      description = "Ascend to the Shrine summit where the majestic dragon 'The Coil' slumbers. Stand in its presence to awaken its ancient golden gaze.",
      targetDistrict = DistrictId.STORM_ANCHOR_SHRINE,
      targetX = 1650f,
      targetY = 400f,
      stepDescription = "Follow Nami's radiant moonlight trail to the dragon's perch."
    )
  )

  fun getLoreEntries(): List<LoreEntry> = listOf(
    LoreEntry(
      "lore_proverb",
      "Courier Proverb",
      "Every Route is Temporary",
      "Every route is temporary. Every delivery is a promise made against the weather.",
      "The Skybound Archipelago is a city built on hanging promises: barges lashed to temple fragments, platforms held by chains that disappear into a cloud sea the color of dirty pearl."
    ),
    LoreEntry(
      "lore_lanterns",
      "The Language of Glass",
      "Blue and Amber",
      "Blue lanterns hooked along the wet black-lacquer avenue... Blue meant you may move. Amber meant you may stop and not be a fool for stopping.",
      "In a city that doesn't keep its shape, blue glass marks the safe, navigated paths through shifting clouds. Amber lamps bloom in doorways, offering a temporary contract of hospitality."
    ),
    LoreEntry(
      "lore_nami",
      "The Companion",
      "Nami the Moon-Koi",
      "She called it Nami, because the old salvage-cant for 'found in moonlight' sounded like that, and because a creature that refused cages deserved a name that wasn't a claim.",
      "Moon-koi swim not in water but in the cloud itself. They feed on residual moonlight and reveal seams in the sky that maps refuse to confess. Moon-koi that accept a cage stop telling the truth."
    ),
    LoreEntry(
      "lore_coil",
      "The Sleeping God",
      "The Coil Around the Shrine",
      "White scales the size of shutters caught the full moon. A head like a temple gate lifted over the highest pagoda, antlers branching into cloud.",
      "Cloud given a spine. The Coil sleeps around the shrine island like a chain that grew opinions. Pell's collar is hardware; The Coil is the reason the hardware still dreams it is holy."
    ),
    LoreEntry(
      "lore_jars",
      "Illicit Weather",
      "Bottled Lightning & Storm Jars",
      "A plank table of ribbed jars in brass caps, each holding a private weather: teal current, violet fork, a slow gold that was almost a lie.",
      "Weather-craft is used by the clandestine Office to push currents and loosen chain-collars, forcing hanging districts to kneel so their ledgers can be rewritten."
    ),
    LoreEntry(
      "lore_lowersalt",
      "The Cloud Sea Floor",
      "Letter of Descent",
      "There is a moment when cloud becomes water and the body knows it before the map does. Spray. Weight. A sound like a city slapping a table.",
      "Below the hanging city lies Lower Salt—true ocean where galleons remember water and families named Venn still wait for forgotten letters."
    )
  )
}
