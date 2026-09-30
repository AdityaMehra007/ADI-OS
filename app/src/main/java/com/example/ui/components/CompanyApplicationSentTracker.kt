package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.viewmodel.TitanViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data model representing the 'Application Sent' status for a target company,
 * designed for persistence inside browser localStorage as well as native state.
 */
data class CompanyApplicationSentStatus(
  val companyName: String,
  val isSent: Boolean = false,
  val role: String = "Strategy & Operations Lead",
  val sentDate: String = "",
  val channel: String = "Careers Portal",
  val notes: String = ""
) {
  fun toJson(): JSONObject {
    val obj = JSONObject()
    obj.put("companyName", companyName)
    obj.put("isSent", isSent)
    obj.put("role", role)
    obj.put("sentDate", sentDate)
    obj.put("channel", channel)
    obj.put("notes", notes)
    return obj
  }

  companion object {
    fun fromJson(obj: JSONObject): CompanyApplicationSentStatus {
      return CompanyApplicationSentStatus(
        companyName = obj.optString("companyName", "Unknown Company"),
        isSent = obj.optBoolean("isSent", false),
        role = obj.optString("role", "Strategy & Operations Lead"),
        sentDate = obj.optString("sentDate", ""),
        channel = obj.optString("channel", "Careers Portal"),
        notes = obj.optString("notes", "")
      )
    }

    fun listToJsonString(list: List<CompanyApplicationSentStatus>): String {
      val array = JSONArray()
      list.forEach { array.put(it.toJson()) }
      return array.toString()
    }

    fun listFromJsonString(jsonStr: String): List<CompanyApplicationSentStatus> {
      val result = mutableListOf<CompanyApplicationSentStatus>()
      if (jsonStr.isBlank()) return result
      try {
        val array = JSONArray(jsonStr)
        for (i in 0 until array.length()) {
          val obj = array.getJSONObject(i)
          result.add(fromJson(obj))
        }
      } catch (e: Exception) {
        // Fallback gracefully on parsing issue
      }
      return result
    }

    fun getDefaultInitialCompanies(): List<CompanyApplicationSentStatus> {
      val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
      return listOf(
        CompanyApplicationSentStatus("Google", isSent = true, role = "Associate Business Analyst", sentDate = today, channel = "Careers Portal", notes = "Applied for Bengaluru Campus"),
        CompanyApplicationSentStatus("Swiggy", isSent = true, role = "Strategy & Operations Lead", sentDate = today, channel = "Executive Referral", notes = "Instamart pod optimization resume attached"),
        CompanyApplicationSentStatus("Zepto", isSent = true, role = "Supply Chain & Ops Analyst", sentDate = today, channel = "Direct Outreach", notes = "Dark store Bellandur focus"),
        CompanyApplicationSentStatus("Bain & Company", isSent = false, role = "Associate Consultant", channel = "Target Networking", notes = "Preparing case interview materials"),
        CompanyApplicationSentStatus("McKinsey & Company", isSent = false, role = "Business Analyst", channel = "Careers Portal", notes = "Drafting customized problem-solving test answers"),
        CompanyApplicationSentStatus("Stripe", isSent = false, role = "Risk Operations Analyst", channel = "Careers Portal", notes = "Fintech scale-up interest"),
        CompanyApplicationSentStatus("Razorpay", isSent = true, role = "Product Operations Associate", sentDate = today, channel = "In-App Automation", notes = "Dispatched via Titan Pipeline"),
        CompanyApplicationSentStatus("Uber", isSent = false, role = "Operations Manager - Mobility", channel = "Direct Referral", notes = "Referral requested from alum"),
        CompanyApplicationSentStatus("CRED", isSent = false, role = "Growth & Operations Analyst", channel = "Careers Portal", notes = "High bar consumer fintech"),
        CompanyApplicationSentStatus("Flipkart", isSent = true, role = "Supply Chain Operations Lead", sentDate = today, channel = "Official Portal", notes = "Hub logistics background highlighted")
      )
    }
  }
}

