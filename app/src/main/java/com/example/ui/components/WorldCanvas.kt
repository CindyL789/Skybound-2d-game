package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AtmosphereMode
import com.example.ui.viewmodel.WorldUiState
import kotlin.math.*

@Composable
fun WorldCanvas(
  uiState: WorldUiState,
  onTapWorld: (Float, Float) -> Unit,
  onPanWorld: (Float, Float) -> Unit,
  modifier: Modifier = Modifier
) {
  // Infinite transition for environmental animations (clouds, stars, lanterns)
  val infiniteTransition = rememberInfiniteTransition(label = "world_anim")
  val cloudOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 2000f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 40000, easing = LinearEasing)
    ),
    label = "cloud_scroll"
  )
  val auroraWave by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 2f * PI.toFloat(),
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 6000, easing = LinearEasing)
    ),
    label = "aurora"
  )
  val lanternPulse by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "lantern_pulse"
  )

  Canvas(
    modifier = modifier
      .fillMaxSize()
      .pointerInput(Unit) {
        detectTapGestures { tapOffset ->
          // Convert screen tap to world coordinates
          // Camera centers on player
          val screenCenter = Offset(size.width / 2f, size.height / 2f)
          val worldX = tapOffset.x - screenCenter.x + uiState.playerX
          val worldY = tapOffset.y - screenCenter.y + uiState.playerY
          onTapWorld(worldX, worldY)
        }
      }
      .pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
          change.consume()
          onPanWorld(-dragAmount.x, -dragAmount.y)
        }
      }
  ) {
    val canvasWidth = size.width
    val canvasHeight = size.height
    val cameraX = uiState.playerX - canvasWidth / 2f
    val cameraY = uiState.playerY - canvasHeight / 2f

    // 1. Render Sky Background
    drawSkyBackground(uiState.atmosphere, auroraWave, canvasWidth, canvasHeight)

    // 2. Render Parallax Clouds in Sky
    drawParallaxCloudSea(cameraX, cameraY, cloudOffset, uiState.atmosphere, canvasWidth, canvasHeight)

    // 3. Render Distant Moon-Koi Elders in high aurora
    if (uiState.atmosphere == AtmosphereMode.AURORA_SKY) {
      drawDistantElders(cameraX, cameraY, cloudOffset)
    }

    // Apply Camera Transform for 2D World Elements
    withTransform({
      translate(-cameraX, -cameraY)
    }) {
      // 4. Chains & Bridges (Rendered behind platforms)
      uiState.chains.forEach { chain ->
        drawChainBridge(chain, lanternPulse)
      }

      // 5. Floating Platforms & Islands
      uiState.platforms.forEach { platform ->
        drawPlatform(platform)
      }

      // 6. The Coil (Great White Dragon curled around Storm Anchor Shrine)
      drawTheCoilDragon(
        centerX = 1620f,
        centerY = 520f,
        breathingPhase = uiState.dragonBreathingPhase,
        isEyesOpen = uiState.isDragonEyesOpen,
        pulse = lanternPulse
      )

      // 7. Interactive Items & Wells
      uiState.items.forEach { item ->
        if (!item.isResolved) {
          drawInteractiveItem(item, lanternPulse)
        }
      }

      // 8. Lanterns (Blue glass, Amber shelter, Violet storm)
      uiState.lanterns.forEach { lantern ->
        drawLantern(lantern, lanternPulse)
      }

      // 9. NPCs
      uiState.npcs.forEach { npc ->
        drawNpc(npc, lanternPulse)
      }

      // 10. Nami (The Moon-Koi Companion & Moonlight Trail)
      drawNamiMoonKoi(
        x = uiState.namiX,
        y = uiState.namiY,
        angle = uiState.namiAngle,
        tailPhase = uiState.namiTailPhase,
        trail = uiState.namiTrail,
        isExcited = uiState.isNamiExcited
      )

      // 11. Player: Sera Venn (The Moon-Koi Courier)
      drawSeraVenn(
        x = uiState.playerX,
        y = uiState.playerY,
        facingLeft = uiState.playerFacingLeft,
        isMoving = uiState.isMoving,
        pulse = lanternPulse
      )
    }

    // 12. Foreground Mist Vignette
    drawForegroundAtmosphere(uiState.atmosphere, canvasWidth, canvasHeight)
  }
}

