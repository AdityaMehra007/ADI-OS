package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.TitanApplication
import com.example.data.TitanDatabase
import com.example.data.model.CareerNote
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
import java.util.UUID

/**
 * ViewModel managing Career Notes state, filtering by category/search query,
 * and database persistence operations in Room.
 */
class CareerNotesViewModel(
  private val repository: TitanRepository
) : ViewModel() {

  // Secondary constructor for ViewModelProvider instantiation with Application context
  constructor(application: Application) : this(
    (application as? TitanApplication)?.repository ?: TitanRepository(
      (application as? TitanApplication)?.database ?: TitanDatabase.getDatabase(
        application,
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
      )
    )
  )

  val allNotes: StateFlow<List<CareerNote>> = repository.getAllCareerNotes()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedCategory = MutableStateFlow("ALL")
  val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

  val filteredNotes: StateFlow<List<CareerNote>> = combine(
    allNotes,
    _searchQuery,
    _selectedCategory
  ) { notes, query, category ->
    notes.filter { note ->
      val matchesCategory = category == "ALL" || note.category.equals(category, ignoreCase = true)
      val matchesQuery = query.isBlank() ||
        note.title.contains(query, ignoreCase = true) ||
        note.content.contains(query, ignoreCase = true) ||
        note.tags.contains(query, ignoreCase = true) ||
        note.targetCompany.contains(query, ignoreCase = true) ||
        note.targetRole.contains(query, ignoreCase = true)
      matchesCategory && matchesQuery
    }.sortedWith(
      compareByDescending<CareerNote> { it.isPinned }
        .thenByDescending { it.updatedAt }
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setCategory(category: String) {
    _selectedCategory.value = category
  }

  fun saveNote(
    title: String,
    category: String,
    content: String,
    targetCompany: String = "",
    targetRole: String = "",
    tags: String = "",
    isPinned: Boolean = false
  ) {
    viewModelScope.launch {
      val note = CareerNote(
        id = UUID.randomUUID().toString(),
        title = title.trim(),
        category = category.trim(),
        content = content.trim(),
        targetCompany = targetCompany.trim(),
        targetRole = targetRole.trim(),
        tags = tags.trim(),
        isPinned = isPinned,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
      )
      repository.insertCareerNote(note)
    }
  }

  fun updateNote(note: CareerNote) {
    viewModelScope.launch {
      repository.updateCareerNote(
        note.copy(updatedAt = System.currentTimeMillis())
      )
    }
  }

  fun deleteNote(id: String) {
    viewModelScope.launch {
      repository.deleteCareerNote(id)
    }
  }

  fun togglePin(id: String, currentPin: Boolean) {
    viewModelScope.launch {
      repository.toggleCareerNotePin(id, !currentPin)
    }
  }
}
