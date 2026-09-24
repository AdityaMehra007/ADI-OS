package com.example

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notification.TitanNotificationManager
import com.example.ui.components.QuickActionFeedbackBanner
import com.example.ui.components.QuickActionFloatingButton
import com.example.ui.components.TitanBottomNav
import com.example.ui.components.TitanGlobalDialogHost
import com.example.ui.components.TitanTopBar
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.MainNavDestination
import com.example.ui.navigation.ScreenRoutes
import com.example.ui.screens.AgentsScreen
import com.example.ui.screens.ApplicationsScreen
import com.example.ui.screens.AutomationRulesScreen
import com.example.ui.screens.CareerNotesScreen
import com.example.ui.screens.CareerStrategyDashboardScreen
import com.example.ui.screens.CompaniesScreen
import com.example.ui.screens.CompanyBookmarksScreen
import com.example.ui.screens.CopilotScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExecutiveCommandCenterScreen
import com.example.ui.screens.GoalSettingScreen
import com.example.ui.screens.InterviewsScreen
import com.example.ui.screens.JobAutomationScreen
import com.example.ui.screens.JobsScreen
import com.example.ui.screens.NetworkScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SkillsProjectsScreen
import com.example.ui.screens.TaskLogsScreen
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.TitanTheme
import com.example.ui.viewmodel.CareerNotesViewModel
import com.example.ui.viewmodel.CareerStrategyViewModel
import com.example.ui.viewmodel.CompanyBookmarksViewModel
import com.example.ui.viewmodel.CompanyIntelViewModel
import com.example.ui.viewmodel.ExecutiveCommandViewModel
import com.example.ui.viewmodel.JobAutomationViewModel
import com.example.ui.viewmodel.ResumeLabViewModel
import com.example.ui.viewmodel.TaskLogsViewModel
import com.example.ui.viewmodel.TitanScreen
import com.example.ui.viewmodel.TitanViewModel
import com.example.ui.viewmodel.TitanViewModelFactory

class MainActivity : ComponentActivity() {

  private val viewModel: TitanViewModel by viewModels {
    (application as? TitanApplication)?.viewModelFactory
      ?: TitanViewModelFactory.createFactory(application)
  }

  private val careerStrategyViewModel: CareerStrategyViewModel by viewModels {
    (application as? TitanApplication)?.viewModelFactory
      ?: TitanViewModelFactory.createFactory(application)
  }

  private val companyIntelViewModel: CompanyIntelViewModel by viewModels {
    (application as? TitanApplication)?.viewModelFactory
      ?: TitanViewModelFactory.createFactory(application)
  }

  private val jobAutomationViewModel: JobAutomationViewModel by viewModels {
    (application as? TitanApplication)?.viewModelFactory
      ?: TitanViewModelFactory.createFactory(application)
  }

  private val resumeLabViewModel: ResumeLabViewModel by viewModels {
    (application as? TitanApplication)?.viewModelFactory
      ?: TitanViewModelFactory.createFactory(application)
  }

  private val executiveCommandViewModel: ExecutiveCommandViewModel by viewModels {
    (application as? TitanApplication)?.viewModelFactory
      ?: TitanViewModelFactory.createFactory(application)
  }

  private val careerNotesViewModel: CareerNotesViewModel by viewModels {
    (application as? TitanApplication)?.viewModelFactory
      ?: TitanViewModelFactory.createFactory(application)
  }

  private val companyBookmarksViewModel: CompanyBookmarksViewModel by viewModels {
    (application as? TitanApplication)?.viewModelFactory
      ?: TitanViewModelFactory.createFactory(application)
  }