private fun DrawScope.drawSkyBackground(
  atmosphere: AtmosphereMode,
  auroraPhase: Float,
  w: Float,
  h: Float
) {
  val (topColor, bottomColor) = when (atmosphere) {
    AtmosphereMode.MOONRISE -> Color(0xFF070B14) to Color(0xFF101C31)
    AtmosphereMode.STRAIN_BELL -> Color(0xFF0D0818) to Color(0xFF221133)
    AtmosphereMode.AURORA_SKY -> Color(0xFF040C1A) to Color(0xFF0D2232)
  }

  drawRect(
    brush = Brush.verticalGradient(
      colors = listOf(topColor, bottomColor)
    ),
    size = Size(w, h)
  )

  // Full Moon
  val moonX = w * 0.8f
  val moonY = h * 0.15f
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFFFFFDE7), Color(0x66FFFDE7), Color.Transparent),
      center = Offset(moonX, moonY),
      radius = 70f
    ),
    radius = 70f,
    center = Offset(moonX, moonY)
  )
  drawCircle(
    color = Color(0xFFFFFFF0),
    radius = 28f,
    center = Offset(moonX, moonY)
  )

  // Aurora Ribbons if in Aurora mode
  if (atmosphere == AtmosphereMode.AURORA_SKY) {
    val auroraPath = Path().apply {
      moveTo(0f, h * 0.1f)
      for (x in 0..w.toInt() step 30) {
        val y = h * 0.18f + sin(x * 0.008f + auroraPhase) * 45f + cos(x * 0.015f) * 20f
        lineTo(x.toFloat(), y)
      }
      lineTo(w, 0f)
      lineTo(0f, 0f)
      close()
    }
    drawPath(
      path = auroraPath,
      brush = Brush.verticalGradient(
        colors = listOf(Color(0x5500E5FF), Color(0x4410B981), Color.Transparent),
        startY = 0f,
        endY = h * 0.35f
      )
    )
  }
}

private fun DrawScope.drawParallaxCloudSea(
  camX: Float,
  camY: Float,
  cloudOffset: Float,
  atmosphere: AtmosphereMode,
  w: Float,
  h: Float
) {
  val cloudColor = when (atmosphere) {
    AtmosphereMode.STRAIN_BELL -> Color(0x33581C87)
    AtmosphereMode.AURORA_SKY -> Color(0x330D9488)
    else -> Color(0x2A38BDF8)
  }

  // Draw 2 layers of soft puffy clouds
  for (i in 0..6) {
    val cx = ((i * 380f + cloudOffset * 0.4f - camX * 0.15f) % (w + 600f)) - 200f
    val cy = (h * 0.75f + sin(i + cloudOffset * 0.002f) * 40f - camY * 0.1f)
    drawCircle(
      color = cloudColor,
      radius = 160f,
      center = Offset(cx, cy)
    )
  }
}

private fun DrawScope.drawDistantElders(camX: Float, camY: Float, offset: Float) {
  // Giant moon-koi elders swimming in background sky
  val elderX = ((offset * 0.6f - camX * 0.05f) % 2500f)
  val elderY = 220f + sin(offset * 0.01f) * 35f - camY * 0.05f

  // Body
  drawOval(
    color = Color(0x55F8FAFC),
    topLeft = Offset(elderX - 120f, elderY - 35f),
    size = Size(240f, 70f)
  )
  // Vermilion wax mark
  drawCircle(
    color = Color(0x66E11D48),
    radius = 14f,
    center = Offset(elderX - 30f, elderY)
  )
  // Whiskers & Tail
  drawLine(
    color = Color(0x44F8FAFC),
    start = Offset(elderX + 110f, elderY),
    end = Offset(elderX + 180f, elderY + sin(offset * 0.03f) * 25f),
    strokeWidth = 6f
  )
}

