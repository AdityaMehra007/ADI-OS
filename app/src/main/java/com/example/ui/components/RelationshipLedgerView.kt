package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDarker
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanCrimson
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import com.example.ui.viewmodel.TitanViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Model representing a Company Application in the Relationship Ledger.
 * Directly serialized and persisted in browser window.localStorage and Android SharedPreferences.
 */
data class CompanyApplicationLedgerItem(
  val companyName: String,
  val status: String = "Applied", // "Targeted", "Applied", "Interviewing", "Offer", "Archived"
  val role: String = "Operations & Strategy Lead",
  val compensation: String = "₹28L - ₹34L CTC",
  val interviewStage: String = "Initial Screening",
  val contactLead: String = "Executive Referral",
  val notes: String = "",
  val lastUpdated: String = "",
  val isStarred: Boolean = true
) {
  fun toJson(): JSONObject {
    val obj = JSONObject()
    obj.put("companyName", companyName)
    obj.put("status", status)
    obj.put("role", role)
    obj.put("compensation", compensation)
    obj.put("interviewStage", interviewStage)
    obj.put("contactLead", contactLead)
    obj.put("notes", notes)
    obj.put("lastUpdated", lastUpdated)
    obj.put("isStarred", isStarred)
    return obj
  }

  companion object {
    fun fromJson(obj: JSONObject): CompanyApplicationLedgerItem {
      return CompanyApplicationLedgerItem(
        companyName = obj.optString("companyName", "Unknown Company"),
        status = obj.optString("status", "Applied"),
        role = obj.optString("role", "Strategy & Operations Lead"),
        compensation = obj.optString("compensation", "₹26L - ₹32L CTC"),
        interviewStage = obj.optString("interviewStage", "Initial Screening"),
        contactLead = obj.optString("contactLead", "Alumni Referral"),
        notes = obj.optString("notes", ""),
        lastUpdated = obj.optString("lastUpdated", ""),
        isStarred = obj.optBoolean("isStarred", true)
      )
    }

    fun listToJsonString(list: List<CompanyApplicationLedgerItem>): String {
      val array = JSONArray()
      list.forEach { array.put(it.toJson()) }
      return array.toString()
    }

    fun listFromJsonString(jsonStr: String): List<CompanyApplicationLedgerItem> {
      val result = mutableListOf<CompanyApplicationLedgerItem>()
      if (jsonStr.isBlank()) return result
      try {
        val array = JSONArray(jsonStr)
        for (i in 0 until array.length()) {
          val obj = array.getJSONObject(i)
          result.add(fromJson(obj))
        }
      } catch (e: Exception) {
        // Fallback safely
      }
      return result
    }

    fun getDefaultInitialCompanies(): List<CompanyApplicationLedgerItem> {
      val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
      return listOf(
        CompanyApplicationLedgerItem(
          companyName = "Zepto",
          status = "Interviewing",
          role = "Operations & Strategy Lead (Dark Store Hubs)",
          compensation = "₹32L CTC",
          interviewStage = "Case Study & War Room Loop",
          contactLead = "VP Operations & Micro-Fulfillment",
          notes = "Presented 42% operational cycle compression model. Positive feedback on Bellandur dark store turnaround.",
          lastUpdated = today,
          isStarred = true
        ),
        CompanyApplicationLedgerItem(
          companyName = "Swiggy",
          status = "Offer",
          role = "Instamart Cluster Strategy Associate",
          compensation = "₹28L - ₹30L CTC",
          interviewStage = "Offer Negotiation & Onboarding",
          contactLead = "Director of Dark Store Unit Economics",
          notes = "Verbal offer extended. Reviewing signing bonus and variable incentives.",
          lastUpdated = today,
          isStarred = true
        ),
        CompanyApplicationLedgerItem(
          companyName = "Blinkit",
          status = "Interviewing",
          role = "Dark Store Expansion Specialist",
          compensation = "₹26L CTC",
          interviewStage = "Round 2 Technical Deep Dive",
          contactLead = "Cluster General Manager, South India",
          notes = "Evaluated automated SKU velocity heatmaps and rider turnaround times.",
          lastUpdated = today,
          isStarred = true
        ),
        CompanyApplicationLedgerItem(
          companyName = "Google",
          status = "Applied",
          role = "Associate Business Analyst",
          compensation = "₹34L CTC",
          interviewStage = "Resume Screening",
          contactLead = "Bengaluru Campus Talent Acquisition",
          notes = "Applied with ATS-optimized resume emphasizing SQL CTEs and cross-functional operations.",
          lastUpdated = today,
          isStarred = true
        ),
        CompanyApplicationLedgerItem(
          companyName = "Razorpay",
          status = "Offer",
          role = "Product Operations Lead",
          compensation = "₹27L CTC",
          interviewStage = "Offer Extended",
          contactLead = "Founder's Office Senior Manager",
          notes = "Offer letter in inbox. Base ₹24L + ₹3L performance bonus.",
          lastUpdated = today,
          isStarred = true
        ),
        CompanyApplicationLedgerItem(
          companyName = "Flipkart Minutes",
          status = "Applied",
          role = "Fleet & Staging Operations Associate",
          compensation = "₹25L CTC",
          interviewStage = "Application Dispatched",
          contactLead = "Supply Chain Strategy Manager",
          notes = "Dispatched via 1-Click Apply Command Center.",
          lastUpdated = today,
          isStarred = false
        ),
        CompanyApplicationLedgerItem(
          companyName = "Bain & Company",
          status = "Interviewing",
          role = "Associate Consultant - Capability Network",
          compensation = "₹30L CTC",
          interviewStage = "Round 1 Case Interview",
          contactLead = "Senior Manager, Strategy Practice",
          notes = "Completed quick-commerce unit economics market-sizing case.",
          lastUpdated = today,
          isStarred = true
        ),
        CompanyApplicationLedgerItem(
          companyName = "Stripe",
          status = "Applied",
          role = "Risk & Merchant Operations Analyst",
          compensation = "₹32L CTC",
          interviewStage = "Application Submitted",
          contactLead = "APAC Talent Lead",
          notes = "Submitted cover letter emphasizing Bengaluru startup payment operations.",
          lastUpdated = today,
          isStarred = false
        ),
        CompanyApplicationLedgerItem(
          companyName = "Uber",
          status = "Targeted",
          role = "Operations Manager - Mobility Network",
          compensation = "₹29L CTC",
          interviewStage = "Networking & Referral Stage",
          contactLead = "Operations Director (Alum)",
          notes = "Reaching out via LinkedIn for warm referral hook.",
          lastUpdated = today,
          isStarred = false
        )
      )
    }
  }
}

