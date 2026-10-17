package com.example.data

import com.example.data.model.CorporateDepartment
import com.example.data.model.CorporateSector
import com.example.data.model.CorporateDailyUpdateItem
import com.example.data.model.GroundedSource

object OmniCorporateRadarDataProvider {

  fun getInitialDailyRadarItems(): List<CorporateDailyUpdateItem> {
    return listOf(
      // 1. ZEPTO (Quick Commerce Unicorn) - Operations & Strategy
      CorporateDailyUpdateItem(
        id = "zepto-strat-ops-01",
        companyName = "Zepto",
        sector = CorporateSector.UNICORN,
        roleTitle = "Operations & Strategy Lead (Dark Store Hubs)",
        department = CorporateDepartment.STRATEGY_OPS,
        location = "Bengaluru, KA (Hybrid)",
        compensationRange = "₹32L - ₹38L CTC",
        postedDate = "Today",
        isNewToday = true,
        hiringUrgency = "IMMEDIATE",
        headlineUpdate = "Zepto raises $665M pre-IPO round; opening 40+ new dark store hubs across Bangalore and NCR.",
        keyRequirements = listOf("4+ yrs in quick-commerce or logistics", "SQL CTEs & cohort velocity analysis", "Turnaround & SLA optimization"),
        hiringManagerLead = "VP of Micro-Fulfillment Operations",
        applyUrl = "https://www.zeptonow.com/careers",
        experienceLevel = "Lead (4-7 yrs)",
        ctcLakhsEstimate = 35,
        groundedSources = listOf(
          GroundedSource("Zepto Secures $665M Funding Ahead of IPO", "https://techcrunch.com/zepto-pre-ipo"),
          GroundedSource("Zepto Micro-Fulfillment Expansion Radar", "https://zeptonow.com/careers/darkstore-lead")
        )
      ),

      // 2. GOOGLE (Tech MNC) - Engineering & AI
      CorporateDailyUpdateItem(
        id = "google-eng-ai-01",
        companyName = "Google",
        sector = CorporateSector.TECH_MNC,
        roleTitle = "Staff AI Systems Engineer (Gemini Infrastructure)",
        department = CorporateDepartment.ENGINEERING_AI,
        location = "Bengaluru, KA (Campus)",
        compensationRange = "₹55L - ₹75L CTC",
        postedDate = "Today",
        isNewToday = true,
        hiringUrgency = "HIGH_PRIORITY",
        headlineUpdate = "Google expanding Bengaluru Gemini inference engineering center to power enterprise AI models across APAC.",
        keyRequirements = listOf("C++, Python, JAX/TensorFlow", "Distributed serving & latency profiling", "LLM quantization & GPU memory optimization"),
        hiringManagerLead = "Director of Cloud AI Platforms",
        applyUrl = "https://careers.google.com/jobs",
        experienceLevel = "Staff (7-12 yrs)",
        ctcLakhsEstimate = 65,
        groundedSources = listOf(
          GroundedSource("Google Expands AI Operations Center in Bengaluru", "https://blog.google/technology/ai/bengaluru-center")
        )
      ),

      // 3. BAIN & COMPANY (Strategy Consulting) - Strategy & BizOps
      CorporateDailyUpdateItem(
        id = "bain-consulting-01",
        companyName = "Bain & Company",
        sector = CorporateSector.CONSULTING,
        roleTitle = "Strategy Consultant - Capability Network",
        department = CorporateDepartment.STRATEGY_OPS,
        location = "Gurugram / Bengaluru",
        compensationRange = "₹34L - ₹42L CTC",
        postedDate = "Today",
        isNewToday = true,
        hiringUrgency = "HIGH_PRIORITY",
        headlineUpdate = "Bain expanding digital transformation and private equity advisory practices in India.",
        keyRequirements = listOf("MBA / Tier-1 background", "Unit economics modeling & market sizing", "Client presentation & stakeholder synthesis"),
        hiringManagerLead = "Partner, Global Strategy & PE Practice",
        applyUrl = "https://www.bain.com/careers",
        experienceLevel = "Consultant (3-6 yrs)",
        ctcLakhsEstimate = 38,
        groundedSources = listOf(
          GroundedSource("Bain Capability Network Strategy Growth", "https://bain.com/careers/india")
        )
      ),

      // 4. RAZORPAY (Fintech Leader) - Product & Design
      CorporateDailyUpdateItem(
        id = "razorpay-prod-01",
        companyName = "Razorpay",
        sector = CorporateSector.FINTECH,
        roleTitle = "Senior Product Manager - Cross-Border Payments",
        department = CorporateDepartment.PRODUCT_DESIGN,
        location = "Bengaluru, KA",
        compensationRange = "₹38L - ₹48L CTC",
        postedDate = "Today",
        isNewToday = true,
        hiringUrgency = "IMMEDIATE",
        headlineUpdate = "Razorpay receives RBI authorization for cross-border merchant payments; scaling global gateway.",
        keyRequirements = listOf("Fintech/Payments ecosystem deep dive", "API product design & developer UX", "Conversion rate funnel optimization"),
        hiringManagerLead = "VP Product, International Expansion",
        applyUrl = "https://razorpay.com/jobs",
        experienceLevel = "Senior (5-8 yrs)",
        ctcLakhsEstimate = 42,
        groundedSources = listOf(
          GroundedSource("Razorpay Cross-Border Inward Remittance Approval", "https://economictimes.com/razorpay-crossborder")
        )
      ),

      // 5. MICROSOFT (Tech MNC) - Strategy & Ops
      CorporateDailyUpdateItem(
        id = "msft-cos-01",
        companyName = "Microsoft",
        sector = CorporateSector.TECH_MNC,
        roleTitle = "Chief of Staff to Corporate VP (Azure Data & AI)",
        department = CorporateDepartment.STRATEGY_OPS,
        location = "Hyderabad / Bengaluru",
        compensationRange = "₹48L - ₹60L CTC",
        postedDate = "Today",
        isNewToday = true,
        hiringUrgency = "EXPANSION_WAVE",
        headlineUpdate = "Microsoft accelerating Azure AI datacenter investments across Hyderabad and Pune regions.",
        keyRequirements = listOf("Executive cadence & OKR alignment", "Cross-functional engineering coordination", "Board-level memo authoring"),
        hiringManagerLead = "Corporate Vice President, Azure Platforms",
        applyUrl = "https://careers.microsoft.com",
        experienceLevel = "Principal (6-10 yrs)",
        ctcLakhsEstimate = 52,
        groundedSources = listOf(
          GroundedSource("Microsoft India Cloud Infrastructure Roadmaps", "https://news.microsoft.com/india-ai-cloud")
        )
      ),

      // 6. BLINKIT (Commerce & Quick Fulfillment) - Fulfillment & Retail
      CorporateDailyUpdateItem(
        id = "blinkit-ops-02",
        companyName = "Blinkit",
        sector = CorporateSector.UNICORN,
        roleTitle = "Cluster Operations Manager - Dark Store Fleet",
        department = CorporateDepartment.COMMERCE,
        location = "Delhi NCR / Mumbai",
        compensationRange = "₹28L - ₹34L CTC",
        postedDate = "Yesterday",
        isNewToday = false,
        hiringUrgency = "IMMEDIATE",
        headlineUpdate = "Blinkit dark store count hits 800+ milestones; adding non-grocery electronics and beauty SKUs.",
        keyRequirements = listOf("Dark store inventory turn rates", "Rider dispatch algorithm tuning", "Loss prevention & cold chain audit"),
        hiringManagerLead = "Regional General Manager, North India",
        applyUrl = "https://blinkit.com/careers",
        experienceLevel = "Manager (3-6 yrs)",
        ctcLakhsEstimate = 30,
        groundedSources = listOf(
          GroundedSource("Blinkit Dark Store Network Velocity Report", "https://blinkit.com/press")
        )
      ),

      // 7. STRIPE (Fintech & Global MNC) - Engineering & AI
      CorporateDailyUpdateItem(
        id = "stripe-eng-01",
        companyName = "Stripe",
        sector = CorporateSector.FINTECH,
        roleTitle = "Staff Backend Engineer - Global Money Movement",
        department = CorporateDepartment.ENGINEERING_AI,
        location = "Bengaluru, KA (Remote Friendly)",
        compensationRange = "₹60L - ₹80L CTC",
        postedDate = "Today",
        isNewToday = true,
        hiringUrgency = "HIGH_PRIORITY",
        headlineUpdate = "Stripe expanding Indian engineering hub to build multi-currency settlement rails.",
        keyRequirements = listOf("Distributed systems & ACID compliance", "Idempotency & ledger reconciliation", "Java, Go, Ruby at high scale"),
        hiringManagerLead = "Engineering Director, APAC Financial Rails",
        applyUrl = "https://stripe.com/jobs",
        experienceLevel = "Staff (7-11 yrs)",
        ctcLakhsEstimate = 70,
        groundedSources = listOf(
          GroundedSource("Stripe India Engineering Expansion", "https://stripe.com/newsroom/india-office")
        )
      ),

      // 8. MCKINSEY & COMPANY (Consulting Giant) - Finance & BizOps
      CorporateDailyUpdateItem(
        id = "mckinsey-bizops-01",
        companyName = "McKinsey & Company",
        sector = CorporateSector.CONSULTING,
        roleTitle = "Manager - Corporate Finance & Valuation Advisory",
        department = CorporateDepartment.FINANCE_BIZOPS,
        location = "Mumbai / Gurugram",
        compensationRange = "₹42L - ₹52L CTC",
        postedDate = "Yesterday",
        isNewToday = false,
        hiringUrgency = "HIGH_PRIORITY",
        headlineUpdate = "McKinsey Client Capabilities Network expanding corporate M&A advisory teams.",
        keyRequirements = listOf("LBO & DCF financial modeling", "Commercial due diligence execution", "Executive boardroom deck synthesis"),
        hiringManagerLead = "Partner, Corporate Finance Practice",
        applyUrl = "https://www.mckinsey.com/careers",
        experienceLevel = "Senior Manager (5-8 yrs)",
        ctcLakhsEstimate = 46,
        groundedSources = listOf(
          GroundedSource("McKinsey India Financial Strategy Hiring", "https://mckinsey.com/careers")
        )
      ),

      // 9. CRED (High-Growth Fintech Startup) - Product & Design
      CorporateDailyUpdateItem(
        id = "cred-prod-01",
        companyName = "CRED",
        sector = CorporateSector.STARTUP,
        roleTitle = "Lead Product Manager - High-Net-Worth Wealth & Lending",
        department = CorporateDepartment.PRODUCT_DESIGN,
        location = "Bengaluru, KA",
        compensationRange = "₹45L - ₹58L CTC",
        postedDate = "Today",
        isNewToday = true,
        hiringUrgency = "IMMEDIATE",
        headlineUpdate = "CRED launches premium peer-to-peer and asset-backed lending product suite.",
        keyRequirements = listOf("0-to-1 consumer credit flows", "Behavioral economics & gamification", "Cross-functional risk & compliance partnering"),
        hiringManagerLead = "Head of Product, Lending Lines",
        applyUrl = "https://cred.club/careers",
        experienceLevel = "Lead (5-9 yrs)",
        ctcLakhsEstimate = 50,
        groundedSources = listOf(
          GroundedSource("CRED Wealth Products Launch", "https://cred.club/press/wealth-launch")
        )
      ),

      // 10. POSTMAN (High-Growth SaaS Startup) - Engineering & AI
      CorporateDailyUpdateItem(
        id = "postman-ai-01",
        companyName = "Postman",
        sector = CorporateSector.STARTUP,
        roleTitle = "Senior AI Software Engineer - Postman Flow & Copilot",
        department = CorporateDepartment.ENGINEERING_AI,
        location = "Bengaluru, KA (Hybrid)",
        compensationRange = "₹42L - ₹54L CTC",
        postedDate = "Today",
        isNewToday = true,
        hiringUrgency = "HIGH_PRIORITY",
        headlineUpdate = "Postman surpasses 35 million developers globally; launching generative AI API testing suites.",
        keyRequirements = listOf("Node.js, TypeScript, Python", "LLM agent orchestration & tool use", "API protocol parsing (GraphQL, gRPC, REST)"),
        hiringManagerLead = "VP of Engineering, AI Platforms",
        applyUrl = "https://www.postman.com/careers",
        experienceLevel = "Senior (4-8 yrs)",
        ctcLakhsEstimate = 48,
        groundedSources = listOf(
          GroundedSource("Postman AI Developer Tool Suite", "https://blog.postman.com/postman-ai-copilot")
        )
      ),

      // 11. SWIGGY INSTAMART (Unicorn Commerce) - Strategy & Ops
      CorporateDailyUpdateItem(
        id = "swiggy-ops-01",
        companyName = "Swiggy",
        sector = CorporateSector.UNICORN,
        roleTitle = "Cluster Strategy Associate - Instamart Logistics",
        department = CorporateDepartment.STRATEGY_OPS,
        location = "Bengaluru, KA",
        compensationRange = "₹28L - ₹34L CTC",
        postedDate = "Today",
        isNewToday = true,
        hiringUrgency = "IMMEDIATE",
        headlineUpdate = "Swiggy filing updated DRHP ahead of $1.2B IPO; scaling Instamart gross order value.",
        keyRequirements = listOf("Operations research & staging queues", "Rider retention & incentive optimization", "P&L attribution modeling"),
        hiringManagerLead = "Director of Dark Store Unit Economics",
        applyUrl = "https://www.swiggy.com/careers",
        experienceLevel = "Associate / Senior (3-5 yrs)",
        ctcLakhsEstimate = 31,
        groundedSources = listOf(
          GroundedSource("Swiggy IPO Prospectus Details", "https://moneycontrol.com/swiggy-ipo-logistics")
        )
      ),

      // 12. MEESHO (E-Commerce Unicorn) - People & Talent
      CorporateDailyUpdateItem(
        id = "meesho-hr-01",
        companyName = "Meesho",
        sector = CorporateSector.UNICORN,
        roleTitle = "Talent Partner Lead - Leadership & Tech Hiring",
        department = CorporateDepartment.PEOPLE_TALENT,
        location = "Bengaluru, KA",
        compensationRange = "₹26L - ₹32L CTC",
        postedDate = "Yesterday",
        isNewToday = false,
        hiringUrgency = "EXPANSION_WAVE",
        headlineUpdate = "Meesho achieves monthly profitability; expanding tech and supply chain leadership benches.",
        keyRequirements = listOf("Executive headhunting & mapping", "Data-driven candidate pipeline tracking", "Offer closing & compensation negotiation"),
        hiringManagerLead = "Head of People & Culture",
        applyUrl = "https://meesho.io/careers",
        experienceLevel = "Lead (4-7 yrs)",
        ctcLakhsEstimate = 29,
        groundedSources = listOf(
          GroundedSource("Meesho Growth and Hiring Momentum", "https://meesho.io/careers")
        )
      ),

      // 13. AMAZON (Global Tech MNC) - Commerce & Fulfillment
      CorporateDailyUpdateItem(
        id = "amazon-ops-01",
        companyName = "Amazon",
        sector = CorporateSector.TECH_MNC,
        roleTitle = "Senior Operations Manager - Regional Fulfillment Centers",
        department = CorporateDepartment.COMMERCE,
        location = "Hyderabad / Bengaluru",
        compensationRange = "₹45L - ₹56L CTC",
        postedDate = "Today",
        isNewToday = true,
        hiringUrgency = "HIGH_PRIORITY",
        headlineUpdate = "Amazon India expanding automated sorting centers and robotics fulfillment infrastructure.",
        keyRequirements = listOf("Six Sigma Black Belt / Lean Logistics", "500+ associate workforce orchestration", "Peak-season capacity forecasting"),
        hiringManagerLead = "Regional Director of Transportation & Fulfillment",
        applyUrl = "https://www.amazon.jobs",
        experienceLevel = "Senior Manager (6-10 yrs)",
        ctcLakhsEstimate = 50,
        groundedSources = listOf(
          GroundedSource("Amazon India Fulfillment Tech Investments", "https://aboutamazon.in/news/operations")
        )
      ),

      // 14. GROWW (Fintech Unicorn) - Finance & BizOps
      CorporateDailyUpdateItem(
        id = "groww-fin-01",
        companyName = "Groww",
        sector = CorporateSector.FINTECH,
        roleTitle = "Head of Financial Planning & Strategic BizOps",
        department = CorporateDepartment.FINANCE_BIZOPS,
        location = "Bengaluru, KA",
        compensationRange = "₹40L - ₹52L CTC",
        postedDate = "Today",
        isNewToday = true,
        hiringUrgency = "HIGH_PRIORITY",
        headlineUpdate = "Groww becomes India's largest active retail discount broker by client count.",
        keyRequirements = listOf("Brokerage clearing & treasury management", "Board reporting & regulatory capital ratios", "Revenue variance analysis"),
        hiringManagerLead = "Chief Financial Officer",
        applyUrl = "https://groww.in/careers",
        experienceLevel = "Lead (5-9 yrs)",
        ctcLakhsEstimate = 46,
        groundedSources = listOf(
          GroundedSource("Groww Overtakes Competitors in Active Users", "https://livemint.com/groww-market-share")
        )
      ),

      // 15. BROWSERSTACK (High-Growth SaaS Startup) - Engineering & AI
      CorporateDailyUpdateItem(
        id = "browserstack-eng-01",
        companyName = "BrowserStack",
        sector = CorporateSector.STARTUP,
        roleTitle = "Principal Cloud Reliability Architect",
        department = CorporateDepartment.ENGINEERING_AI,
        location = "Mumbai (Hybrid / Remote)",
        compensationRange = "₹65L - ₹85L CTC",
        postedDate = "Today",
        isNewToday = true,
        hiringUrgency = "IMMEDIATE",
        headlineUpdate = "BrowserStack launches AI-driven visual regression testing across 3,000+ real mobile devices.",
        keyRequirements = listOf("Bare-metal virtualization & containerization", "Global latency mitigation", "Golang, Kubernetes, eBPF telemetry"),
        hiringManagerLead = "VP Infrastructure Engineering",
        applyUrl = "https://www.browserstack.com/careers",
        experienceLevel = "Principal (8-14 yrs)",
        ctcLakhsEstimate = 75,
        groundedSources = listOf(
          GroundedSource("BrowserStack AI Testing Engine Launch", "https://browserstack.com/press")
        )
      )
    )
  }