private fun DrawScope.drawChainBridge(chain: ChainBridge, pulse: Float) {
  val dx = chain.endX - chain.startX
  val dy = chain.endY - chain.startY
  val length = hypot(dx, dy)
  val steps = chain.linkCount

  val sagAmount = if (chain.isSlack) 55f else 18f
  val chainColor = if (chain.isBraced) Color(0xFF38BDF8) else if (chain.isSlack) Color(0xFFA855F7) else Color(0xFF64748B)

  val path = Path().apply {
    moveTo(chain.startX, chain.startY)
    val midX = (chain.startX + chain.endX) / 2f
    val midY = (chain.startY + chain.endY) / 2f + sagAmount
    quadraticTo(midX, midY, chain.endX, chain.endY)
  }

  // Draw main chain line
  drawPath(
    path = path,
    color = chainColor,
    style = Stroke(width = 8f, cap = StrokeCap.Round)
  )

  // Draw individual chain links
  for (i in 0..steps) {
    val t = i.toFloat() / steps
    val curX = (1 - t) * (1 - t) * chain.startX + 2 * (1 - t) * t * ((chain.startX + chain.endX) / 2f) + t * t * chain.endX
    val curY = (1 - t) * (1 - t) * chain.startY + 2 * (1 - t) * t * ((chain.startY + chain.endY) / 2f + sagAmount) + t * t * chain.endY

    drawOval(
      color = Color(0xFF1E293B),
      topLeft = Offset(curX - 10f, curY - 7f),
      size = Size(20f, 14f)
    )
    drawOval(
      color = chainColor,
      topLeft = Offset(curX - 8f, curY - 5f),
      size = Size(16f, 10f),
      style = Stroke(width = 2.5f)
    )
  }

  // If slack, draw warning crackle
  if (chain.isSlack && !chain.isBraced) {
    val midX = (chain.startX + chain.endX) / 2f
    val midY = (chain.startY + chain.endY) / 2f + sagAmount
    drawCircle(
      color = Color(0x66A855F7),
      radius = 24f * pulse,
      center = Offset(midX, midY)
    )
  }
}

private fun DrawScope.drawPlatform(p: Platform2D) {
  val (topColor, bodyColor, trimColor) = when (p.type) {
    PlatformType.BLACK_LACQUER -> Triple(Color(0xFF0F172A), Color(0xFF050811), Color(0xFF00E5FF))
    PlatformType.PRAYER_STONE -> Triple(Color(0xFF0F766E), Color(0xFF134E4A), Color(0xFF2DD4BF))
    PlatformType.IRON_COLLAR -> Triple(Color(0xFF334155), Color(0xFF1E293B), Color(0xFF94A3B8))
    PlatformType.BARGE_HOLD -> Triple(Color(0xFF2E1065), Color(0xFF1E1B4B), Color(0xFFF59E0B))
    PlatformType.ROPE_PONTOON -> Triple(Color(0xFF451A03), Color(0xFF290E02), Color(0xFFD97706))
  }

  // Shadow onto cloud abyss
  drawRoundRect(
    color = Color(0x44000000),
    topLeft = Offset(p.x + 8f, p.y + 12f),
    size = Size(p.width, p.height),
    cornerRadius = CornerRadius(8f)
  )

  // Underneath platform support structure (chains & beams hanging down)
  for (bx in 0..(p.width.toInt() - 40) step 70) {
    drawLine(
      color = Color(0x881E293B),
      start = Offset(p.x + bx + 20f, p.y + p.height),
      end = Offset(p.x + bx + 20f, p.y + p.height + 40f),
      strokeWidth = 3f
    )
    drawCircle(
      color = Color(0xFF475569),
      radius = 5f,
      center = Offset(p.x + bx + 20f, p.y + p.height + 42f)
    )
  }

  // Main Platform Body
  drawRoundRect(
    color = bodyColor,
    topLeft = Offset(p.x, p.y),
    size = Size(p.width, p.height),
    cornerRadius = CornerRadius(6f)
  )

  // Surface Decking / Planks
  drawRoundRect(
    color = topColor,
    topLeft = Offset(p.x + 4f, p.y + 4f),
    size = Size(p.width - 8f, p.height - 12f),
    cornerRadius = CornerRadius(4f)
  )

  // Wood / Stone lines
  for (lx in 20..(p.width.toInt() - 20) step 35) {
    drawLine(
      color = Color(0x33FFFFFF),
      start = Offset(p.x + lx, p.y + 6f),
      end = Offset(p.x + lx, p.y + p.height - 12f),
      strokeWidth = 1f
    )
  }

  // Trim edge glow
  drawLine(
    color = trimColor,
    start = Offset(p.x + 6f, p.y + 5f),
    end = Offset(p.x + p.width - 6f, p.y + 5f),
    strokeWidth = 2f
  )
}