fun CompanyApplicationLedgerItem.toEntity(): com.example.data.model.ApplicationPipelineCompanyEntity =
  com.example.data.model.ApplicationPipelineCompanyEntity(
    companyName = companyName,
    status = status,
    role = role,
    compensation = compensation,
    interviewStage = interviewStage,
    contactLead = contactLead,
    notes = notes,
    lastUpdated = lastUpdated,
    isStarred = isStarred
  )

fun com.example.data.model.ApplicationPipelineCompanyEntity.toLedgerItem(): CompanyApplicationLedgerItem =
  CompanyApplicationLedgerItem(
    companyName = companyName,
    status = status,
    role = role,
    compensation = compensation,
    interviewStage = interviewStage,
    contactLead = contactLead,
    notes = notes,
    lastUpdated = lastUpdated,
    isStarred = isStarred
  )

/**
 * JavaScript interface for bidirectional sync between browser window.localStorage and native state.
 */
class RelationshipLedgerStorageBridge(
  private val onSyncFromBrowser: (List<CompanyApplicationLedgerItem>) -> Unit,
  private val onNotifyVerified: (String, Int) -> Unit
) {
  @JavascriptInterface
  fun onLedgerStateUpdated(jsonString: String) {
    val list = CompanyApplicationLedgerItem.listFromJsonString(jsonString)
    onSyncFromBrowser(list)
  }

  @JavascriptInterface
  fun onLocalStorageVerified(storageData: String, byteLength: Int) {
    onNotifyVerified(storageData, byteLength)
  }

  @JavascriptInterface
  fun logFromWeb(message: String) {
    android.util.Log.d("TitanRelationshipLedger", message)
  }
}

/**
 * Generates the HTML application containing full window.localStorage logic.
 */