  fun getScannedExpansionRadarItems(): List<CorporateDailyUpdateItem> {
    return listOf(
      // 16. NVIDIA (Global AI Hardware & Systems MNC) - Engineering & AI
      CorporateDailyUpdateItem(
        id = "nvidia-ai-arch-01",
        companyName = "NVIDIA",
        sector = CorporateSector.TECH_MNC,
        roleTitle = "Solutions Architect - Enterprise Generative AI & LLM Systems",
        department = CorporateDepartment.ENGINEERING_AI,
        location = "Bengaluru / Pune (Hybrid)",
        compensationRange = "₹62L - ₹88L CTC",
        postedDate = "Today (Just Scanned)",
        isNewToday = true,
        hiringUrgency = "IMMEDIATE",
        headlineUpdate = "NVIDIA expands Indian sovereign AI sovereign cluster partnership with Yotta and Tata Communications.",
        keyRequirements = listOf("CUDA & TensorRT-LLM inference profiling", "NeMo guardrails & vLLM orchestration", "Enterprise GPU cluster sizing"),
        hiringManagerLead = "Director of Solution Architecture, APAC",
        applyUrl = "https://www.nvidia.com/en-us/about-nvidia/careers",
        experienceLevel = "Lead / Principal (6-10 yrs)",
        ctcLakhsEstimate = 72,
        groundedSources = listOf(
          GroundedSource("NVIDIA AI Infrastructure Sovereign Hub", "https://nvidianews.nvidia.com")
        )
      ),

      // 17. PHONEPE (Fintech Giant) - Product & Design
      CorporateDailyUpdateItem(
        id = "phonepe-prod-01",
        companyName = "PhonePe",
        sector = CorporateSector.FINTECH,
        roleTitle = "Associate Director - Merchant Credit & UPI Rails",
        department = CorporateDepartment.PRODUCT_DESIGN,
        location = "Bengaluru, KA",
        compensationRange = "₹45L - ₹58L CTC",
        postedDate = "Today (Just Scanned)",
        isNewToday = true,
        hiringUrgency = "HIGH_PRIORITY",
        headlineUpdate = "PhonePe processes 6.5B monthly UPI transactions; scaling merchant overdraft credit facility.",
        keyRequirements = listOf("Credit lifecycle underwriting algorithms", "Merchant QR distribution unit economics", "RBI digital lending directive compliance"),
        hiringManagerLead = "SVP Product, Financial Services",
        applyUrl = "https://www.phonepe.com/careers",
        experienceLevel = "Associate Director (7-11 yrs)",
        ctcLakhsEstimate = 50,
        groundedSources = listOf(
          GroundedSource("PhonePe Merchant Credit Expansion", "https://phonepe.com/newsroom")
        )
      ),

      // 18. BOSTON CONSULTING GROUP (Strategy Consulting) - Strategy & Ops
      CorporateDailyUpdateItem(
        id = "bcg-strat-01",
        companyName = "BCG",
        sector = CorporateSector.CONSULTING,
        roleTitle = "Project Leader - Digital BCG & Tech Advantage",
        department = CorporateDepartment.STRATEGY_OPS,
        location = "Mumbai / Bengaluru",
        compensationRange = "₹42L - ₹54L CTC",
        postedDate = "Today (Just Scanned)",
        isNewToday = true,
        hiringUrgency = "IMMEDIATE",
        headlineUpdate = "BCG India expanding generative enterprise AI advisory and supply chain resilience taskforces.",
        keyRequirements = listOf("Tier-1 MBA / Top-tier consulting background", "Corporate restructuring & synergy realization", "Executive stakeholder buy-in"),
        hiringManagerLead = "Partner & Managing Director",
        applyUrl = "https://www.bcg.com/careers",
        experienceLevel = "Project Leader (5-8 yrs)",
        ctcLakhsEstimate = 48,
        groundedSources = listOf(
          GroundedSource("BCG Tech Advantage Practice Expansion", "https://bcg.com/careers")
        )
      ),

      // 19. URBAN COMPANY (High-Growth Startup) - Strategy & Ops
      CorporateDailyUpdateItem(
        id = "urban-strat-01",
        companyName = "Urban Company",
        sector = CorporateSector.STARTUP,
        roleTitle = "Category Head - High-Velocity Home Services",
        department = CorporateDepartment.STRATEGY_OPS,
        location = "Gurugram / Bengaluru",
        compensationRange = "₹32L - ₹40L CTC",
        postedDate = "Today (Just Scanned)",
        isNewToday = true,
        hiringUrgency = "HIGH_PRIORITY",
        headlineUpdate = "Urban Company records FY24 EBITDA breakeven; preparing domestic public market listing.",
        keyRequirements = listOf("Partner supply onboarding & retention", "City cluster unit profitability", "NPS quality loops & SLA monitoring"),
        hiringManagerLead = "VP of Categories & Marketplace",
        applyUrl = "https://www.urbancompany.com/careers",
        experienceLevel = "Head (5-8 yrs)",
        ctcLakhsEstimate = 36,
        groundedSources = listOf(
          GroundedSource("Urban Company Path to IPO", "https://entrackr.com/urbancompany-ipo")
        )
      ),

      // 20. FLIPKART (E-Commerce Giant) - Commerce & Fulfillment
      CorporateDailyUpdateItem(
        id = "flipkart-ops-01",
        companyName = "Flipkart",
        sector = CorporateSector.COMMERCE,
        roleTitle = "Principal Supply Chain Network Architect",
        department = CorporateDepartment.COMMERCE,
        location = "Bengaluru, KA",
        compensationRange = "₹50L - ₹68L CTC",
        postedDate = "Today (Just Scanned)",
        isNewToday = true,
        hiringUrgency = "EXPANSION_WAVE",
        headlineUpdate = "Flipkart expanding Minutes 10-minute grocery service across 15 tier-1 metro hubs.",
        keyRequirements = listOf("Network topology & linehaul routing", "Sort center automation & conveyor belt robotics", "Peak festival inventory modeling"),
        hiringManagerLead = "Vice President, Supply Chain Operations",
        applyUrl = "https://www.flipkartcareers.com",
        experienceLevel = "Principal (8-12 yrs)",
        ctcLakhsEstimate = 58,
        groundedSources = listOf(
          GroundedSource("Flipkart Minutes Quick Delivery Expansion", "https://economictimes.com/flipkart-minutes")
        )
      )
    )
  }
}
