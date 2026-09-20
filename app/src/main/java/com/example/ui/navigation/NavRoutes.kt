package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.viewmodel.TitanScreen

/**
 * Primary navigation destinations featured in the main Bottom Navigation.
 */
sealed class MainNavDestination(
  val route: String,
  val title: String,
  val icon: ImageVector,
  val testTag: String,
  val associatedTitanScreen: TitanScreen
) {
  object CareerStrategy : MainNavDestination(
    route = ScreenRoutes.CAREER_STRATEGY,
    title = "Career Strategy",
    icon = Icons.AutoMirrored.Filled.TrendingUp,
    testTag = "nav_tab_career_strategy",
    associatedTitanScreen = TitanScreen.CAREER_STRATEGY
  )

  object CompanyIntelligence : MainNavDestination(
    route = ScreenRoutes.COMPANY_INTELLIGENCE,
    title = "Company Intelligence",
    icon = Icons.Default.Business,
    testTag = "nav_tab_company_intelligence",
    associatedTitanScreen = TitanScreen.COMPANIES
  )

  object JobAutomation : MainNavDestination(
    route = ScreenRoutes.JOB_AUTOMATION,
    title = "Job Automation",
    icon = Icons.Default.SmartToy,
    testTag = "nav_tab_job_automation",
    associatedTitanScreen = TitanScreen.AUTOMATION_RULES
  )

  companion object {
    val items = listOf(
      CareerStrategy,
      CompanyIntelligence,
      JobAutomation
    )

    fun fromRoute(route: String?): MainNavDestination? {
      return items.find { it.route == route }
    }
  }
}

/**
 * Route registry for all destinations hosted by the NavHost.
 */
object ScreenRoutes {
  const val CAREER_STRATEGY = "career_strategy"
  const val COMPANY_INTELLIGENCE = "company_intelligence"
  const val JOB_AUTOMATION = "job_automation"

  // Extended routes for deep-linking and auxiliary navigation
  const val DASHBOARD = "dashboard"
  const val COMMAND_CENTER = "command_center"
  const val JOBS = "jobs"
  const val COMPANIES = "companies"
  const val COMPANY_BOOKMARKS = "company_bookmarks"
  const val APPLICATIONS = "applications"
  const val INTERVIEWS = "interviews"
  const val SKILLS_PROJECTS = "skills_projects"
  const val NETWORK = "network"
  const val GOALS = "goals"
  const val AGENTS = "agents"
  const val AUTOMATION_RULES = "automation_rules"
  const val TASK_LOGS = "task_logs"
  const val COPILOT = "copilot"
  const val PROFILE = "profile"
  const val CAREER_NOTES = "career_notes"

  fun screenToRoute(screen: TitanScreen): String {
    return when (screen) {
      TitanScreen.CAREER_STRATEGY -> CAREER_STRATEGY
      TitanScreen.COMPANIES -> COMPANY_INTELLIGENCE
      TitanScreen.AUTOMATION_RULES -> JOB_AUTOMATION
      TitanScreen.DASHBOARD -> DASHBOARD
      TitanScreen.COMMAND_CENTER -> COMMAND_CENTER
      TitanScreen.JOBS -> JOBS
      TitanScreen.COMPANY_BOOKMARKS -> COMPANY_BOOKMARKS
      TitanScreen.APPLICATIONS -> APPLICATIONS
      TitanScreen.INTERVIEWS -> INTERVIEWS
      TitanScreen.SKILLS_PROJECTS -> SKILLS_PROJECTS
      TitanScreen.NETWORK -> NETWORK
      TitanScreen.GOALS -> GOALS
      TitanScreen.AGENTS -> AGENTS
      TitanScreen.TASK_LOGS -> TASK_LOGS
      TitanScreen.COPILOT -> COPILOT
      TitanScreen.PROFILE -> PROFILE
      TitanScreen.CAREER_NOTES -> CAREER_NOTES
    }
  }

  fun routeToScreen(route: String?): TitanScreen? {
    return when (route) {
      CAREER_STRATEGY -> TitanScreen.CAREER_STRATEGY
      COMPANY_INTELLIGENCE, COMPANIES -> TitanScreen.COMPANIES
      JOB_AUTOMATION, AUTOMATION_RULES -> TitanScreen.AUTOMATION_RULES
      DASHBOARD -> TitanScreen.DASHBOARD
      COMMAND_CENTER -> TitanScreen.COMMAND_CENTER
      JOBS -> TitanScreen.JOBS
      COMPANY_BOOKMARKS -> TitanScreen.COMPANY_BOOKMARKS
      APPLICATIONS -> TitanScreen.APPLICATIONS
      INTERVIEWS -> TitanScreen.INTERVIEWS
      SKILLS_PROJECTS -> TitanScreen.SKILLS_PROJECTS
      NETWORK -> TitanScreen.NETWORK
      GOALS -> TitanScreen.GOALS
      AGENTS -> TitanScreen.AGENTS
      TASK_LOGS -> TitanScreen.TASK_LOGS
      COPILOT -> TitanScreen.COPILOT
      PROFILE -> TitanScreen.PROFILE
      CAREER_NOTES -> TitanScreen.CAREER_NOTES
      else -> null
    }
  }
}
