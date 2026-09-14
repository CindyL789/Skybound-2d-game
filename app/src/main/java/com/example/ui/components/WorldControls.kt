package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.WorldUiState
import kotlin.math.*

@Composable
fun WorldTopBar(
  uiState: WorldUiState,
  onOpenMap: () -> Unit,
  onOpenCodex: () -> Unit,
  onToggleAtmosphere: () -> Unit,
  onToggleSound: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 8.dp)
      .testTag("world_top_bar"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MidnightSky.copy(alpha = 0.88f)
    ),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = Brush.horizontalGradient(
        listOf(LanternBlue.copy(alpha = 0.6f), AmberLight.copy(alpha = 0.4f))
      )
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left: District & Weather Title
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(Color(uiState.currentDistrict.colorHex))
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = uiState.currentDistrict.displayName,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MoonKoiWhite
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
        Text(
          text = uiState.currentDistrict.subtitle,
          style = MaterialTheme.typography.bodySmall.copy(
            color = MoonKoiSilver.copy(alpha = 0.75f),
            fontSize = 11.sp
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      // Right: Tool Buttons
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Atmosphere toggle button
        IconButton(
          onClick = onToggleAtmosphere,
          modifier = Modifier
            .size(38.dp)
            .testTag("btn_atmosphere")
        ) {
          Icon(
            imageVector = Icons.Default.WbTwilight,
            contentDescription = "Change Atmosphere: ${uiState.atmosphere.displayName}",
            tint = AmberLight
          )
        }

        // Map Button
        IconButton(
          onClick = onOpenMap,
          modifier = Modifier
            .size(38.dp)
            .testTag("btn_world_map")
        ) {
          Icon(
            imageVector = Icons.Default.Explore,
            contentDescription = "Open Archipelago Map",
            tint = LanternBlue
          )
        }

        // Lore Codex Button
        IconButton(
          onClick = onOpenCodex,
          modifier = Modifier
            .size(38.dp)
            .testTag("btn_codex")
        ) {
          Icon(
            imageVector = Icons.Default.MenuBook,
            contentDescription = "Open Courier Codex",
            tint = VermilionSash
          )
        }

        // Sound Button
        IconButton(
          onClick = onToggleSound,
          modifier = Modifier
            .size(38.dp)
            .testTag("btn_sound")
        ) {
          Icon(
            imageVector = if (uiState.soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
            contentDescription = "Toggle Audio",
            tint = MoonKoiSilver
          )
        }
      }
    }
  }
}

@Composable
fun QuestBanner(
  uiState: WorldUiState,
  modifier: Modifier = Modifier
) {
  val activeQuest = uiState.quests.getOrNull(uiState.activeQuestIndex) ?: return

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp)
      .testTag("quest_banner"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = SurfaceDark.copy(alpha = 0.90f)
    ),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = Brush.horizontalGradient(
        listOf(LanternBlue.copy(alpha = 0.5f), Color.Transparent)
      )
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.MarkunreadMailbox,
        contentDescription = null,
        tint = LanternBlue,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "Delivery: ${activeQuest.title}",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = LanternBlue
          )
        )
        Text(
          text = activeQuest.stepDescription,
          style = MaterialTheme.typography.bodySmall.copy(
            color = MoonKoiSilver.copy(alpha = 0.9f),
            fontSize = 11.5.sp
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

@Composable
fun ScaleCompassHud(
  compassAngle: Float,
  distance: Float,
  modifier: Modifier = Modifier
) {
  // Scale compass with vibrating moon-koi needle
  Card(
    modifier = modifier
      .size(76.dp)
      .testTag("compass_hud"),
    shape = CircleShape,
    colors = CardDefaults.cardColors(
      containerColor = MidnightSky.copy(alpha = 0.9f)
    ),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = Brush.radialGradient(
        listOf(BrassGold, IronChain)
      )
    )
  ) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      // Rotating needle pointing toward residual moonlight / objective
      Box(
        modifier = Modifier
          .fillMaxSize()
          .rotate(compassAngle),
        contentAlignment = Alignment.Center
      ) {
        // Needle representation
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxHeight()
        ) {
          // Top needle (silver moon-koi scale)
          Box(
            modifier = Modifier
              .width(5.dp)
              .height(26.dp)
              .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
              .background(LanternBlue)
          )
          Spacer(modifier = Modifier.weight(1f))
          // Bottom tail
          Box(
            modifier = Modifier
              .width(3.dp)
              .height(18.dp)
              .clip(RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp))
              .background(VermilionSash)
          )
        }
      }

      // Compass center brass rivet
      Box(
        modifier = Modifier
          .size(10.dp)
          .clip(CircleShape)
          .background(BrassGold)
      )

      // Distance tag
      Text(
        text = "${distance.toInt()}m",
        style = MaterialTheme.typography.labelSmall.copy(
          fontSize = 9.sp,
          color = MoonKoiSilver.copy(alpha = 0.8f),
          fontWeight = FontWeight.Bold
        ),
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(bottom = 2.dp)
      )
    }
  }
}

