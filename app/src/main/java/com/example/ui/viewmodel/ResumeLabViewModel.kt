package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.TitanApplication
import com.example.data.TitanDatabase
import com.example.data.model.MockResumeDocument
import com.example.data.model.ParsedResumeDocument
import com.example.data.model.ResumeOptimizationFeedback
import com.example.data.model.ResumeVariation
import com.example.data.model.VerifiedFact
import com.example.repository.TitanRepository
import com.example.service.LocalDocumentParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Domain ViewModel focused on Resume Optimization, ATS Scans,
 * Bullet Rewriting, and Resume Variation versioning.
 */
class ResumeLabViewModel(
  private val repository: TitanRepository
) : ViewModel() {

  constructor(application: Application) : this(
    (application as? TitanApplication)?.repository ?: TitanRepository(
      (application as? TitanApplication)?.database ?: TitanDatabase.getDatabase(
        application,
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
      )
    )
  )

  val allResumeVariations: StateFlow<List<ResumeVariation>> = repository.allResumeVariationsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val primaryResumeVariation: StateFlow<ResumeVariation?> = repository.primaryResumeVariationFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val verifiedFacts: StateFlow<List<VerifiedFact>> = repository.verifiedFactsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _availableMockDocs = MutableStateFlow(LocalDocumentParser.getAvailableMockDocuments())
  val availableMockDocs: StateFlow<List<MockResumeDocument>> = _availableMockDocs.asStateFlow()

  private val _activeFeedback = MutableStateFlow<ResumeOptimizationFeedback?>(null)
  val activeFeedback: StateFlow<ResumeOptimizationFeedback?> = _activeFeedback.asStateFlow()

  private val _isAnalyzing = MutableStateFlow(false)
  val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

  fun runOptimizationAudit(
    parsedDoc: ParsedResumeDocument,
    targetRole: String = "Lead Strategy & Operations",
    targetCompany: String = "Zepto"
  ) {
    viewModelScope.launch {
      _isAnalyzing.value = true
      try {
        val feedback = repository.optimizeResumeDocument(
          parsedDoc = parsedDoc,
          targetRole = targetRole,
          targetCompany = targetCompany
        )
        _activeFeedback.value = feedback
      } finally {
        _isAnalyzing.value = false
      }
    }
  }

  fun setPrimaryVariation(id: String) {
    viewModelScope.launch {
      repository.resumeVariationRepository.setPrimary(id)
    }
  }

  fun saveResumeVariation(variation: ResumeVariation) {
    viewModelScope.launch {
      repository.resumeVariationRepository.insertOrUpdate(variation)
    }
  }
}