/**
 * Android JavaScript Interface allowing bidirectional synchronization between
 * browser window.localStorage and native state.
 */
class LocalStorageTrackerBridge(
  private val onSyncFromBrowser: (List<CompanyApplicationSentStatus>) -> Unit,
  private val onNotifyStorageEvent: (String) -> Unit
) {
  @JavascriptInterface
  fun onTrackerStateUpdated(jsonString: String) {
    val parsed = CompanyApplicationSentStatus.listFromJsonString(jsonString)
    onSyncFromBrowser(parsed)
  }

  @JavascriptInterface
  fun onLocalStorageVerified(storedValue: String, byteLength: Int) {
    onNotifyStorageEvent("localStorage verified: $byteLength bytes stored.")
  }

  @JavascriptInterface
  fun logFromWeb(message: String) {
    android.util.Log.d("TitanLocalStorage", message)
  }
}

/**
 * Builds the interactive HTML/JS web application that directly uses
 * browser window.localStorage to monitor and persist 'Application Sent' status.
 */
fun buildCompanySentTrackerHtml(initialDataJson: String): String {
  val cleanJson = initialDataJson
    .replace("\\", "\\\\")
    .replace("\"", "\\\"")
    .replace("\n", "\\n")
    .replace("\r", "")

  val dollar = "$"

  return """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
  <title>Application Sent Tracker - localStorage</title>
  <style>
    :root {
      --bg-dark: #0A0D12;
      --card-bg: #121721;
      --card-elevated: #1A2233;
      --border-color: #26334D;
      --cyan: #00E5FF;
      --emerald: #00E676;
      --gold: #FFD600;
      --crimson: #FF5252;
      --text-primary: #F0F4F8;
      --text-secondary: #94A3B8;
      --text-muted: #64748B;
    }
    * { box-sizing: border-box; margin: 0; padding: 0; -webkit-tap-highlight-color: transparent; }
    body {
      background-color: var(--bg-dark);
      color: var(--text-primary);
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
      padding: 12px;
      font-size: 13px;
      line-height: 1.4;
    }
    .header-bar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
      padding-bottom: 8px;
      border-bottom: 1px solid var(--border-color);
    }
    .title-group h2 {
      font-size: 15px;
      font-weight: 700;
      color: var(--text-primary);
      display: flex;
      align-items: center;
      gap: 6px;
    }
    .storage-badge {
      display: inline-flex;
      align-items: center;
      gap: 4px;
      font-size: 10px;
      font-weight: 700;
      color: var(--emerald);
      background: rgba(0, 230, 118, 0.12);
      border: 1px solid rgba(0, 230, 118, 0.3);
      padding: 2px 6px;
      border-radius: 4px;
      letter-spacing: 0.5px;
    }
    .stats-card {
      background: var(--card-bg);
      border: 1px solid var(--border-color);
      border-radius: 10px;
      padding: 12px;
      margin-bottom: 12px;
    }
    .stats-row {
      display: flex;
      justify-content: space-around;
      text-align: center;
      margin-bottom: 8px;
    }
    .stat-box .num {
      font-size: 18px;
      font-weight: 800;
    }
    .stat-box .label {
      font-size: 10px;
      color: var(--text-secondary);
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
    .progress-bar-bg {
      background: var(--card-elevated);
      height: 6px;
      border-radius: 3px;
      overflow: hidden;
      margin-top: 6px;
    }
    .progress-bar-fill {
      height: 100%;
      background: linear-gradient(90deg, var(--cyan), var(--emerald));
      width: 0%;
      transition: width 0.3s ease;
    }
    .controls-row {
      display: flex;
      gap: 8px;
      margin-bottom: 12px;
    }
    .search-input {
      flex: 1;
      background: var(--card-elevated);
      border: 1px solid var(--border-color);
      color: var(--text-primary);
      padding: 8px 12px;
      border-radius: 8px;
      font-size: 12px;
      outline: none;
    }
    .search-input:focus {
      border-color: var(--cyan);
    }
    .filter-tabs {
      display: flex;
      gap: 6px;
      margin-bottom: 12px;
      overflow-x: auto;
      padding-bottom: 2px;
    }
    .filter-btn {
      background: var(--card-elevated);
      border: 1px solid var(--border-color);
      color: var(--text-secondary);
      padding: 5px 10px;
      border-radius: 6px;
      font-size: 11px;
      font-weight: 600;
      cursor: pointer;
      white-space: nowrap;
    }
    .filter-btn.active {
      background: var(--cyan);
      color: #000;
      border-color: var(--cyan);
    }
    .company-list {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    .company-item {
      background: var(--card-bg);
      border: 1px solid var(--border-color);
      border-radius: 8px;
      padding: 10px 12px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      transition: transform 0.22s cubic-bezier(0.2, 0, 0, 1), box-shadow 0.22s cubic-bezier(0.2, 0, 0, 1), border-color 0.22s ease, background-color 0.22s ease;
      cursor: pointer;
    }
    .company-item:hover {
      background: var(--card-elevated);
      border-color: rgba(0, 229, 255, 0.45);
      transform: translateY(-2px);
      box-shadow: 0 4px 14px rgba(0, 229, 255, 0.12);
    }
    .company-item.is-sent {
      border-color: rgba(0, 230, 118, 0.4);
      background: rgba(0, 230, 118, 0.03);
    }
    .company-item.is-sent:hover {
      background: rgba(0, 230, 118, 0.07);
      border-color: rgba(0, 230, 118, 0.65);
      box-shadow: 0 4px 14px rgba(0, 230, 118, 0.16);
    }
    .company-item:active {
      transform: translateY(0);
      box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
    }
    .company-info {
      flex: 1;
      min-width: 0;
      padding-right: 10px;
    }
    .company-name-row {
      display: flex;
      align-items: center;
      gap: 6px;
      margin-bottom: 2px;
    }
    .company-name {
      font-weight: 700;
      font-size: 14px;
      color: var(--text-primary);
    }
    .status-pill {
      font-size: 9px;
      font-weight: 700;
      padding: 2px 6px;
      border-radius: 4px;
      letter-spacing: 0.5px;
      text-transform: uppercase;
    }
    .status-pill.sent {
      background: rgba(0, 230, 118, 0.15);
      color: var(--emerald);
      border: 1px solid rgba(0, 230, 118, 0.3);
    }
    .status-pill.pending {
      background: rgba(255, 214, 0, 0.12);
      color: var(--gold);
      border: 1px solid rgba(255, 214, 0, 0.3);
    }
    .company-meta {
      font-size: 11px;
      color: var(--text-secondary);
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      align-items: center;
    }
    .company-meta span {
      display: inline-flex;
      align-items: center;
      gap: 3px;
    }
    .btn-toggle {
      border: none;
      padding: 6px 12px;
      border-radius: 6px;
      font-weight: 700;
      font-size: 11px;
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 4px;
      transition: transform 0.1s, background-color 0.2s;
    }
    .btn-toggle.mark-sent {
      background: var(--card-elevated);
      color: var(--text-secondary);
      border: 1px solid var(--border-color);
    }
    .btn-toggle.sent-done {
      background: rgba(0, 230, 118, 0.2);
      color: var(--emerald);
      border: 1px solid rgba(0, 230, 118, 0.4);
    }
    .btn-toggle:active {
      transform: scale(0.96);
    }
    .footer-actions {
      margin-top: 14px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-top: 10px;
      border-top: 1px solid var(--border-color);
    }
    .mini-btn {
      background: var(--card-elevated);
      border: 1px solid var(--border-color);
      color: var(--text-secondary);
      padding: 5px 9px;
      border-radius: 5px;
      font-size: 10px;
      cursor: pointer;
    }
    .mini-btn.accent {
      color: var(--cyan);
      border-color: rgba(0, 229, 255, 0.3);
    }
    .toast-popup {
      position: fixed;
      bottom: 16px;
      left: 50%;
      transform: translateX(-50%);
      background: #1E293B;
      border: 1px solid var(--cyan);
      color: var(--cyan);
      padding: 6px 12px;
      border-radius: 6px;
      font-size: 11px;
      font-weight: 600;
      opacity: 0;
      transition: opacity 0.3s;
      pointer-events: none;
      z-index: 100;
    }
    .toast-popup.visible {
      opacity: 1;
    }
  </style>
</head>
<body>
  <div class="header-bar">
    <div class="title-group">
      <h2>🏢 Company Sent Tracker</h2>
    </div>
    <div class="storage-badge" id="storageStatusBadge">
      ● localStorage ACTIVE
    </div>
  </div>

  <div class="stats-card">
    <div class="stats-row">
      <div class="stat-box">
        <div class="num" id="totalCount" style="color: var(--text-primary)">0</div>
        <div class="label">Companies</div>
      </div>
      <div class="stat-box">
        <div class="num" id="sentCount" style="color: var(--emerald)">0</div>
        <div class="label">Application Sent</div>
      </div>
      <div class="stat-box">
        <div class="num" id="pendingCount" style="color: var(--gold)">0</div>
        <div class="label">Pending Action</div>
      </div>
      <div class="stat-box">
        <div class="num" id="pctSent" style="color: var(--cyan)">0%</div>
        <div class="label">Sent Ratio</div>
      </div>
    </div>
    <div class="progress-bar-bg">
      <div class="progress-bar-fill" id="progressFill"></div>
    </div>
  </div>

  <div class="controls-row">
    <input type="text" class="search-input" id="searchBox" placeholder="Filter company name or role..." oninput="handleSearch(this.value)">
  </div>

  <div class="filter-tabs">
    <button class="filter-btn active" id="filterAll" onclick="setFilter('ALL')">All Companies (<span id="tabCountAll">0</span>)</button>
    <button class="filter-btn" id="filterSent" onclick="setFilter('SENT')">Application Sent (<span id="tabCountSent">0</span>)</button>
    <button class="filter-btn" id="filterPending" onclick="setFilter('PENDING')">Pending Review (<span id="tabCountPending">0</span>)</button>
  </div>

  <div class="company-list" id="companyList">
    <!-- Rendered dynamically from window.localStorage -->
  </div>

  <div class="footer-actions">
    <button class="mini-btn accent" onclick="verifyLocalStorage()">Verify localStorage Persistence</button>
    <button class="mini-btn" onclick="resetToInitial()">Reset Defaults</button>
  </div>

  <div class="toast-popup" id="toast"></div>

  <script>
    var STORAGE_KEY = 'titan_company_application_sent_tracker_v1';
    var currentFilter = 'ALL';
    var searchQuery = '';
    var trackerData = [];

    function showToast(msg) {
      var toast = document.getElementById('toast');
      toast.innerText = msg;
      toast.classList.add('visible');
      setTimeout(function() { toast.classList.remove('visible'); }, 2200);
    }

    function initTracker() {
      try {
        var stored = window.localStorage.getItem(STORAGE_KEY);
        if (stored && stored.trim().length > 2) {
          trackerData = JSON.parse(stored);
        } else {
          var initialSeed = "$cleanJson";
          trackerData = JSON.parse(initialSeed);
          saveToLocalStorage(false);
        }
      } catch (err) {
        console.error("Failed loading from localStorage:", err);
        try {
          trackerData = JSON.parse("$cleanJson");
        } catch(e) {
          trackerData = [];
        }
      }
      renderUI();
    }

    function saveToLocalStorage(notifyNative) {
      try {
        var jsonStr = JSON.stringify(trackerData);
        window.localStorage.setItem(STORAGE_KEY, jsonStr);
        if (notifyNative && window.AndroidBridge && typeof window.AndroidBridge.onTrackerStateUpdated === 'function') {
          window.AndroidBridge.onTrackerStateUpdated(jsonStr);
        }
      } catch (err) {
        console.error("Failed saving to localStorage:", err);
      }
    }

    function toggleSent(companyName) {
      var item = trackerData.find(function(c) {
        return c.companyName.toLowerCase() === companyName.toLowerCase();
      });
      if (item) {
        item.isSent = !item.isSent;
        if (item.isSent && !item.sentDate) {
          item.sentDate = new Date().toISOString().split('T')[0];
        }
        saveToLocalStorage(true);
        renderUI();
        showToast(item.isSent ? ("Marked " + item.companyName + " as Application Sent") : ("Marked " + item.companyName + " as Pending"));
      }
    }

    function setFilter(filter) {
      currentFilter = filter;
      document.getElementById('filterAll').classList.toggle('active', filter === 'ALL');
      document.getElementById('filterSent').classList.toggle('active', filter === 'SENT');
      document.getElementById('filterPending').classList.toggle('active', filter === 'PENDING');
      renderUI();
    }

    function handleSearch(val) {
      searchQuery = val.trim().toLowerCase();
      renderUI();
    }

    function verifyLocalStorage() {
      try {
        var stored = window.localStorage.getItem(STORAGE_KEY);
        var byteCount = stored ? new Blob([stored]).size : 0;
        var count = stored ? JSON.parse(stored).length : 0;
        showToast("localStorage verified: " + count + " companies (" + byteCount + " B)");
        if (window.AndroidBridge && typeof window.AndroidBridge.onLocalStorageVerified === 'function') {
          window.AndroidBridge.onLocalStorageVerified(stored, byteCount);
        }
      } catch (err) {
        showToast("localStorage error: " + err.message);
      }
    }

    function resetToInitial() {
      try {
        var initialSeed = "$cleanJson";
        trackerData = JSON.parse(initialSeed);
        saveToLocalStorage(true);
        renderUI();
        showToast("Reset tracker to default company pipeline");
      } catch (e) {
        console.error(e);
      }
    }

    function renderUI() {
      var listEl = document.getElementById('companyList');
      listEl.innerHTML = '';

      var total = trackerData.length;
      var sent = trackerData.filter(function(i) { return i.isSent; }).length;
      var pending = total - sent;
      var pct = total > 0 ? Math.round((sent / total) * 100) : 0;

      document.getElementById('totalCount').innerText = total;
      document.getElementById('sentCount').innerText = sent;
      document.getElementById('pendingCount').innerText = pending;
      document.getElementById('pctSent').innerText = pct + '%';
      document.getElementById('progressFill').style.width = pct + '%';

      document.getElementById('tabCountAll').innerText = total;
      document.getElementById('tabCountSent').innerText = sent;
      document.getElementById('tabCountPending').innerText = pending;

      var filtered = trackerData.filter(function(item) {
        var matchesFilter = (currentFilter === 'ALL') ||
                            (currentFilter === 'SENT' && item.isSent) ||
                            (currentFilter === 'PENDING' && !item.isSent);
        var matchesSearch = !searchQuery ||
                            item.companyName.toLowerCase().indexOf(searchQuery) !== -1 ||
                            (item.role && item.role.toLowerCase().indexOf(searchQuery) !== -1) ||
                            (item.channel && item.channel.toLowerCase().indexOf(searchQuery) !== -1);
        return matchesFilter && matchesSearch;
      });

      if (filtered.length === 0) {
        listEl.innerHTML = '<div style="text-align: center; padding: 24px; color: var(--text-muted); font-size: 12px;">No target companies match current filter.</div>';
        return;
      }

      filtered.forEach(function(item) {
        var itemEl = document.createElement('div');
        itemEl.className = 'company-item' + (item.isSent ? ' is-sent' : '');

        var statusPill = item.isSent
          ? '<span class="status-pill sent">Application Sent</span>'
          : '<span class="status-pill pending">Pending</span>';

        var buttonText = item.isSent ? '✓ Sent' : '+ Mark Sent';
        var buttonClass = item.isSent ? 'btn-toggle sent-done' : 'btn-toggle mark-sent';
        var dateHtml = (item.isSent && item.sentDate) ? ('<span>📅 ' + item.sentDate + '</span>') : '';

        itemEl.innerHTML =
          '<div class="company-info">' +
            '<div class="company-name-row">' +
              '<span class="company-name">' + item.companyName + '</span>' +
              statusPill +
            '</div>' +
            '<div class="company-meta">' +
              '<span>🎯 ' + (item.role || 'Strategy & Ops') + '</span>' +
              '<span>📡 ' + (item.channel || 'Direct') + '</span>' +
              dateHtml +
            '</div>' +
          '</div>' +
          '<button class="' + buttonClass + '" onclick="toggleSent(\'' + item.companyName.replace(/'/g, "\\'") + '\')">' +
            buttonText +
          '</button>';

        listEl.appendChild(itemEl);
      });
    }

    window.addEventListener('DOMContentLoaded', initTracker);
  </script>
</body>
</html>
  """.trimIndent()
}