@Composable
fun VirtualJoystick(
  onDirectionChanged: (Float, Float) -> Unit,
  modifier: Modifier = Modifier
) {
  var knobOffset by remember { mutableStateOf(Offset.Zero) }
  val maxRadius = 55f

  Box(
    modifier = modifier
      .size(130.dp)
      .clip(CircleShape)
      .background(MidnightSky.copy(alpha = 0.65f))
      .border(1.5.dp, LanternBlue.copy(alpha = 0.4f), CircleShape)
      .pointerInput(Unit) {
        detectDragGestures(
          onDragStart = { },
          onDragEnd = {
            knobOffset = Offset.Zero
            onDirectionChanged(0f, 0f)
          },
          onDragCancel = {
            knobOffset = Offset.Zero
            onDirectionChanged(0f, 0f)
          },
          onDrag = { change, dragAmount ->
            change.consume()
            val nextOffset = knobOffset + dragAmount
            val distance = hypot(nextOffset.x, nextOffset.y)
            knobOffset = if (distance > maxRadius) {
              val factor = maxRadius / distance
              Offset(nextOffset.x * factor, nextOffset.y * factor)
            } else {
              nextOffset
            }
            val normalizedX = (knobOffset.x / maxRadius).coerceIn(-1f, 1f)
            val normalizedY = (knobOffset.y / maxRadius).coerceIn(-1f, 1f)
            onDirectionChanged(normalizedX, normalizedY)
          }
        )
      },
    contentAlignment = Alignment.Center
  ) {
    // Inner cross guide marks
    Box(
      modifier = Modifier
        .width(80.dp)
        .height(1.dp)
        .background(Color(0x33FFFFFF))
    )
    Box(
      modifier = Modifier
        .height(80.dp)
        .width(1.dp)
        .background(Color(0x33FFFFFF))
    )

    // Movable knob
    Box(
      modifier = Modifier
        .offset { IntOffset(knobOffset.x.roundToInt(), knobOffset.y.roundToInt()) }
        .size(48.dp)
        .shadow(6.dp, CircleShape)
        .clip(CircleShape)
        .background(
          Brush.radialGradient(
            listOf(LanternBlue, DeepSeaBlue)
          )
        )
        .border(1.5.dp, MoonKoiWhite, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .size(10.dp)
          .clip(CircleShape)
          .background(Color.White)
      )
    }
  }
}

@Composable
fun WorldBottomControls(
  uiState: WorldUiState,
  onDirectionChanged: (Float, Float) -> Unit,
  onInteract: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 14.dp)
  ) {
    // Left: Virtual Joystick
    VirtualJoystick(
      onDirectionChanged = onDirectionChanged,
      modifier = Modifier.align(Alignment.BottomStart)
    )

    // Right Center: Scale Compass HUD
    ScaleCompassHud(
      compassAngle = uiState.compassAngle,
      distance = uiState.compassDistance,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 24.dp)
    )

    // Right: Action / Interact Button
    Column(
      modifier = Modifier.align(Alignment.BottomEnd),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Notification text if near interaction
      AnimatedVisibility(
        visible = uiState.nearestInteraction != null,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MidnightSky.copy(alpha = 0.9f),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(LanternBlue, AmberLight))
          ),
          modifier = Modifier.padding(bottom = 8.dp)
        ) {
          Text(
            text = uiState.nearestInteraction ?: "",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MoonKoiWhite,
              fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      // Primary Action / Interact Button
      Button(
        onClick = onInteract,
        modifier = Modifier
          .size(68.dp)
          .testTag("btn_interact"),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
          containerColor = if (uiState.nearestInteraction != null) AmberWarm else LanternBlue
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
        contentPadding = PaddingValues(0.dp)
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = when {
              uiState.nearestInteraction?.contains("Speak", true) == true -> Icons.Default.ChatBubble
              uiState.nearestInteraction?.contains("Lantern", true) == true -> Icons.Default.Lightbulb
              uiState.nearestInteraction?.contains("Lock", true) == true -> Icons.Default.LockOpen
              uiState.nearestInteraction?.contains("Brace", true) == true -> Icons.Default.Build
              uiState.nearestInteraction?.contains("Well", true) == true -> Icons.Default.WaterDrop
              else -> Icons.Default.PanTool
            },
            contentDescription = "Interact Action",
            tint = MidnightSky,
            modifier = Modifier.size(28.dp)
          )
          Text(
            text = if (uiState.nearestInteraction != null) "ACT" else "NAMI",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MidnightSky,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          )
        }
      }
    }
  }
}
