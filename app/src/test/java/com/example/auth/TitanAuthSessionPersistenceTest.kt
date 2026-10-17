package com.example.auth

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TitanAuthSessionPersistenceTest {

  private lateinit var context: Context
  private val testDispatcher = StandardTestDispatcher()
  private val testScope = TestScope(testDispatcher)

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
    context.getSharedPreferences("titan_auth_session", Context.MODE_PRIVATE).edit().clear().commit()
  }

  @Test
  fun `unauthenticated session initially denies executive command center clearance`() {
    val authService = TitanAuthService(context, testScope)
    val accessResult = authService.requireExecutiveAccess()

    assertTrue(accessResult.isFailure)
    assertFalse(authService.isExecutiveAccessGranted.value)
    assertEquals(ExecutiveSessionState.Unauthenticated, authService.sessionState.value)
    assertNull(authService.currentUser.value)
  }

  @Test
  fun `persisted session is restored and provides executive clearance`() = runTest {
    val prefs = context.getSharedPreferences("titan_auth_session", Context.MODE_PRIVATE)
    prefs.edit()
      .putString("session_persisted_uid", "executive_test_uid_999")
      .putString("session_persisted_email", "executive@titan.ai")
      .putString("session_persisted_display_name", "Chief Executive Titan")
      .putBoolean("session_executive_clearance_active", true)
      .putLong("session_persisted_timestamp", 1700000000000L)
      .commit()

    val authService = TitanAuthService(context, testScope)

    // Should restore persisted session
    val currentUser = authService.currentUser.value
    assertNotNull(currentUser)
    assertEquals("executive_test_uid_999", currentUser?.uid)
    assertEquals("executive@titan.ai", currentUser?.email)
    assertEquals("Chief Executive Titan", currentUser?.displayName)
    assertTrue(authService.isExecutiveAccessGranted.value)
    assertEquals(AuthState.AUTHENTICATED, authService.authState.value)

    val accessResult = authService.requireExecutiveAccess()
    assertTrue(accessResult.isSuccess)
    assertEquals("executive_test_uid_999", accessResult.getOrNull()?.uid)
  }

  @Test
  fun `signOut clears persisted session and revokes executive clearance`() = runTest {
    val prefs = context.getSharedPreferences("titan_auth_session", Context.MODE_PRIVATE)
    prefs.edit()
      .putString("session_persisted_uid", "executive_test_uid_999")
      .putBoolean("session_executive_clearance_active", true)
      .commit()

    val authService = TitanAuthService(context, testScope)
    assertTrue(authService.isExecutiveAccessGranted.value)

    authService.signOut()

    assertFalse(authService.isExecutiveAccessGranted.value)
    assertNull(authService.currentUser.value)
    assertEquals(AuthState.UNAUTHENTICATED, authService.authState.value)
    assertEquals(ExecutiveSessionState.Unauthenticated, authService.sessionState.value)
    assertFalse(prefs.getBoolean("session_executive_clearance_active", false))
    assertNull(prefs.getString("session_persisted_uid", null))
  }

  @Test
  fun `dispose unregisters listener safely without exceptions`() {
    val authService = TitanAuthService(context, testScope)
    authService.dispose()
  }
}