fun buildRelationshipLedgerHtml(initialDataJson: String): String {
  val cleanJson = initialDataJson
    .replace("\\", "\\\\")
    .replace("\"", "\\\"")
    .replace("\n", "\\n")
    .replace("\r", "")

  return """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
  <title>Relationship Ledger - localStorage</title>
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
      --indigo: #7C4DFF;
      --text-primary: #F0F4F8;
      --text-secondary: #94A3B8;
      --text-muted: #64748B;
    }
    * { box-sizing: border-box; margin: 0; padding: 0; -webkit-tap-highlight-color: transparent; }
    body {
      background-color: var(--bg-dark);
      color: var(--text-primary);
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
      padding: 10px;
      font-size: 13px;
    }
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-bottom: 8px;
      border-bottom: 1px solid var(--border-color);
      margin-bottom: 10px;
    }
    .badge {
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
    }
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 6px;
      background: var(--card-bg);
      border: 1px solid var(--border-color);
      border-radius: 8px;
      padding: 10px;
      margin-bottom: 10px;
      text-align: center;
    }
    .stat-val { font-size: 16px; font-weight: 800; }
    .stat-lbl { font-size: 9px; color: var(--text-secondary); text-transform: uppercase; }
    .company-card {
      background: var(--card-bg);
      border: 1px solid var(--border-color);
      border-radius: 8px;
      padding: 10px;
      margin-bottom: 8px;
      transition: all 0.25s ease;
      position: relative;
    }
    .company-card.card-accent-Offer, .company-card.card-accent-offer {
      background: linear-gradient(135deg, rgba(0, 230, 118, 0.18) 0%, rgba(14, 38, 26, 0.96) 100%);
      border: 1.5px solid rgba(0, 230, 118, 0.65);
      box-shadow: 0 4px 14px rgba(0, 230, 118, 0.14);
    }
    .company-card.card-accent-Interviewing, .company-card.card-accent-interviewing {
      background: linear-gradient(135deg, rgba(255, 214, 0, 0.16) 0%, rgba(42, 34, 12, 0.96) 100%);
      border: 1.5px solid rgba(255, 214, 0, 0.6);
      box-shadow: 0 4px 14px rgba(255, 214, 0, 0.12);
    }
    .company-card.card-accent-Applied, .company-card.card-accent-applied {
      background: linear-gradient(135deg, rgba(0, 229, 255, 0.14) 0%, rgba(13, 34, 53, 0.96) 100%);
      border: 1.5px solid rgba(0, 229, 255, 0.55);
      box-shadow: 0 4px 14px rgba(0, 229, 255, 0.10);
    }
    .company-card.card-accent-Targeted, .company-card.card-accent-targeted {
      background: linear-gradient(135deg, rgba(124, 77, 255, 0.14) 0%, rgba(27, 21, 48, 0.96) 100%);
      border: 1.5px solid rgba(124, 77, 255, 0.5);
    }
    .card-top {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 6px;
    }
    .company-name { font-size: 14px; font-weight: 700; color: var(--text-primary); }
    .role-title { font-size: 11px; color: var(--text-secondary); }
    .status-badge {
      font-size: 10px;
      font-weight: 700;
      padding: 2px 6px;
      border-radius: 4px;
      text-transform: uppercase;
    }
    .status-Applied { background: rgba(0, 229, 255, 0.15); color: var(--cyan); border: 1px solid rgba(0, 229, 255, 0.3); }
    .status-Interviewing { background: rgba(255, 214, 0, 0.15); color: var(--gold); border: 1px solid rgba(255, 214, 0, 0.3); }
    .status-Offer { background: rgba(0, 230, 118, 0.2); color: var(--emerald); border: 1px solid rgba(0, 230, 118, 0.4); }
    .status-Targeted { background: rgba(124, 77, 255, 0.15); color: var(--indigo); border: 1px solid rgba(124, 77, 255, 0.3); }
    .btn-group {
      display: flex;
      gap: 4px;
      margin-top: 8px;
      overflow-x: auto;
    }
    .status-btn {
      background: var(--card-elevated);
      border: 1px solid var(--border-color);
      color: var(--text-secondary);
      font-size: 10px;
      font-weight: 700;
      padding: 4px 8px;
      border-radius: 4px;
      cursor: pointer;
      white-space: nowrap;
    }
    .status-btn.active {
      background: var(--cyan);
      color: #000;
      border-color: var(--cyan);
    }
    .notes-text {
      font-size: 10px;
      color: var(--text-muted);
      margin-top: 6px;
      font-style: italic;
    }
  </style>
</head>
<body>
  <div class="header">
    <strong>🏢 Relationship Ledger</strong>
    <span class="badge">● window.localStorage ACTIVE</span>
  </div>

  <!-- Recharts Application Conversion Rates Engine in DOM -->
  <div style="background:var(--card-bg); border:1px solid var(--border-color); border-radius:8px; padding:10px; margin-bottom:10px;">
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
      <strong style="color:var(--cyan); font-size:11px; letter-spacing:0.5px;">📈 RECHARTS CONVERSION RATES</strong>
      <span style="font-size:9px; color:var(--emerald); background:rgba(0,230,118,0.12); padding:1px 5px; border-radius:3px;">AUTO-COMPUTED</span>
    </div>
    <div style="display:grid; grid-template-columns:repeat(3, 1fr); gap:6px; margin-bottom:8px; text-align:center;">
      <div style="background:var(--card-elevated); padding:6px; border-radius:6px; border:1px solid var(--border-color);">
        <div style="font-size:9px; color:var(--text-secondary);">Applied → Interview</div>
        <div style="font-size:14px; font-weight:800; color:var(--cyan);" id="appToIntRate">0.0%</div>
        <div style="font-size:8px; color:var(--text-muted);" id="appToIntCount">0 / 0 converted</div>
      </div>
      <div style="background:var(--card-elevated); padding:6px; border-radius:6px; border:1px solid var(--border-color);">
        <div style="font-size:9px; color:var(--text-secondary);">Interview → Offer</div>
        <div style="font-size:14px; font-weight:800; color:var(--gold);" id="intToOfferRate">0.0%</div>
        <div style="font-size:8px; color:var(--text-muted);" id="intToOfferCount">0 / 0 converted</div>
      </div>
      <div style="background:var(--card-elevated); padding:6px; border-radius:6px; border:1px solid var(--border-color);">
        <div style="font-size:9px; color:var(--text-secondary);">Net Offer Yield</div>
        <div style="font-size:14px; font-weight:800; color:var(--emerald);" id="netYieldRate">0.0%</div>
        <div style="font-size:8px; color:var(--text-muted);" id="netYieldCount">0 offers</div>
      </div>
    </div>
    <!-- Visual Conversion Progress Funnel Bars -->
    <div style="display:flex; flex-direction:column; gap:4px;">
      <div>
        <div style="display:flex; justify-content:space-between; font-size:9px; color:var(--text-secondary); margin-bottom:2px;">
          <span>1. Applications Submitted</span>
          <span id="funnelAppliedText" style="color:var(--cyan); font-weight:700;">100%</span>
        </div>
        <div style="height:6px; background:var(--card-elevated); border-radius:3px; overflow:hidden;">
          <div id="funnelAppliedBar" style="height:100%; width:100%; background:var(--cyan); border-radius:3px;"></div>
        </div>
      </div>
      <div>
        <div style="display:flex; justify-content:space-between; font-size:9px; color:var(--text-secondary); margin-bottom:2px;">
          <span>2. Interviews Secured (Conversion)</span>
          <span id="funnelInterviewText" style="color:var(--gold); font-weight:700;">0.0%</span>
        </div>
        <div style="height:6px; background:var(--card-elevated); border-radius:3px; overflow:hidden;">
          <div id="funnelInterviewBar" style="height:100%; width:0%; background:var(--gold); border-radius:3px;"></div>
        </div>
      </div>
      <div>
        <div style="display:flex; justify-content:space-between; font-size:9px; color:var(--text-secondary); margin-bottom:2px;">
          <span>3. Offers Extended (Yield)</span>
          <span id="funnelOfferText" style="color:var(--emerald); font-weight:700;">0.0%</span>
        </div>
        <div style="height:6px; background:var(--card-elevated); border-radius:3px; overflow:hidden;">
          <div id="funnelOfferBar" style="height:100%; width:0%; background:var(--emerald); border-radius:3px;"></div>
        </div>
      </div>
    </div>
  </div>

  <div class="stats-grid">
    <div>
      <div class="stat-val" id="totalCount" style="color:var(--text-primary)">0</div>
      <div class="stat-lbl">Tracked</div>
    </div>
    <div>
      <div class="stat-val" id="appliedCount" style="color:var(--cyan)">0</div>
      <div class="stat-lbl">Applied</div>
    </div>
    <div>
      <div class="stat-val" id="interviewCount" style="color:var(--gold)">0</div>
      <div class="stat-lbl">Interviewing</div>
    </div>
    <div>
      <div class="stat-val" id="offerCount" style="color:var(--emerald)">0</div>
      <div class="stat-lbl">Offers</div>
    </div>
  </div>

  <div id="companyCardsList"></div>

  <script>
    var STORAGE_KEY = 'titan_relationship_ledger_v1';
    var ledgerData = [];

    function init() {
      try {
        var stored = window.localStorage.getItem(STORAGE_KEY);
        if (stored && stored.length > 2) {
          ledgerData = JSON.parse(stored);
        } else {
          ledgerData = JSON.parse("$cleanJson");
          persist();
        }
      } catch (e) {
        ledgerData = JSON.parse("$cleanJson");
      }
      render();
    }

    function persist() {
      try {
        var jsonStr = JSON.stringify(ledgerData);
        window.localStorage.setItem(STORAGE_KEY, jsonStr);
        if (window.AndroidBridge && typeof window.AndroidBridge.onLedgerStateUpdated === 'function') {
          window.AndroidBridge.onLedgerStateUpdated(jsonStr);
        }
      } catch (err) {
        console.error("Storage write error:", err);
      }
    }

    function updateCompanyStatus(companyName, newStatus) {
      var item = ledgerData.find(function(c) {
        return c.companyName.toLowerCase() === companyName.toLowerCase();
      });
      if (item) {
        item.status = newStatus;
        item.lastUpdated = new Date().toISOString().split('T')[0];
        persist();
        render();
      }
    }

    function render() {
      var container = document.getElementById('companyCardsList');
      container.innerHTML = '';

      var total = ledgerData.length;
      var applied = ledgerData.filter(function(i){ return i.status.toLowerCase() === 'applied'; }).length;
      var interviewing = ledgerData.filter(function(i){ return i.status.toLowerCase() === 'interviewing'; }).length;
      var offers = ledgerData.filter(function(i){ return i.status.toLowerCase() === 'offer'; }).length;

      document.getElementById('totalCount').innerText = total;
      document.getElementById('appliedCount').innerText = applied;
      document.getElementById('interviewCount').innerText = interviewing;
      document.getElementById('offerCount').innerText = offers;

      var submitted = applied + interviewing + offers;
      var intReached = interviewing + offers;
      var appToIntPct = submitted > 0 ? ((intReached / submitted) * 100).toFixed(1) : "0.0";
      var intToOfferPct = intReached > 0 ? ((offers / intReached) * 100).toFixed(1) : "0.0";
      var netYieldPct = submitted > 0 ? ((offers / submitted) * 100).toFixed(1) : "0.0";

      document.getElementById('appToIntRate').innerText = appToIntPct + '%';
      document.getElementById('appToIntCount').innerText = intReached + ' / ' + submitted + ' converted';
      document.getElementById('intToOfferRate').innerText = intToOfferPct + '%';
      document.getElementById('intToOfferCount').innerText = offers + ' / ' + intReached + ' converted';
      document.getElementById('netYieldRate').innerText = netYieldPct + '%';
      document.getElementById('netYieldCount').innerText = offers + ' offers in hand';

      document.getElementById('funnelInterviewText').innerText = appToIntPct + '%';
      document.getElementById('funnelInterviewBar').style.width = Math.min(100, Math.max(0, parseFloat(appToIntPct))) + '%';
      document.getElementById('funnelOfferText').innerText = netYieldPct + '%';
      document.getElementById('funnelOfferBar').style.width = Math.min(100, Math.max(0, parseFloat(netYieldPct))) + '%';

      ledgerData.forEach(function(item) {
        var card = document.createElement('div');
        card.className = 'company-card card-accent-' + (item.status || 'Applied');

        var top = document.createElement('div');
        top.className = 'card-top';

        var info = document.createElement('div');
        info.innerHTML = '<div class="company-name">' + item.companyName + '</div><div class="role-title">' + item.role + ' • ' + item.compensation + '</div>';

        var badge = document.createElement('span');
        badge.className = 'status-badge status-' + item.status;
        badge.innerText = item.status;

        top.appendChild(info);
        top.appendChild(badge);
        card.appendChild(top);

        var btnGroup = document.createElement('div');
        btnGroup.className = 'btn-group';

        ['Applied', 'Interviewing', 'Offer', 'Targeted'].forEach(function(st) {
          var btn = document.createElement('button');
          btn.className = 'status-btn ' + (item.status === st ? 'active' : '');
          btn.innerText = (st === 'Applied' ? '✓ Applied' : st === 'Interviewing' ? '🎙️ Interviewing' : st === 'Offer' ? '🏆 Offer' : '🎯 Targeted');
          btn.onclick = function() { updateCompanyStatus(item.companyName, st); };
          btnGroup.appendChild(btn);
        });

        card.appendChild(btnGroup);

        if (item.notes) {
          var notes = document.createElement('div');
          notes.className = 'notes-text';
          notes.innerText = '📝 ' + item.notes;
          card.appendChild(notes);
        }

        container.appendChild(card);
      });
    }

    window.updateCompanyStatusFromNative = function(compName, st) {
      updateCompanyStatus(compName, st);
    };

    window.verifyLocalStorageBridge = function() {
      var stored = window.localStorage.getItem(STORAGE_KEY);
      var byteLength = stored ? new Blob([stored]).size : 0;
      if (window.AndroidBridge && typeof window.AndroidBridge.onLocalStorageVerified === 'function') {
        window.AndroidBridge.onLocalStorageVerified(stored, byteLength);
      }
    };

    window.onload = init;
  </script>
</body>
</html>
  """.trimIndent()
}