private fun DrawScope.drawLantern(lan: Lantern2D, pulse: Float) {
  val (coreColor, glowColor) = when (lan.type) {
    LanternType.BLUE_GLASS -> Color(0xFF00E5FF) to Color(0x4400E5FF)
    LanternType.AMBER_SHELTER -> Color(0xFFF59E0B) to Color(0x55F59E0B)
    LanternType.VIOLET_STORM -> Color(0xFFA855F7) to Color(0x55A855F7)
  }

  // Lantern Post
  drawLine(
    color = Color(0xFF64748B),
    start = Offset(lan.x, lan.y),
    end = Offset(lan.x, lan.y + 24f),
    strokeWidth = 4f
  )

  if (lan.isLit) {
    // Ground Light Pool
    drawOval(
      brush = Brush.radialGradient(
        colors = listOf(glowColor, Color.Transparent),
        center = Offset(lan.x, lan.y + 20f),
        radius = lan.glowRadius * pulse
      ),
      topLeft = Offset(lan.x - lan.glowRadius * 0.8f, lan.y + 5f),
      size = Size(lan.glowRadius * 1.6f, lan.glowRadius * 0.5f)
    )

    // Lantern Glass Glow
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(coreColor, glowColor, Color.Transparent),
        center = Offset(lan.x, lan.y),
        radius = 26f * pulse
      ),
      radius = 26f * pulse,
      center = Offset(lan.x, lan.y)
    )

    // Inner bright bulb
    drawCircle(
      color = Color.White,
      radius = 5f,
      center = Offset(lan.x, lan.y)
    )
  } else {
    // Unlit / Dimmed glass
    drawCircle(
      color = Color(0xFF1E293B),
      radius = 7f,
      center = Offset(lan.x, lan.y)
    )
    drawCircle(
      color = Color(0xFF38BDF8),
      radius = 8f,
      center = Offset(lan.x, lan.y),
      style = Stroke(width = 1.5f)
    )
  }

  // Brass Cap & Frame
  drawRect(
    color = Color(0xFFD97706),
    topLeft = Offset(lan.x - 6f, lan.y - 8f),
    size = Size(12f, 4f)
  )
}

