package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DistrictId
import com.example.ui.theme.*
import com.example.ui.viewmodel.WorldUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldMapSheet(
  uiState: WorldUiState,
  onDismiss: () -> Unit,
  onFastTravel: (DistrictId) -> Unit
) {
  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = MidnightSky,
    contentColor = MoonKoiWhite,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp)
        .testTag("world_map_sheet")
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Explore,
            contentDescription = null,
            tint = LanternBlue,
            modifier = Modifier.size(26.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "The Skybound Archipelago",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MoonKoiWhite
              )
            )
            Text(
              text = "2D Hanging World Navigation & Chain Network",
              style = MaterialTheme.typography.bodySmall.copy(
                color = MoonKoiSilver.copy(alpha = 0.7f)
              )
            )
          }
        }

        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = MoonKoiSilver)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Status pill
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = SurfaceDark,
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(listOf(TealShrine, LanternBlue))
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          val litLanterns = uiState.lanterns.count { it.isLit }
          val totalLanterns = uiState.lanterns.size
          Text(
            text = "Lanterns Lit: $litLanterns / $totalLanterns",
            style = MaterialTheme.typography.labelSmall.copy(color = LanternBlue)
          )
          val bracedChains = uiState.chains.count { !it.isSlack || it.isBraced }
          Text(
            text = "Chains Secured: $bracedChains / ${uiState.chains.size}",
            style = MaterialTheme.typography.labelSmall.copy(color = AmberLight)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "DISTRICT PLATFORMS",
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.Bold,
          color = MoonKoiSilver.copy(alpha = 0.6f),
          letterSpacing = 1.2.sp
        )
      )

      Spacer(modifier = Modifier.height(8.dp))

      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
      ) {
        items(DistrictId.entries) { district ->
          val isCurrent = uiState.currentDistrict == district
          val hasActiveQuest = uiState.quests.getOrNull(uiState.activeQuestIndex)?.targetDistrict == district

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onFastTravel(district) },
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isCurrent) DeepIndigo else SurfaceDark
            ),
            border = if (isCurrent) {
              CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(
                  listOf(Color(district.colorHex), Color.Transparent)
                )
              )
            } else null
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(district.colorHex).copy(alpha = 0.2f))
                    .border(1.dp, Color(district.colorHex), CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = when (district) {
                      DistrictId.BAZAAR -> Icons.Default.Storefront
                      DistrictId.HIGH_CUT -> Icons.Default.LinearScale
                      DistrictId.UNDERTOW_DEN -> Icons.Default.LocalBar
                      DistrictId.SAILCLOTH_ROW -> Icons.Default.Anchor
                      DistrictId.STORM_ANCHOR_SHRINE -> Icons.Default.TempleBuddhist
                      DistrictId.LOWER_SALT_GALLEON -> Icons.Default.DirectionsBoat
                    },
                    contentDescription = null,
                    tint = Color(district.colorHex),
                    modifier = Modifier.size(18.dp)
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = district.displayName,
                      style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MoonKoiWhite
                      )
                    )
                    if (isCurrent) {
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = "HERE",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = LanternBlue,
                          fontWeight = FontWeight.Bold,
                          fontSize = 9.sp
                        )
                      )
                    }
                    if (hasActiveQuest) {
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = "OBJECTIVE",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = AmberLight,
                          fontWeight = FontWeight.Bold,
                          fontSize = 9.sp
                        )
                      )
                    }
                  }
                  Text(
                    text = district.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = MoonKoiSilver.copy(alpha = 0.7f),
                      fontSize = 11.5.sp
                    )
                  )
                }
              }

              Button(
                onClick = { onFastTravel(district) },
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (isCurrent) Color.Transparent else Color(district.colorHex)
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Text(
                  text = if (isCurrent) "Walking" else "Travel",
                  color = if (isCurrent) MoonKoiSilver else MidnightSky,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