  private val taskLogsViewModel: TaskLogsViewModel by viewModels {
    (application as? TitanApplication)?.viewModelFactory
      ?: TitanViewModelFactory.createFactory(application)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    handleNotificationIntent(intent)

    setContent {
      TitanTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: ScreenRoutes.CAREER_STRATEGY

        val currentScreen by viewModel.currentScreen.collectAsState()
        val userProfile by viewModel.userProfile.collectAsState()
        val notificationUnreadCount by viewModel.notificationUnreadCount.collectAsState()
        val isNotificationCenterOpen by viewModel.isNotificationCenterOpen.collectAsState()
        val isCommandPaletteOpen by viewModel.isCommandPaletteOpen.collectAsState()
        val isSalaryCalculatorOpen by viewModel.isSalaryCalculatorOpen.collectAsState()
        val isCapabilityMatrixOpen by viewModel.isCapabilityMatrixOpen.collectAsState()
        val isScenarioSimulatorOpen by viewModel.isScenarioSimulatorOpen.collectAsState()
        val isMultiOfferComparatorOpen by viewModel.isMultiOfferComparatorOpen.collectAsState()
        val authUser by viewModel.authUser.collectAsState()
        val isAuthDialogOpen by viewModel.isAuthDialogOpen.collectAsState()

        // Sync external ViewModel screen changes with NavController
        LaunchedEffect(currentScreen) {
          val targetRoute = ScreenRoutes.screenToRoute(currentScreen)
          if (navController.currentDestination?.route != targetRoute) {
            navController.navigate(targetRoute) {
              popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
              }
              launchSingleTop = true
              restoreState = true
            }
          }
        }

        // Sync NavController route back to ViewModel screen for back-press coherence
        LaunchedEffect(currentRoute) {
          val matchedScreen = ScreenRoutes.routeToScreen(currentRoute)
          if (matchedScreen != null && currentScreen != matchedScreen) {
            viewModel.navigateTo(matchedScreen)
          }
        }

        // Notification permission request on Android 13+ (API 33+)
        val notificationPermissionLauncher = rememberLauncherForActivityResult(
          contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
          android.util.Log.d("MainActivity", "POST_NOTIFICATIONS granted: $isGranted")
        }

        LaunchedEffect(Unit) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
          }
        }

        Scaffold(
          modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark),
          topBar = {
            TitanTopBar(
              userProfile = userProfile,
              authUser = authUser,
              notificationUnreadCount = notificationUnreadCount,
              onToggleBrutalMode = { viewModel.toggleBrutalMode() },
              onAutomationModeClick = {
                val nextMode = when (userProfile?.automationMode) {
                  "AUTO" -> "APPROVAL"
                  "APPROVAL" -> "MANUAL"
                  else -> "AUTO"
                }
                viewModel.setAutomationMode(nextMode)
              },
              onSearchClick = {
                viewModel.setCommandPaletteOpen(true)
              },
              onNotificationClick = {
                viewModel.setNotificationCenterOpen(true)
              },
              onAuthClick = {
                viewModel.setAuthDialogOpen(true)
              }
            )
          },
          bottomBar = {
            TitanBottomNav(
              currentRoute = currentRoute,
              onDestinationSelected = { dest ->
                if (currentRoute != dest.route) {
                  navController.navigate(dest.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                      saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                  }
                }
                viewModel.navigateTo(dest.associatedTitanScreen)
              }
            )
          }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
              .background(ObsidianDark)
          ) {
            NavHost(
              navController = navController,
              startDestination = ScreenRoutes.CAREER_STRATEGY,
              modifier = Modifier.fillMaxSize()
            ) {
              // Main Three Pillars
              composable(ScreenRoutes.CAREER_STRATEGY) {
                CareerStrategyDashboardScreen(viewModel)
              }
              composable(ScreenRoutes.COMPANY_INTELLIGENCE) {
                CompaniesScreen(viewModel)
              }
              composable(ScreenRoutes.JOB_AUTOMATION) {
                JobAutomationScreen(viewModel, taskLogsViewModel)
              }

              // Deep-linkable auxiliary screens
              composable(ScreenRoutes.DASHBOARD) { DashboardScreen(viewModel) }
              composable(ScreenRoutes.COMMAND_CENTER) { ExecutiveCommandCenterScreen(viewModel) }
              composable(ScreenRoutes.JOBS) { JobsScreen(viewModel) }
              composable(ScreenRoutes.COMPANIES) { CompaniesScreen(viewModel) }
              composable(ScreenRoutes.COMPANY_BOOKMARKS) { CompanyBookmarksScreen(companyBookmarksViewModel) }
              composable(ScreenRoutes.APPLICATIONS) { ApplicationsScreen(viewModel) }
              composable(ScreenRoutes.INTERVIEWS) { InterviewsScreen(viewModel) }
              composable(ScreenRoutes.SKILLS_PROJECTS) { SkillsProjectsScreen(viewModel) }
              composable(ScreenRoutes.NETWORK) { NetworkScreen(viewModel) }
              composable(ScreenRoutes.GOALS) { GoalSettingScreen(viewModel) }
              composable(ScreenRoutes.AGENTS) { AgentsScreen(viewModel) }
              composable(ScreenRoutes.AUTOMATION_RULES) { AutomationRulesScreen(viewModel) }
              composable(ScreenRoutes.TASK_LOGS) { TaskLogsScreen(taskLogsViewModel) }
              composable(ScreenRoutes.COPILOT) { CopilotScreen(viewModel) }
              composable(ScreenRoutes.PROFILE) { ProfileScreen(viewModel) }
              composable(ScreenRoutes.CAREER_NOTES) { CareerNotesScreen(careerNotesViewModel) }
            }

            // Quick Action Execution Feedback Banner
            val quickActionTelemetry by viewModel.quickActionTelemetry.collectAsState()
            val hasScreenLocalFab = currentRoute == ScreenRoutes.CAREER_NOTES ||
                currentRoute == ScreenRoutes.COMPANY_BOOKMARKS ||
                currentRoute == ScreenRoutes.AUTOMATION_RULES ||
                currentRoute == ScreenRoutes.TASK_LOGS

            QuickActionFeedbackBanner(
              telemetry = quickActionTelemetry,
              onDismiss = { viewModel.dismissQuickActionTelemetry() },
              modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = if (hasScreenLocalFab) 154.dp else 84.dp)
            )

            // Quick Action Floating Button (1-tap trigger for predefined automations)
            val isExecutingQuickAction by viewModel.isQuickActionExecuting.collectAsState()
            val executingQuickAction by viewModel.executingQuickAction.collectAsState()

            QuickActionFloatingButton(
              isExecuting = isExecutingQuickAction,
              executingAction = executingQuickAction,
              onTriggerAction = { actionType -> viewModel.triggerQuickAction(actionType) },
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                  bottom = if (hasScreenLocalFab) 92.dp else 20.dp,
                  end = 20.dp
                )
            )
          }
        }

        TitanGlobalDialogHost(
          viewModel = viewModel,
          careerNotesViewModel = careerNotesViewModel
        )
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    handleNotificationIntent(intent)
  }

  private fun handleNotificationIntent(intent: Intent?) {
    if (intent == null) return
    val targetScreen = intent.getStringExtra(TitanNotificationManager.EXTRA_TARGET_SCREEN)
    val itemName = intent.getStringExtra(TitanNotificationManager.EXTRA_ITEM_NAME)

    when (targetScreen) {
      "JOBS" -> {
        viewModel.navigateTo(TitanScreen.JOBS)
      }
      "COMPANIES" -> {
        viewModel.navigateTo(TitanScreen.COMPANIES)
        if (!itemName.isNullOrBlank()) {
          viewModel.selectCompanyForIntel(itemName)
        }
      }
      "APPLICATIONS" -> {
        viewModel.navigateTo(TitanScreen.APPLICATIONS)
      }
    }
  }
}


