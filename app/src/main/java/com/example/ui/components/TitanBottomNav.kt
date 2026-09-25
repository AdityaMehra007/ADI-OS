package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.MainNavDestination
import com.example.ui.navigation.ScreenRoutes
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.TitanTheme
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TitanCyan
import com.example.ui.viewmodel.TitanScreen

/**
 * Main Bottom Navigation component featuring the three primary application pillars:
 * - Career Strategy
 * - Company Intelligence
 * - Job Automation
 */
@Composable
fun TitanBottomNav(
  currentRoute: String?,
  onDestinationSelected: (MainNavDestination) -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    color = ObsidianDark,
    modifier = modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .testTag("main_bottom_navigation")
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, SlateBorder)
        .background(ObsidianDark)
        .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        MainNavDestination.items.forEach { destination ->
          val isSelected = when (destination) {
            MainNavDestination.CareerStrategy -> currentRoute == ScreenRoutes.CAREER_STRATEGY ||
                currentRoute == ScreenRoutes.CAREER_NOTES ||
                currentRoute == ScreenRoutes.GOALS
            MainNavDestination.CompanyIntelligence -> currentRoute == ScreenRoutes.COMPANY_INTELLIGENCE ||
                currentRoute == ScreenRoutes.COMPANIES ||
                currentRoute == ScreenRoutes.COMPANY_BOOKMARKS
            MainNavDestination.JobAutomation -> currentRoute == ScreenRoutes.JOB_AUTOMATION ||
                currentRoute == ScreenRoutes.JOBS ||
                currentRoute == ScreenRoutes.AUTOMATION_RULES ||
                currentRoute == ScreenRoutes.AGENTS ||
                currentRoute == ScreenRoutes.TASK_LOGS
            MainNavDestination.SovereignSuperApp -> currentRoute == ScreenRoutes.SOVEREIGN_SUPER_APP
          }

          val animatedBorderColor by animateColorAsState(
            targetValue = if (isSelected) TitanCyan else Color.Transparent,
            label = "tab_border_color"
          )
          val animatedBgColor by animateColorAsState(
            targetValue = if (isSelected) TitanCyan.copy(alpha = 0.12f) else Color.Transparent,
            label = "tab_bg_color"
          )

          Box(
            modifier = Modifier
              .weight(1f)
              .height(58.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(animatedBgColor)
              .border(1.dp, animatedBorderColor, RoundedCornerShape(12.dp))
              .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Tab,
                onClick = { onDestinationSelected(destination) }
              )
              .padding(horizontal = 6.dp, vertical = 4.dp)
              .testTag(destination.testTag),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = destination.icon,
                contentDescription = destination.title,
                tint = if (isSelected) TitanCyan else TextMutedDark,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = destination.title,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) TextPrimaryDark else TextMutedDark,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp,
                maxLines = 1
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Backward compatibility overload that maps a TitanScreen to the navigation routes.
 */
@Composable
fun TitanBottomNav(
  currentScreen: TitanScreen,
  onScreenSelected: (TitanScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  val currentRoute = ScreenRoutes.screenToRoute(currentScreen)
  TitanBottomNav(
    currentRoute = currentRoute,
    onDestinationSelected = { destination ->
      onScreenSelected(destination.associatedTitanScreen)
    },
    modifier = modifier
  )
}

@androidx.compose.ui.tooling.preview.Preview(name = "Titan Bottom Nav - Obsidian Dark")
@Composable
fun TitanBottomNavPreview() {
  TitanTheme {
    TitanBottomNav(
      currentRoute = ScreenRoutes.CAREER_STRATEGY,
      onDestinationSelected = {}
    )
  }
}
