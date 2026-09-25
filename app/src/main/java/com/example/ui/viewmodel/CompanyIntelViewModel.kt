package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.TitanApplication
import com.example.ai.CompanyResearchFocus
import com.example.ai.CompanyResearchRequest
import com.example.ai.CompanyResearchResponse
import com.example.data.TitanDatabase
import com.example.data.model.Company
import com.example.data.model.CompanyBookmark
import com.example.data.model.CompanyIntelReport
import com.example.data.model.TargetCompany
import com.example.data.model.TargetCompanyWithMarketIntelligence
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
 * Domain ViewModel focused on Target Companies, Corporate Intelligence,
 * Live Search Grounded Dossiers, and Company Bookmarking.
 */
class CompanyIntelViewModel(
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

  val allCompanies: StateFlow<List<Company>> = repository.companiesFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val targetCompanies: StateFlow<List<TargetCompanyWithMarketIntelligence>> = repository.targetCompaniesWithIntelFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val bookmarks: StateFlow<List<CompanyBookmark>> = repository.getAllCompanyBookmarks()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedTierFilter = MutableStateFlow("ALL")
  val selectedTierFilter: StateFlow<String> = _selectedTierFilter.asStateFlow()

  private val _selectedCompanyForIntel = MutableStateFlow<Company?>(null)
  val selectedCompanyForIntel: StateFlow<Company?> = _selectedCompanyForIntel.asStateFlow()

  private val _activeIntelReport = MutableStateFlow<CompanyIntelReport?>(null)
  val activeIntelReport: StateFlow<CompanyIntelReport?> = _activeIntelReport.asStateFlow()

  private val _isResearchingCompany = MutableStateFlow(false)
  val isResearchingCompany: StateFlow<Boolean> = _isResearchingCompany.asStateFlow()

  val filteredCompanies: StateFlow<List<Company>> = combine(
    allCompanies,
    _searchQuery,
    _selectedTierFilter
  ) { companies, query, tier ->
    companies.filter { comp ->
      val matchesQuery = query.isBlank() ||
        comp.name.contains(query, ignoreCase = true) ||
        comp.industry.contains(query, ignoreCase = true) ||
        comp.subIndustry.contains(query, ignoreCase = true) ||
        comp.hqLocation.contains(query, ignoreCase = true)

      val matchesTier = tier == "ALL" || comp.tier.equals(tier, ignoreCase = true)
      matchesQuery && matchesTier
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setTierFilter(tier: String) {
    _selectedTierFilter.value = tier
  }

  fun selectCompanyForIntel(company: Company) {
    _selectedCompanyForIntel.value = company
    researchCompany(company.name, company)
  }

  fun researchCompany(
    companyName: String,
    company: Company? = null,
    focus: CompanyResearchFocus = CompanyResearchFocus.ALL
  ) {
    viewModelScope.launch {
      _isResearchingCompany.value = true
      try {
        val request = CompanyResearchRequest(
          companyName = companyName,
          focus = focus,
          company = company
        )
        val result = repository.geminiService.researchCompany(request)
        if (result.isSuccess) {
          val response = result.getOrNull()
          _activeIntelReport.value = response?.toCompanyIntelReport()
        }
      } finally {
        _isResearchingCompany.value = false
      }
    }
  }

  fun toggleCompanyBookmark(companyName: String, tier: String = "Tier-1", notes: String = "") {
    viewModelScope.launch {
      val existing = bookmarks.value.find { it.companyName.equals(companyName, ignoreCase = true) }
      if (existing != null) {
        repository.deleteCompanyBookmark(existing.companyId)
      } else {
        repository.insertCompanyBookmark(
          CompanyBookmark(
            companyId = "bm_${System.currentTimeMillis()}",
            companyName = companyName,
            tier = tier,
            personalNotes = notes,
            bookmarkedAt = System.currentTimeMillis()
          )
        )
      }
    }
  }
}
