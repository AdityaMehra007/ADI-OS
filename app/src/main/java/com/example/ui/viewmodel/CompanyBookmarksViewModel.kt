package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.TitanApplication
import com.example.data.TitanDatabase
import com.example.data.model.CompanyBookmark
import com.example.repository.TitanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel managing Company Bookmarks state, tier/priority filtering,
 * search querying, and Room persistence.
 */
class CompanyBookmarksViewModel(
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

  val allBookmarks: StateFlow<List<CompanyBookmark>> = repository.getAllCompanyBookmarks()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedTier = MutableStateFlow("ALL")
  val selectedTier: StateFlow<String> = _selectedTier.asStateFlow()

  private val _selectedPriority = MutableStateFlow("ALL")
  val selectedPriority: StateFlow<String> = _selectedPriority.asStateFlow()

  val filteredBookmarks: StateFlow<List<CompanyBookmark>> = combine(
    allBookmarks,
    _searchQuery,
    _selectedTier,
    _selectedPriority
  ) { bookmarks, query, tier, priority ->
    bookmarks.filter { bm ->
      val matchesTier = tier == "ALL" || bm.tier.equals(tier, ignoreCase = true)
      val matchesPriority = priority == "ALL" || bm.priority.equals(priority, ignoreCase = true)
      val matchesQuery = query.isBlank() ||
        bm.companyName.contains(query, ignoreCase = true) ||
        bm.personalNotes.contains(query, ignoreCase = true) ||
        bm.industry.contains(query, ignoreCase = true) ||
        bm.targetRole.contains(query, ignoreCase = true)
      matchesTier && matchesPriority && matchesQuery
    }.sortedWith(
      compareByDescending<CompanyBookmark> {
        when (it.priority.uppercase()) {
          "DREAM" -> 4
          "HIGH" -> 3
          "MEDIUM" -> 2
          "WATCH" -> 1
          else -> 0
        }
      }.thenByDescending { it.bookmarkedAt }
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setSelectedTier(tier: String) {
    _selectedTier.value = tier
  }

  fun setSelectedPriority(priority: String) {
    _selectedPriority.value = priority
  }

  fun addBookmark(
    companyId: String,
    companyName: String,
    logoEmoji: String = "🏢",
    industry: String = "",
    tier: String = "S",
    priority: String = "HIGH",
    personalNotes: String = "",
    targetRole: String = "",
    alertOnNewJobs: Boolean = true
  ) {
    viewModelScope.launch {
      val bookmark = CompanyBookmark(
        companyId = companyId.trim(),
        companyName = companyName.trim(),
        logoEmoji = logoEmoji.ifBlank { "🏢" },
        industry = industry.trim(),
        tier = tier.trim().uppercase(),
        priority = priority.trim().uppercase(),
        personalNotes = personalNotes.trim(),
        targetRole = targetRole.trim(),
        bookmarkedAt = System.currentTimeMillis(),
        alertOnNewJobs = alertOnNewJobs
      )
      repository.insertCompanyBookmark(bookmark)
    }
  }

  fun updateNotes(companyId: String, notes: String) {
    viewModelScope.launch {
      repository.updateCompanyBookmarkNotes(companyId, notes.trim())
    }
  }

  fun toggleJobAlerts(bookmark: CompanyBookmark) {
    viewModelScope.launch {
      val updated = bookmark.copy(alertOnNewJobs = !bookmark.alertOnNewJobs)
      repository.insertCompanyBookmark(updated)
    }
  }

  fun removeBookmark(companyId: String) {
    viewModelScope.launch {
      repository.deleteCompanyBookmark(companyId)
    }
  }
}
