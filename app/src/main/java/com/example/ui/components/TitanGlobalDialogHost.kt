package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.viewmodel.CareerNotesViewModel
import com.example.ui.viewmodel.TitanViewModel

/**
 * Encapsulated host for global modal overlays and executive dialogs.
 * Decouples modal presentation logic from MainActivity root Scaffold.
 */
@Composable
fun TitanGlobalDialogHost(
  viewModel: TitanViewModel,
  careerNotesViewModel: CareerNotesViewModel
) {
  val isNotificationCenterOpen by viewModel.isNotificationCenterOpen.collectAsState()
  if (isNotificationCenterOpen) {
    NotificationCenterDialog(
      viewModel = viewModel,
      onDismissRequest = { viewModel.setNotificationCenterOpen(false) }
    )
  }

  val isCommandPaletteOpen by viewModel.isCommandPaletteOpen.collectAsState()
  if (isCommandPaletteOpen) {
    CommandPaletteDialog(
      viewModel = viewModel,
      onDismissRequest = { viewModel.setCommandPaletteOpen(false) }
    )
  }

  val isSalaryCalculatorOpen by viewModel.isSalaryCalculatorOpen.collectAsState()
  if (isSalaryCalculatorOpen) {
    SalaryCalculatorDialog(
      onDismissRequest = { viewModel.setSalaryCalculatorOpen(false) }
    )
  }

  val isCapabilityMatrixOpen by viewModel.isCapabilityMatrixOpen.collectAsState()
  if (isCapabilityMatrixOpen) {
    MasterCapabilityMatrixDialog(
      viewModel = viewModel,
      onDismissRequest = { viewModel.setCapabilityMatrixOpen(false) }
    )
  }

  val isScenarioSimulatorOpen by viewModel.isScenarioSimulatorOpen.collectAsState()
  if (isScenarioSimulatorOpen) {
    CareerScenarioSimulatorDialog(
      onDismissRequest = { viewModel.setScenarioSimulatorOpen(false) }
    )
  }

  val isMultiOfferComparatorOpen by viewModel.isMultiOfferComparatorOpen.collectAsState()
  if (isMultiOfferComparatorOpen) {
    MultiOfferComparatorDialog(
      onDismissRequest = { viewModel.setMultiOfferComparatorOpen(false) }
    )
  }

  val isAuthDialogOpen by viewModel.isAuthDialogOpen.collectAsState()
  if (isAuthDialogOpen) {
    FirebaseAuthDialog(
      viewModel = viewModel,
      onDismissRequest = { viewModel.setAuthDialogOpen(false) }
    )
  }

  val isResumeOptimizerOpen by viewModel.isResumeOptimizerOpen.collectAsState()
  if (isResumeOptimizerOpen) {
    ResumeOptimizationLabDialog(
      viewModel = viewModel,
      onDismiss = { viewModel.closeResumeOptimizer() }
    )
  }

  val isResumeHealthScannerOpen by viewModel.isResumeHealthScannerOpen.collectAsState()
  if (isResumeHealthScannerOpen) {
    ResumeHealthScannerDialog(
      viewModel = viewModel,
      onDismiss = { viewModel.closeResumeHealthScanner() }
    )
  }

  val isResumeComparisonDialogOpen by viewModel.isResumeComparisonDialogOpen.collectAsState()
  if (isResumeComparisonDialogOpen) {
    ResumeJobComparisonDialog(
      viewModel = viewModel,
      onDismissRequest = { viewModel.closeResumeComparison() }
    )
  }

  val isJobActionPlanOpen by viewModel.isJobActionPlanOpen.collectAsState()
  if (isJobActionPlanOpen) {
    JobActionPlanDialog(
      viewModel = viewModel,
      onDismiss = { viewModel.closeJobActionPlan() }
    )
  }

  val isCoverLetterGeneratorOpen by viewModel.isCoverLetterGeneratorOpen.collectAsState()
  if (isCoverLetterGeneratorOpen) {
    CoverLetterGeneratorDialog(
      viewModel = viewModel,
      onDismiss = { viewModel.closeCoverLetterGenerator() }
    )
  }

  val isReferralOutreachDialogOpen by viewModel.isReferralOutreachDialogOpen.collectAsState()
  if (isReferralOutreachDialogOpen) {
    ReferralOutreachGeneratorDialog(
      onDismissRequest = { viewModel.closeReferralOutreachDialog() }
    )
  }

  val isSalaryNegotiationDialogOpen by viewModel.isSalaryNegotiationDialogOpen.collectAsState()
  if (isSalaryNegotiationDialogOpen) {
    SalaryBenchmarkNegotiationDialog(
      viewModel = viewModel,
      onDismiss = { viewModel.closeSalaryNegotiationDialog() }
    )
  }

  val showWeeklyHealthReportDialog by viewModel.showWeeklyHealthReportDialog.collectAsState()
  if (showWeeklyHealthReportDialog) {
    WeeklyCareerHealthReportDialog(
      viewModel = viewModel,
      onDismissRequest = { viewModel.showWeeklyHealthReportDialog.value = false }
    )
  }

  val isCareerNoteSummaryDialogOpen by viewModel.isCareerNoteSummaryDialogOpen.collectAsState()
  val activeCareerNoteSummary by viewModel.activeCareerNoteSummary.collectAsState()
  if (isCareerNoteSummaryDialogOpen && activeCareerNoteSummary != null) {
    CareerNoteSummaryDialog(
      summary = activeCareerNoteSummary!!,
      onDismiss = { viewModel.dismissCareerNoteSummaryDialog() },
      onSaveAsNewNote = { title, content, company, role ->
        careerNotesViewModel.saveNote(
          title = title,
          category = "STRATEGY",
          content = content,
          targetCompany = company,
          targetRole = role,
          tags = "ExecutiveSummary, QuickAction, AI",
          isPinned = true
        )
      }
    )
  }
}
