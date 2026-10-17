# Omni-Corporate Daily Radar & Intelligence Feed

An automated daily intelligence engine and job discovery radar that tracks startups, global MNCs, consulting firms, and fintech unicorns across all corporate departments, delivering verified daily updates, role openings, and actionable alerts.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following product requirements and architectural directions were confirmed during interactive clarification and will guide the implementation:

- **Confirmed Decision 1 (Sector Scope)**: Track all sectors comprehensively—including high-growth tech startups, Fortune 500 MNCs, Tier-1 strategy consulting firms, fintech leaders, and quick-commerce disruptors.
- **Confirmed Decision 2 (Department & Role Coverage)**: Encompass all corporate functions and leadership tiers—Strategy & Chief of Staff, Software Engineering & AI, Product & Design, Business Operations, Finance, and Talent Acquisition.
- **Confirmed Decision 3 (Delivery Mechanism)**: Implement an interactive Daily Intelligence Feed inside the app, paired with automated job alert chips, daily digest summaries, and direct 1-Click Apply queue dispatch.
- **Recommended Default (Grounding & Intelligence Engine)**: Leverage server-side Gemini Search Grounding with live web queries to source real-time job openings and company news, persisting feed items locally in Room SQLite for offline speed and zero latency.

---

## 1. Overview & Core Concept

- **What It Does**: The Omni-Corporate Daily Radar operates as a real-time corporate pulse for executive job seekers. It monitors hundreds of top tech startups and global MNCs (e.g., Google, Microsoft, Zepto, Blinkit, Swiggy, Stripe, McKinsey, Bain, Razorpay), parses newly opened roles across all departments, highlights strategic leadership moves, and aggregates daily corporate news into a high-density, interactive intelligence stream.
- **Target Audience / Persona**: High-velocity operators, chiefs of staff, engineers, product leaders, and job seekers who need early, daily visibility into unadvertised roles, stealth startup hiring surges, and enterprise MNC expansion.
- **Key Value**: Eliminates manual job board hunting by unifying daily corporate news, live role openings, and 1-click ledger tracking in a single centralized command feed with zero stale data.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **Daily Morning Briefing & Radar Scan**: Upon opening the Daily Radar tab (in Companies/Command Center), the user sees today's Top Signal Briefing: fresh role counts, hiring momentum spikes, and sector breakdown pills.
2. **Interactive Sector & Department Filtering**: The user filters by company tier (MNC vs Startup vs Unicorn), department (Strategy, Engineering, Product, Ops, Finance), or freshness (`Today`, `Past 48h`, `This Week`).
3. **Deep Role Inspection & Live Verification**: Tapping any role opens an expandable intel modal with key requirements, salary CTC benchmarks, hiring manager contacts, and live Gemini search grounding citations.
4. **1-Click Apply & Relationship Ledger Sync**: From the daily feed card, the user can immediately tap **"Dispatch 1-Click Apply"** or **"Track in Relationship Ledger"**, instantly populating their local ledger and firing Firebase Analytics engagement telemetry.
5. **Instant Daily Refresh & Notification Trigger**: Tapping **"Scan Daily Openings"** triggers a real-time Gemini Search Grounding pass, surfacing verified new openings with subtle spring animations.

### Visual Identity & Theme
- **Aesthetic Direction**: Obsidian executive terminal aesthetic matching the existing Adi OS sovereign design language (`#0A0D12` background, `#121721` cards, `#26334D` borders).
- **Color Hierarchy & Accent Tokens**:
  - `TitanCyan` (`#00E5FF`): Startups, newly discovered roles, active search grounding pulses.
  - `TitanGold` (`#FFD600`): MNC & Enterprise signals, executive alerts, CTC compensation tags.
  - `TitanEmerald` (`#00E676`): Confirmed active applications, hiring surge indicators, high-match scores.
  - `TitanIndigo` (`#7C4DFF`): Consulting & Fintech specialized tracks.
- **Component Styling & Layout**:
  - High-density card design with horizontal category tags, salary chips, and department badges.
  - Live pulse indicators showing green glowing status dots for roles posted within the last 24 hours.
  - Minimum 48dp touch targets on all interactive filter chips, refresh buttons, and action pills.

---

## 3. Key Product Decisions & Trade-Offs

### Decision 1: Live Grounded Search vs. Static Mock Roster
- **Chosen Approach**: Live search grounding via Gemini API coupled with a comprehensive Room SQLite local seed database of top MNCs and high-growth startups across India and global hubs.
- **Why**: Real users require factual, up-to-date company updates and authentic role requirements rather than fabricated placeholders, while local SQLite caching guarantees instantaneous UI loading and full offline access.
- **Alternatives Considered**: Pure manual entry (too high friction) or hardcoded static feeds (quickly becomes outdated).

