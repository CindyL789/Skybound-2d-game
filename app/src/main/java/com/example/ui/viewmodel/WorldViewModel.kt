package com.example.ui.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.WorldRepository
import com.example.util.AudioSynthesizer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.*

enum class AtmosphereMode(val displayName: String, val description: String) {
  MOONRISE("Moonrise Over Bazaar", "Honest blue & amber lanterns glowing in the mist"),
  STRAIN_BELL("Strain-Bell Weather", "Thunder and violet jar-weather drifting from the abyss"),
  AURORA_SKY("Aurora & Elders", "Vibrant green-violet auroral rivers and distant swimming elders")
}

data class WorldUiState(
  // Player
  val playerX: Float = 320f,
  val playerY: Float = 430f,
  val playerFacingLeft: Boolean = false,
  val isMoving: Boolean = false,
  val currentDistrict: DistrictId = DistrictId.BAZAAR,

  // Nami (Moon-Koi)
  val namiX: Float = 350f,
  val namiY: Float = 400f,
  val namiAngle: Float = 0f,
  val namiTailPhase: Float = 0f,
  val namiTrail: List<Offset> = emptyList(),
  val isNamiExcited: Boolean = false,

  // Dragon (The Coil)
  val dragonBreathingPhase: Float = 0f,
  val isDragonEyesOpen: Boolean = false,

  // World Elements
  val platforms: List<Platform2D> = WorldRepository.getInitialPlatforms(),
  val chains: List<ChainBridge> = WorldRepository.getInitialChains(),
  val lanterns: List<Lantern2D> = WorldRepository.getInitialLanterns(),
  val npcs: List<Npc2D> = WorldRepository.getInitialNpcs(),
  val items: List<InteractiveItem2D> = WorldRepository.getInitialItems(),
  val quests: List<CourierQuest> = WorldRepository.getQuests(),

  // Inventory & State
  val inventory: List<String> = listOf("Staff-Lantern", "Scale-Compass", "Vermilion Sash"),
  val activeQuestIndex: Int = 0,
  val compassAngle: Float = 0f,
  val compassDistance: Float = 0f,
  val nearestInteraction: String? = null,
  val nearestInteractionType: InteractiveType? = null,

  // Environment
  val atmosphere: AtmosphereMode = AtmosphereMode.MOONRISE,
  val soundEnabled: Boolean = true,
  val notificationMessage: String? = null,

  // UI Panels
  val activeNpcDialog: Npc2D? = null,
  val isWorldMapOpen: Boolean = false,
  val isCodexOpen: Boolean = false,
  val selectedLore: LoreEntry? = null
)

class WorldViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(WorldUiState())
  val uiState: StateFlow<WorldUiState> = _uiState.asStateFlow()

  // Physics & Loop variables
  private var targetMoveX: Float? = null
  private var targetMoveY: Float? = null
  private var joystickDirX: Float = 0f
  private var joystickDirY: Float = 0f

  init {
    startPhysicsLoop()
    updateCompass()
  }

  private fun startPhysicsLoop() {
    viewModelScope.launch {
      var tick: Long = 0
      while (true) {
        tick++
        stepWorld(tick)
        delay(16) // ~60fps
      }
    }
  }

  fun setJoystickInput(dx: Float, dy: Float) {
    joystickDirX = dx
    joystickDirY = dy
    if (dx != 0f || dy != 0f) {
      targetMoveX = null
      targetMoveY = null
    }
  }

  fun setTapDestination(targetX: Float, targetY: Float) {
    targetMoveX = targetX.coerceIn(50f, 2200f)
    targetMoveY = targetY.coerceIn(150f, 1950f)
    AudioSynthesizer.playChime()
  }

  fun toggleAtmosphere() {
    val modes = AtmosphereMode.entries
    val nextIndex = (_uiState.value.atmosphere.ordinal + 1) % modes.size
    val newMode = modes[nextIndex]
    _uiState.value = _uiState.value.copy(
      atmosphere = newMode,
      notificationMessage = "Atmosphere: ${newMode.displayName}"
    )
    AudioSynthesizer.playTempleBell()
  }

  fun toggleSound() {
    val newState = !_uiState.value.soundEnabled
    AudioSynthesizer.soundEnabled = newState
    _uiState.value = _uiState.value.copy(soundEnabled = newState)
  }

  fun openWorldMap(open: Boolean) {
    _uiState.value = _uiState.value.copy(isWorldMapOpen = open)
  }

  fun openCodex(open: Boolean) {
    _uiState.value = _uiState.value.copy(isCodexOpen = open)
  }

  fun selectLore(lore: LoreEntry?) {
    _uiState.value = _uiState.value.copy(selectedLore = lore)
  }

  fun openNpcDialog(npc: Npc2D?) {
    _uiState.value = _uiState.value.copy(activeNpcDialog = npc)
    if (npc != null) {
      AudioSynthesizer.playChime()
    }
  }

  fun fastTravelToDistrict(district: DistrictId) {
    val targetPlatform = _uiState.value.platforms.firstOrNull { it.district == district }
    if (targetPlatform != null) {
      val newX = targetPlatform.x + targetPlatform.width / 2f
      val newY = targetPlatform.y + targetPlatform.height / 2f
      _uiState.value = _uiState.value.copy(
        playerX = newX,
        playerY = newY,
        namiX = newX + 30f,
        namiY = newY - 20f,
        isWorldMapOpen = false,
        notificationMessage = "Arrived at ${district.displayName}"
      )
      AudioSynthesizer.playChainClink()
      updateCompass()
    }
  }

  fun interact() {
    val state = _uiState.value
    val px = state.playerX
    val py = state.playerY

    // 1. Check nearby unlit lanterns to ignite
    val nearbyLantern = state.lanterns.firstOrNull { lan ->
      hypot(lan.x - px, lan.y - py) < 70f && !lan.isLit
    }
    if (nearbyLantern != null) {
      val updatedLanterns = state.lanterns.map { lan ->
        if (lan.id == nearbyLantern.id) lan.copy(isLit = true) else lan
      }
      AudioSynthesizer.playBlueLanternGlow()

      // Check if this fulfills High Cut quest
      val unlitLeft = updatedLanterns.count { it.district == DistrictId.HIGH_CUT && !it.isLit }
      if (unlitLeft == 0) {
        completeQuest("quest_highcut")
      }
      _uiState.value = _uiState.value.copy(
        lanterns = updatedLanterns,
        notificationMessage = "Ignited Blue-Glass Lantern: ${nearbyLantern.label}!"
      )
      return
    }

    // 2. Check nearby NPCs
    val nearbyNpc = state.npcs.firstOrNull { npc ->
      hypot(npc.x - px, npc.y - py) < 75f
    }
    if (nearbyNpc != null) {
      openNpcDialog(nearbyNpc)
      return
    }

    // 3. Check interactive items
    val nearbyItem = state.items.firstOrNull { item ->
      hypot(item.x - px, item.y - py) < 80f && !item.isResolved
    }
    if (nearbyItem != null) {
      handleItemInteraction(nearbyItem)
      return
    }

    // 4. If near dragon summit
    val distToDragon = hypot(1650f - px, 400f - py)
    if (distToDragon < 150f) {
      completeQuest("quest_coil")
      _uiState.value = _uiState.value.copy(
        isDragonEyesOpen = true,
        notificationMessage = "The Coil opened its golden eye, acknowledging the Moon-Koi Courier!"
      )
      AudioSynthesizer.playTempleBell()
      return
    }

    _uiState.value = _uiState.value.copy(
      notificationMessage = "Nami swished her tail into the cloud."
    )
    AudioSynthesizer.playKoiFlutter()
  }

  private fun handleItemInteraction(item: InteractiveItem2D) {
    val state = _uiState.value
    when (item.type) {
      InteractiveType.DELIVERY_PACKET -> {
        val newInv = state.inventory + item.name
        val newItems = state.items.map { if (it.id == item.id) it.copy(isResolved = true) else it }
        _uiState.value = state.copy(
          inventory = newInv,
          items = newItems,
          notificationMessage = "Delivered: ${item.name}!"
        )
        AudioSynthesizer.playChime()
        if (item.id == "item_spice_packet") completeQuest("quest_spice")
        if (item.id == "item_pell_packet") completeQuest("quest_pell")
      }
      InteractiveType.PELL_LOCK -> {
        val hasPacket = state.inventory.any { it.contains("Pell", ignoreCase = true) }
        if (hasPacket) {
          val newItems = state.items.map { if (it.id == item.id) it.copy(isResolved = true) else it }
          _uiState.value = state.copy(
            items = newItems,
            notificationMessage = "Pell's collar accepted the glass seal! Collar seated deeper."
          )
          AudioSynthesizer.playChainClink()
          completeQuest("quest_pell")
        } else {
          _uiState.value = state.copy(
            notificationMessage = "Pell's Lock requires the warm blue-glass packet from the Bazaar!"
          )
        }
      }
      InteractiveType.WEATHER_JAR -> {
        val newInv = state.inventory + "Bottled Violet Weather"
        val newItems = state.items.map { if (it.id == item.id) it.copy(isResolved = true) else it }
        _uiState.value = state.copy(
          inventory = newInv,
          items = newItems,
          notificationMessage = "Confiscated illicit storm jar! Take it to the Moon Well."
        )
        AudioSynthesizer.playWeatherUnmake()
      }
      InteractiveType.STORM_BRACE -> {
        val newItems = state.items.map { if (it.id == item.id) it.copy(isResolved = true) else it }
        val updatedChains = state.chains.map {
          if (it.id == "chain_slack_section") it.copy(isSlack = false, isBraced = true) else it
        }
        _uiState.value = state.copy(
          items = newItems,
          chains = updatedChains,
          notificationMessage = "Braced the slack chain! The collar holds taut against the wind."
        )
        AudioSynthesizer.playChainClink()
        completeQuest("quest_brace")
      }
      InteractiveType.MOON_WELL -> {
        val hasJar = state.inventory.any { it.contains("Violet", ignoreCase = true) }
        if (hasJar) {
          val newInv = state.inventory.filterNot { it.contains("Violet", ignoreCase = true) } + "Silver Moon Residue"
          val newItems = state.items.map { if (it.id == item.id) it.copy(isResolved = true) else it }
          _uiState.value = state.copy(
            inventory = newInv,
            items = newItems,
            notificationMessage = "Moonlight dissolved the violet jar into silver rain mist!"
          )
          AudioSynthesizer.playWeatherUnmake()
          completeQuest("quest_unmake")
        } else {
          _uiState.value = state.copy(
            notificationMessage = "The Inner Moon Well glows with calm residual moonlight."
          )
        }
      }
      InteractiveType.SECRET_CHART -> {
        val newInv = state.inventory + "Secret Chart of Lower Salt"
        val newItems = state.items.map { if (it.id == item.id) it.copy(isResolved = true) else it }
        _uiState.value = state.copy(
          inventory = newInv,
          items = newItems,
          notificationMessage = "Received Tavi's true map of Lower Salt!"
        )
        AudioSynthesizer.playChime()
      }
      InteractiveType.TEA_STALL -> {
        _uiState.value = state.copy(
          notificationMessage = "A steaming bowl of peppery broth warms your gloves."
        )
        AudioSynthesizer.playChime()
      }
    }
  }

  private fun completeQuest(questId: String) {
    val quests = _uiState.value.quests
    val updated = quests.map { if (it.id == questId) it.copy(isCompleted = true) else it }
    var nextActive = _uiState.value.activeQuestIndex
    if (updated[nextActive].isCompleted && nextActive < updated.size - 1) {
      nextActive++
    }
    _uiState.value = _uiState.value.copy(
      quests = updated,
      activeQuestIndex = nextActive
    )
    AudioSynthesizer.playTempleBell()
  }

  private fun stepWorld(tick: Long) {
    val current = _uiState.value
    var px = current.playerX
    var py = current.playerY
    var isMoving = false
    var facingLeft = current.playerFacingLeft

    val speed = 4.5f

    // 1. Calculate movement delta
    var dx = 0f
    var dy = 0f

    if (joystickDirX != 0f || joystickDirY != 0f) {
      dx = joystickDirX * speed
      dy = joystickDirY * speed
      isMoving = true
      if (dx < -0.1f) facingLeft = true
      if (dx > 0.1f) facingLeft = false
    } else if (targetMoveX != null && targetMoveY != null) {
      val dist = hypot(targetMoveX!! - px, targetMoveY!! - py)
      if (dist > 6f) {
        val angle = atan2(targetMoveY!! - py, targetMoveX!! - px)
        dx = cos(angle) * min(dist, speed)
        dy = sin(angle) * min(dist, speed)
        isMoving = true
        if (dx < -0.1f) facingLeft = true
        if (dx > 0.1f) facingLeft = false
      } else {
        targetMoveX = null
        targetMoveY = null
      }
    }

    px = (px + dx).coerceIn(80f, 2150f)
    py = (py + dy).coerceIn(200f, 1850f)

    // District detection
    val currentDistrict = detectDistrict(px, py)

    // 2. Step Nami (Moon-Koi companion)
    // Nami orbits or swims near Sera's shoulder with smooth serpentine ease
    val orbitRadius = 40f
    val orbitSpeed = 0.05f
    val targetNamiX = px + (if (facingLeft) 35f else -35f) + cos(tick * orbitSpeed) * 15f
    val targetNamiY = py - 30f + sin(tick * orbitSpeed) * 12f

    val nx = current.namiX + (targetNamiX - current.namiX) * 0.12f
    val ny = current.namiY + (targetNamiY - current.namiY) * 0.12f
    val namiAngle = atan2(ny - current.namiY, nx - current.namiX) * 180f / PI.toFloat()
    val namiTailPhase = sin(tick * 0.25f).toFloat()

    // Append to moonlight trail (keep last 12 points)
    val newTrail = (listOf(Offset(nx, ny)) + current.namiTrail).take(12)

    // Check nearest interaction affordance
    val (interactionText, interactionType) = findNearestAffordance(px, py, current)

    // Dragon breathing phase
    val dragonPhase = (sin(tick * 0.03) * 0.5 + 0.5).toFloat()
    val isNearDragon = hypot(1650f - px, 400f - py) < 200f

    _uiState.value = current.copy(
      playerX = px,
      playerY = py,
      playerFacingLeft = facingLeft,
      isMoving = isMoving,
      currentDistrict = currentDistrict,
      namiX = nx,
      namiY = ny,
      namiAngle = namiAngle,
      namiTailPhase = namiTailPhase,
      namiTrail = newTrail,
      dragonBreathingPhase = dragonPhase,
      isDragonEyesOpen = current.isDragonEyesOpen || isNearDragon,
      nearestInteraction = interactionText,
      nearestInteractionType = interactionType
    )

    if (tick % 5L == 0L) {
      updateCompass()
    }
  }

  private fun detectDistrict(x: Float, y: Float): DistrictId {
    return when {
      x < 700f && y < 650f -> DistrictId.BAZAAR
      x < 500f && y >= 750f && y < 1150f -> DistrictId.UNDERTOW_DEN
      x in 700f..1350f && y < 550f -> DistrictId.HIGH_CUT
      x in 450f..1150f && y >= 1100f && y < 1550f -> DistrictId.SAILCLOTH_ROW
      x >= 1350f && y < 1200f -> DistrictId.STORM_ANCHOR_SHRINE
      y >= 1500f -> DistrictId.LOWER_SALT_GALLEON
      else -> DistrictId.BAZAAR
    }
  }

  private fun findNearestAffordance(
    px: Float,
    py: Float,
    state: WorldUiState
  ): Pair<String?, InteractiveType?> {
    // 1. Unlit lantern
    val lan = state.lanterns.firstOrNull { hypot(it.x - px, it.y - py) < 70f && !it.isLit }
    if (lan != null) return "Relight Blue Lantern" to InteractiveType.TEA_STALL

    // 2. NPC
    val npc = state.npcs.firstOrNull { hypot(it.x - px, it.y - py) < 75f }
    if (npc != null) return "Speak with ${npc.name}" to null

    // 3. Interactive item
    val item = state.items.firstOrNull { hypot(it.x - px, it.y - py) < 80f && !it.isResolved }
    if (item != null) {
      return when (item.type) {
        InteractiveType.DELIVERY_PACKET -> "Pick up ${item.name}" to item.type
        InteractiveType.PELL_LOCK -> "Seat Packet into Lock" to item.type
        InteractiveType.WEATHER_JAR -> "Secure Storm Jar" to item.type
        InteractiveType.STORM_BRACE -> "Brace Slack Collar" to item.type
        InteractiveType.MOON_WELL -> "Unmake Weather in Well" to item.type
        InteractiveType.SECRET_CHART -> "Inspect True Chart" to item.type
        InteractiveType.TEA_STALL -> "Drink Hot Pepper Broth" to item.type
      }
    }

    // 4. Dragon
    if (hypot(1650f - px, 400f - py) < 180f) {
      return "Commune with The Coil" to null
    }

    return null to null
  }

  private fun updateCompass() {
    val state = _uiState.value
    val activeQuest = state.quests.getOrNull(state.activeQuestIndex) ?: return
    val dx = activeQuest.targetX - state.playerX
    val dy = activeQuest.targetY - state.playerY
    val dist = hypot(dx, dy)
    val angle = atan2(dy, dx) * 180f / PI.toFloat()

    _uiState.value = _uiState.value.copy(
      compassAngle = angle,
      compassDistance = dist
    )
  }

  fun dismissNotification() {
    _uiState.value = _uiState.value.copy(notificationMessage = null)
  }
}
