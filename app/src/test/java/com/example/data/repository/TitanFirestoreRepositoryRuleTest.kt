package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.FirestoreDirectiveExecution
import com.example.data.model.FirestorePinnedDirective
import com.example.data.model.FirestoreUserProfile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class TitanFirestoreRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun saveAndObserveUserProfile_authenticatedOwner_succeeds() {
    runBlocking {
      val uid = signInTestUser("alice_titan@example.com")
      val repository = TitanFirestoreRepository(firestore)

      withTimeout(5000L) {
        repository.saveUserProfile(
          displayName = "Alice Apex",
          email = "alice_titan@example.com",
          tier = "APEX_FOUNDER",
          targetCTC = 150.0,
          currentTitle = "Chief Executive"
        )
      }

      val profile = withTimeout(5000L) {
        repository.observeUserProfile().first { it: FirestoreUserProfile? -> it != null }
      }

      assertNotNull(profile)
      assertEquals(uid, profile?.userId)
      assertEquals("Alice Apex", profile?.displayName)
      assertEquals("APEX_FOUNDER", profile?.tier)
    }
  }

  @Test
  fun recordAndObserveExecutions_authenticatedOwner_succeeds() {
    runBlocking {
      signInTestUser("alice_exec@example.com")
      val repository = TitanFirestoreRepository(firestore)

      withTimeout(5000L) {
        repository.recordExecution(
          id = "exec_test_01",
          directiveCode = "01",
          directiveTitle = "JD Decompiler",
          category = "CAREER",
          latencyMs = 12L,
          status = "COMPLETED",
          summary = "Decompiled JD with 98% ATS alignment score."
        )
      }

      val executions = withTimeout(5000L) {
        repository.observeRecentExecutions().first { list: List<FirestoreDirectiveExecution> ->
          list.any { it.id == "exec_test_01" }
        }
      }

      assertTrue(executions.any { it.directiveCode == "01" && it.directiveTitle == "JD Decompiler" })
    }
  }

  @Test
  fun pinAndUnpinDirective_authenticatedOwner_succeeds() {
    runBlocking {
      signInTestUser("alice_pin@example.com")
      val repository = TitanFirestoreRepository(firestore)

      withTimeout(5000L) {
        repository.pinDirective("18", "Dark Store Daily Health Audit")
      }

      val pinnedList = withTimeout(5000L) {
        repository.observePinnedDirectives().first { list: List<FirestorePinnedDirective> ->
          list.any { it.directiveCode == "18" }
        }
      }

      assertTrue(pinnedList.any { it.directiveCode == "18" })

      withTimeout(5000L) {
        repository.unpinDirective("18")
      }
    }
  }

  @Test
  fun unauthenticatedAccess_failsWithIllegalStateException() {
    runBlocking {
      auth.signOut()
      val repository = TitanFirestoreRepository(firestore)

      assertThrows(IllegalStateException::class.java) {
        repository.requireUserId()
      }
    }
  }
}