### Decision 2: Feed Architecture & Screen Placement
- **Chosen Approach**: Integrate the Daily Radar Feed as a dedicated sub-view and widget accessible in the Companies Screen (alongside Target Tracker, Roster, and Relationship Ledger) as well as the Executive Command Center.
- **Why**: Keeps navigation streamlined without cluttering the primary bottom navigation bar, providing logical cohesion with the newly built Relationship Ledger and corporate application targets.
- **Alternatives Considered**: Creating a standalone top-level bottom nav item (would crowd the primary navigation rail on mobile devices).

---

## 4. Technical Architecture & Data Strategy

### System Architecture & Component Hierarchy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Adi OS Command UI                               │
├───────────────────────────────────┬────────────────────────────────────┤
│   ExecutiveCommandCenterScreen   │          CompaniesScreen           │
│    (Daily Digest Briefing Card)  │   (Tab 2: Daily Radar & Live Feed) │
└─────────────────┬─────────────────┴──────────────────┬─────────────────┘
                  │                                    │
                  ▼                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│             OmniCorporateDailyRadarFeed Component                      │
│   • Sector Filter Chips (MNC / Startup / Fintech / Consulting)         │
│   • Department Matrix (Strategy, Tech, Product, Ops, Finance)          │
│   • Live Search Grounding Trigger with Pulse Animation                 │
│   • Daily Job Role Cards with CTC, Stage & 1-Click Action Hub          │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
                                   ▼
┌────────────────────────────────────────────────────────────────────────┐
│                  TitanViewModel & State Layer                          │
│   • dailyRadarItems: StateFlow<List<CorporateDailyUpdateItem>>         │
│   • isRefreshingRadar: StateFlow<Boolean>                              │
│   • fetchDailyCorporateUpdates(sector, roleCategory)                   │
│   • dispatchDailyRoleToRelationshipLedger(item)                        │
└──────────────────┬───────────────────────────────────┬─────────────────┘
                   │                                   │
                   ▼                                   ▼
┌──────────────────────────────────────┐ ┌───────────────────────────────┐
│        Gemini Career Service         │ │     Room SQLite Database      │
│  • Search Grounding REST API         │ │  • CorporateDailyUpdateDao    │
│  • Daily Hiring Alert Synthesis      │ │  • RelationshipLedger Sync    │
│  • Model: gemini-3.5-flash           │ │  • Offline Feed Cache         │
└──────────────────────────────────────┘ └───────────────────────────────┘
```

### Data Model & State Strategy
- **`CorporateDailyUpdateItem`**:
  - `id`: Unique identifier (UUID or company-role slug).
  - `companyName`: Target organization (e.g., Google, Zepto, McKinsey, Stripe).
  - `companyType`: `MNC`, `STARTUP`, `UNICORN`, `CONSULTING`.
  - `roleTitle`: Targeted role (e.g., "Strategy & Ops Lead", "Staff AI Engineer").
  - `department`: `STRATEGY_OPS`, `ENGINEERING_AI`, `PRODUCT`, `FINANCE`, `PEOPLE`.
  - `location`: "Bengaluru (Hybrid)", "Mumbai", "Remote", etc.
  - `compensationRange`: e.g., "₹32L - ₹38L CTC".
  - `postedDate`: e.g., "2026-10-13" (Freshness badge).
  - `summary`: Strategic context (e.g., "Expanding quick-commerce cluster in NCR; hiring ops lead for dark store turnaround").
  - `applyUrl`: Direct corporate career link or referral channel.
  - `isNewToday`: Boolean flag for highlighting new daily discoveries.
  - `isSavedToLedger`: Boolean indicating if already tracked in Relationship Ledger.

### Interactive State Mapping
- **Filter Changes**: Selecting a sector chip (`ALL`, `MNC`, `STARTUP`, `CONSULTING`) filters `dailyRadarItems` in-memory with sub-millisecond response.
- **Search Query**: Real-time debounce query filtering across company names, role titles, and technology keywords.
- **1-Click Track Button**: Directly writes the role to `CompanyApplicationLedgerItem` in both Android `SharedPreferences` and the browser `window.localStorage` bridge, keeping the Recharts conversion dashboard synchronized.
- **Live Gemini Refresh**: Invokes `geminiCareerService.fetchDailyCorporateRadarUpdates()` with Google Search Grounding to append fresh discoveries to the Room SQLite database.