private fun DrawScope.drawTheCoilDragon(
  centerX: Float,
  centerY: Float,
  breathingPhase: Float,
  isEyesOpen: Boolean,
  pulse: Float
) {
  // Majestic white dragon sleeping around Storm Anchor Shrine
  val bodyOffset = sin(breathingPhase * 2f * PI.toFloat()) * 8f

  // Serpentine body coils (Loops behind the shrine)
  val coilPath = Path().apply {
    moveTo(centerX - 240f, centerY + 180f)
    cubicTo(
      centerX - 280f, centerY + 40f + bodyOffset,
      centerX - 180f, centerY - 140f - bodyOffset,
      centerX, centerY - 180f
    )
    cubicTo(
      centerX + 180f, centerY - 160f + bodyOffset,
      centerX + 260f, centerY - 20f,
      centerX + 220f, centerY + 140f
    )
    cubicTo(
      centerX + 160f, centerY + 240f - bodyOffset,
      centerX - 40f, centerY + 220f,
      centerX - 100f, centerY + 110f
    )
  }

  // Outer Dragon Glow
  drawPath(
    path = coilPath,
    color = Color(0x3300E5FF),
    style = Stroke(width = 64f, cap = StrokeCap.Round)
  )

  // Dragon Body White Spine
  drawPath(
    path = coilPath,
    color = Color(0xFFF1F5F9),
    style = Stroke(width = 46f, cap = StrokeCap.Round)
  )

  // Dragon Body Scales
  drawPath(
    path = coilPath,
    color = Color(0xFFCBD5E1),
    style = Stroke(width = 40f, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 14f)))
  )

  // Dragon Head (Gate-like noble shape at top-left of the coil)
  val headX = centerX - 190f
  val headY = centerY - 130f + bodyOffset

  // Head Silhouette
  drawOval(
    color = Color(0xFFF8FAFC),
    topLeft = Offset(headX - 35f, headY - 25f),
    size = Size(70f, 50f)
  )

  // Branching Antlers
  drawLine(
    color = Color(0xFFE2E8F0),
    start = Offset(headX, headY - 15f),
    end = Offset(headX + 25f, headY - 55f),
    strokeWidth = 5f,
    cap = StrokeCap.Round
  )
  drawLine(
    color = Color(0xFFCBD5E1),
    start = Offset(headX + 10f, headY - 32f),
    end = Offset(headX + 40f, headY - 42f),
    strokeWidth = 3f,
    cap = StrokeCap.Round
  )

  // Dragon Whiskers
  drawLine(
    color = Color(0x88FFFFFF),
    start = Offset(headX - 25f, headY + 5f),
    end = Offset(headX - 70f, headY + 25f + bodyOffset),
    strokeWidth = 2.5f
  )

  // Eyes (Golden Amber - opens if approached)
  val eyeColor = if (isEyesOpen) Color(0xFFF59E0B) else Color(0xFF64748B)
  val eyeRadius = if (isEyesOpen) 6f else 2.5f
  drawCircle(
    color = eyeColor,
    radius = eyeRadius,
    center = Offset(headX - 10f, headY - 5f)
  )
  if (isEyesOpen) {
    drawCircle(
      color = Color(0x88F59E0B),
      radius = 12f * pulse,
      center = Offset(headX - 10f, headY - 5f)
    )
  }

  // Cloud breath
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(Color(0x66FFFFFF), Color.Transparent),
      center = Offset(headX - 45f, headY + 15f),
      radius = 35f
    ),
    radius = 35f,
    center = Offset(headX - 45f, headY + 15f)
  )
}

private fun DrawScope.drawNpc(npc: Npc2D, pulse: Float) {
  // Shadow
  drawOval(
    color = Color(0x44000000),
    topLeft = Offset(npc.x - 14f, npc.y + 16f),
    size = Size(28f, 10f)
  )

  // Robe / Body
  drawRoundRect(
    color = Color(npc.accentColor),
    topLeft = Offset(npc.x - 10f, npc.y - 12f),
    size = Size(20f, 26f),
    cornerRadius = CornerRadius(5f)
  )

  // Head
  drawCircle(
    color = Color(0xFFFED7AA),
    radius = 7f,
    center = Offset(npc.x, npc.y - 19f)
  )

  // Indicator Marker above NPC head
  drawCircle(
    color = Color(0xFF00E5FF),
    radius = 4f * pulse,
    center = Offset(npc.x, npc.y - 34f)
  )
}

