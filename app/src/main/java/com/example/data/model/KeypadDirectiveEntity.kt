package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room SQLite entity to locally persist 25-Keypad executive directives,
 * execution frequency telemetry, pinned status, and custom operator notes across sessions.
 */
@Entity(
  tableName = "keypad_directives",
  indices = [
    Index(value = ["category"]),
    Index(value = ["isPinned"])
  ]
)
data class KeypadDirectiveEntity(
  @PrimaryKey val code: String,
  val id: Int,
  val title: String,
  val summary: String,
  val category: String,
  val iconEmoji: String,
  val isPinned: Boolean = false,
  val executionCount: Int = 0,
  val lastTriggeredTimestamp: Long = 0L,
  val customNotes: String = ""
)
