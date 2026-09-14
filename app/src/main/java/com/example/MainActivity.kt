package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.WorldViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val viewModel: WorldViewModel = viewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        // Auto-dismiss notification after 3 seconds
        LaunchedEffect(uiState.notificationMessage) {
          if (uiState.notificationMessage != null) {
            delay(3500)
            viewModel.dismissNotification()
          }
        }

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          containerColor = MidnightSky,
          contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            // 1. 2D World Canvas Layer (The living Skybound Archipelago)
            WorldCanvas(
              uiState = uiState,
              onTapWorld = { worldX, worldY ->
                viewModel.setTapDestination(worldX, worldY)
              },
              onPanWorld = { dx, dy ->
                // Dragging world canvas can also direct the player or camera
                viewModel.setTapDestination(uiState.playerX + dx * 0.5f, uiState.playerY + dy * 0.5f)
              },
              modifier = Modifier
                .fillMaxSize()
                .testTag("world_canvas")
            )

            // 2. Top HUD Bar & Quest Status
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
            ) {
              WorldTopBar(
                uiState = uiState,
                onOpenMap = { viewModel.openWorldMap(true) },
                onOpenCodex = { viewModel.openCodex(true) },
                onToggleAtmosphere = { viewModel.toggleAtmosphere() },
                onToggleSound = { viewModel.toggleSound() }
              )

              Spacer(modifier = Modifier.height(4.dp))

              QuestBanner(uiState = uiState)
            }

            // 3. Floating Notification Pill
            AnimatedVisibility(
              visible = uiState.notificationMessage != null,
              enter = slideInVertically { -it } + fadeIn(),
              exit = slideOutVertically { -it } + fadeOut(),
              modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 135.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(20.dp),
                color = MidnightSky.copy(alpha = 0.95f),
                border = CardDefaults.outlinedCardBorder().copy(
                  brush = Brush.horizontalGradient(listOf(LanternBlue, AmberLight))
                ),
                shadowElevation = 8.dp,
                modifier = Modifier
                  .padding(horizontal = 24.dp)
                  .clickable { viewModel.dismissNotification() }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = LanternBlue,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = uiState.notificationMessage ?: "",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = MoonKoiWhite,
                      fontWeight = FontWeight.SemiBold,
                      fontSize = 12.sp
                    )
                  )
                }
              }
            }

            // 4. Bottom Controls: Virtual Joystick, Scale-Compass, Action Button
            WorldBottomControls(
              uiState = uiState,
              onDirectionChanged = { dx, dy ->
                viewModel.setJoystickInput(dx, dy)
              },
              onInteract = {
                viewModel.interact()
              },
              modifier = Modifier.align(Alignment.BottomCenter)
            )

            // 5. Overlays: NPC Dialog
            uiState.activeNpcDialog?.let { npc ->
              NpcDialog(
                npc = npc,
                onDismiss = { viewModel.openNpcDialog(null) },
                onActionPrompt = { }
              )
            }

            // 6. Overlays: World Map Sheet
            if (uiState.isWorldMapOpen) {
              WorldMapSheet(
                uiState = uiState,
                onDismiss = { viewModel.openWorldMap(false) },
                onFastTravel = { district ->
                  viewModel.fastTravelToDistrict(district)
                }
              )
            }

            // 7. Overlays: Story Codex Sheet
            if (uiState.isCodexOpen) {
              StoryCodexSheet(
                uiState = uiState,
                onDismiss = { viewModel.openCodex(false) },
                onSelectLore = { lore ->
                  viewModel.selectLore(lore)
                }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}