private fun DrawScope.drawInteractiveItem(item: InteractiveItem2D, pulse: Float) {
  val center = Offset(item.x, item.y)
  when (item.type) {
    InteractiveType.DELIVERY_PACKET -> {
      // Warm brass tube with blue glass ribs
      drawRect(
        color = Color(0xFFFBBF24),
        topLeft = Offset(item.x - 8f, item.y - 14f),
        size = Size(16f, 28f)
      )
      drawCircle(
        color = Color(0x8800E5FF),
        radius = 18f * pulse,
        center = center
      )
    }
    InteractiveType.PELL_LOCK -> {
      // Pell's giant engraved iron collar lock
      drawCircle(
        color = Color(0xFF1E293B),
        radius = 18f,
        center = center
      )
      drawCircle(
        color = Color(0xFF38BDF8),
        radius = 19f,
        center = center,
        style = Stroke(width = 3f)
      )
    }
    InteractiveType.WEATHER_JAR -> {
      // Violet bubbling weather jar
      drawRoundRect(
        color = Color(0xFF7C3AED),
        topLeft = Offset(item.x - 12f, item.y - 16f),
        size = Size(24f, 32f),
        cornerRadius = CornerRadius(6f)
      )
      drawCircle(
        color = Color(0x66A855F7),
        radius = 24f * pulse,
        center = center
      )
    }
    InteractiveType.MOON_WELL -> {
      // Sacred moon well glowing with silver light
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(Color(0xFFE0F2FE), Color(0x5538BDF8), Color.Transparent),
          center = center,
          radius = 50f * pulse
        ),
        radius = 50f * pulse,
        center = center
      )
      drawCircle(
        color = Color(0xFF0F766E),
        radius = 26f,
        center = center,
        style = Stroke(width = 5f)
      )
    }
    InteractiveType.STORM_BRACE -> {
      // Iron grappling hook and spare pin
      drawLine(
        color = Color(0xFFF59E0B),
        start = Offset(item.x - 10f, item.y + 10f),
        end = Offset(item.x + 10f, item.y - 10f),
        strokeWidth = 4f
      )
    }
    else -> {
      drawCircle(
        color = Color(0xFFF59E0B),
        radius = 10f,
        center = center
      )
    }
  }
}

private fun DrawScope.drawNamiMoonKoi(
  x: Float,
  y: Float,
  angle: Float,
  tailPhase: Float,
  trail: List<Offset>,
  isExcited: Boolean
) {
  // 1. Residual Moonlight Trail
  trail.forEachIndexed { index, pos ->
    val alpha = (1f - index.toFloat() / trail.size.coerceAtLeast(1)) * 0.45f
    val radius = 9f * (1f - index.toFloat() / trail.size.coerceAtLeast(1))
    drawCircle(
      color = Color(0xFF38BDF8).copy(alpha = alpha),
      radius = radius,
      center = pos
    )
  }

  // 2. Nami Body
  withTransform({
    translate(x, y)
    rotate(angle)
  }) {
    // Moonlight Aura
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(Color(0x8800E5FF), Color(0x33F8FAFC), Color.Transparent),
        center = Offset.Zero,
        radius = 32f
      ),
      radius = 32f,
      center = Offset.Zero
    )

    // Translucent fish body
    val bodyPath = Path().apply {
      moveTo(14f, 0f) // Head
      quadraticTo(4f, -7f, -10f, 0f) // Back
      quadraticTo(4f, 7f, 14f, 0f) // Belly
      close()
    }
    drawPath(
      path = bodyPath,
      color = Color(0xFFF8FAFC)
    )

    // Vermilion gill mark (Like a drop of sealing wax)
    drawCircle(
      color = Color(0xFFE11D48),
      radius = 2.8f,
      center = Offset(7f, -2f)
    )

    // Whiskers
    drawLine(
      color = Color(0xCC00E5FF),
      start = Offset(14f, -1f),
      end = Offset(20f, -4f),
      strokeWidth = 1.2f
    )

    // Animated Waving Tail
    val tailSway = tailPhase * 10f
    val tailPath = Path().apply {
      moveTo(-10f, 0f)
      lineTo(-24f, -6f + tailSway)
      lineTo(-18f, 0f + tailSway * 0.5f)
      lineTo(-24f, 6f + tailSway)
      close()
    }
    drawPath(
      path = tailPath,
      color = Color(0xCCF8FAFC)
    )

    // Fins
    drawLine(
      color = Color(0x9900E5FF),
      start = Offset(2f, -5f),
      end = Offset(-4f, -12f),
      strokeWidth = 2f
    )
    drawLine(
      color = Color(0x9900E5FF),
      start = Offset(2f, 5f),
      end = Offset(-4f, 12f),
      strokeWidth = 2f
    )
  }
}

