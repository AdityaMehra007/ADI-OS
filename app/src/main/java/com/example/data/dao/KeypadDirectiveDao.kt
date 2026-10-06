package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.KeypadDirectiveEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KeypadDirectiveDao {

  @Query("SELECT * FROM keypad_directives ORDER BY CAST(code AS INTEGER) ASC")
  fun getAllDirectives(): Flow<List<KeypadDirectiveEntity>>

  @Query("SELECT * FROM keypad_directives WHERE isPinned = 1 ORDER BY CAST(code AS INTEGER) ASC")
  fun getPinnedDirectives(): Flow<List<KeypadDirectiveEntity>>

  @Query("SELECT * FROM keypad_directives WHERE code = :code LIMIT 1")
  suspend fun getDirectiveByCode(code: String): KeypadDirectiveEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(directives: List<KeypadDirectiveEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(directive: KeypadDirectiveEntity)

  @Update
  suspend fun update(directive: KeypadDirectiveEntity)

  @Query("UPDATE keypad_directives SET isPinned = :isPinned WHERE code = :code")
  suspend fun setPinned(code: String, isPinned: Boolean)

  @Query("UPDATE keypad_directives SET executionCount = executionCount + 1, lastTriggeredTimestamp = :timestamp WHERE code = :code")
  suspend fun recordExecution(code: String, timestamp: Long)

  @Query("UPDATE keypad_directives SET customNotes = :notes WHERE code = :code")
  suspend fun updateCustomNotes(code: String, notes: String)

  @Query("SELECT COUNT(*) FROM keypad_directives")
  suspend fun getCount(): Int
}