/**
 * Main Relationship Ledger Screen & Component.
 * Implements full tracking of company application statuses ('Applied', 'Interviewing', 'Offer')
 * with native-and-DOM localStorage persistence.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RelationshipLedgerView(
  viewModel: TitanViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  val prefs = remember(context) {
    context.getSharedPreferences("titan_relationship_ledger_prefs", Context.MODE_PRIVATE)
  }

  val roomPipelineCompanies by viewModel.applicationPipelineCompanies.collectAsState()

  // Reactive state synced with Room SQLite offline database & localStorage
  var ledgerItems by remember {
    val initialList = if (roomPipelineCompanies.isNotEmpty()) {
      roomPipelineCompanies.map { it.toLedgerItem() }
    } else {
      val savedJson = prefs.getString("stored_relationship_ledger_data", null)
      if (!savedJson.isNullOrBlank()) {
        CompanyApplicationLedgerItem.listFromJsonString(savedJson)
      } else {
        CompanyApplicationLedgerItem.getDefaultInitialCompanies()
      }
    }
    mutableStateOf(initialList)
  }

  var selectedFilter by remember { mutableStateOf("ALL") } // ALL, Applied, Interviewing, Offer, Targeted
  var searchQuery by remember { mutableStateOf("") }
  var showAddDialog by remember { mutableStateOf(false) }
  var storageVerificationMsg by remember { mutableStateOf("● Room SQLite OFFLINE PERSISTENCE ACTIVE (Zero Internet Required)") }
  var showWebViewInspector by remember { mutableStateOf(false) }
  var ledgerViewMode by remember { mutableStateOf("UNIFIED") } // UNIFIED, DASHBOARD, CARDS

  var webViewRef by remember { mutableStateOf<WebView?>(null) }

  // Synchronize when Room database flow updates
  LaunchedEffect(roomPipelineCompanies) {
    if (roomPipelineCompanies.isNotEmpty()) {
      ledgerItems = roomPipelineCompanies.map { it.toLedgerItem() }
      storageVerificationMsg = "● Room SQLite OFFLINE PERSISTENCE ACTIVE • ${roomPipelineCompanies.size} Companies Cached"
    } else {
      // Seed default initial pipeline into Room SQLite if empty
      CompanyApplicationLedgerItem.getDefaultInitialCompanies().forEach { item ->
        viewModel.saveApplicationPipelineCompany(item.toEntity())
      }
    }
  }

  // Sync to Room SQLite, SharedPreferences and trigger localStorage in WebView
  val persistLedger = { updatedList: List<CompanyApplicationLedgerItem> ->
    ledgerItems = updatedList
    val jsonStr = CompanyApplicationLedgerItem.listToJsonString(updatedList)
    coroutineScope.launch {
      prefs.edit().putString("stored_relationship_ledger_data", jsonStr).apply()
      updatedList.forEach { item ->
        viewModel.saveApplicationPipelineCompany(item.toEntity())
      }
    }
  }

  // Update status for a specific company card with immediate Room SQLite persistence
  val updateStatus = { companyName: String, newStatus: String ->
    val updated = ledgerItems.map { item ->
      if (item.companyName.equals(companyName, ignoreCase = true)) {
        item.copy(
          status = newStatus,
          lastUpdated = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        )
      } else {
        item
      }
    }
    persistLedger(updated)
    viewModel.updateApplicationPipelineStatus(companyName, newStatus)

    // Notify WebView localStorage
    webViewRef?.evaluateJavascript(
      "if (window.updateCompanyStatusFromNative) { window.updateCompanyStatusFromNative('$companyName', '$newStatus'); }",
      null
    )
  }

  val filteredList = remember(ledgerItems, selectedFilter, searchQuery) {
    ledgerItems.filter { item ->
      val matchesFilter = when (selectedFilter) {
        "ALL" -> true
        else -> item.status.equals(selectedFilter, ignoreCase = true)
      }
      val matchesSearch = searchQuery.isBlank() ||
        item.companyName.contains(searchQuery, ignoreCase = true) ||
        item.role.contains(searchQuery, ignoreCase = true) ||
        item.notes.contains(searchQuery, ignoreCase = true)
      matchesFilter && matchesSearch
    }
  }

  val totalCount = ledgerItems.size
  val appliedCount = ledgerItems.count { it.status.equals("Applied", ignoreCase = true) }
  val interviewingCount = ledgerItems.count { it.status.equals("Interviewing", ignoreCase = true) }
  val offerCount = ledgerItems.count { it.status.equals("Offer", ignoreCase = true) }
  val offerRatePct = if (totalCount > 0) ((offerCount.toFloat() / totalCount.toFloat()) * 100).toInt() else 0

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianDark)
      .padding(horizontal = 14.dp, vertical = 8.dp)
      .testTag("relationship_ledger_view")
  ) {
    // -------------------------------------------------------------
    // TOP HEADER
    // -------------------------------------------------------------
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(TitanGold.copy(alpha = 0.15f))
            .border(1.dp, TitanGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Handshake, contentDescription = "Ledger", tint = TitanGold, modifier = Modifier.size(20.dp))
        }

        Column {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = "RELATIONSHIP LEDGER",
              style = MaterialTheme.typography.labelSmall,
              color = TitanGold,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(TitanEmerald)
            )
          }
          Text(
            text = "Company Application Statuses",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimaryDark,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = { showAddDialog = true },
          modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(TitanGold.copy(alpha = 0.15f))
            .testTag("add_company_ledger_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = "Add Company", tint = TitanGold, modifier = Modifier.size(18.dp))
        }

        IconButton(
          onClick = {
            webViewRef?.evaluateJavascript("if (window.verifyLocalStorageBridge) { window.verifyLocalStorageBridge(); }", null)
          },
          modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(SlateElevated)
            .testTag("verify_localstorage_button")
        ) {
          Icon(Icons.Default.Storage, contentDescription = "Verify localStorage", tint = TitanEmerald, modifier = Modifier.size(16.dp))
        }
      }
    }

    // Room SQLite Offline Persistence Status Bar
    Spacer(modifier = Modifier.height(6.dp))
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(6.dp))
        .background(SlateDarker)
        .border(0.8.dp, TitanEmerald.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
        .padding(horizontal = 10.dp, vertical = 6.dp)
        .testTag("offline_persistence_status_bar"),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
          modifier = Modifier
            .size(7.dp)
            .clip(CircleShape)
            .background(TitanEmerald)
        )
        Text(
          text = "● ROOM SQLITE OFFLINE PERSISTENCE: ACTIVE • ${ledgerItems.size} COMPANIES CACHED",
          style = MaterialTheme.typography.labelSmall,
          color = TitanEmerald,
          fontWeight = FontWeight.Bold,
          fontSize = 9.sp
        )
      }

      Text(
        text = if (showWebViewInspector) "Hide DOM" else "Inspect DOM",
        style = MaterialTheme.typography.labelSmall,
        color = TitanCyan,
        fontWeight = FontWeight.Bold,
        fontSize = 9.sp,
        modifier = Modifier.clickable { showWebViewInspector = !showWebViewInspector }
      )
    }

    // -------------------------------------------------------------
    // VIEW MODE SELECTOR (Unified Flow, Recharts Dashboard, Company Cards)
    // -------------------------------------------------------------
    Spacer(modifier = Modifier.height(6.dp))
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(SlateCard)
        .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
        .padding(3.dp)
        .testTag("ledger_view_mode_toggle"),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      listOf(
        Triple("UNIFIED", "Unified Flow", Icons.AutoMirrored.Filled.TrendingUp),
        Triple("DASHBOARD", "Recharts Dashboard", Icons.AutoMirrored.Filled.ShowChart),
        Triple("CARDS", "Company Cards ($totalCount)", Icons.Default.Handshake)
      ).forEach { (modeKey, label, icon) ->
        val isSelected = ledgerViewMode == modeKey
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) TitanCyan else Color.Transparent)
            .clickable { ledgerViewMode = modeKey }
            .padding(vertical = 6.dp)
            .testTag("ledger_view_mode_${modeKey.lowercase()}"),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = icon,
              contentDescription = null,
              modifier = Modifier.size(12.dp),
              tint = if (isSelected) ObsidianDark else TextSecondaryDark
            )
            Text(
              text = label,
              style = MaterialTheme.typography.labelSmall,
              color = if (isSelected) ObsidianDark else TextSecondaryDark,
              fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
              fontSize = 10.5.sp,
              maxLines = 1
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // -------------------------------------------------------------
    // MAIN SCROLLABLE CONTENT (Controlled by ledgerViewMode)
    // -------------------------------------------------------------
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(bottom = 32.dp)
    ) {
      // 1. RECHARTS DASHBOARD (Visualizes Applied to Interview & Interview to Offer conversion rates)
      if (ledgerViewMode == "UNIFIED" || ledgerViewMode == "DASHBOARD") {
        item {
          RelationshipLedgerRechartsDashboard(
            ledgerItems = ledgerItems,
            onFilterByStatus = { status ->
              selectedFilter = status
              if (ledgerViewMode == "DASHBOARD") {
                ledgerViewMode = "CARDS"
              }
            }
          )
        }
      }

      // In DASHBOARD mode: show conversion strategy summary card
      if (ledgerViewMode == "DASHBOARD") {
        item {
          ConversionDeepDiveStrategyCard(
            ledgerItems = ledgerItems,
            onSwitchToCards = { ledgerViewMode = "CARDS" }
          )
        }
      }

      // 2. PIPELINE METRICS KPI BAR (Shown in CARDS mode)
      if (ledgerViewMode == "CARDS") {
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(SlateCard)
              .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
              .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("TRACKED", style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 9.sp)
              Text("$totalCount", style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("APPLIED", style = MaterialTheme.typography.labelSmall, color = TitanCyan, fontSize = 9.sp)
              Text("$appliedCount", style = MaterialTheme.typography.titleMedium, color = TitanCyan, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("INTERVIEWING", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontSize = 9.sp)
              Text("$interviewingCount", style = MaterialTheme.typography.titleMedium, color = TitanGold, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("OFFERS", style = MaterialTheme.typography.labelSmall, color = TitanEmerald, fontSize = 9.sp)
              Text("$offerCount", style = MaterialTheme.typography.titleMedium, color = TitanEmerald, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("OFFER RATE", style = MaterialTheme.typography.labelSmall, color = TitanGold, fontSize = 9.sp)
              Text("$offerRatePct%", style = MaterialTheme.typography.titleMedium, color = TitanGold, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // 3. SEARCH & STATUS FILTER CHIPS (Shown in UNIFIED and CARDS modes)
      if (ledgerViewMode == "UNIFIED" || ledgerViewMode == "CARDS") {
        item {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(
              value = searchQuery,
              onValueChange = { searchQuery = it },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("relationship_ledger_search_input"),
              placeholder = { Text("Filter company name, role, notes...", color = TextMutedDark, fontSize = 12.sp) },
              leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TitanCyan, modifier = Modifier.size(16.dp)) },
              singleLine = true,
              shape = RoundedCornerShape(8.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TitanCyan,
                unfocusedBorderColor = SlateBorder,
                focusedContainerColor = SlateCard,
                unfocusedContainerColor = SlateCard,
                focusedTextColor = TextPrimaryDark,
                unfocusedTextColor = TextPrimaryDark
              )
            )

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              listOf("ALL", "Applied", "Interviewing", "Offer", "Targeted").forEach { st ->
                val isSelected = selectedFilter == st
                val count = when (st) {
                  "ALL" -> totalCount
                  "Applied" -> appliedCount
                  "Interviewing" -> interviewingCount
                  "Offer" -> offerCount
                  else -> ledgerItems.count { it.status.equals("Targeted", ignoreCase = true) }
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                      if (isSelected) {
                        when (st) {
                          "Applied" -> TitanCyan
                          "Interviewing" -> TitanGold
                          "Offer" -> TitanEmerald
                          else -> TitanGold
                        }
                      } else SlateElevated
                    )
                    .border(1.dp, if (isSelected) Color.Transparent else SlateBorder, RoundedCornerShape(6.dp))
                    .clickable { selectedFilter = st }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .testTag("filter_chip_$st")
                ) {
                  Text(
                    text = "$st ($count)",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) ObsidianDark else TextSecondaryDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  )
                }
              }
            }
          }
        }
      }

      // 4. OPTIONAL WEBVIEW DOM INSPECTOR
      if (showWebViewInspector) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .height(260.dp)
              .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = SlateDarker),
            border = BorderStroke(1.dp, TitanCyan.copy(alpha = 0.5f))
          ) {
            Column(modifier = Modifier.fillMaxSize().padding(6.dp)) {
              Text("Browser DOM window.localStorage Inspector & Recharts Bridge", color = TitanCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              AndroidView(
                factory = { ctx ->
                  WebView(ctx).apply {
                    @SuppressLint("SetJavaScriptEnabled")
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = WebViewClient()

                    addJavascriptInterface(
                      RelationshipLedgerStorageBridge(
                        onSyncFromBrowser = { updatedFromWeb ->
                          persistLedger(updatedFromWeb)
                        },
                        onNotifyVerified = { _, byteLength ->
                          storageVerificationMsg = "● localStorage VERIFIED: $byteLength bytes active"
                        }
                      ),
                      "AndroidBridge"
                    )

                    val html = buildRelationshipLedgerHtml(CompanyApplicationLedgerItem.listToJsonString(ledgerItems))
                    loadDataWithBaseURL("https://titan.ai", html, "text/html", "UTF-8", null)
                    webViewRef = this
                  }
                },
                modifier = Modifier.fillMaxSize()
              )
            }
          }
        }
      }

      // 5. COMPANY CARDS (Shown in UNIFIED and CARDS modes)
      if (ledgerViewMode == "UNIFIED" || ledgerViewMode == "CARDS") {
        if (filteredList.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SlateCard)
                .padding(24.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No company applications found matching filter.", color = TextSecondaryDark, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                  onClick = {
                    selectedFilter = "ALL"
                    searchQuery = ""
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = TitanCyan, contentColor = ObsidianDark)
                ) {
                  Text("Clear Filters", fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        } else {
          items(filteredList, key = { it.companyName }) { item ->
            CompanyApplicationLedgerCard(
              item = item,
              onStatusChanged = { newStatus ->
                updateStatus(item.companyName, newStatus)
              },
              onToggleStar = {
                val updated = ledgerItems.map { c ->
                  if (c.companyName == item.companyName) c.copy(isStarred = !c.isStarred) else c
                }
                persistLedger(updated)
                viewModel.toggleApplicationPipelineStarred(item.companyName, !item.isStarred)
              }
            )
          }
        }
      }
    }
  }

  // Dialog to Add a New Company to Ledger
  if (showAddDialog) {
    AddLedgerCompanyDialog(
      onDismiss = { showAddDialog = false },
      onSave = { newItem ->
        val exists = ledgerItems.any { it.companyName.equals(newItem.companyName, ignoreCase = true) }
        val updated = if (exists) {
          ledgerItems.map { if (it.companyName.equals(newItem.companyName, ignoreCase = true)) newItem else it }
        } else {
          listOf(newItem) + ledgerItems
        }
        persistLedger(updated)
        viewModel.saveApplicationPipelineCompany(newItem.toEntity())
        showAddDialog = false
      }
    )
  }
}

/**
 * Sub-component: Conversion Deep Dive Strategy Card
 */
