package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Npc2D
import com.example.ui.theme.*

@Composable
fun NpcDialog(
  npc: Npc2D,
  onDismiss: () -> Unit,
  onActionPrompt: (String) -> Unit
) {
  var dialogueIndex by remember { mutableStateOf(0) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("npc_dialog"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MidnightSky.copy(alpha = 0.95f)),
      border = CardDefaults.outlinedCardBorder().copy(
        brush = Brush.verticalGradient(
          listOf(Color(npc.accentColor), SurfaceDark)
        )
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(npc.accentColor).copy(alpha = 0.2f))
                .border(1.5.dp, Color(npc.accentColor), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = npc.name.take(1),
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(npc.accentColor)
                )
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = npc.name,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = MoonKoiWhite
                )
              )
              Text(
                text = npc.role,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = MoonKoiSilver.copy(alpha = 0.7f),
                  fontSize = 11.5.sp
                )
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("btn_close_npc_dialog")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = MoonKoiSilver
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Greeting / Main Dialogue Quote
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = SurfaceDark,
          border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
              listOf(Color(npc.accentColor).copy(alpha = 0.4f), Color.Transparent)
            )
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = if (dialogueIndex == 0) npc.greeting else npc.dialogue.getOrElse(dialogueIndex - 1) { npc.greeting },
              style = MaterialTheme.typography.bodyMedium.copy(
                color = MoonKoiWhite,
                fontStyle = FontStyle.Italic,
                lineHeight = 22.sp
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Dialogue Navigation Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Passage ${dialogueIndex + 1} of ${npc.dialogue.size + 1}",
            style = MaterialTheme.typography.labelSmall.copy(
              color = MoonKoiSilver.copy(alpha = 0.6f)
            )
          )

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (dialogueIndex < npc.dialogue.size) {
              OutlinedButton(
                onClick = { dialogueIndex++ },
                colors = ButtonDefaults.outlinedButtonColors(
                  contentColor = Color(npc.accentColor)
                )
              ) {
                Text("Listen Further")
              }
            } else {
              Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color(npc.accentColor)
                )
              ) {
                Text("Continue Courier Run", color = MidnightSky, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}