/**
 * Interactive WebView Composable that renders the browser localStorage state tracker.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CompanyApplicationSentTrackerWebView(
  initialItems: List<CompanyApplicationSentStatus>,
  onStatusUpdated: (List<CompanyApplicationSentStatus>) -> Unit,
  onStorageVerified: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val initialJson = remember(initialItems) {
    CompanyApplicationSentStatus.listToJsonString(initialItems)
  }
  val htmlContent = remember(initialJson) {
    buildCompanySentTrackerHtml(initialJson)
  }

  AndroidView(
    factory = { ctx ->
      WebView(ctx).apply {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        setBackgroundColor(android.graphics.Color.parseColor("#0A0D12"))
        webViewClient = WebViewClient()
        addJavascriptInterface(
          LocalStorageTrackerBridge(
            onSyncFromBrowser = { updatedList ->
              onStatusUpdated(updatedList)
            },
            onNotifyStorageEvent = { msg ->
              onStorageVerified(msg)
            }
          ),
          "AndroidBridge"
        )
        loadDataWithBaseURL("https://titan.local", htmlContent, "text/html", "UTF-8", null)
      }
    },
    update = { _ -> },
    modifier = modifier
  )
}

/**
 * Main Persistent State Tracker Card embedded into the Application Hub (ApplicationsScreen).
 * Provides both interactive browser localStorage integration and native Android status controls.
 */