private fun DrawScope.drawSeraVenn(
  x: Float,
  y: Float,
  facingLeft: Boolean,
  isMoving: Boolean,
  pulse: Float
) {
  // Shadow
  drawOval(
    color = Color(0x55000000),
    topLeft = Offset(x - 14f, y + 20f),
    size = Size(28f, 9f)
  )

  val dir = if (facingLeft) -1f else 1f

  withTransform({
    translate(x, y)
    scale(scaleX = dir, scaleY = 1f, pivot = Offset.Zero)
  }) {
    // 1. Staff-Lantern (Right side)
    val staffX = 14f
    val staffTopY = -24f
    // Staff Pole
    drawLine(
      color = Color(0xFF78350F),
      start = Offset(staffX, staffTopY),
      end = Offset(staffX, 22f),
      strokeWidth = 3f
    )
    // Ribbed Blue Glass Lantern
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(Color(0xFF00E5FF), Color(0x4400E5FF), Color.Transparent),
        center = Offset(staffX, staffTopY),
        radius = 28f * pulse
      ),
      radius = 28f * pulse,
      center = Offset(staffX, staffTopY)
    )
    drawCircle(
      color = Color(0xFF00E5FF),
      radius = 6f,
      center = Offset(staffX, staffTopY)
    )
    drawCircle(
      color = Color.White,
      radius = 2.5f,
      center = Offset(staffX, staffTopY)
    )

    // 2. Sera's Boots & Legs
    val walkOffset = if (isMoving) sin(pulse * 10f) * 4f else 0f
    drawLine(
      color = Color(0xFF1E293B),
      start = Offset(-4f, 10f),
      end = Offset(-4f + walkOffset, 22f),
      strokeWidth = 4f
    )
    drawLine(
      color = Color(0xFF1E293B),
      start = Offset(4f, 10f),
      end = Offset(4f - walkOffset, 22f),
      strokeWidth = 4f
    )

    // 3. Indigo Cloak & Dark Shirt
    val cloakPath = Path().apply {
      moveTo(0f, -14f)
      lineTo(-13f, 14f)
      lineTo(11f, 14f)
      close()
    }
    drawPath(
      path = cloakPath,
      color = Color(0xFF1E1B4B)
    )

    // 4. Vermilion Sash (Tied in courier hitch across waist)
    drawRect(
      color = Color(0xFFE11D48),
      topLeft = Offset(-8f, 2f),
      size = Size(16f, 5f)
    )
    // Sash tail
    drawLine(
      color = Color(0xFFE11D48),
      start = Offset(-4f, 6f),
      end = Offset(-10f, 15f),
      strokeWidth = 3f
    )

    // 5. Brass Message Tubes on Ribs
    drawRect(
      color = Color(0xFFF59E0B),
      topLeft = Offset(-6f, -4f),
      size = Size(4f, 8f)
    )

    // 6. Sera's Head & Dark Hair
    drawCircle(
      color = Color(0xFFFED7AA),
      radius = 7.5f,
      center = Offset(0f, -17f)
    )
    // Hair tied back with courier indifference
    drawCircle(
      color = Color(0xFF0F172A),
      radius = 8.5f,
      center = Offset(-2f, -19f)
    )
  }
}

private fun DrawScope.drawForegroundAtmosphere(
  atmosphere: AtmosphereMode,
  w: Float,
  h: Float
) {
  // Vignette along screen edges
  val vignetteColor = when (atmosphere) {
    AtmosphereMode.STRAIN_BELL -> Color(0x333B0764)
    AtmosphereMode.AURORA_SKY -> Color(0x22047857)
    else -> Color(0x33000000)
  }

  drawRect(
    brush = Brush.radialGradient(
      colors = listOf(Color.Transparent, vignetteColor),
      center = Offset(w / 2f, h / 2f),
      radius = max(w, h) * 0.75f
    ),
    size = Size(w, h)
  )
}
