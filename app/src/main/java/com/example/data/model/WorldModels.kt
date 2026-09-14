package com.example.data.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

enum class DistrictId(val displayName: String, val subtitle: String, val colorHex: Long) {
  BAZAAR("Lantern Bazaar", "Wet lacquer avenue & amber awnings", 0xFF00E5FF),
  HIGH_CUT("The High Cut", "Aerial rope-bridges & blue-glass path", 0xFF38BDF8),
  UNDERTOW_DEN("The Undertow Den", "Caulked grain-hauler & neutral tavern", 0xFFF59E0B),
  SAILCLOTH_ROW("Sailcloth Row", "Giant chain-links & service ledges", 0xFF94A3B8),
  STORM_ANCHOR_SHRINE("Storm Anchor Shrine", "Teal colonnade, Pell's collar & The Coil", 0xFF14B8A6),
  LOWER_SALT_GALLEON("Letter of Descent", "Galleon moored at the cloud floor", 0xFFA855F7)
}

enum class PlatformType {
  BLACK_LACQUER,
  PRAYER_STONE,
  IRON_COLLAR,
  BARGE_HOLD,
  ROPE_PONTOON
}

data class Platform2D(
  val id: String,
  val name: String,
  val district: DistrictId,
  val x: Float,
  val y: Float,
  val width: Float,
  val height: Float,
  val type: PlatformType,
  val elevation: Float = 0f
)

data class ChainBridge(
  val id: String,
  val startX: Float,
  val startY: Float,
  val endX: Float,
  val endY: Float,
  val isSlack: Boolean = false,
  val isBraced: Boolean = false,
  val linkCount: Int = 12
)

enum class LanternType {
  BLUE_GLASS,   // Navigable path - safe to move
  AMBER_SHELTER, // Shelter, hospitality, contracts - safe to stop
  VIOLET_STORM   // Illicit jar-weather anomaly
}

data class Lantern2D(
  val id: String,
  val x: Float,
  val y: Float,
  val type: LanternType,
  val isLit: Boolean = true,
  val glowRadius: Float = 90f,
  val label: String = "",
  val district: DistrictId = DistrictId.BAZAAR
)

data class Npc2D(
  val id: String,
  val name: String,
  val role: String,
  val district: DistrictId,
  val x: Float,
  val y: Float,
  val greeting: String,
  val dialogue: List<String>,
  val accentColor: Long
)

enum class InteractiveType {
  DELIVERY_PACKET,
  PELL_LOCK,
  WEATHER_JAR,
  MOON_WELL,
  SECRET_CHART,
  TEA_STALL,
  STORM_BRACE
}

data class InteractiveItem2D(
  val id: String,
  val name: String,
  val type: InteractiveType,
  val x: Float,
  val y: Float,
  val description: String,
  val isResolved: Boolean = false
)

data class CourierQuest(
  val id: String,
  val title: String,
  val giver: String,
  val description: String,
  val targetDistrict: DistrictId,
  val targetX: Float,
  val targetY: Float,
  val isCompleted: Boolean = false,
  val stepDescription: String
)

data class LoreEntry(
  val id: String,
  val chapter: String,
  val title: String,
  val quote: String,
  val excerpt: String
)

data class CloudJelly(
  val id: Int,
  var x: Float,
  var y: Float,
  val size: Float,
  val speed: Float,
  val color: Color
)