@Composable
fun CompanyApplicationSentTrackerCard(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val applications by viewModel.applications.collectAsState()

  val prefs = remember(context) {
    context.getSharedPreferences("titan_app_sent_tracker_prefs", Context.MODE_PRIVATE)
  }

  var trackerItems by remember {
    val savedJson = prefs.getString("stored_company_sent_data", null)
    val initialList = if (!savedJson.isNullOrBlank()) {
      CompanyApplicationSentStatus.listFromJsonString(savedJson)
    } else {
      val defaultItems = CompanyApplicationSentStatus.getDefaultInitialCompanies().toMutableList()
      applications.forEach { app ->
        val exists = defaultItems.any { it.companyName.equals(app.companyName, ignoreCase = true) }
        val isSent = app.status.equals("APPLIED", ignoreCase = true) ||
                     app.status.equals("ASSESSMENT", ignoreCase = true) ||
                     app.status.equals("INTERVIEW", ignoreCase = true) ||
                     app.status.equals("OFFER", ignoreCase = true)
        if (!exists) {
          defaultItems.add(
            CompanyApplicationSentStatus(
              companyName = app.companyName,
              isSent = isSent,
              role = app.roleTitle,
              sentDate = app.dateApplied.ifBlank { "2026-09-28" },
              channel = "Official Pipeline"
            )
          )
        }
      }
      defaultItems
    }
    mutableStateOf(initialList)
  }

  var isExpanded by remember { mutableStateOf(true) }
  var viewMode by remember { mutableStateOf("WEB_LOCALSTORAGE") }
  var verificationToast by remember { mutableStateOf<String?>(null) }
  var showAddDialog by remember { mutableStateOf(false) }

  var newCompanyName by remember { mutableStateOf("") }
  var newRoleTitle by remember { mutableStateOf("") }
  var newIsSent by remember { mutableStateOf(false) }

  fun persistState(updated: List<CompanyApplicationSentStatus>) {
    trackerItems = updated
    coroutineScope.launch(Dispatchers.IO) {
      prefs.edit().putString("stored_company_sent_data", CompanyApplicationSentStatus.listToJsonString(updated)).apply()
    }
  }

  val totalTracked = trackerItems.size
  val sentCount = trackerItems.count { it.isSent }
  val pendingCount = totalTracked - sentCount
  val completionRate = if (totalTracked > 0) (sentCount.toFloat() / totalTracked) else 0f

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("application_hub_sent_tracker"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(TitanCyan.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.AutoMirrored.Filled.Send,
              contentDescription = "Application Sent Tracker",
              tint = TitanCyan,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Application Sent State Tracker",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanEmerald.copy(alpha = 0.2f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "localStorage Active",
                  style = MaterialTheme.typography.labelSmall,
                  color = TitanEmerald,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp
                )
              }
            }
            Text(
              text = "Hub progress stored directly in browser localStorage",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondaryDark,
              fontSize = 11.sp
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier.testTag("expand_toggle_sent_tracker")
          ) {
            Icon(
              if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
              contentDescription = if (isExpanded) "Collapse" else "Expand",
              tint = TextSecondaryDark
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("Target Companies", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 10.sp)
          Text("$totalTracked Tracked", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
        }
        Column {
          Text("Application Sent", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 10.sp)
          Text("$sentCount Sent", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TitanEmerald)
        }
        Column {
          Text("Pending Review", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 10.sp)
          Text("$pendingCount Pending", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TitanGold)
        }
        Column(horizontalAlignment = Alignment.End) {
          Text("Sent Rate", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 10.sp)
          Text("${(completionRate * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TitanCyan)
        }
      }

      Spacer(modifier = Modifier.height(6.dp))
      LinearProgressIndicator(
        progress = { completionRate },
        modifier = Modifier
          .fillMaxWidth()
          .height(4.dp)
          .clip(RoundedCornerShape(2.dp)),
        color = TitanEmerald,
        trackColor = SlateElevated
      )

      AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Column {
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Button(
                onClick = { viewMode = "WEB_LOCALSTORAGE" },
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (viewMode == "WEB_LOCALSTORAGE") TitanCyan else SlateElevated,
                  contentColor = if (viewMode == "WEB_LOCALSTORAGE") Color.Black else TextSecondaryDark
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                  .height(30.dp)
                  .testTag("mode_web_localstorage_btn")
              ) {
                Icon(Icons.Default.Web, contentDescription = null, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Web localStorage View", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = { viewMode = "NATIVE_VIEW" },
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (viewMode == "NATIVE_VIEW") TitanCyan else SlateElevated,
                  contentColor = if (viewMode == "NATIVE_VIEW") Color.Black else TextSecondaryDark
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                  .height(30.dp)
                  .testTag("mode_native_view_btn")
              ) {
                Icon(Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Native Quick View", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              OutlinedButton(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TitanCyan),
                border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                  .height(30.dp)
                  .testTag("add_company_tracker_button")
              ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(2.dp))
                Text("Add", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          if (viewMode == "WEB_LOCALSTORAGE") {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(440.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                .testTag("company_sent_tracker_webview")
            ) {
              CompanyApplicationSentTrackerWebView(
                initialItems = trackerItems,
                onStatusUpdated = { updatedList ->
                  persistState(updatedList)
                },
                onStorageVerified = { message ->
                  verificationToast = message
                },
                modifier = Modifier.fillMaxWidth().height(440.dp)
              )
            }
          } else {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SlateElevated)
                .padding(10.dp)
                .testTag("native_sent_tracker_list"),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              trackerItems.forEach { item ->
                val sentSubtitle = buildString {
                  append(item.role)
                  append(" • ")
                  append(item.channel)
                  if (item.isSent && item.sentDate.isNotBlank()) {
                    append(" • Sent: ")
                    append(item.sentDate)
                  }
                }

                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (item.isSent) TitanEmerald.copy(alpha = 0.08f) else SlateCard)
                    .border(
                      1.dp,
                      if (item.isSent) TitanEmerald.copy(alpha = 0.3f) else SlateBorder,
                      RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = item.companyName,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark,
                        fontSize = 13.sp
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(4.dp))
                          .background(
                            if (item.isSent) TitanEmerald.copy(alpha = 0.2f) else TitanGold.copy(alpha = 0.2f)
                          )
                          .padding(horizontal = 5.dp, vertical = 2.dp)
                      ) {
                        Text(
                          text = if (item.isSent) "Application Sent" else "Pending",
                          color = if (item.isSent) TitanEmerald else TitanGold,
                          fontWeight = FontWeight.Bold,
                          fontSize = 9.sp
                        )
                      }
                    }
                    Text(
                      text = sentSubtitle,
                      style = MaterialTheme.typography.labelSmall,
                      color = TextSecondaryDark,
                      fontSize = 10.sp
                    )
                  }

                  val testTagKey = "toggle_sent_status_" + item.companyName.lowercase().replace(" ", "_")
                  Switch(
                    checked = item.isSent,
                    onCheckedChange = { checked ->
                      val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                      val updated = trackerItems.map { curr ->
                        if (curr.companyName.equals(item.companyName, ignoreCase = true)) {
                          curr.copy(isSent = checked, sentDate = if (checked) today else "")
                        } else curr
                      }
                      persistState(updated)
                    },
                    colors = SwitchDefaults.colors(
                      checkedThumbColor = TitanEmerald,
                      checkedTrackColor = TitanEmerald.copy(alpha = 0.3f),
                      uncheckedThumbColor = TextMutedDark,
                      uncheckedTrackColor = SlateCard
                    ),
                    modifier = Modifier.testTag(testTagKey)
                  )
                }
              }
            }
          }

          if (verificationToast != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(TitanEmerald.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TitanEmerald, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = verificationToast.orEmpty(),
                color = TitanEmerald,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }
    }
  }

  if (showAddDialog) {
    AlertDialog(
      onDismissRequest = { showAddDialog = false },
      containerColor = SlateCard,
      title = {
        Text("Track Target Company", color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            "Add a company to monitor 'Application Sent' status in browser localStorage.",
            color = TextSecondaryDark,
            fontSize = 12.sp
          )
          OutlinedTextField(
            value = newCompanyName,
            onValueChange = { newCompanyName = it },
            label = { Text("Company Name (e.g. OpenAI, Tesla)") },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("dialog_new_company_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )
          OutlinedTextField(
            value = newRoleTitle,
            onValueChange = { newRoleTitle = it },
            label = { Text("Role Title (e.g. Operations Lead)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = TitanCyan,
              unfocusedBorderColor = SlateBorder,
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            )
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Already Sent Application?", color = TextPrimaryDark, fontSize = 12.sp)
            Switch(
              checked = newIsSent,
              onCheckedChange = { newIsSent = it },
              colors = SwitchDefaults.colors(
                checkedThumbColor = TitanEmerald,
                checkedTrackColor = TitanEmerald.copy(alpha = 0.3f)
              )
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newCompanyName.isNotBlank()) {
              val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
              val newItem = CompanyApplicationSentStatus(
                companyName = newCompanyName.trim(),
                isSent = newIsSent,
                role = newRoleTitle.ifBlank { "Strategy & Operations Lead" },
                sentDate = if (newIsSent) today else "",
                channel = "Direct Outreach"
              )
              val updated = trackerItems + newItem
              persistState(updated)
              newCompanyName = ""
              newRoleTitle = ""
              newIsSent = false
              showAddDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = Color.Black),
          shape = RoundedCornerShape(6.dp)
        ) {
          Text("Add to Tracker", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddDialog = false }) {
          Text("Cancel", color = TextSecondaryDark)
        }
      }
    )
  }
}