@Composable
private fun ConversionDeepDiveStrategyCard(
  ledgerItems: List<CompanyApplicationLedgerItem>,
  onSwitchToCards: () -> Unit
) {
  val appliedCount = ledgerItems.count { it.status.equals("Applied", ignoreCase = true) }
  val interviewingCount = ledgerItems.count { it.status.equals("Interviewing", ignoreCase = true) }
  val offerCount = ledgerItems.count { it.status.equals("Offer", ignoreCase = true) }
  val totalSubmitted = appliedCount + interviewingCount + offerCount
  val reachedInterview = interviewingCount + offerCount

  val appToInt = if (totalSubmitted > 0) (reachedInterview.toFloat() / totalSubmitted * 100f).roundToInt() else 0
  val intToOffer = if (reachedInterview > 0) (offerCount.toFloat() / reachedInterview * 100f).roundToInt() else 0

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = BorderStroke(1.dp, SlateBorder),
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "🎯 CONVERSION FUNNEL DEEP DIVE",
          style = MaterialTheme.typography.labelSmall,
          color = TitanGold,
          fontWeight = FontWeight.Black,
          fontSize = 10.sp
        )
        TextButton(
          onClick = onSwitchToCards,
          contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text("View Company Cards →", color = TitanCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
      }

      // Conversion Stage 1: Applied -> Interview
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = SlateDarker,
        border = BorderStroke(0.8.dp, SlateBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("1. Applied → Interview Conversion", color = TitanCyan, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            Text("$appToInt%", color = TitanCyan, fontSize = 14.sp, fontWeight = FontWeight.Black)
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "$reachedInterview out of $totalSubmitted applications reached active interview loops.",
            color = TextSecondaryDark,
            fontSize = 10.sp
          )
          val interviewCompanies = ledgerItems.filter { it.status.equals("Interviewing", true) || it.status.equals("Offer", true) }.map { it.companyName }
          if (interviewCompanies.isNotEmpty()) {
            Text(
              text = "Active: ${interviewCompanies.joinToString(", ")}",
              color = TitanCyan.copy(alpha = 0.9f),
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      // Conversion Stage 2: Interview -> Offer
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = SlateDarker,
        border = BorderStroke(0.8.dp, SlateBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("2. Interview → Offer Conversion", color = TitanGold, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            Text("$intToOffer%", color = TitanGold, fontSize = 14.sp, fontWeight = FontWeight.Black)
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "$offerCount out of $reachedInterview interview loops converted into formal offers.",
            color = TextSecondaryDark,
            fontSize = 10.sp
          )
          val offerCompanies = ledgerItems.filter { it.status.equals("Offer", true) }.map { it.companyName }
          if (offerCompanies.isNotEmpty()) {
            Text(
              text = "Offers in Hand: ${offerCompanies.joinToString(", ")}",
              color = TitanEmerald,
              fontSize = 9.5.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

/**
 * Company Card inside the Relationship Ledger.
 * Displays application status (Applied, Interviewing, Offer) and one-click update actions.
 */
@Composable
fun CompanyApplicationLedgerCard(
  item: CompanyApplicationLedgerItem,
  onStatusChanged: (String) -> Unit,
  onToggleStar: () -> Unit
) {
  val statusAccentColor = when (item.status) {
    "Offer" -> TitanEmerald
    "Interviewing" -> TitanGold
    "Applied" -> TitanCyan
    "Targeted" -> TitanIndigo
    else -> SlateBorder
  }

  // Dynamic background color accents to visualize application progress at a glance:
  // e.g. green for 'Offer', yellow for 'Interviewing', blue for 'Applied'
  val dynamicBackgroundColor = when (item.status) {
    "Offer" -> Color(0xFF0C2619) // Deep rich emerald / green accent
    "Interviewing" -> Color(0xFF2B230B) // Deep rich warm gold / yellow accent
    "Applied" -> Color(0xFF0E2235) // Deep rich corporate blue / cyan accent
    "Targeted" -> Color(0xFF1B1530) // Deep rich indigo accent
    else -> SlateCard
  }

  val dynamicBorderColor = when (item.status) {
    "Offer" -> TitanEmerald.copy(alpha = 0.75f)
    "Interviewing" -> TitanGold.copy(alpha = 0.7f)
    "Applied" -> TitanCyan.copy(alpha = 0.65f)
    "Targeted" -> TitanIndigo.copy(alpha = 0.6f)
    else -> SlateBorder
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("company_card_${item.companyName.lowercase().replace(" ", "_")}")
      .animateContentSize(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = dynamicBackgroundColor),
    border = BorderStroke(1.2.dp, dynamicBorderColor)
  ) {
    // Dynamic top accent glow ribbon
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(3.5.dp)
        .background(
          Brush.horizontalGradient(
            listOf(
              statusAccentColor,
              statusAccentColor.copy(alpha = 0.5f),
              Color.Transparent
            )
          )
        )
    )

    Column(modifier = Modifier.padding(12.dp)) {
      // Card Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          IconButton(
            onClick = onToggleStar,
            modifier = Modifier.size(24.dp)
          ) {
            Icon(
              imageVector = if (item.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
              contentDescription = if (item.isStarred) "Starred" else "Unstarred",
              tint = if (item.isStarred) TitanGold else TextMutedDark,
              modifier = Modifier.size(18.dp)
            )
          }

          Column {
            Text(
              text = item.companyName,
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
            Text(
              text = "${item.role} • ${item.compensation}",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark,
              fontSize = 11.sp
            )
          }
        }

        // Current Status Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
              when (item.status) {
                "Offer" -> TitanEmerald.copy(alpha = 0.2f)
                "Interviewing" -> TitanGold.copy(alpha = 0.2f)
                "Applied" -> TitanCyan.copy(alpha = 0.15f)
                "Targeted" -> TitanIndigo.copy(alpha = 0.15f)
                else -> SlateElevated
              }
            )
            .border(
              1.dp,
              when (item.status) {
                "Offer" -> TitanEmerald
                "Interviewing" -> TitanGold
                "Applied" -> TitanCyan
                "Targeted" -> TitanIndigo
                else -> SlateBorder
              },
              RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = when (item.status) {
              "Offer" -> "🏆 OFFER"
              "Interviewing" -> "🎙️ INTERVIEWING"
              "Applied" -> "✓ APPLIED"
              "Targeted" -> "🎯 TARGETED"
              else -> item.status.uppercase()
            },
            style = MaterialTheme.typography.labelSmall,
            color = when (item.status) {
              "Offer" -> TitanEmerald
              "Interviewing" -> TitanGold
              "Applied" -> TitanCyan
              "Targeted" -> TitanIndigo
              else -> TextSecondaryDark
            },
            fontWeight = FontWeight.Black,
            fontSize = 9.sp
          )
        }
      }

      // Interview stage & contact hook
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Stage: ${item.interviewStage}",
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontSize = 10.sp
        )
        if (item.contactLead.isNotBlank()) {
          Text(
            text = "Lead: ${item.contactLead}",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark,
            fontSize = 10.sp
          )
        }
      }

      if (item.notes.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "📝 ${item.notes}",
          style = MaterialTheme.typography.bodySmall,
          color = TextMutedDark,
          fontSize = 10.sp,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
      }

      // -------------------------------------------------------------
      // ONE-CLICK APPLICATION STATUS PERSISTENCE SELECTORS (localStorage)
      // -------------------------------------------------------------
      Spacer(modifier = Modifier.height(10.dp))
      HorizontalDivider(color = SlateBorder.copy(alpha = 0.5f), thickness = 0.8.dp)
      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "UPDATE STATUS (PERSISTS IN LOCALSTORAGE):",
        style = MaterialTheme.typography.labelSmall,
        color = TextMutedDark,
        fontWeight = FontWeight.Bold,
        fontSize = 8.sp,
        letterSpacing = 0.5.sp
      )

      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Status 1: Applied
        StatusButton(
          title = "✓ Applied",
          isSelected = item.status.equals("Applied", ignoreCase = true),
          activeColor = TitanCyan,
          onClick = { onStatusChanged("Applied") },
          modifier = Modifier.weight(1f)
        )

        // Status 2: Interviewing
        StatusButton(
          title = "🎙️ Interviewing",
          isSelected = item.status.equals("Interviewing", ignoreCase = true),
          activeColor = TitanGold,
          onClick = { onStatusChanged("Interviewing") },
          modifier = Modifier.weight(1f)
        )

        // Status 3: Offer
        StatusButton(
          title = "🏆 Offer",
          isSelected = item.status.equals("Offer", ignoreCase = true),
          activeColor = TitanEmerald,
          onClick = { onStatusChanged("Offer") },
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
private fun StatusButton(
  title: String,
  isSelected: Boolean,
  activeColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(if (isSelected) activeColor else SlateElevated)
      .border(
        1.dp,
        if (isSelected) activeColor else SlateBorder,
        RoundedCornerShape(6.dp)
      )
      .clickable { onClick() }
      .padding(vertical = 6.dp, horizontal = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall,
      color = if (isSelected) ObsidianDark else TextSecondaryDark,
      fontWeight = FontWeight.Bold,
      fontSize = 9.sp,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}

@Composable
private fun AddLedgerCompanyDialog(
  onDismiss: () -> Unit,
  onSave: (CompanyApplicationLedgerItem) -> Unit
) {
  var companyName by remember { mutableStateOf("") }
  var role by remember { mutableStateOf("Operations & Strategy Lead") }
  var comp by remember { mutableStateOf("₹28L CTC") }
  var status by remember { mutableStateOf("Applied") }
  var stage by remember { mutableStateOf("Initial Screening") }
  var notes by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = SlateCard,
      border = BorderStroke(1.dp, TitanGold)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text("Add to Relationship Ledger", style = MaterialTheme.typography.titleMedium, color = TitanGold, fontWeight = FontWeight.Bold)

        OutlinedTextField(
          value = companyName,
          onValueChange = { companyName = it },
          label = { Text("Company Name (e.g. Zepto, Google)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = role,
          onValueChange = { role = it },
          label = { Text("Role Title") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = comp,
          onValueChange = { comp = it },
          label = { Text("Target Compensation (CTC)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("Applied", "Interviewing", "Offer").forEach { st ->
            val isSel = status == st
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isSel) TitanGold else SlateElevated)
                .clickable { status = st }
                .padding(vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                st,
                color = if (isSel) ObsidianDark else TextSecondaryDark,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }
        }

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Relationship Notes & Context") },
          maxLines = 2,
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondaryDark) }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (companyName.isNotBlank()) {
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                onSave(
                  CompanyApplicationLedgerItem(
                    companyName = companyName.trim(),
                    status = status,
                    role = role.trim(),
                    compensation = comp.trim(),
                    interviewStage = stage,
                    notes = notes.trim(),
                    lastUpdated = today,
                    isStarred = true
                  )
                )
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = TitanGold, contentColor = ObsidianDark)
          ) {
            Text("Save to Ledger", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
