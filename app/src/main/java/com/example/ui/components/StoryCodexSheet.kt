package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.LoreEntry
import com.example.data.repository.WorldRepository
import com.example.ui.theme.*
import com.example.ui.viewmodel.WorldUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryCodexSheet(
  uiState: WorldUiState,
  onDismiss: () -> Unit,
  onSelectLore: (LoreEntry?) -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("Chronicles", "Courier Log", "Satchel")

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = MidnightSky,
    contentColor = MoonKoiWhite,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 18.dp)
        .testTag("story_codex_sheet")
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(
            text = "The Skybound Archipelago",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = MoonKoiWhite
            )
          )
          Text(
            text = "A Promise Against the Weather · Book One",
            style = MaterialTheme.typography.bodySmall.copy(
              color = LanternBlue,
              fontStyle = FontStyle.Italic
            )
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = MoonKoiSilver)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Tab Row
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = SurfaceDark,
        contentColor = LanternBlue
      ) {
        tabs.forEachIndexed { index, title ->
          Tab(
            selected = selectedTab == index,
            onClick = { selectedTab = index },
            text = { Text(title, fontWeight = FontWeight.SemiBold) }
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      when (selectedTab) {
        0 -> ChroniclesTab(onSelectLore)
        1 -> CourierQuestsTab(uiState)
        2 -> SatchelInventoryTab(uiState)
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun ChroniclesTab(onSelectLore: (LoreEntry?) -> Unit) {
  val loreEntries = remember { WorldRepository.getLoreEntries() }

  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(max = 420.dp)
  ) {
    // Hero Illustration Banner
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
      ) {
        Column {
          Image(
            painter = painterResource(id = R.drawable.img_archipelago_art_1789323813036),
            contentDescription = "Skybound Archipelago World Banner",
            modifier = Modifier
              .fillMaxWidth()
              .height(140.dp),
            contentScale = ContentScale.Crop
          )
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "\"Every route is temporary. Every delivery is a promise made against the weather.\"",
              style = MaterialTheme.typography.bodySmall.copy(
                fontStyle = FontStyle.Italic,
                color = AmberLight,
                lineHeight = 18.sp
              )
            )
            Text(
              text = "— Courier proverb of the night market",
              style = MaterialTheme.typography.labelSmall.copy(
                color = MoonKoiSilver.copy(alpha = 0.6f)
              )
            )
          }
        }
      }
    }

    items(loreEntries) { lore ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onSelectLore(lore) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = Brush.horizontalGradient(listOf(LanternBlue.copy(alpha = 0.3f), Color.Transparent))
        )
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = lore.title,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MoonKoiWhite
              )
            )
            Text(
              text = lore.chapter,
              style = MaterialTheme.typography.labelSmall.copy(
                color = AmberLight
              )
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = lore.quote,
            style = MaterialTheme.typography.bodySmall.copy(
              fontStyle = FontStyle.Italic,
              color = MoonKoiSilver.copy(alpha = 0.85f),
              fontSize = 12.sp
            )
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = lore.excerpt,
            style = MaterialTheme.typography.bodySmall.copy(
              color = MoonKoiSilver.copy(alpha = 0.7f),
              fontSize = 11.5.sp,
              lineHeight = 17.sp
            )
          )
        }
      }
    }
  }
}

@Composable
private fun CourierQuestsTab(uiState: WorldUiState) {
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(10.dp),
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(max = 420.dp)
  ) {
    items(uiState.quests) { quest ->
      val isActive = uiState.quests.indexOf(quest) == uiState.activeQuestIndex && !quest.isCompleted

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isActive) DeepIndigo else SurfaceDark
        ),
        border = if (isActive) {
          CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(LanternBlue, AmberLight))
          )
        } else null
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(
                if (quest.isCompleted) TealShrine.copy(alpha = 0.2f)
                else if (isActive) LanternBlue.copy(alpha = 0.2f)
                else MoonKoiSilver.copy(alpha = 0.1f)
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (quest.isCompleted) Icons.Default.CheckCircle else Icons.Default.Pending,
              contentDescription = null,
              tint = if (quest.isCompleted) TealShrine else if (isActive) LanternBlue else MoonKoiSilver,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = quest.title,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = if (quest.isCompleted) MoonKoiSilver else MoonKoiWhite
                )
              )
              if (isActive) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "IN PROGRESS",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = LanternBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                  )
                )
              }
            }
            Text(
              text = quest.description,
              style = MaterialTheme.typography.bodySmall.copy(
                color = MoonKoiSilver.copy(alpha = 0.75f),
                fontSize = 11.5.sp
              )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Giver: ${quest.giver} · Location: ${quest.targetDistrict.displayName}",
              style = MaterialTheme.typography.labelSmall.copy(
                color = AmberLight.copy(alpha = 0.8f),
                fontSize = 10.sp
              )
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SatchelInventoryTab(uiState: WorldUiState) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(max = 420.dp)
  ) {
    Text(
      text = "COURIER RIG & RAIN SATCHEL",
      style = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.Bold,
        color = MoonKoiSilver.copy(alpha = 0.6f),
        letterSpacing = 1.2.sp
      )
    )

    Spacer(modifier = Modifier.height(10.dp))

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(uiState.inventory) { itemName ->
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceDark),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = when {
                itemName.contains("Lantern", true) -> Icons.Default.Lightbulb
                itemName.contains("Compass", true) -> Icons.Default.Explore
                itemName.contains("Sash", true) -> Icons.Default.Bookmark
                itemName.contains("Packet", true) -> Icons.Default.Mail
                itemName.contains("Weather", true) -> Icons.Default.Thunderstorm
                itemName.contains("Chart", true) -> Icons.Default.Map
                else -> Icons.Default.Inventory2
              },
              contentDescription = null,
              tint = when {
                itemName.contains("Lantern", true) -> LanternBlue
                itemName.contains("Sash", true) -> VermilionSash
                itemName.contains("Weather", true) -> JarWeatherViolet
                else -> AmberLight
              },
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = itemName,
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = MoonKoiWhite
                )
              )
              Text(
                text = when {
                  itemName.contains("Lantern", true) -> "Brass staff-lantern with ribbed blue glass"
                  itemName.contains("Compass", true) -> "Sliver of moon-koi scale in brass cup"
                  itemName.contains("Sash", true) -> "Formal courier hitch, vermilion cloth"
                  itemName.contains("Packet", true) -> "Sealed delivery packet with wax or ribbed glass"
                  itemName.contains("Weather", true) -> "Compressed violet jar-weather"
                  itemName.contains("Chart", true) -> "True route parchment drawn by Tavi Quill"
                  else -> "Standard courier gear"
                },
                style = MaterialTheme.typography.bodySmall.copy(
                  color = MoonKoiSilver.copy(alpha = 0.65f),
                  fontSize = 11.sp
                )
              )
            }
          }
        }
      }
    }
  }
}
