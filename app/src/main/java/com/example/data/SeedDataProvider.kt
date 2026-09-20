package com.example.data

import com.example.data.model.AgentAuditLog
import com.example.data.model.AgentItem
import com.example.data.model.Application
import com.example.data.model.AutomationTask
import com.example.data.model.AutomationTaskLog
import com.example.data.model.CareerMilestone
import com.example.data.model.CareerNote
import com.example.data.model.CareerStrategyNote
import com.example.data.model.Company
import com.example.data.model.CompanyBookmark
import com.example.data.model.CompanyIntelligenceWidgetCache
import com.example.data.model.DailyBriefing
import com.example.data.model.DecisionItem
import com.example.data.model.GoalItem
import com.example.data.model.Job
import com.example.data.model.MarketRadarItem
import com.example.data.model.MilestoneTask
import com.example.data.model.ProjectItem
import com.example.data.model.RecruiterContact
import com.example.data.model.ResumeVariation
import com.example.data.model.SkillItem
import com.example.data.model.StarStory
import com.example.data.model.TargetCompany
import com.example.data.model.TargetCompanyMarketIntel
import com.example.data.model.UserProfile
import com.example.data.model.VerifiedFact

object SeedDataProvider {

  suspend fun populateInitialData(db: TitanDatabase) {
    // 1. User Profile
    val profile = UserProfile(
      id = "ADI_PRIMARY_ID",
      name = "Adi",
      location = "Bengaluru, India",
      careerStage = "Early-Career / Graduate",
      educationDegree = "BBA (Bachelor of Business Administration)",
      educationSpecialization = "International Business",
      university = "Bengaluru University",
      targetRoles = "Business Analyst, Associate Business Analyst, Strategy & Operations Associate, AI Operations, BizDev Associate, Product Operations",
      interests = "AI, Technology, International Business, Strategy, Operations, Automation, Fintech, Markets, Startups",
      careerScore = 88,
      careerCapitalScore = 85,
      aiLeverageScore = 92,
      brutalStrategyMode = false,
      automationMode = "APPROVAL",
      profileCompleteness = 96,
      totalApplicationsTracked = 24,
      totalInterviewsSecured = 4,
      totalActiveOffers = 1
    )
    db.profileDao().insertOrUpdateProfile(profile)

    // 2. Verified Facts
    val facts = listOf(
      VerifiedFact(
        category = "EDUCATION",
        claim = "Completed BBA with Specialization in International Business with First Class Honors",
        evidenceDetails = "Bengaluru University Transcripts (GPA 3.82 / 4.0), Core coursework: Global Trade Logistics, Business Analytics, Corporate Finance, Strategic Management",
        tags = "BBA, International Business, Honors, Bengaluru"
      ),
      VerifiedFact(
        category = "EXPERIENCE",
        claim = "Led Digitalization & Operations Modernization in Family Enterprise",
        evidenceDetails = "Automated invoice processing and inventory forecasting using AI tools & SQL dashboards, cutting cycle time by 42% and eliminating stockouts over 9 months",
        tags = "Operations, Automation, Family Business, Analytics"
      ),
      VerifiedFact(
        category = "EXPERIENCE",
        claim = "Freelance Business Development & Market Research Consultant",
        evidenceDetails = "Executed targeted outbound campaigns for 3 B2B tech startups, generating 65+ qualified enterprise leads and ₹18L in pipeline value",
        tags = "Sales, Business Development, Outbound, Lead Gen"
      ),
      VerifiedFact(
        category = "PROJECT",
        claim = "Architected AI Market Intelligence & Supply Chain Analytics Dashboard",
        evidenceDetails = "Engineered automated data pipelines monitoring APAC commodities, currency fluctuations, and trade tariffs using Python, Gemini API, and PowerBI",
        tags = "AI, Market Intelligence, Gemini, PowerBI, Python"
      ),
      VerifiedFact(
        category = "EXPERIENCE",
        claim = "Head of Corporate Relations & Event Leadership for Annual Business Conclave",
        evidenceDetails = "Spearheaded 22-member student committee, hosted 1,500+ attendees, secured 14 corporate sponsors including Tier-1 fintech brands",
        tags = "Leadership, Event Management, Sponsorship, Corporate Relations"
      ),
      VerifiedFact(
        category = "SKILL",
        claim = "Certified Business Intelligence Analyst & SQL Advanced Practitioner",
        evidenceDetails = "Verified Credentials in Advanced SQL, Tableau, PowerBI, Excel Financial Modeling, and Prompt Engineering for Enterprise Workflows",
        tags = "SQL, PowerBI, Financial Modeling, Analytics"
      )
    )
    db.verifiedFactDao().insertAll(facts)

    // 3. Companies (Global & Bengaluru Universe)
    val companies = listOf(
      Company(
        id = "comp_msft",
        name = "Microsoft India",
        logoEmoji = "🔷",
        website = "https://www.microsoft.com",
        careersUrl = "https://careers.microsoft.com",
        industry = "Enterprise Technology & Cloud",
        subIndustry = "AI, Cloud Infrastructure & SaaS",
        hqLocation = "Redmond, WA / Bengaluru R&D",
        bengaluruPresence = "Prestige Ferns Galaxy & Vigyan Nagar (5,000+ Engineers & Biz Ops)",
        indiaPresence = "Bengaluru, Hyderabad, Noida, Mumbai",
        employeeScale = "220,000+ Global",
        tier = "S+",
        strategicPriority = "INTERVIEWING",
        hiringVelocity = "ACCELERATING",
        aiAdoptionLevel = "VERY HIGH",
        compensationTier = "₹18L - ₹28L Base + Stock",
        isOpenForEarlyCareer = true,
        isWatched = true,
        watchNotes = "Final round scheduled for Associate Strategy & BizOps track. Focus on Azure AI ecosystem growth.",
        scoreExplanation = "S+ Tier: Unmatched global brand upside, top-tier compensation, high AI exposure, strong Bengaluru mobility.",
        activeOpeningsCount = 6
      ),
      Company(
        id = "comp_google",
        name = "Google India",
        logoEmoji = "🔴",
        website = "https://www.google.com",
        careersUrl = "https://careers.google.com",
        industry = "Internet & AI Ecosystem",
        subIndustry = "Search, Cloud & AI Systems",
        hqLocation = "Mountain View, CA / Bengaluru Campus",
        bengaluruPresence = "RMZ Infinity & Bagmane Tech Park (Tier-1 Operations & Strategy)",
        indiaPresence = "Bengaluru, Hyderabad, Gurugram, Mumbai",
        employeeScale = "180,000+ Global",
        tier = "S+",
        strategicPriority = "HIGH_PRIORITY",
        hiringVelocity = "ACCELERATING",
        aiAdoptionLevel = "VERY HIGH",
        compensationTier = "₹20L - ₹32L Base + Equity",
        isOpenForEarlyCareer = true,
        isWatched = true,
        watchNotes = "Targeting Associate Account Strategist / Operations Analyst roles. Recruiter connection established.",
        scoreExplanation = "S+ Tier: World-class career capital, immense learning velocity, stellar resume leverage.",
        activeOpeningsCount = 8
      ),
      Company(
        id = "comp_bain",
        name = "Bain & Company (Bain Capability Network)",
        logoEmoji = "🔴",
        website = "https://www.bain.com",
        careersUrl = "https://www.bain.com/careers",
        industry = "Management Consulting",
        subIndustry = "Strategy, Private Equity & Operations",
        hqLocation = "Boston, MA / Bengaluru BCN",
        bengaluruPresence = "EcoWorld Bellandur (Premier Strategy & Commercial Analytics Hub)",
        indiaPresence = "Bengaluru, Gurugram, Mumbai",
        employeeScale = "15,000+ Global",
        tier = "S+",
        strategicPriority = "APPLIED",
        hiringVelocity = "STABLE",
        aiAdoptionLevel = "HIGH",
        compensationTier = "₹16L - ₹24L + Performance Bonus",
        isOpenForEarlyCareer = true,
        isWatched = true,
        watchNotes = "Application submitted for Associate Consulting Analyst - Tech & Strategy practice.",
        scoreExplanation = "S+ Tier: Elite strategic pedigree, exceptional problem-solving rigor, high private equity exit value.",
        activeOpeningsCount = 4
      ),
      Company(
        id = "comp_razorpay",
        name = "Razorpay",
        logoEmoji = "⚡",
        website = "https://razorpay.com",
        careersUrl = "https://razorpay.com/jobs",
        industry = "Fintech & Payments Infrastructure",
        subIndustry = "Digital Payments, Banking & Neo-Banking",
        hqLocation = "Bengaluru, India",
        bengaluruPresence = "Koramangala HQ (High Innovation Intensity)",
        indiaPresence = "Bengaluru, Mumbai, Delhi-NCR",
        employeeScale = "3,500+",
        tier = "S",
        strategicPriority = "OFFER",
        hiringVelocity = "ACCELERATING",
        aiAdoptionLevel = "HIGH",
        compensationTier = "₹14L - ₹20L + ESOPs",
        isOpenForEarlyCareer = true,
        isWatched = true,
        watchNotes = "Offer received: Associate Product Operations (₹15.5L + ₹3L Retention Bonus). Validating decision matrix.",
        scoreExplanation = "S Tier: India's undisputed fintech leader, hyper-growth autonomy, high early-career leadership leverage.",
        activeOpeningsCount = 12
      ),
      Company(
        id = "comp_swiggy",
        name = "Swiggy",
        logoEmoji = "🟠",
        website = "https://www.swiggy.com",
        careersUrl = "https://careers.swiggy.com",
        industry = "Hyperlocal Commerce & Logistics",
        subIndustry = "Quick Commerce (Instamart), Food Delivery",
        hqLocation = "Bengaluru, India",
        bengaluruPresence = "Embassy TechVillage, Outer Ring Road",
        indiaPresence = "Bengaluru, Gurugram, Mumbai",
        employeeScale = "6,000+",
        tier = "S",
        strategicPriority = "HIGH_PRIORITY",
        hiringVelocity = "ACCELERATING",
        aiAdoptionLevel = "HIGH",
        compensationTier = "₹12L - ₹18L",
        isOpenForEarlyCareer = true,
        isWatched = true,
        watchNotes = "Watching Business Analyst - Quick Commerce Growth roles. Hiring velocity spike in Instamart supply chain.",
        scoreExplanation = "S Tier: Unmatched operational scale, real-time algorithmic dispatch, massive data analytics volume.",
        activeOpeningsCount = 9
      ),
      Company(
        id = "comp_zerodha",
        name = "Zerodha",
        logoEmoji = "🔷",
        website = "https://zerodha.com",
        careersUrl = "https://zerodha.com/careers",
        industry = "Capital Markets & Fintech",
        subIndustry = "Stock Broking & Wealth Tech",
        hqLocation = "Bengaluru, India",
        bengaluruPresence = "JP Nagar HQ (Profitable Bootstrapped Giant)",
        indiaPresence = "Bengaluru",
        employeeScale = "1,100+",
        tier = "S",
        strategicPriority = "WATCH",
        hiringVelocity = "STABLE",
        aiAdoptionLevel = "HIGH",
        compensationTier = "₹15L - ₹22L",
        isOpenForEarlyCareer = true,
        isWatched = true,
        watchNotes = "Monitored for Market Operations & Risk Analyst openings. Low attrition rate, pristine culture.",
        scoreExplanation = "S Tier: Exceptional stability, zero debt, high engineering craftsmanship, strong wealth creation.",
        activeOpeningsCount = 3
      ),
      Company(
        id = "comp_mckinsey",
        name = "McKinsey & Company",
        logoEmoji = "🔷",
        website = "https://www.mckinsey.com",
        careersUrl = "https://www.mckinsey.com/careers",
        industry = "Global Management Consulting",
        subIndustry = "Strategy, Digital Transformation & Operations",
        hqLocation = "New York, NY / Client Capability Hubs",
        bengaluruPresence = "UB City & Outer Ring Road Consulting Center",
        indiaPresence = "Bengaluru, Gurugram, Mumbai, Chennai",
        employeeScale = "45,000+ Global",
        tier = "S+",
        strategicPriority = "DREAM",
        hiringVelocity = "STABLE",
        aiAdoptionLevel = "VERY HIGH",
        compensationTier = "₹18L - ₹26L Base",
        isOpenForEarlyCareer = true,
        isWatched = true,
        watchNotes = "Preparing referral network outreach for Business Analyst (Strategy/Operations track).",
        scoreExplanation = "S+ Tier: Sovereign-grade corporate brand, unmatched global alumni network, gold standard in business analysis.",
        activeOpeningsCount = 5
      ),
      Company(
        id = "comp_phonepe",
        name = "PhonePe (Walmart Group)",
        logoEmoji = "🟣",
        website = "https://phonepe.com",
        careersUrl = "https://phonepe.com/careers",
        industry = "Fintech & Digital Commerce",
        subIndustry = "UPI Payments, Insurance, Wealth & Pincode",
        hqLocation = "Bengaluru, India",
        bengaluruPresence = "Bellandur & Green Glen Layout HQ",
        indiaPresence = "Bengaluru, Mumbai, Delhi",
        employeeScale = "5,400+",
        tier = "S",
        strategicPriority = "HIGH_PRIORITY",
        hiringVelocity = "ACCELERATING",
        aiAdoptionLevel = "HIGH",
        compensationTier = "₹14L - ₹21L",
        isOpenForEarlyCareer = true,
        isWatched = true,
        watchNotes = "Targeting Associate Business Analyst (Merchant Lending & Insurance). High transaction volume.",
        scoreExplanation = "S Tier: Over 500M registered users, immense market share, deep analytics rigor.",
        activeOpeningsCount = 7
      ),
      Company(
        id = "comp_nvidia",
        name = "NVIDIA India",
        logoEmoji = "🟢",
        website = "https://www.nvidia.com",
        careersUrl = "https://nvidia.wd5.myworkdayjobs.com/NVIDIAExternalCareerSite",
        industry = "Semiconductors & AI Compute",
        subIndustry = "Accelerated Computing, LLMs & Enterprise AI",
        hqLocation = "Santa Clara, CA / Bengaluru Tech Center",
        bengaluruPresence = "Whitefield & Manyata Tech Park (AI Software & BizOps Hub)",
        indiaPresence = "Bengaluru, Pune, Hyderabad",
        employeeScale = "30,000+ Global",
        tier = "S+",
        strategicPriority = "DREAM",
        hiringVelocity = "ACCELERATING",
        aiAdoptionLevel = "VERY HIGH",
        compensationTier = "₹22L - ₹35L Base + RSUs",
        isOpenForEarlyCareer = true,
        isWatched = true,
        watchNotes = "Monitoring AI Solutions Business Operations & Global Partner Analyst opportunities.",
        scoreExplanation = "S+ Tier: The undisputed epicenter of global AI transformation, astronomical market momentum.",
        activeOpeningsCount = 6
      ),
      Company(
        id = "comp_deloitte",
        name = "Deloitte US-India (USI)",
        logoEmoji = "🟢",
        website = "https://www2.deloitte.com",
        careersUrl = "https://usijobs.deloitte.com",
        industry = "Professional Services & Advisory",
        subIndustry = "Analytics, Strategy & Enterprise Transformation",
        hqLocation = "New York / Bengaluru USI Hub",
        bengaluruPresence = "RMZ Ecospace Bellandur (Major International Analytics & Advisory Wing)",
        indiaPresence = "Bengaluru, Hyderabad, Mumbai, Gurugram",
        employeeScale = "100,000+ India USI",
        tier = "A",
        strategicPriority = "APPLIED",
        hiringVelocity = "ACCELERATING",
        aiAdoptionLevel = "HIGH",
        compensationTier = "₹10L - ₹15L",
        isOpenForEarlyCareer = true,
        isWatched = true,
        watchNotes = "Online assessment completed for Strategy & Analytics Analyst. Awaiting interview slot.",
        scoreExplanation = "A Tier: Massive global enterprise footprint, structured graduate onboarding, strong foundational training.",
        activeOpeningsCount = 14
      ),
      Company(
        id = "comp_anthropic",
        name = "Anthropic",
        logoEmoji = "🟣",
        website = "https://www.anthropic.com",
        careersUrl = "https://jobs.lever.co/anthropic",
        industry = "Frontier AI & Safety Research",
        subIndustry = "LLMs, Claude & Enterprise AI Intelligence",
        hqLocation = "San Francisco, CA",
        bengaluruPresence = "Remote / APAC International Business Expansion",
        indiaPresence = "Remote Global Contracts & Partner Operations",
        employeeScale = "1,000+",
        tier = "S+",
        strategicPriority = "WATCH",
        hiringVelocity = "ACCELERATING",
        aiAdoptionLevel = "VERY HIGH",
        compensationTier = "USD $90k - $140k (Remote/Contract)",
        isOpenForEarlyCareer = true,
        isWatched = true,
        watchNotes = "Watching for AI Operations Associate / Enterprise Partner Operations (Remote Global).",
        scoreExplanation = "S+ Tier: World leading AI laboratory, premier technical leverage, exponential upside.",
        activeOpeningsCount = 2
      )
    )
    db.companyDao().insertAll(companies)

    // 4. Curated Jobs
    val jobs = listOf(
      Job(
        id = "job_msft_01",
        companyId = "comp_msft",
        companyName = "Microsoft India",
        title = "Associate Strategy & Business Operations Analyst",
        roleFamily = "STRATEGY_OPS",
        department = "Cloud & Enterprise Commercial Strategy",
        location = "Bengaluru, Karnataka, India",
        city = "Bengaluru",
        country = "India",
        remoteStatus = "HYBRID",
        employmentType = "FULL_TIME",
        salaryRange = "₹18,00,000 - ₹24,00,000 + Stock",
        currency = "INR",
        experienceRequirement = "0-2 Years / Fresh Graduates with Analytics Acumen",
        adiFitScore = 95,
        opportunityScore = 98,
        whyItFits = "Adi's BBA International Business foundation + proven family business operational automation + advanced SQL and AI workflow modeling aligns 100% with the cloud commercial strategy mandate.",
        whyItDoesntFit = "None. Adi meets all analytical and international trade criteria.",
        missingRequirements = "Optional: Azure Fundamentals certification (Recommended 30-day target).",
        recommendedAction = "APPLY NOW. Customized Master Resume V4.2 and STAR Story Bank attached for final round prep.",
        applicationUrl = "https://careers.microsoft.com/us/en/job/1829394/Strategy-BizOps-Analyst",
        officialSource = "Microsoft Careers Portal",
        sourceQuality = "OFFICIAL_CAREERS_PORTAL",
        postedDate = "2026-08-28",
        isFresherFriendly = true,
        strategicPriority = "APPLY_NOW",
        status = "INTERVIEWING"
      ),
      Job(
        id = "job_bain_01",
        companyId = "comp_bain",
        companyName = "Bain & Company",
        title = "Associate Consulting Analyst - Commercial Strategy & Private Equity",
        roleFamily = "CONSULTING",
        department = "Bain Capability Network (BCN)",
        location = "Bengaluru, Karnataka, India",
        city = "Bengaluru",
        country = "India",
        remoteStatus = "HYBRID",
        employmentType = "FULL_TIME",
        salaryRange = "₹16,00,000 - ₹22,00,000 + Annual Bonus",
        currency = "INR",
        experienceRequirement = "0-1 Years / Campus & Off-Campus Graduates",
        adiFitScore = 92,
        opportunityScore = 96,
        whyItFits = "Deep market sizing, financial modeling, cross-border commerce understanding from International Business degree, coupled with strong hypothesis-driven problem-solving.",
        whyItDoesntFit = "Requires fast-paced case interview agility.",
        missingRequirements = "Case interview practice: 10 structured market sizing & profitability cases.",
        recommendedAction = "APPLIED. Run AI Mock Interview simulation focused on Bain case frameworks.",
        applicationUrl = "https://www.bain.com/careers/roles/analyst-bcn",
        officialSource = "Bain Global Portal",
        sourceQuality = "OFFICIAL_CAREERS_PORTAL",
        postedDate = "2026-08-25",
        isFresherFriendly = true,
        strategicPriority = "HIGH_PRIORITY",
        status = "APPLIED"
      ),
      Job(
        id = "job_razorpay_01",
        companyId = "comp_razorpay",
        companyName = "Razorpay",
        title = "Associate Product Operations & Business Analyst",
        roleFamily = "BUSINESS_ANALYST",
        department = "Cross-Border Payments & Merchant Growth",
        location = "Bengaluru, Karnataka, India (Koramangala)",
        city = "Bengaluru",
        country = "India",
        remoteStatus = "ONSITE",
        employmentType = "FULL_TIME",
        salaryRange = "₹15,50,000 + ₹3,00,000 Bonus + ESOPs",
        currency = "INR",
        experienceRequirement = "0-2 Years / Freshers Welcome",
        adiFitScore = 98,
        opportunityScore = 94,
        whyItFits = "Matches Adi's International Business specialization and experience building AI automation pipelines. Strong merchant understanding.",
        whyItDoesntFit = "None - Offer secured.",
        missingRequirements = "None. Offer decision matrix pending review against Microsoft final round.",
        recommendedAction = "OFFER RECEIVED. Compare against career capital score and negotiate timeline.",
        applicationUrl = "https://razorpay.com/jobs/cross-border-bizops",
        officialSource = "Razorpay Greenhouse",
        sourceQuality = "OFFICIAL_CAREERS_PORTAL",
        postedDate = "2026-08-15",
        isFresherFriendly = true,
        strategicPriority = "APPLY_NOW",
        status = "OFFER"
      ),
      Job(
        id = "job_swiggy_01",
        companyId = "comp_swiggy",
        companyName = "Swiggy",
        title = "Business Analyst - Instamart Supply Chain & Growth",
        roleFamily = "BUSINESS_ANALYST",
        department = "Quick Commerce Analytics",
        location = "Bengaluru, Karnataka, India",
        city = "Bengaluru",
        country = "India",
        remoteStatus = "HYBRID",
        employmentType = "FULL_TIME",
        salaryRange = "₹13,00,000 - ₹17,00,000",
        currency = "INR",
        experienceRequirement = "0-2 Years (Graduate Trainee eligible)",
        adiFitScore = 91,
        opportunityScore = 89,
        whyItFits = "Direct correlation with Adi's verified experience optimizing inventory forecasting and stockout elimination in family business.",
        whyItDoesntFit = "High operational intensity and fast turnaround deadlines.",
        missingRequirements = "Advanced Python Pandas for real-time order stream analytics.",
        recommendedAction = "HIGH PRIORITY. Dispatch tailored Resume V3.1 emphasizing inventory optimization metrics.",
        applicationUrl = "https://careers.swiggy.com/jobs/ba-instamart-supply",
        officialSource = "Swiggy Careers Portal",
        sourceQuality = "OFFICIAL_CAREERS_PORTAL",
        postedDate = "2026-08-30",
        isFresherFriendly = true,
        strategicPriority = "HIGH_PRIORITY",
        status = "SHORTLISTED"
      ),
      Job(
        id = "job_google_01",
        companyId = "comp_google",
        companyName = "Google India",
        title = "Associate Account Strategist - Global Customer Operations",
        roleFamily = "STRATEGY_OPS",
        department = "Large Customer Sales / Global BizOps",
        location = "Bengaluru, Karnataka, India",
        city = "Bengaluru",
        country = "India",
        remoteStatus = "HYBRID",
        employmentType = "FULL_TIME",
        salaryRange = "₹20,00,000 - ₹28,00,000 + RSUs",
        currency = "INR",
        experienceRequirement = "Fresh Graduate / Early-Career (0-2 Yrs)",
        adiFitScore = 94,
        opportunityScore = 99,
        whyItFits = "International Business perspective, digital marketing analytics acumen, high client empathy, and leadership in corporate conclave sponsorship.",
        whyItDoesntFit = "High competition pool.",
        missingRequirements = "Recruiter referral (Networking Agent currently engaging senior Googler).",
        recommendedAction = "PREPARING. Warm referral path being queued with alumnus contact.",
        applicationUrl = "https://careers.google.com/jobs/results/associate-account-strategist",
        officialSource = "Google Careers",
        sourceQuality = "OFFICIAL_CAREERS_PORTAL",
        postedDate = "2026-08-29",
        isFresherFriendly = true,
        strategicPriority = "HIGH_PRIORITY",
        status = "PREPARING"
      ),
      Job(
        id = "job_deloitte_01",
        companyId = "comp_deloitte",
        companyName = "Deloitte US-India",
        title = "Analyst - Strategy & Digital Transformation Advisory",
        roleFamily = "CONSULTING",
        department = "Strategy & AI Advisory",
        location = "Bengaluru, Karnataka, India",
        city = "Bengaluru",
        country = "India",
        remoteStatus = "HYBRID",
        employmentType = "FULL_TIME",
        salaryRange = "₹11,00,000 - ₹14,50,000",
        currency = "INR",
        experienceRequirement = "0-1 Years / Campus & Off-Campus",
        adiFitScore = 89,
        opportunityScore = 85,
        whyItFits = "Clear match for international enterprise modernization and consulting presentation skills.",
        whyItDoesntFit = "Slightly lower compensation than S+ Tier peers.",
        missingRequirements = "Awaiting assessment results.",
        recommendedAction = "ASSESSMENT COMPLETED. Keep active as reliable high-probability Tier-A benchmark.",
        applicationUrl = "https://usijobs.deloitte.com/job/strategy-analyst",
        officialSource = "Deloitte USI Portal",
        sourceQuality = "OFFICIAL_CAREERS_PORTAL",
        postedDate = "2026-08-22",
        isFresherFriendly = true,
        strategicPriority = "REVIEW",
        status = "ASSESSMENT"
      )
    )
    db.jobDao().insertAll(jobs)

    // 5. Applications Pipeline
    val apps = listOf(
      Application(
        id = "app_01",
        jobId = "job_razorpay_01",
        companyName = "Razorpay",
        roleTitle = "Associate Product Operations & Business Analyst",
        status = "OFFER",
        dateDiscovered = "2026-08-15",
        dateApplied = "2026-08-16",
        resumeVersion = "Resume_Adi_BizOps_v4.pdf",
        coverLetterSnippet = "Leveraged BBA International Business insights to propose optimization for cross-border merchant settlement latencies...",
        customAnswersJson = "{\"Why Razorpay\": \"Pioneering Indian fintech with unmatched API-first developer empathy.\", \"Salary Expectation\": \"₹15.5L CTC + Variable\"}",
        followUpDate = "2026-09-05",
        followUpNotes = "Formal offer letter received. Acceptance deadline in 7 days. Decision Matrix calculated 89/100 value.",
        outcomeNotes = "Offer in hand: ₹15.5L fixed + ₹3L bonus + ₹2L ESOP grant.",
        interviewScore = 94
      ),
      Application(
        id = "app_02",
        jobId = "job_msft_01",
        companyName = "Microsoft India",
        roleTitle = "Associate Strategy & Business Operations Analyst",
        status = "INTERVIEW",
        dateDiscovered = "2026-08-28",
        dateApplied = "2026-08-29",
        resumeVersion = "Resume_Adi_Strategy_v5.pdf",
        coverLetterSnippet = "Demonstrated ability to convert complex operational datasets into actionable executive insights at the intersection of AI and cloud commerce...",
        customAnswersJson = "{\"Why Microsoft\": \"To scale generative business workflows across enterprise clients in India & APAC.\"}",
        followUpDate = "2026-09-03",
        followUpNotes = "Technical & Case round passed with 96% score. Final Director Round scheduled this Thursday at 2:30 PM.",
        outcomeNotes = "Progressed through Rounds 1 & 2 with exceptional feedback on structured communication.",
        interviewScore = 96
      ),
      Application(
        id = "app_03",
        jobId = "job_bain_01",
        companyName = "Bain & Company",
        roleTitle = "Associate Consulting Analyst - Commercial Strategy",
        status = "APPLIED",
        dateDiscovered = "2026-08-25",
        dateApplied = "2026-08-26",
        resumeVersion = "Resume_Adi_Consulting_v4.pdf",
        coverLetterSnippet = "Strong grounding in international business economics, financial analysis, and market research synthesis...",
        customAnswersJson = "{\"Relevant Coursework\": \"International Economics, Corporate Strategy, Business Analytics\"}",
        followUpDate = "2026-09-06",
        followUpNotes = "Follow-up email queued for HR Recruiter on Day 10 if status remains unreviewed.",
        outcomeNotes = "Application submitted via official BCN Careers pipeline.",
        interviewScore = 0
      ),
      Application(
        id = "app_04",
        jobId = "job_deloitte_01",
        companyName = "Deloitte US-India",
        roleTitle = "Analyst - Strategy & Digital Transformation Advisory",
        status = "ASSESSMENT",
        dateDiscovered = "2026-08-22",
        dateApplied = "2026-08-23",
        resumeVersion = "Resume_Adi_Advisory_v3.pdf",
        coverLetterSnippet = "Prepared to deliver quantitative rigour and stakeholder communication in enterprise modernization projects...",
        customAnswersJson = "{\"Availability\": \"Immediate upon graduation\"}",
        followUpDate = "2026-09-04",
        followUpNotes = "Cognitive and Business Situational Assessment completed with 91 percentile.",
        outcomeNotes = "Awaiting interview scheduling confirmation from USI Talent Acquisition.",
        interviewScore = 88
      )
    )
    db.applicationDao().insertAll(apps)

    // 6. Recruiter Contacts / Network CRM
    val contacts = listOf(
      RecruiterContact(
        id = "rec_01",
        companyId = "comp_msft",
        companyName = "Microsoft India",
        name = "Pooja Sharma",
        title = "Senior Talent Acquisition Partner - BizOps & Commercial",
        department = "University & Early Career Recruiting (India)",
        linkedinUrl = "https://linkedin.com/in/poojasharma-msft-talent",
        email = "pooja.sharma@microsoft.com",
        relationshipState = "CONVERSATION",
        lastInteractionDate = "2026-08-30",
        notes = "Coordinating Final Director Interview round. Very supportive of Adi's international business background.",
        nextFollowUpDate = "2026-09-03"
      ),
      RecruiterContact(
        id = "rec_02",
        companyId = "comp_bain",
        companyName = "Bain & Company",
        name = "Rohan Varma",
        title = "Lead Recruiter - Bain Capability Network",
        department = "BCN Consulting Analyst Hiring",
        linkedinUrl = "https://linkedin.com/in/rohanvarma-bain-recruiting",
        email = "rohan.varma@bain.com",
        relationshipState = "AWARE",
        lastInteractionDate = "2026-08-26",
        notes = "Connected on LinkedIn with personalized outreach highlighting business conclave leadership.",
        nextFollowUpDate = "2026-09-06"
      ),
      RecruiterContact(
        id = "rec_03",
        companyId = "comp_razorpay",
        companyName = "Razorpay",
        name = "Ananya Iyer",
        title = "Product & Ops Hiring Lead",
        department = "Early Career & Lateral Talent",
        linkedinUrl = "https://linkedin.com/in/ananya-iyer-razorpay",
        email = "ananya.iyer@razorpay.com",
        relationshipState = "STRONG_RELATIONSHIP",
        lastInteractionDate = "2026-08-29",
        notes = "Extended formal offer letter. Highly engaged and offered to connect with VP of Ops.",
        nextFollowUpDate = "2026-09-05"
      ),
      RecruiterContact(
        id = "rec_04",
        companyId = "comp_google",
        companyName = "Google India",
        name = "Siddharth Menon",
        title = "Strategy & Ops Lead (Bengaluru)",
        department = "Alumnus / Mentor Network",
        linkedinUrl = "https://linkedin.com/in/siddharth-menon-google",
        email = "siddharth.m@alumni.org",
        relationshipState = "REFERRAL",
        lastInteractionDate = "2026-08-31",
        notes = "Agreed to submit internal employee referral for Associate Account Strategist opening.",
        nextFollowUpDate = "2026-09-04"
      )
    )
    db.networkDao().insertAll(contacts)

    // 7. STAR Story Bank
    val stories = listOf(
      StarStory(
        title = "Modernized Family Business Supply Chain with AI & Analytics",
        storyType = "PROBLEM_SOLVING",
        situation = "Family wholesale manufacturing business faced erratic demand, frequent stockouts of fast-moving items, and manual paper-based invoicing.",
        task = "Re-architect the ordering, inventory tracking, and sales analytics workflows without disrupting ongoing business.",
        action = "Implemented automated inventory tracking using SQL databases and custom AI-powered predictive demand forecasting models in Excel and Python. Automated invoice generation and reorder trigger alerts.",
        result = "Reduced stockout events by 100%, slashed invoice processing time by 42%, and improved cashflow predictability by ₹24L over 9 months.",
        verifiedFactsUsed = "Fact #2 (Family Business Operations Modernization)",
        relevantSkills = "Business Analysis, Process Automation, SQL, Supply Chain, Problem-Solving",
        matchedQuestions = "Tell me about a time you solved a complex operational bottleneck; Give an example of using data to drive business impact."
      ),
      StarStory(
        title = "Orchestrated 1,500-Attendee National Business Conclave",
        storyType = "LEADERSHIP",
        situation = "University annual business conclave lacked corporate brand sponsors and had declining student participation post-pandemic.",
        task = "Lead the 22-member student corporate relations and logistics committee to secure Tier-1 sponsors and double attendee registrations.",
        action = "Structured a professional sponsorship pitch deck with ROI metrics for corporate partners. Directly negotiated with 14 corporate sponsors (fintech and MNCs) and ran targeted campus ambassador outreach.",
        result = "Raised ₹8.5L in corporate sponsorship, achieved a record 1,500+ delegates across 18 universities, and received 94% positive satisfaction rating.",
        verifiedFactsUsed = "Fact #5 (Conclave Corporate Relations & Event Leadership)",
        relevantSkills = "Team Leadership, Negotiation, Stakeholder Management, Event Ops",
        matchedQuestions = "Describe a time you led a large team under high pressure; How do you influence external stakeholders?"
      ),
      StarStory(
        title = "B2B Tech Outbound Sales Lead Engine",
        storyType = "ACHIEVEMENT",
        situation = "An early-stage SaaS startup struggled with low outbound email response rates (<2%) and high customer acquisition cost.",
        task = "Design a targeted outbound prospecting workflow to generate qualified B2B enterprise leads in Southeast Asia and India.",
        action = "Segmented target ICPs using LinkedIn Sales Navigator, crafted personalized multi-touch email sequences emphasizing concrete ROI, and verified deliverability.",
        result = "Boosted response rate from 2% to 11.4%, generated 65+ qualified executive demo calls, and added ₹18L to the qualified sales pipeline.",
        verifiedFactsUsed = "Fact #3 (Freelance Business Development & Lead Gen)",
        relevantSkills = "Business Development, Outbound Strategy, Market Research, Communication",
        matchedQuestions = "How do you handle difficult customer acquisition goals?; Tell me about a time you exceeded your targets."
      )
    )
    db.starStoryDao().insertAll(stories)

    // 8. Skills & AI Combinations
    val skills = listOf(
      SkillItem(
        id = "sk_sql",
        name = "SQL & Relational Databases",
        category = "BUSINESS_ANALYTICS",
        proficiency = "ADVANCED",
        isTargetDemand = true,
        isGap = false,
        aiMultiplierDesc = "SQL + AI: Auto-generates complex multi-table joins and performance-tuned analytical queries in seconds.",
        recommendedProject = "Automated SQL ETL Pipeline for Fintech Transaction Metrics"
      ),
      SkillItem(
        id = "sk_powerbi",
        name = "PowerBI & Tableau Visualization",
        category = "BUSINESS_ANALYTICS",
        proficiency = "ADVANCED",
        isTargetDemand = true,
        isGap = false,
        aiMultiplierDesc = "BI + AI: Generates automated natural-language narrative summaries of dashboard trends for executive briefing.",
        recommendedProject = "Executive APAC Market Intelligence Dashboard"
      ),
      SkillItem(
        id = "sk_ai_ops",
        name = "AI Workflow Engineering & Prompt Architecture",
        category = "AI_AUTOMATION",
        proficiency = "ADVANCED",
        isTargetDemand = true,
        isGap = false,
        aiMultiplierDesc = "Prompt Ops + Business: Transforms manual text processing, market research, and meeting synthesis into instant automated agents.",
        recommendedProject = "Autonomous Recruiter & Company Intelligence Agent"
      ),
      SkillItem(
        id = "sk_case_interviews",
        name = "Hypothesis-Driven Case Structuring (MBB Frameworks)",
        category = "STRATEGY",
        proficiency = "INTERMEDIATE",
        isTargetDemand = true,
        isGap = true,
        aiMultiplierDesc = "Strategy + AI: Rapid issue-tree generation and MECE breakdown validation for management consulting cases.",
        recommendedProject = "Case Master: 20 Interactive Management Consulting Drills"
      ),
      SkillItem(
        id = "sk_intl_trade",
        name = "International Trade Regulations & Logistics",
        category = "INTERNATIONAL_BUSINESS",
        proficiency = "ADVANCED",
        isTargetDemand = true,
        isGap = false,
        aiMultiplierDesc = "Intl Trade + AI: Real-time tariff lookup, HS code classification, and automated customs compliance verification.",
        recommendedProject = "Global Trade Tariff Simulator & Arbitrage Radar"
      ),
      SkillItem(
        id = "sk_financial_modeling",
        name = "Financial Modeling & Unit Economics",
        category = "FINANCE",
        proficiency = "INTERMEDIATE",
        isTargetDemand = true,
        isGap = false,
        aiMultiplierDesc = "Finance + AI: Instant DCF sensitivity tables and unit economics scenario simulation (Best/Base/Worst).",
        recommendedProject = "SaaS & Quick Commerce Unit Economics Decision Matrix"
      )
    )
    db.skillProjectDao().insertAllSkills(skills)

    // 9. Projects & Venture Lab
    val projects = listOf(
      ProjectItem(
        id = "proj_01",
        title = "AI Market Intelligence & Commodity Trade Radar",
        projectType = "ADI_PROJECT_LAB",
        problem = "SMEs in international trade lack affordable real-time intelligence on global tariff changes, shipping freight indices, and currency volatility.",
        solution = "Built an automated intelligence platform that ingests global trade data, computes price variance, and alerts businesses on arbitrage opportunities.",
        technology = "Python, Gemini API, PowerBI, SQLite, Streamlit",
        businessImpact = "Demonstrated actionable 14% freight cost savings in backtested Asian shipping routes.",
        skillsDemonstrated = "International Business, Market Analysis, AI Engineering, PowerBI",
        status = "COMPLETED",
        demoUrl = "https://github.com/adi-intelligence/trade-radar-demo",
        targetRoleRelevance = "High relevance for Microsoft BizOps and Bain Strategy Analyst."
      ),
      ProjectItem(
        id = "proj_02",
        title = "Autonomous B2B Lead Enrichment & Outreach Engine",
        projectType = "ADI_PROJECT_LAB",
        problem = "Sales reps spend 60% of their time manually looking up company financial filings and finding relevant decision-makers.",
        solution = "Engineered an AI agent pipeline that identifies target high-growth Indian startups, extracts hiring signals, and drafts hyper-tailored outreach.",
        technology = "Node.js, Gemini Flash, REST APIs, LinkedIn Scraping Heuristics",
        businessImpact = "Cut lead qualification time from 15 minutes per account to 45 seconds.",
        skillsDemonstrated = "Business Development, Automation, LLM Orchestration, Sales Ops",
        status = "COMPLETED",
        demoUrl = "https://github.com/adi-intelligence/sales-copilot-engine",
        targetRoleRelevance = "Direct match for Razorpay BizOps and Google LCS roles."
      ),
      ProjectItem(
        id = "proj_03",
        title = "Venture: B2B Cross-Border Compliance AI Agent",
        projectType = "ADI_VENTURE_LAB",
        problem = "Indian D2C brands expanding to UAE and US struggle with export documentation compliance, incurring heavy customs delays and fines.",
        solution = "SaaS copilot that validates product HS codes, auto-generates export declarations, and maps duty optimization pathways.",
        technology = "FastAPI, Gemini Pro, PostgreSQL, PDF Automation",
        businessImpact = "Target TAM: $4.2B APAC cross-border commerce compliance market. ₹0 startup cost with high software margins.",
        skillsDemonstrated = "Entrepreneurship, International Trade, Regulatory Strategy, Software Product Management",
        status = "PLANNED",
        demoUrl = "https://venture.adi-os.internal/crossborder-ai",
        targetRoleRelevance = "Venture Idea #1 - Validating with 10 Bengaluru exporter interviews."
      )
    )
    db.skillProjectDao().insertAllProjects(projects)

    // 10. Goals & Timelines
    val goals = listOf(
      GoalItem(
        id = "goal_today",
        title = "Ace Microsoft Final Director Strategy Round",
        category = "INTERVIEWS",
        horizon = "TODAY",
        targetMetric = "100% Preparation (STAR Stories + Azure Cloud Commercial Strategy)",
        progressPercent = 90,
        status = "ACTIVE",
        nextAction = "Review Executive Talking Points & Questions to ask the Director."
      ),
      GoalItem(
        id = "goal_7d",
        title = "Finalize Tier-1 Job Offer Decision Matrix",
        category = "CAREER",
        horizon = "WEEKLY_7D",
        targetMetric = "Compare Microsoft vs Razorpay (Base, Brand, Learning, Upside)",
        progressPercent = 75,
        status = "ACTIVE",
        nextAction = "Run Decision Engine scenario simulation."
      ),
      GoalItem(
        id = "goal_30d",
        title = "Secure Top-Tier Position with ₹16L+ Compensation in Bengaluru",
        category = "INCOME",
        horizon = "MONTHLY_30D",
        targetMetric = "Offer Signed & Onboarding Roadmap Formulated",
        progressPercent = 85,
        status = "ACTIVE",
        nextAction = "Consolidate background verification facts and document vault."
      ),
      GoalItem(
        id = "goal_1yr",
        title = "Achieve Top-Decile Performance & Launch Enterprise AI Initiative",
        category = "CAREER",
        horizon = "ANNUAL_1YR",
        targetMetric = "Promoted to Senior Analyst / Lead High-Impact Strategic Project",
        progressPercent = 20,
        status = "ACTIVE",
        nextAction = "Develop 30-60-90 day on-the-job excellence playbook."
      )
    )
    db.goalDecisionDao().insertAllGoals(goals)

    // 10b. Long-Term Career Milestones
    val milestones = listOf(
      CareerMilestone(
        id = "ms_6m",
        title = "Tier-1 Strategy & BizOps Lead at Top Decile Startup",
        horizon = "6M",
        horizonYears = 0.5f,
        targetQuarter = "2026-Q4",
        category = "ROLE_TRANSITION",
        targetMetric = "₹18-24 LPA + ₹5L ESOP Grant in Bengaluru",
        strategicRationale = "Establish operational command center, own dark store unit economics, and build executive board presence.",
        status = "ON_TRACK",
        priority = "CRITICAL"
      ),
      CareerMilestone(
        id = "ms_1y",
        title = "Senior Strategy Lead & Cross-Functional Operations Pod Head",
        horizon = "1Y",
        horizonYears = 1.0f,
        targetQuarter = "2027-Q2",
        category = "LEADERSHIP",
        targetMetric = "Lead 8-12 person operations pod, deliver ₹10Cr+ annual efficiency",
        strategicRationale = "Transition from high-performing individual contributor to strategic multiplier leading cross-functional teams.",
        status = "ON_TRACK",
        priority = "HIGH"
      ),
      CareerMilestone(
        id = "ms_2y",
        title = "Director of Business Operations / Chief of Staff",
        horizon = "2Y",
        horizonYears = 2.0f,
        targetQuarter = "2028-Q2",
        category = "ROLE_TRANSITION",
        targetMetric = "₹35-45 LPA + Substantial Secondary Equity Liquidity",
        strategicRationale = "Direct advisor to executive leadership; architecting corporate expansion and operational excellence.",
        status = "IN_PROGRESS",
        priority = "STRATEGIC"
      ),
      CareerMilestone(
        id = "ms_3y",
        title = "VP of Strategy & Operations / Regional General Manager",
        horizon = "3Y",
        horizonYears = 3.0f,
        targetQuarter = "2029-Q2",
        category = "COMPENSATION",
        targetMetric = "P&L Ownership of ₹100Cr+ Operations, ₹60L+ Total Target Cash/Equity",
        strategicRationale = "Full P&L authority and organizational leadership over multi-city infrastructure.",
        status = "NOT_STARTED",
        priority = "HIGH"
      ),
      CareerMilestone(
        id = "ms_5y",
        title = "Co-Founder / Venture Partner / Angel Operator",
        horizon = "5Y",
        horizonYears = 5.0f,
        targetQuarter = "2031-Q2",
        category = "VENTURE_EQUITY",
        targetMetric = "Net Worth Equity ₹2.5Cr+ / Active Angel Investment in 5 Early-Stage AI Startups",
        strategicRationale = "Building generational equity through venture creation and ecosystem-level advisory.",
        status = "NOT_STARTED",
        priority = "STRATEGIC"
      )
    )
    db.goalDecisionDao().insertAllMilestones(milestones)

    val milestoneTasks = listOf(
      // Tasks for 6M Milestone
      MilestoneTask(
        id = "task_6m_1",
        milestoneId = "ms_6m",
        title = "Master Dark Store Unit Economics & Advanced SQL Analytics",
        description = "Deep dive into Zepto/Blinkit unit economics, cohort retention, and SQL window functions.",
        category = "SKILLS",
        weightPoints = 2,
        isCompleted = true,
        completedAtTimestamp = 1724000000000L,
        dueDate = "2026-08-30"
      ),
      MilestoneTask(
        id = "task_6m_2",
        milestoneId = "ms_6m",
        title = "Deploy Autonomous Career Agent Suite for Outbound Pipeline",
        description = "Automate inbound job discovery, match scoring, and recruiter touchpoint sequencing.",
        category = "PORTFOLIO",
        weightPoints = 3,
        isCompleted = true,
        completedAtTimestamp = 1725000000000L,
        dueDate = "2026-09-05"
      ),
      MilestoneTask(
        id = "task_6m_3",
        milestoneId = "ms_6m",
        title = "Consolidate Verified Executive Fact Vault & Portfolio",
        description = "Organize all verified GPA, thesis, and internship metrics into single-click proof artifacts.",
        category = "PORTFOLIO",
        weightPoints = 2,
        isCompleted = true,
        completedAtTimestamp = 1725500000000L,
        dueDate = "2026-09-15"
      ),
      MilestoneTask(
        id = "task_6m_4",
        milestoneId = "ms_6m",
        title = "Ace Final Director Strategy Rounds at Microsoft and Zepto",
        description = "Execute prepared STAR stories, Azure commercial framing, and dark store expansion models.",
        category = "INTERVIEW",
        weightPoints = 3,
        isCompleted = false,
        dueDate = "2026-10-15"
      ),
      MilestoneTask(
        id = "task_6m_5",
        milestoneId = "ms_6m",
        title = "Negotiate Equity Vesting & Performance Bonus Structure",
        description = "Benchmark tier-1 compensation packages and negotiate favorable cliff and vesting terms.",
        category = "EXECUTION",
        weightPoints = 2,
        isCompleted = false,
        dueDate = "2026-11-01"
      ),

      // Tasks for 1Y Milestone
      MilestoneTask(
        id = "task_1y_1",
        milestoneId = "ms_1y",
        title = "Implement Real-Time Predictive Inventory Dispatch Model",
        description = "Bridge ML forecasts with daily warehouse and delivery fleet operations.",
        category = "EXECUTION",
        weightPoints = 3,
        isCompleted = true,
        completedAtTimestamp = 1725800000000L,
        dueDate = "2026-12-15"
      ),
      MilestoneTask(
        id = "task_1y_2",
        milestoneId = "ms_1y",
        title = "Formulate 30-60-90 Day Executive Leadership Playbook",
        description = "Create on-the-job operating principles for rapid team onboarding and impact delivery.",
        category = "EXECUTION",
        weightPoints = 2,
        isCompleted = false,
        dueDate = "2027-01-30"
      ),
      MilestoneTask(
        id = "task_1y_3",
        milestoneId = "ms_1y",
        title = "Directly Mentor 3 Associate Strategy Analysts",
        description = "Host weekly case structuring and data presentation coaching sessions.",
        category = "NETWORKING",
        weightPoints = 2,
        isCompleted = false,
        dueDate = "2027-03-15"
      ),
      MilestoneTask(
        id = "task_1y_4",
        milestoneId = "ms_1y",
        title = "Deliver High-Impact Board QBR Deck to VP of Operations & C-Suite",
        description = "Present quarterly efficiency gains, cost optimization, and automation yield.",
        category = "INTERVIEW",
        weightPoints = 3,
        isCompleted = false,
        dueDate = "2027-05-30"
      ),

      // Tasks for 2Y Milestone
      MilestoneTask(
        id = "task_2y_1",
        milestoneId = "ms_2y",
        title = "Drive Autonomous AI Agent Transformation Across 100+ Centers",
        description = "Implement automated replenishment and workforce scheduling agents.",
        category = "EXECUTION",
        weightPoints = 3,
        isCompleted = false,
        dueDate = "2027-11-15"
      ),
      MilestoneTask(
        id = "task_2y_2",
        milestoneId = "ms_2y",
        title = "Author & Publish Bengaluru Quick Commerce Strategic Case Study",
        description = "Publish in-depth whitepaper on sustainable last-mile logistics in tier-1 Indian metros.",
        category = "PORTFOLIO",
        weightPoints = 2,
        isCompleted = false,
        dueDate = "2028-02-01"
      ),
      MilestoneTask(
        id = "task_2y_3",
        milestoneId = "ms_2y",
        title = "Cultivate Advisory Relationships with 25+ Venture Capitalists & Founders",
        description = "Engage top fintech & logistics seed investors through tactical strategic commentary.",
        category = "NETWORKING",
        weightPoints = 2,
        isCompleted = false,
        dueDate = "2028-04-10"
      ),
      MilestoneTask(
        id = "task_2y_4",
        milestoneId = "ms_2y",
        title = "Lead Operational Due Diligence for Strategic Regional Acquisition",
        description = "Structure valuation model, operational audit, and integration roadmap.",
        category = "EXECUTION",
        weightPoints = 3,
        isCompleted = false,
        dueDate = "2028-06-01"
      ),

      // Tasks for 3Y Milestone
      MilestoneTask(
        id = "task_3y_1",
        milestoneId = "ms_3y",
        title = "Assume Full P&L Responsibility for South India Regional Hubs",
        description = "Lead multi-city revenue, operating costs, partner relations, and margin expansion.",
        category = "EXECUTION",
        weightPoints = 3,
        isCompleted = false,
        dueDate = "2028-10-01"
      ),
      MilestoneTask(
        id = "task_3y_2",
        milestoneId = "ms_3y",
        title = "Scale Organization from 20 to 80+ Strategic Operators & Leads",
        description = "Hire and mentor senior managers, regional leads, and specialized data engineers.",
        category = "NETWORKING",
        weightPoints = 2,
        isCompleted = false,
        dueDate = "2029-01-15"
      ),
      MilestoneTask(
        id = "task_3y_3",
        milestoneId = "ms_3y",
        title = "Achieve EBITDA Positive Unit Economics across All Managed Dark Stores",
        description = "Eliminate dark store waste, streamline delivery batches, and optimize vendor terms.",
        category = "EXECUTION",
        weightPoints = 3,
        isCompleted = false,
        dueDate = "2029-05-01"
      ),

      // Tasks for 5Y Milestone
      MilestoneTask(
        id = "task_5y_1",
        milestoneId = "ms_5y",
        title = "Incorporate Next-Gen AI Operations Platform / Venture Studio",
        description = "Build proprietary AI operational co-pilot for high-volume commerce enterprises.",
        category = "EXECUTION",
        weightPoints = 3,
        isCompleted = false,
        dueDate = "2030-08-01"
      ),
      MilestoneTask(
        id = "task_5y_2",
        milestoneId = "ms_5y",
        title = "Close Oversubscribed Institutional Seed Round ($1.5M - $3M)",
        description = "Pitch top venture firms (Lightspeed, Accel, Peak XV) with verified MVP traction.",
        category = "PORTFOLIO",
        weightPoints = 3,
        isCompleted = false,
        dueDate = "2031-01-15"
      ),
      MilestoneTask(
        id = "task_5y_3",
        milestoneId = "ms_5y",
        title = "Accept Board Seat at 2 Fast-Growing Logistics & AI Enterprises",
        description = "Provide executive guidance on scaling, organizational design, and enterprise AI adoption.",
        category = "NETWORKING",
        weightPoints = 2,
        isCompleted = false,
        dueDate = "2031-05-15"
      )
    )
    db.goalDecisionDao().insertAllTasks(milestoneTasks)

    // 11. Decisions
    val decisions = listOf(
      DecisionItem(
        id = "dec_01",
        title = "Target High-Upside Strategy & AI Roles vs Generic Mass IT Recruitment",
        question = "Should Adi accept generic mass IT consulting offers or concentrate firepower on Tier-1 Product/Strategy firms in Bengaluru?",
        chosenOption = "Focus exclusively on Tier-1 Strategy, Product Ops & High-Growth AI firms (Microsoft, Bain, Razorpay, Swiggy)",
        rationale = "Long-term career capital is 4.8x higher in Tier-1 strategy/ops roles compared to generic mass staffing. Opportunity cost of low-learning roles is immense.",
        evidenceSummary = "Verified salary and career velocity analysis shows 3-year promotion trajectory in Tier-1 strategy leads to ₹35L+ versus ₹12L in mass IT.",
        riskVsUpside = "Risk: Higher interview selectivity. Upside: Top-percentile brand, elite peer group, direct AI leverage.",
        dateCreated = "2026-08-10",
        status = "VALIDATED"
      ),
      DecisionItem(
        id = "dec_02",
        title = "Razorpay Offer vs Microsoft Final Round Decision Strategy",
        question = "How to handle the current ₹18.5L Razorpay offer timeline while completing Microsoft's final round?",
        chosenOption = "Request 5-day extension from Razorpay citing ongoing final discussions while preparing intensely for Microsoft Director round.",
        rationale = "Razorpay is enthusiastic and will accommodate a professional extension. Microsoft S+ Tier provides unmatched global mobility.",
        evidenceSummary = "Both companies score S/S+ on brand, compensation, and Bengaluru presence.",
        riskVsUpside = "Risk: Minor timeline tension. Upside: Maximized leverage and zero downside.",
        dateCreated = "2026-08-30",
        status = "ACTIVE"
      )
    )
    db.goalDecisionDao().insertAllDecisions(decisions)

    // 12. 22 Autonomous Agents Architecture
    val agents = listOf(
      AgentItem(
        id = "ag_01",
        agentNumber = 1,
        name = "Executive Career Agent",
        roleCategory = "STRATEGY",
        permissionScope = "READ_PROFILE, READ_MARKET, WRITE_RECOMMENDATIONS",
        status = "ACTIVE",
        lastRunTimestamp = "Just now",
        currentTask = "Optimizing Adi's 3-year career capital upside & decision roadmap",
        successCount = 142,
        failureCount = 0,
        confidenceScore = 98
      ),
      AgentItem(
        id = "ag_02",
        agentNumber = 2,
        name = "Profile & Fact Graph Agent",
        roleCategory = "IDENTITY",
        permissionScope = "READ_PROFILE, WRITE_PROFILE, VERIFY_FACTS",
        status = "ACTIVE",
        lastRunTimestamp = "10 mins ago",
        currentTask = "Maintaining verified fact store and profile completeness integrity",
        successCount = 98,
        failureCount = 0,
        confidenceScore = 99
      ),
      AgentItem(
        id = "ag_03",
        agentNumber = 3,
        name = "Job Discovery Agent",
        roleCategory = "DISCOVERY",
        permissionScope = "READ_EXTERNAL_FEEDS, WRITE_JOB_RECORDS",
        status = "ACTIVE",
        lastRunTimestamp = "15 mins ago",
        currentTask = "Monitoring Bengaluru & Global careers portals for S/S+ fresher openings",
        successCount = 380,
        failureCount = 2,
        confidenceScore = 96
      ),
      AgentItem(
        id = "ag_04",
        agentNumber = 4,
        name = "Company Discovery Agent",
        roleCategory = "INTELLIGENCE",
        permissionScope = "READ_MARKET, WRITE_COMPANY_PROFILES",
        status = "ACTIVE",
        lastRunTimestamp = "25 mins ago",
        currentTask = "Tracking 500+ Fortune & Bengaluru tech ecosystem organizations",
        successCount = 210,
        failureCount = 1,
        confidenceScore = 97
      ),
      AgentItem(
        id = "ag_05",
        agentNumber = 5,
        name = "Job Matching & Fit Engine",
        roleCategory = "INTELLIGENCE",
        permissionScope = "READ_PROFILE, READ_JOBS, COMPUTE_SCORES",
        status = "ACTIVE",
        lastRunTimestamp = "5 mins ago",
        currentTask = "Computing ADI Fit Score / 100 & Opportunity Score across active universe",
        successCount = 412,
        failureCount = 0,
        confidenceScore = 99
      ),
      AgentItem(
        id = "ag_06",
        agentNumber = 6,
        name = "Application Automation Agent",
        roleCategory = "EXECUTION",
        permissionScope = "PREPARE_APPLICATION, SUBMIT_WITH_APPROVAL",
        status = "ACTIVE",
        lastRunTimestamp = "1 hour ago",
        currentTask = "Preparing 1-tap application packages under Mode B (Approval)",
        successCount = 64,
        failureCount = 0,
        confidenceScore = 98
      ),
      AgentItem(
        id = "ag_07",
        agentNumber = 7,
        name = "Resume Intelligence Agent",
        roleCategory = "DOCUMENTS",
        permissionScope = "READ_FACTS, WRITE_RESUMES, ATS_OPTIMIZE",
        status = "ACTIVE",
        lastRunTimestamp = "30 mins ago",
        currentTask = "Dynamic ATS keyword synthesis from verified candidate facts",
        successCount = 89,
        failureCount = 0,
        confidenceScore = 98
      ),
      AgentItem(
        id = "ag_08",
        agentNumber = 8,
        name = "Cover Letter Engine",
        roleCategory = "DOCUMENTS",
        permissionScope = "READ_FACTS, GENERATE_LETTERS",
        status = "IDLE",
        lastRunTimestamp = "2 hours ago",
        currentTask = "Standing by for high-priority bespoke application requests",
        successCount = 45,
        failureCount = 0,
        confidenceScore = 95
      ),
      AgentItem(
        id = "ag_09",
        agentNumber = 9,
        name = "Recruiter Intelligence Agent",
        roleCategory = "NETWORKING",
        permissionScope = "READ_PUBLIC_CONTACTS, MAP_RECRUITERS",
        status = "ACTIVE",
        lastRunTimestamp = "45 mins ago",
        currentTask = "Mapping hiring managers & recruiters for S-tier Bengaluru roles",
        successCount = 130,
        failureCount = 1,
        confidenceScore = 94
      ),
      AgentItem(
        id = "ag_10",
        agentNumber = 10,
        name = "Networking & Outreach Agent",
        roleCategory = "NETWORKING",
        permissionScope = "DRAFT_MESSAGES, SCHEDULE_FOLLOWUPS",
        status = "ACTIVE",
        lastRunTimestamp = "1 hour ago",
        currentTask = "Generating personalized alumni referral and recruiter intro notes",
        successCount = 76,
        failureCount = 0,
        confidenceScore = 97
      ),
      AgentItem(
        id = "ag_11",
        agentNumber = 11,
        name = "Follow-Up Timing Agent",
        roleCategory = "AUTOMATION",
        permissionScope = "MONITOR_CALENDAR, SCHEDULE_ALERTS",
        status = "ACTIVE",
        lastRunTimestamp = "20 mins ago",
        currentTask = "Tracking 5-day and 10-day application communication cadences",
        successCount = 115,
        failureCount = 0,
        confidenceScore = 99
      ),
      AgentItem(
        id = "ag_12",
        agentNumber = 12,
        name = "Interview OS & Simulator Agent",
        roleCategory = "INTERVIEWS",
        permissionScope = "SIMULATE_INTERVIEWS, EVALUATE_STAR",
        status = "ACTIVE",
        lastRunTimestamp = "3 mins ago",
        currentTask = "Calibrating real-time scorecards and behavioral weakness detection",
        successCount = 52,
        failureCount = 0,
        confidenceScore = 97
      ),
      AgentItem(
        id = "ag_13",
        agentNumber = 13,
        name = "Skill Gap & Market Radar Agent",
        roleCategory = "SKILLS",
        permissionScope = "COMPUTE_SKILL_GAPS, MAP_LEARNING",
        status = "ACTIVE",
        lastRunTimestamp = "4 hours ago",
        currentTask = "Comparing Adi's profile against global job market demand indices",
        successCount = 88,
        failureCount = 0,
        confidenceScore = 96
      ),
      AgentItem(
        id = "ag_14",
        agentNumber = 14,
        name = "Project Lab & Proof Agent",
        roleCategory = "PROJECTS",
        permissionScope = "RECOMMEND_PROJECTS, MAP_PROOF",
        status = "IDLE",
        lastRunTimestamp = "5 hours ago",
        currentTask = "Mapping project artifacts to job requirement specifications",
        successCount = 34,
        failureCount = 0,
        confidenceScore = 98
      ),
      AgentItem(
        id = "ag_15",
        agentNumber = 15,
        name = "Market Intelligence Agent",
        roleCategory = "INTELLIGENCE",
        permissionScope = "READ_MARKET_TRENDS, COMPUTE_INDICES",
        status = "ACTIVE",
        lastRunTimestamp = "1 hour ago",
        currentTask = "Compiling Bengaluru Opportunity Index (89.4) & hiring velocities",
        successCount = 160,
        failureCount = 0,
        confidenceScore = 98
      ),
      AgentItem(
        id = "ag_16",
        agentNumber = 16,
        name = "Career Analytics & Funnel Agent",
        roleCategory = "ANALYTICS",
        permissionScope = "ANALYZE_FUNNEL, DIAGNOSE_DROPOFFS",
        status = "ACTIVE",
        lastRunTimestamp = "35 mins ago",
        currentTask = "Measuring conversion efficiency across 24 tracked applications",
        successCount = 92,
        failureCount = 0,
        confidenceScore = 99
      ),
      AgentItem(
        id = "ag_17",
        agentNumber = 17,
        name = "Personal Knowledge Base Agent",
        roleCategory = "KNOWLEDGE",
        permissionScope = "INDEX_KNOWLEDGE, REASON_NOTES",
        status = "ACTIVE",
        lastRunTimestamp = "2 hours ago",
        currentTask = "Indexing personal career notes, case studies, and business insights",
        successCount = 74,
        failureCount = 0,
        confidenceScore = 97
      ),
      AgentItem(
        id = "ag_18",
        agentNumber = 18,
        name = "Decision Support & Matrix Agent",
        roleCategory = "STRATEGY",
        permissionScope = "COMPUTE_DECISION_MATRICES, SIMULATE_SCENARIOS",
        status = "ACTIVE",
        lastRunTimestamp = "15 mins ago",
        currentTask = "Evaluating Microsoft vs Razorpay offer comparison parameters",
        successCount = 41,
        failureCount = 0,
        confidenceScore = 99
      ),
      AgentItem(
        id = "ag_19",
        agentNumber = 19,
        name = "Quality Control & Truth Validator",
        roleCategory = "GOVERNANCE",
        permissionScope = "AUDIT_OUTPUTS, BLOCK_FABRICATIONS",
        status = "ACTIVE",
        lastRunTimestamp = "Just now",
        currentTask = "Enforcing 100% strict verification against verified fact store",
        successCount = 512,
        failureCount = 0,
        confidenceScore = 100
      ),
      AgentItem(
        id = "ag_20",
        agentNumber = 20,
        name = "Security & Anti-Scam Guard",
        roleCategory = "SECURITY",
        permissionScope = "DETECT_SCAMS, BLOCK_UNSAFE_ACTIONS",
        status = "ACTIVE",
        lastRunTimestamp = "10 mins ago",
        currentTask = "Scanning job sources for illegitimate listings & fee requests",
        successCount = 280,
        failureCount = 0,
        confidenceScore = 100
      ),
      AgentItem(
        id = "ag_21",
        agentNumber = 21,
        name = "Automation Orchestrator",
        roleCategory = "ORCHESTRATION",
        permissionScope = "DISPATCH_TASKS, MANAGE_PIPELINES",
        status = "ACTIVE",
        lastRunTimestamp = "Just now",
        currentTask = "Managing multi-agent execution pipeline & event loop",
        successCount = 640,
        failureCount = 0,
        confidenceScore = 99
      ),
      AgentItem(
        id = "ag_22",
        agentNumber = 22,
        name = "Executive Notification Agent",
        roleCategory = "NOTIFICATIONS",
        permissionScope = "DISPATCH_CRITICAL_ALERTS, COMPOSE_BRIEFS",
        status = "ACTIVE",
        lastRunTimestamp = "8 mins ago",
        currentTask = "Broadcasting high-priority interview reminder & offer deadline alerts",
        successCount = 188,
        failureCount = 0,
        confidenceScore = 99
      )
    )
    db.agentDao().insertAllAgents(agents)

    // 13. Agent Audit Logs
    val logs = listOf(
      AgentAuditLog(
        timestamp = "Today 08:30 AM",
        agentName = "Executive Notification Agent",
        actionType = "BRIEFING_GENERATED",
        inputSummary = "Ingested daily market radar, pending interviews, and active applications",
        outputSummary = "Dispatched Adi Daily Intelligence Briefing to Command Center",
        status = "SUCCESS",
        confidence = 99,
        source = "INTERNAL_ORCHESTRATOR"
      ),
      AgentAuditLog(
        timestamp = "Today 07:45 AM",
        agentName = "Job Discovery Agent",
        actionType = "JOB_DISCOVERED",
        inputSummary = "Scanned Microsoft India & Google India careers portals",
        outputSummary = "Found 2 new S+ Analyst openings; verified legitimate career URLs",
        status = "SUCCESS",
        confidence = 98,
        source = "OFFICIAL_CAREERS_PORTAL"
      ),
      AgentAuditLog(
        timestamp = "Today 07:50 AM",
        agentName = "Job Matching & Fit Engine",
        actionType = "FIT_CALCULATED",
        inputSummary = "Evaluated Microsoft Strategy & BizOps role against Adi's profile",
        outputSummary = "Fit Score 95/100, Opportunity Score 98/100. Status: INTERVIEWING",
        status = "SUCCESS",
        confidence = 99,
        source = "FIT_ENGINE"
      ),
      AgentAuditLog(
        timestamp = "Yesterday 04:15 PM",
        agentName = "Quality Control Agent",
        actionType = "FACT_CHECK_PASSED",
        inputSummary = "Audited customized resume for Microsoft interview submission",
        outputSummary = "100% statements traced to Verified Candidate Fact Store",
        status = "SUCCESS",
        confidence = 100,
        source = "VERIFIED_FACT_STORE"
      )
    )
    for (log in logs) {
      db.agentDao().insertLog(log)
    }

    // 14. Market Radar
    val radar = listOf(
      MarketRadarItem(
        id = "rad_01",
        metricName = "Bengaluru Opportunity Index",
        region = "BENGALURU",
        value = "89.4 / 100",
        trend = "ACCELERATING",
        insightSummary = "High surge in Strategy, BizOps & AI Operations roles across Bellandur, Koramangala & Outer Ring Road tech parks."
      ),
      MarketRadarItem(
        id = "rad_02",
        metricName = "Fresher & Early-Career Demand (0-2 Yrs)",
        region = "INDIA",
        value = "78.2 / 100",
        trend = "ACCELERATING",
        insightSummary = "Top-tier MNCs actively recruiting for Graduate Analytics & Associate Strategy tracks with strong AI problem-solving."
      ),
      MarketRadarItem(
        id = "rad_03",
        metricName = "Global Remote Strategy & Ops Index",
        region = "GLOBAL",
        value = "82.6 / 100",
        trend = "STABLE",
        insightSummary = "Frontier AI and US/APAC SaaS scaleups expanding global contractor hubs for business & prompt ops."
      )
    )
    db.marketRadarDao().insertAllRadar(radar)

    // 15. Daily Briefing
    val briefing = DailyBriefing(
      id = "brief_today",
      dateString = "Tuesday, Executive Briefing",
      headline = "High Leverage Day: Microsoft Final Round Scheduled + Razorpay Decision Window",
      keyActions = "1. Complete 15-min AI Mock Interview for Microsoft Strategy Director.\n2. Review Offer Decision Matrix (Microsoft vs Razorpay).\n3. Dispatch follow-up note to Bain Capability Network recruiter.",
      marketNews = "Bengaluru tech hiring momentum up 14% this quarter with sharp demand for hybrid Business Analysis + AI capabilities.",
      strategicDirective = "Maximize leverage by completing Microsoft round before locking final compensation terms.",
      interviewFocus = "Azure AI ecosystem growth, structured commercial sizing, and family business supply chain automation STAR story."
    )
    db.marketRadarDao().insertBriefing(briefing)

    // 16. Missions
    val missions = listOf(
      com.example.data.model.MissionItem(
        id = "msn_01",
        title = "Secure Tier-1 Strategy / BizOps Role in Bengaluru",
        objective = "Land an S/S+ tier Associate Strategy, BizOps, or Product Operations offer in Bengaluru with ₹18L-₹28L compensation.",
        keyResults = "1. 3+ Final Rounds at S-Tier Companies\n2. 95%+ STAR Story Interview Score\n3. 100% Verified Fact ATS Alignment",
        milestones = "• Phase 1: Target Universe Mapping (Completed)\n• Phase 2: Application Sprint & Recruiter Outreach (Completed)\n• Phase 3: Technical & Case Interviews (In Progress)\n• Phase 4: Offer Negotiation (Upcoming)",
        tasks = "Complete Microsoft mock round; follow up with Google recruiter; review Razorpay in-hand CTC model.",
        assignedAgentId = "ag_01",
        dependencies = "Zero-Hallucination Fact Store, Microsoft Azure Case Study",
        deadline = "Q3 2026",
        riskLevel = "LOW",
        targetMetric = "Tier-1 Offer Secured (₹18L+)",
        progressPercent = 82,
        status = "ACTIVE"
      ),
      com.example.data.model.MissionItem(
        id = "msn_02",
        title = "Scale B2B AI Operations & Enterprise Analytics Venture",
        objective = "Establish high-margin consultancy delivering AI-augmented SQL pipelines, supply chain forecasting, and executive dashboards.",
        keyResults = "1. ₹25L+ Annual Contract Value pipeline\n2. 5 enterprise case studies\n3. 40%+ operational efficiency gains for clients",
        milestones = "• Family Enterprise Prototype (Completed - 42% Cycle Time Cut)\n• Freelance Client Acquisition (Completed - 3 Clients, ₹18L Pipeline)\n• Packaged SaaS Productization (In Progress)",
        tasks = "Publish Supply Chain Analytics teardown on LinkedIn; conduct outbound demo with 10 logistics SMEs.",
        assignedAgentId = "ag_11",
        dependencies = "Family Enterprise Case Study, Gemini API Analytics Pipeline",
        deadline = "Q4 2026",
        riskLevel = "MODERATE",
        targetMetric = "₹25L Contract Value",
        progressPercent = 68,
        status = "ACTIVE"
      ),
      com.example.data.model.MissionItem(
        id = "msn_03",
        title = "Build 100-Company Enterprise Recruiter Network",
        objective = "Establish direct, warm relationships with 100+ hiring leaders, alumni, and strategy directors across Bengaluru.",
        keyResults = "1. 100 targeted recruiter profiles mapped\n2. 40+ active conversations\n3. 10+ referral fast-tracks",
        milestones = "• Recruiter Mapping (Completed - 50+ contacts)\n• Tier-1 Alum Outreach (Completed - 28 connections)\n• Strategy Director Cadence (In Progress)",
        tasks = "Follow up with Bain Capability Network talent lead; connect with Swiggy Strategy VP.",
        assignedAgentId = "ag_09",
        dependencies = "Alumni Database, Verified Fact Credibility Pack",
        deadline = "Continuous",
        riskLevel = "LOW",
        targetMetric = "100+ Recruiter Connections",
        progressPercent = 90,
        status = "ACTIVE"
      )
    )
    db.missionDao().insertAll(missions)

    // 17. Automations
    val automations = listOf(
      com.example.data.model.AutomationItem(
        id = "auto_01",
        name = "Daily S-Tier Bengaluru Opportunity Scanner",
        triggerType = "SCHEDULED_EVENT",
        triggerCondition = "Every morning at 08:00 IST across official career portals",
        actionDescription = "Ingests new openings, computes ADI Fit Score (>80%), checks fresher friendliness, and queues notifications.",
        verificationPolicy = "Zero-scam domain verification against official company whitelist.",
        ownerAgentId = "ag_03",
        permissionLevel = "LEVEL_2_ANALYZE",
        executionCount = 142,
        retryCount = 0,
        failureState = "NONE",
        isEnabled = true
      ),
      com.example.data.model.AutomationItem(
        id = "auto_02",
        name = "Verified Fact Hallucination Interceptor",
        triggerType = "INCOMING_DATA",
        triggerCondition = "Upon generation of any resume bullet, cover letter, or outreach draft",
        actionDescription = "Cross-checks 100% of claims, metrics, and dates against the Verified Fact Store; rejects unverified statements.",
        verificationPolicy = "Deterministic match against verified evidence database.",
        ownerAgentId = "ag_07",
        permissionLevel = "LEVEL_1_READ",
        executionCount = 286,
        retryCount = 0,
        failureState = "NONE",
        isEnabled = true
      ),
      com.example.data.model.AutomationItem(
        id = "auto_03",
        name = "Recruiter Follow-up Cadence Scheduler",
        triggerType = "STATE_CHANGE",
        triggerCondition = "When application moves to 'APPLIED' status",
        actionDescription = "Creates a calendar event and drafts a respectful 5-day follow-up message for user review.",
        verificationPolicy = "Human approval required before dispatch.",
        ownerAgentId = "ag_10",
        permissionLevel = "LEVEL_3_PREPARE",
        executionCount = 38,
        retryCount = 0,
        failureState = "NONE",
        isEnabled = true
      ),
      com.example.data.model.AutomationItem(
        id = "auto_04",
        name = "Offer Tax & In-Hand Compensation Modeler",
        triggerType = "STATE_CHANGE",
        triggerCondition = "When application status updates to 'OFFER'",
        actionDescription = "Calculates monthly in-hand net salary, EPF deduction, professional tax, and prepares counter-offer pitch.",
        verificationPolicy = "New Tax Regime FY 2025-26 Indian Income Tax slabs formula.",
        ownerAgentId = "ag_14",
        permissionLevel = "LEVEL_2_ANALYZE",
        executionCount = 12,
        retryCount = 0,
        failureState = "NONE",
        isEnabled = true
      )
    )
    db.automationDao().insertAll(automations)

    // 17b. User-Configured Automation Rules
    val defaultAutomationRules = listOf(
      com.example.data.model.AutomationRule(
        id = "rule_zepto_inbound",
        name = "Zepto Recruiter Inbound Auto-Responder",
        triggerCategory = "EMAIL_RECEIVED",
        targetCompany = "Zepto",
        triggerCondition = "If email from company Zepto received",
        triggerFilterKeywords = "interview, availability, role, strategy, quick commerce",
        responseType = "AUTO_DRAFT_REPLY",
        automatedResponse = "Auto-draft high-conviction executive reply confirming interest & weekday 10 AM IST availability, notify mobile device",
        responseTemplate = "Dear Zepto Talent Team,\n\nThank you for reaching out regarding the opportunity! I have been closely tracking Zepto's quick commerce growth, hub density, and unit-economics optimization. I am very interested and available this week for an introductory discussion.\n\nBest regards,\nAditya",
        targetAgent = "Inbound Recruiter Specialist (ag_10)",
        requireHumanApproval = false,
        isEnabled = true,
        executionCount = 14,
        lastTriggeredTime = "Yesterday, 16:42 IST",
        lastExecutionResult = "Drafted availability email & queued mobile push alert"
      ),
      com.example.data.model.AutomationRule(
        id = "rule_google_interview_prep",
        name = "Google Interview Invite War-Room Swarm",
        triggerCategory = "INTERVIEW_INVITE",
        targetCompany = "Google",
        triggerCondition = "If interview invite received from company Google",
        triggerFilterKeywords = "round, interview, meet, video, invite",
        responseType = "AI_INTERVIEW_PREP",
        automatedResponse = "Trigger Gemini 360° Company Intelligence, compile executive brief, and pre-populate STAR stories",
        responseTemplate = "Synthesize Google Cloud / Strategy focus areas, benchmark candidate STAR stories against Google leadership principles, and generate 5 tactical mock questions.",
        targetAgent = "Interview OS Architect (ag_12)",
        requireHumanApproval = false,
        isEnabled = true,
        executionCount = 5,
        lastTriggeredTime = "3 days ago",
        lastExecutionResult = "Compiled 12-page executive interview war-room dossier"
      ),
      com.example.data.model.AutomationRule(
        id = "rule_offer_ctc_modeler",
        name = "Offer Letter In-Hand CTC Breakdown",
        triggerCategory = "APPLICATION_STATUS",
        targetCompany = "Any Company",
        triggerCondition = "If application status updates to 'OFFER'",
        triggerFilterKeywords = "offer, compensation, ctc, joining, letter",
        responseType = "PIPELINE_UPDATE",
        automatedResponse = "Calculate monthly in-hand net salary, EPF deduction, FY 25-26 tax liability, and generate counter-offer negotiation script",
        responseTemplate = "Apply New Tax Regime formula, estimate ESOP vesting schedule, and draft 15% counter-pitch based on market benchmark.",
        targetAgent = "Salary Economics Modeler (ag_14)",
        requireHumanApproval = true,
        isEnabled = true,
        executionCount = 2,
        lastTriggeredTime = "1 week ago",
        lastExecutionResult = "Calculated in-hand breakdown and stored in Pipeline CRM"
      ),
      com.example.data.model.AutomationRule(
        id = "rule_flipkart_connection_ack",
        name = "Flipkart Recruiter Connection Auto-Ack",
        triggerCategory = "RECRUITER_OUTREACH",
        targetCompany = "Flipkart",
        triggerCondition = "If email from company Flipkart received",
        triggerFilterKeywords = "connect, opportunity, sourcing, referral",
        responseType = "AUTO_DRAFT_REPLY",
        automatedResponse = "Send tailored proof-of-work link highlighting supply chain analytics and operational leadership",
        responseTemplate = "Hi {{recruiter_name}},\n\nGlad to connect! Here is my proof-of-work portfolio covering supply-chain optimization and cross-functional team scaling: https://portfolio.adi.dev\n\nLooking forward to speaking soon.",
        targetAgent = "Networking Outreach Agent (ag_09)",
        requireHumanApproval = true,
        isEnabled = true,
        executionCount = 9,
        lastTriggeredTime = "2 days ago",
        lastExecutionResult = "Drafted personalized portfolio link message"
      ),
      com.example.data.model.AutomationRule(
        id = "rule_rejection_feedback_cadence",
        name = "Rejection Post-Mortem & Feedback Request",
        triggerCategory = "APPLICATION_STATUS",
        targetCompany = "Any Company",
        triggerCondition = "If application moves to 'REJECTED' status",
        triggerFilterKeywords = "rejected, decline, not moving forward, other candidates",
        responseType = "CUSTOM_AGENT_ACTION",
        automatedResponse = "Send polite gratitude note requesting feedback, archive in CRM with lessons learned, and update target skill gap",
        responseTemplate = "Thank you for the update. While disappointed, I appreciate the team's time and would welcome any constructive feedback on areas for growth.",
        targetAgent = "Executive Pipeline Copilot (ag_01)",
        requireHumanApproval = true,
        isEnabled = false,
        executionCount = 3,
        lastTriggeredTime = "2 weeks ago",
        lastExecutionResult = "Sent polite reply and updated Skill Gap analysis"
      )
    )
    db.automationRuleDao().insertAll(defaultAutomationRules)

    // 18. Approvals
    val approvals = listOf(
      com.example.data.model.ApprovalItem(
        id = "appr_01",
        actionTitle = "Dispatch Recruiter Referral Note to Google Bengaluru Lead",
        whyReason = "Google Strategy Associate role posted 2 days ago; candidate fit score is 94%.",
        toolUsed = "Recruiter Outreach Engine via LinkedIn / Email Adapter",
        targetEntity = "Priya Sharma (Lead Talent Partner - Google India)",
        dataInvolved = "Candidate verified BBA honors, Hackathon finalist credentials, and custom interest memo",
        expectedResult = "Initiates formal referral review for Google Strategy & Operations track",
        riskLevel = "MEDIUM",
        requestedByAgent = "Recruiter Intelligence Agent",
        timestamp = "Today, 08:30 AM",
        status = "PENDING"
      ),
      com.example.data.model.ApprovalItem(
        id = "appr_02",
        actionTitle = "Submit Tailored Application Package to Bain Capability Network",
        whyReason = "Bain BCN early-career consulting analyst window closes in 4 days; candidate fit is 91%.",
        toolUsed = "1-Tap Application Automation Agent",
        targetEntity = "Bain & Company Official Portal (Req #BCN-2026-904)",
        dataInvolved = "ATS-tailored resume (v4.2), Bain case study cover letter, and verified GPA transcript",
        expectedResult = "Official submission logged in Bain ATS with confirmation reference",
        riskLevel = "HIGH",
        requestedByAgent = "Application Automation Agent",
        timestamp = "Yesterday, 04:15 PM",
        status = "PENDING"
      ),
      com.example.data.model.ApprovalItem(
        id = "appr_03",
        actionTitle = "Sync Verified Projects to LinkedIn & Public Portfolio Hub",
        whyReason = "Updated Family Enterprise Operations Teardown & Razorpay FinTech Hackathon credentials.",
        toolUsed = "Portfolio & Knowledge Sync Adapter",
        targetEntity = "Candidate LinkedIn Profile & Public GitHub/Portfolio",
        dataInvolved = "Project summaries, verified metrics (42% cycle time cut, 98% capstone score)",
        expectedResult = "Synchronizes public profile to match internal verified fact store",
        riskLevel = "LOW",
        requestedByAgent = "Executive Assistant Agent",
        timestamp = "2 days ago",
        status = "APPROVED"
      )
    )
    db.approvalDao().insertAll(approvals)

    // 19. Personal Memory
    val memories = listOf(
      com.example.data.model.PersonalMemoryItem(
        id = "mem_01",
        memoryLayer = "LONG_TERM_PREFERENCES",
        title = "Target Location & Base Philosophy",
        content = "Prefers Bengaluru tech corridor (Outer Ring Road, Bellandur, Koramangala, Indiranagar, Whitefield) or high-flexibility remote. Target base compensation floor is ₹14L CTC.",
        confidence = 100,
        sourceAgent = "CEO / Strategy Agent",
        timestamp = "Sep 2026",
        isProtected = true
      ),
      com.example.data.model.PersonalMemoryItem(
        id = "mem_02",
        memoryLayer = "VERIFIED_FACT",
        title = "Family Enterprise Modernization Metric",
        content = "Reduced order fulfillment cycle time from 14 days to 8 days (42% improvement) through SQL database & inventory automation. Maintained zero stockout emergencies over 9 months.",
        confidence = 100,
        sourceAgent = "Zero-Hallucination Fact Store",
        timestamp = "Verified May 2026",
        isProtected = true
      ),
      com.example.data.model.PersonalMemoryItem(
        id = "mem_03",
        memoryLayer = "ACTIVE_TASK",
        title = "Microsoft Strategy Final Round Preparation",
        content = "Focus areas: Azure AI monetization models, enterprise customer expansion in India, and commercial supply chain case walkthrough.",
        confidence = 95,
        sourceAgent = "Interview OS Agent",
        timestamp = "Active Today",
        isProtected = false
      ),
      com.example.data.model.PersonalMemoryItem(
        id = "mem_04",
        memoryLayer = "DECISION",
        title = "Prioritize Tier-1 Brand Upside over Early-Stage Equity",
        content = "Strategic decision: First 3-5 years of career must optimize for Tier-1 brand pedigree (Microsoft, Bain, Google, Razorpay) to maximize lifelong career capital before launching scalable venture.",
        confidence = 98,
        sourceAgent = "CEO / Strategy Agent",
        timestamp = "Aug 2026",
        isProtected = true
      )
    )
    db.personalMemoryDao().insertAll(memories)

    // 20. Knowledge Graph
    val nodes = listOf(
      com.example.data.model.KnowledgeNode(id = "kn_adi", nodeType = "USER", label = "Adi (Candidate)", category = "Identity", details = "BBA International Business Honors Graduate", significanceScore = 100),
      com.example.data.model.KnowledgeNode(id = "kn_deg", nodeType = "EXPERIENCE", label = "BBA Honors Degree", category = "Education", details = "Bengaluru University, GPA 3.82 / 4.0", significanceScore = 90),
      com.example.data.model.KnowledgeNode(id = "kn_fam", nodeType = "EXPERIENCE", label = "Family Enterprise Modernization", category = "Operations", details = "42% Cycle Time Cut, SQL & Inventory Digitization", significanceScore = 95),
      com.example.data.model.KnowledgeNode(id = "kn_hack", nodeType = "PROJECT", label = "Razorpay FinTech Hackathon", category = "Product Strategy", details = "National Top 5 Finalist out of 400+ Teams", significanceScore = 88),
      com.example.data.model.KnowledgeNode(id = "kn_msft", nodeType = "COMPANY", label = "Microsoft India", category = "Target Enterprise", details = "S+ Tier, Final Round Scheduled", significanceScore = 98),
      com.example.data.model.KnowledgeNode(id = "kn_bain", nodeType = "COMPANY", label = "Bain & Company", category = "Target Enterprise", details = "S+ Tier, Applied & Tracked", significanceScore = 94),
      com.example.data.model.KnowledgeNode(id = "kn_rzp", nodeType = "COMPANY", label = "Razorpay", category = "Target Enterprise", details = "S Tier, Active Offer ₹18.5L CTC", significanceScore = 96),
      com.example.data.model.KnowledgeNode(id = "kn_sk_sql", nodeType = "SKILL", label = "Advanced SQL & Analytics", category = "Core Capability", details = "Database schemas, window functions, PowerBI", significanceScore = 92),
      com.example.data.model.KnowledgeNode(id = "kn_sk_strat", nodeType = "SKILL", label = "Strategy & Market Sizing", category = "Core Capability", details = "TAM/SAM modeling, unit economics, BBA Capstone", significanceScore = 94),
      com.example.data.model.KnowledgeNode(id = "kn_msn_job", nodeType = "MISSION", label = "Secure Tier-1 Strategy Role", category = "Active Mission", details = "82% Complete, Target ₹18L-₹28L", significanceScore = 99)
    )
    val edges = listOf(
      com.example.data.model.KnowledgeEdge(id = "ke_1", sourceId = "kn_adi", targetId = "kn_deg", relationshipType = "DEMONSTRATES", weight = 1.0f),
      com.example.data.model.KnowledgeEdge(id = "ke_2", sourceId = "kn_adi", targetId = "kn_fam", relationshipType = "DEMONSTRATES", weight = 1.0f),
      com.example.data.model.KnowledgeEdge(id = "ke_3", sourceId = "kn_adi", targetId = "kn_hack", relationshipType = "DEMONSTRATES", weight = 0.9f),
      com.example.data.model.KnowledgeEdge(id = "ke_4", sourceId = "kn_fam", targetId = "kn_sk_sql", relationshipType = "DEMONSTRATES", weight = 1.0f),
      com.example.data.model.KnowledgeEdge(id = "ke_5", sourceId = "kn_deg", targetId = "kn_sk_strat", relationshipType = "DEMONSTRATES", weight = 1.0f),
      com.example.data.model.KnowledgeEdge(id = "ke_6", sourceId = "kn_adi", targetId = "kn_msn_job", relationshipType = "DRIVES", weight = 1.0f),
      com.example.data.model.KnowledgeEdge(id = "ke_7", sourceId = "kn_msn_job", targetId = "kn_msft", relationshipType = "TARGETS", weight = 1.0f),
      com.example.data.model.KnowledgeEdge(id = "ke_8", sourceId = "kn_msn_job", targetId = "kn_bain", relationshipType = "TARGETS", weight = 0.9f),
      com.example.data.model.KnowledgeEdge(id = "ke_9", sourceId = "kn_msn_job", targetId = "kn_rzp", relationshipType = "TARGETS", weight = 0.95f)
    )
    db.knowledgeDao().insertAllNodes(nodes)
    db.knowledgeDao().insertAllEdges(edges)

    // 21. Integrations
    val integrations = listOf(
      com.example.data.model.IntegrationItem(id = "int_gemini", serviceName = "Google Gemini 2.5 Flash API", serviceType = "AI_SERVICES", status = "CONNECTED", description = "Live multi-agent orchestration, reasoning, ATS resume tailoring & interview evaluation.", iconEmoji = "✨", lastSyncTimestamp = "Active"),
      com.example.data.model.IntegrationItem(id = "int_room", serviceName = "Room SQLite Encrypted Local Vault", serviceType = "DATABASE", status = "CONNECTED", description = "Local zero-cloud-leakage persistent store for candidate facts, applications, and logs.", iconEmoji = "🛡️", lastSyncTimestamp = "Real-time"),
      com.example.data.model.IntegrationItem(id = "int_gmail", serviceName = "Google Workspace / Gmail", serviceType = "GMAIL", status = "REQUIRES_AUTHORIZATION", description = "Automated interview invitation tracking and recruiter follow-up drafting.", iconEmoji = "✉️", lastSyncTimestamp = "Pending Auth"),
      com.example.data.model.IntegrationItem(id = "int_cal", serviceName = "Google Calendar", serviceType = "GOOGLE_CALENDAR", status = "REQUIRES_AUTHORIZATION", description = "Sync interview rounds, case assessments, and follow-up deadlines.", iconEmoji = "📅", lastSyncTimestamp = "Pending Auth"),
      com.example.data.model.IntegrationItem(id = "int_drive", serviceName = "Google Drive & Docs", serviceType = "GOOGLE_DRIVE", status = "REQUIRES_AUTHORIZATION", description = "Export tailored PDFs, CV packages, and case study decks directly to Drive.", iconEmoji = "📁", lastSyncTimestamp = "Pending Auth"),
      com.example.data.model.IntegrationItem(id = "int_gh", serviceName = "GitHub Enterprise & Repos", serviceType = "GITHUB", status = "CONNECTED", description = "Sync project repositories, SQL schemas, and automation scripts.", iconEmoji = "🐙", lastSyncTimestamp = "Synced 1h ago"),
      com.example.data.model.IntegrationItem(id = "int_slack", serviceName = "Slack Operational Webhooks", serviceType = "SLACK", status = "DEMO_MODE", description = "Real-time notifications for agent discoveries and critical approval requests.", iconEmoji = "💬", lastSyncTimestamp = "Demo Mode"),
      com.example.data.model.IntegrationItem(id = "int_mcp", serviceName = "Model Context Protocol (MCP) Hub", serviceType = "MCP_TOOL", status = "CONNECTED", description = "Standardized interface for tool dispatch, file system access, and external data feeds.", iconEmoji = "⚡", lastSyncTimestamp = "Active")
    )
    db.integrationDao().insertAll(integrations)

    // 22. System Health Metrics
    val healthMetrics = listOf(
      com.example.data.model.SystemHealthMetric(id = "sh_01", metricName = "Gemini API Latency", category = "API_LATENCY", value = "138", unit = "ms", status = "HEALTHY", description = "Average response latency for structured career copilot inferences."),
      com.example.data.model.SystemHealthMetric(id = "sh_02", metricName = "Room DB Query Latency", category = "DB_PERFORMANCE", value = "1.1", unit = "ms", status = "HEALTHY", description = "High-speed indexed local database retrieval performance."),
      com.example.data.model.SystemHealthMetric(id = "sh_03", metricName = "Autonomous Agent Fleet Health", category = "AGENT_UPTIME", value = "100%", unit = "uptime", status = "HEALTHY", description = "22 agents operating with zero infinite loops and zero unhandled faults."),
      com.example.data.model.SystemHealthMetric(id = "sh_04", metricName = "Zero-Hallucination Integrity", category = "SECURITY_SCORE", value = "100%", unit = "score", status = "HEALTHY", description = "All generated resumes, cover letters, and pitch memos verified against local fact store."),
      com.example.data.model.SystemHealthMetric(id = "sh_05", metricName = "Memory & Garbage Footprint", category = "MEMORY_USAGE", value = "42.8", unit = "MB", status = "HEALTHY", description = "Lean Android JVM memory consumption with zero memory leaks."),
      com.example.data.model.SystemHealthMetric(id = "sh_06", metricName = "Model Token Efficiency", category = "MODEL_TOKENS", value = "96.5%", unit = "efficiency", status = "HEALTHY", description = "Optimized prompt templates with deterministic fallback heuristics.")
    )
    db.systemHealthDao().insertAll(healthMetrics)

    // 23. Local User Career Strategy Notes (Offline Accessible)
    val initialNotes = listOf(
      CareerStrategyNote(
        id = "note_strat_01",
        title = "Tier-1 Bengaluru High-Velocity Tech Strategy",
        category = "TARGET_ROLES",
        content = "Focus intensely on Associate Business Analyst & Strategy Ops roles at Zepto, Swiggy, Razorpay. Frame BBA background around quantitative logistics, supply chain unit economics, and 42% operational cycle reduction in family enterprise. Target compensation: ₹16L-₹24L LPA.",
        targetCompany = "Zepto / Swiggy / Razorpay",
        targetRole = "Business Analyst / Strategy Ops",
        tags = "Bengaluru, High-Growth, ₹16L+, Unit Economics",
        isPinned = true,
        createdAt = System.currentTimeMillis() - 86400000L * 3,
        updatedAt = System.currentTimeMillis() - 3600000L * 5
      ),
      CareerStrategyNote(
        id = "note_strat_02",
        title = "Interview Battle-Plan: The 42% Cycle Time Narrative",
        category = "INTERVIEW_PREP",
        content = "When answering 'Walk me through a difficult problem':\n1. Situation: Inventory bottlenecks & manual ledger in wholesale enterprise.\n2. Task: Modernize dispatch and forecast demand.\n3. Action: Built automated SQL/Python tracker & reorder alerts.\n4. Result: Zero stockouts, 42% faster dispatch, ₹8.4L annualized savings.",
        targetCompany = "All Companies",
        targetRole = "Business Analyst",
        tags = "STAR Framework, Leadership, Quantifiable Impact",
        isPinned = true,
        createdAt = System.currentTimeMillis() - 86400000L * 2,
        updatedAt = System.currentTimeMillis() - 3600000L * 2
      ),
      CareerStrategyNote(
        id = "note_strat_03",
        title = "Compensation & Offer Negotiation Playbook",
        category = "NEGOTIATION",
        content = "Strict compensation rule: Walk-away threshold is ₹12 LPA. Desired anchor: ₹18-20 LPA base + performance bonus. If base is constrained, negotiate joining bonus, accelerated 6-month appraisal, and equity vesting.",
        targetCompany = "Tier-1 Startups",
        targetRole = "Strategy Analyst",
        tags = "Salary, Negotiation, ₹18L Anchor",
        isPinned = false,
        createdAt = System.currentTimeMillis() - 86400000L,
        updatedAt = System.currentTimeMillis() - 3600000L
      )
    )
    db.careerStrategyNoteDao().insertAll(initialNotes)

    // 24. Company Bookmarks (Offline Accessible)
    val initialBookmarks = listOf(
      CompanyBookmark(
        companyId = "comp_zepto",
        companyName = "Zepto",
        logoEmoji = "⚡",
        industry = "Quick Commerce",
        tier = "S+",
        priority = "DREAM",
        personalNotes = "Top target in Bellandur. High-velocity hiring for Operations Associates and Growth Analysts.",
        targetRole = "Associate Business Analyst",
        bookmarkedAt = System.currentTimeMillis() - 86400000L * 4,
        alertOnNewJobs = true
      ),
      CompanyBookmark(
        companyId = "comp_swiggy",
        companyName = "Swiggy",
        logoEmoji = "🛵",
        industry = "Food Tech & Quick Commerce",
        tier = "S+",
        priority = "HIGH",
        personalNotes = "Headquarters on Outer Ring Road. Looking for Strategy Associate openings in Instamart.",
        targetRole = "Strategy & Operations Associate",
        bookmarkedAt = System.currentTimeMillis() - 86400000L * 3,
        alertOnNewJobs = true
      ),
      CompanyBookmark(
        companyId = "comp_razorpay",
        companyName = "Razorpay",
        logoEmoji = "💳",
        industry = "Fintech",
        tier = "S",
        priority = "HIGH",
        personalNotes = "Koramangala campus. Fintech operations and merchant payment analytics. Excellent culture.",
        targetRole = "Operations Associate",
        bookmarkedAt = System.currentTimeMillis() - 86400000L * 2,
        alertOnNewJobs = true
      )
    )
    db.companyBookmarkDao().insertAll(initialBookmarks)

    // 25. Automation Task Logs (Offline Accessible)
    val initialTaskLogs = listOf(
      AutomationTaskLog(
        taskId = "JOB_RADAR_SWARM",
        taskName = "Autonomous Radar Crawler",
        triggerSource = "PERIODIC_RADAR",
        timestamp = System.currentTimeMillis() - 3600000L * 2,
        formattedTime = "2 hours ago",
        status = "SUCCESS",
        summary = "Scanned 10 target companies across Bengaluru. Filtered for Early-Career Fresher friendly roles.",
        details = "Identified 4 high-match roles at Zepto, Swiggy, and Razorpay exceeding 85% Adi-Fit index. Dispatched local push notifications.",
        itemsDiscoveredCount = 4,
        highPriorityMatchesCount = 2,
        executionDurationMs = 1420L
      ),
      AutomationTaskLog(
        taskId = "RESUME_TAILOR_SWARM",
        taskName = "ATS Optimization Engine",
        triggerSource = "SYSTEM_AUTOMATION",
        timestamp = System.currentTimeMillis() - 3600000L * 4,
        formattedTime = "4 hours ago",
        status = "SUCCESS",
        summary = "Tailored executive resume for Zepto Business Analyst posting.",
        details = "Synthesized bullet points aligning with demand for SQL, supply chain forecasting, and cost optimization.",
        itemsDiscoveredCount = 1,
        highPriorityMatchesCount = 1,
        executionDurationMs = 2150L
      ),
      AutomationTaskLog(
        taskId = "MARKET_INTEL_AGENT",
        taskName = "Bengaluru Tech Talent Radar",
        triggerSource = "SCHEDULED_EVENT",
        timestamp = System.currentTimeMillis() - 3600000L * 12,
        formattedTime = "12 hours ago",
        status = "SUCCESS",
        summary = "Aggregated compensation benchmarks and hiring velocity metrics for Q3.",
        details = "Updated compensation tier distributions: Tier-1 entry level average is ₹16.4 LPA in Bellandur & Outer Ring Road.",
        itemsDiscoveredCount = 3,
        highPriorityMatchesCount = 0,
        executionDurationMs = 980L
      )
    )
    db.automationTaskLogDao().insertAll(initialTaskLogs)

    // 26. Career Notes (Room Offline Storage)
    val initialCareerNotes = listOf(
      CareerNote(
        id = "cn_01",
        title = "Bengaluru Fast-Track Strategy: Ops & Growth",
        category = "STRATEGY",
        content = "Target fast-scaling tech companies with Bengaluru HQs (Zepto, Swiggy, Razorpay, Cred). Emphasize hands-on inventory system builds, SQL dashboards, and rapid operational turnaround.",
        targetCompany = "Zepto / Swiggy / Razorpay",
        targetRole = "Associate Business Analyst",
        tags = "Bengaluru, Strategy, Growth, ₹16L-₹24L",
        isPinned = true,
        createdAt = System.currentTimeMillis() - 86400000L * 2,
        updatedAt = System.currentTimeMillis() - 3600000L * 4
      ),
      CareerNote(
        id = "cn_02",
        title = "STAR Story Preparation: 42% Cycle Reduction",
        category = "INTERVIEW_PREP",
        content = "Highlight the end-to-end execution of the inventory tracking system and reorder automation script that achieved a 42% reduction in delivery bottlenecks and ₹8.4L in annual savings.",
        targetCompany = "Tier-1 Tech Companies",
        targetRole = "Operations & Strategy Analyst",
        tags = "STAR, Leadership, Quantifiable Impact",
        isPinned = true,
        createdAt = System.currentTimeMillis() - 86400000L,
        updatedAt = System.currentTimeMillis() - 3600000L * 2
      )
    )
    db.careerNoteDao().insertAll(initialCareerNotes)

    // 27. Automation Tasks (Room Offline Storage)
    val initialAutomationTasks = listOf(
      AutomationTask(
        id = "task_radar_crawler",
        title = "Bengaluru Tech Job Radar Crawler",
        taskType = "JOB_RADAR",
        description = "Monitors Zepto, Swiggy, and Razorpay career endpoints for new Associate BA and Strategy Ops roles matching profile.",
        frequency = "DAILY",
        triggerCondition = "SCHEDULED_PERIODIC",
        isEnabled = true,
        lastExecutedTimestamp = System.currentTimeMillis() - 3600000L * 2,
        nextScheduledTimestamp = System.currentTimeMillis() + 3600000L * 22,
        status = "IDLE",
        priorityLevel = "CRITICAL",
        executionCount = 14,
        successCount = 14,
        failureCount = 0,
        lastResultSummary = "Scanned 10 target companies. Found 4 high-match postings."
      ),
      AutomationTask(
        id = "task_resume_optimizer",
        title = "ATS Resume Bullet Tailoring Engine",
        taskType = "RESUME_REFRESH",
        description = "Dynamically adjusts resume action verbs and keywords based on target job description requirements.",
        frequency = "ON_DEMAND",
        triggerCondition = "EVENT_TRIGGERED",
        isEnabled = true,
        lastExecutedTimestamp = System.currentTimeMillis() - 3600000L * 5,
        nextScheduledTimestamp = null,
        status = "COMPLETED",
        priorityLevel = "HIGH",
        executionCount = 8,
        successCount = 8,
        failureCount = 0,
        lastResultSummary = "Tailored bullets for Zepto and Swiggy submissions."
      ),
      AutomationTask(
        id = "task_market_intel",
        title = "Bengaluru Compensation & Velocity Radar",
        taskType = "MARKET_INTEL",
        description = "Collects compensation benchmarks and quarterly talent velocity trends across Koramangala and Outer Ring Road tech hubs.",
        frequency = "WEEKLY",
        triggerCondition = "SCHEDULED_PERIODIC",
        isEnabled = true,
        lastExecutedTimestamp = System.currentTimeMillis() - 86400000L * 2,
        nextScheduledTimestamp = System.currentTimeMillis() + 86400000L * 5,
        status = "IDLE",
        priorityLevel = "MEDIUM",
        executionCount = 6,
        successCount = 6,
        failureCount = 0,
        lastResultSummary = "Updated market compensation median for Entry-Level Strategy Analysts."
      )
    )
    db.automationTaskDao().insertAll(initialAutomationTasks)

    // 29. Target Companies & Granular Market Intelligence (Room Persistence Schema)
    val targetCompanies = listOf(
      TargetCompany(
        id = "tc_google_india",
        name = "Google India",
        tickerOrDomain = "google.com",
        logoEmoji = "🌐",
        industry = "Big Tech & Cloud AI",
        subIndustry = "APAC Business Operations & Commercial Strategy",
        hqLocation = "Mountain View, CA / Bengaluru, India",
        bengaluruHub = "Bagmane Constellation Tech Park, Outer Ring Road",
        priorityTier = "TIER_1_DREAM",
        trackingStatus = "ACTIVE_TARGET",
        targetRoleTitle = "Strategy & Operations Analyst (Global Business Solutions)",
        targetCompensationRange = "₹26,00,000 - ₹34,00,000 CTC + Equity",
        culturalFitScore = 96,
        hiringVelocity = "SURGING",
        targetQuarter = "Q3 2026",
        websiteUrl = "https://about.google",
        careersUrl = "https://careers.google.com/jobs/results/?location=Bengaluru%2C%20India",
        notes = "Focus on Gemini enterprise operations and APAC commercial intelligence. Strategic contact in Bellandur campus.",
        isWatched = true
      ),
      TargetCompany(
        id = "tc_microsoft_india",
        name = "Microsoft India",
        tickerOrDomain = "microsoft.com",
        logoEmoji = "🔷",
        industry = "Enterprise Cloud & AI",
        subIndustry = "Azure Commercial Strategy & Customer Success",
        hqLocation = "Redmond, WA / Bengaluru, India",
        bengaluruHub = "Prestige Ferns Galaxy, Bellandur, Bengaluru",
        priorityTier = "TIER_1_DREAM",
        trackingStatus = "INTERVIEWING",
        targetRoleTitle = "Associate Strategy & Business Operations Analyst",
        targetCompensationRange = "₹22,00,000 - ₹28,00,000 CTC + Stock",
        culturalFitScore = 94,
        hiringVelocity = "SURGING",
        targetQuarter = "Q3 2026",
        websiteUrl = "https://microsoft.com",
        careersUrl = "https://careers.microsoft.com/v2/global/en/locations/bangalore.html",
        notes = "Interview round scheduled for cloud commercial strategy. Prepared with SQL revenue models and Azure OpenAI adoption study.",
        isWatched = true
      ),
      TargetCompany(
        id = "tc_razorpay",
        name = "Razorpay",
        tickerOrDomain = "razorpay.com",
        logoEmoji = "⚡",
        industry = "Fintech & Payments",
        subIndustry = "Merchant Banking & AI Financial Workflows",
        hqLocation = "Bengaluru, Karnataka, India",
        bengaluruHub = "Koramangala 4th Block, Bengaluru",
        priorityTier = "TIER_1_DREAM",
        trackingStatus = "APPLICATION_STAGED",
        targetRoleTitle = "Associate Product Operations & Analytics Lead",
        targetCompensationRange = "₹18,00,000 - ₹24,00,000 CTC + ESOPs",
        culturalFitScore = 92,
        hiringVelocity = "SURGING",
        targetQuarter = "Q3 2026",
        websiteUrl = "https://razorpay.com",
        careersUrl = "https://razorpay.com/jobs/",
        notes = "Fast-moving hiring cycles. Referral staged via alumni network in payment routing ops.",
        isWatched = true
      ),
      TargetCompany(
        id = "tc_swiggy",
        name = "Swiggy",
        tickerOrDomain = "swiggy.com",
        logoEmoji = "🍔",
        industry = "Consumer Tech & Quick Commerce",
        subIndustry = "Instamart Supply Chain & Dark Store Operations",
        hqLocation = "Bengaluru, Karnataka, India",
        bengaluruHub = "Embassy Tech Village, Outer Ring Road, Bengaluru",
        priorityTier = "TIER_2_STRATEGIC",
        trackingStatus = "NETWORKING",
        targetRoleTitle = "Supply Chain & City Operations Analyst",
        targetCompensationRange = "₹16,00,000 - ₹22,00,000 CTC",
        culturalFitScore = 88,
        hiringVelocity = "STEADY",
        targetQuarter = "Q4 2026",
        websiteUrl = "https://swiggy.com",
        careersUrl = "https://careers.swiggy.com",
        notes = "Instamart dark store margin efficiency and unit economics focus. Connected with Central Ops Manager.",
        isWatched = false
      ),
      TargetCompany(
        id = "tc_cred",
        name = "CRED",
        tickerOrDomain = "cred.club",
        logoEmoji = "💎",
        industry = "Fintech & Lifestyle Commerce",
        subIndustry = "Credit Cards, Lending & High-Trust Commerce",
        hqLocation = "Bengaluru, Karnataka, India",
        bengaluruHub = "Indiranagar 100 Feet Road, Bengaluru",
        priorityTier = "TIER_2_STRATEGIC",
        trackingStatus = "EXPLORING",
        targetRoleTitle = "Strategy & Business Operations Associate",
        targetCompensationRange = "₹20,00,000 - ₹28,00,000 CTC + ESOPs",
        culturalFitScore = 89,
        hiringVelocity = "SELECTIVE",
        targetQuarter = "Q4 2026",
        websiteUrl = "https://cred.club",
        careersUrl = "https://cred.club/careers",
        notes = "Focus on vehicle management platform monetization and member retention loops.",
        isWatched = false
      ),
      TargetCompany(
        id = "tc_nvidia_india",
        name = "NVIDIA India",
        tickerOrDomain = "nvidia.com",
        logoEmoji = "🟢",
        industry = "Semiconductor & Accelerated Computing",
        subIndustry = "Enterprise AI Infrastructure & Sovereign AI Cloud",
        hqLocation = "Santa Clara, CA / Bengaluru, India",
        bengaluruHub = "Manyata Tech Park, Nagavara, Bengaluru",
        priorityTier = "TIER_1_DREAM",
        trackingStatus = "ACTIVE_TARGET",
        targetRoleTitle = "Enterprise Operations & Partner Program Analyst",
        targetCompensationRange = "₹28,00,000 - ₹38,00,000 CTC + RSUs",
        culturalFitScore = 95,
        hiringVelocity = "SURGING",
        targetQuarter = "Q3 2026",
        websiteUrl = "https://nvidia.com",
        careersUrl = "https://www.nvidia.com/en-in/about-nvidia/careers/",
        notes = "India AI supercomputing infrastructure buildout. Looking for operational leverage across partner clouds.",
        isWatched = true
      )
    )
    db.targetCompanyDao().insertTargetCompanies(targetCompanies)

    val marketIntelList = listOf(
      TargetCompanyMarketIntel(
        id = "intel_google_01",
        targetCompanyId = "tc_google_india",
        companyName = "Google India",
        headlineSummary = "Google expands India AI CoE; opening 300+ commercial strategy & product operations positions in Bengaluru.",
        marketSentiment = "BULLISH",
        sentimentScore = 95,
        strategicExpansionSignals = "Aggressive expansion in Google Cloud India enterprise solutions and Gemini developer ecosystem with a multi-million dollar regional infrastructure commitment.",
        executiveLeadershipShifts = "New VP of APAC Customer Solutions appointed to scale go-to-market operations across India and ASEAN.",
        hiringTrends = "Heavy demand for Business Analysts with SQL, automated data pipelines, and prompt engineering workflow skills.",
        financialHealthAndGrowth = "Alphabet reported 15% revenue growth; Google Cloud segment operating income surged over 40% YoY.",
        competitiveMoat = "Massive global search distribution, Android ecosystem reach, and cutting-edge Gemini 1.5 Pro & Flash multi-modal models.",
        risksAndHeadwinds = "Antitrust scrutiny in app store billing models, intensifying competition from Microsoft/OpenAI in enterprise cloud.",
        interviewTalkingPoints = "Highlight BBA trade logistics, prompt engineering automation, and structured root-cause analysis for enterprise sales operations.",
        confidenceRating = 97,
        sourceGrounding = "Bloomberg, Economic Times Tech, Google Press Releases"
      ),
      TargetCompanyMarketIntel(
        id = "intel_microsoft_01",
        targetCompanyId = "tc_microsoft_india",
        companyName = "Microsoft India",
        headlineSummary = "Azure AI infrastructure contracts surging across Indian banking & telecom sectors; commercial operations team expanding.",
        marketSentiment = "BULLISH",
        sentimentScore = 93,
        strategicExpansionSignals = "Investing $3.3B in new India cloud region data centers and commercial go-to-market acceleration.",
        executiveLeadershipShifts = "Country Head of Cloud Commercial Solutions leading new graduate talent acquisition initiative.",
        hiringTrends = "Strong intake for Associate Strategy & Business Operations roles with cross-functional analytics prowess.",
        financialHealthAndGrowth = "Azure Cloud revenue up 29% in constant currency; enterprise AI customer base expanding at triple digits.",
        competitiveMoat = "Deep enterprise enterprise integration through Office 365, Copilot, and Azure ecosystem.",
        risksAndHeadwinds = "Supply constraints on high-end GPU clusters; data privacy compliance requirements under India DPDP Act.",
        interviewTalkingPoints = "Emphasize enterprise software lifecycle, Azure OpenAI commercialization scenarios, and KPI tracking dashboards.",
        confidenceRating = 95,
        sourceGrounding = "Microsoft Investor Relations, Gartner Cloud Report, ET Prime"
      ),
      TargetCompanyMarketIntel(
        id = "intel_razorpay_01",
        targetCompanyId = "tc_razorpay",
        companyName = "Razorpay",
        headlineSummary = "Razorpay turns net profitable ahead of planned India IPO; scaling UPI international and enterprise payroll ops.",
        marketSentiment = "BULLISH",
        sentimentScore = 91,
        strategicExpansionSignals = "Scaling international payment gateways across Southeast Asia; launching Ray AI-driven merchant concierge.",
        executiveLeadershipShifts = "Elevated former CFO to Executive Board; hiring dedicated Operations & Risk Analytics leads.",
        hiringTrends = "Fast-moving hiring cycles (2-week turnaround); high premium on analytical problem ownership and hustle.",
        financialHealthAndGrowth = "Net profitable with over ₹2,200 Cr in revenue and 50%+ market share in online payment aggregation.",
        competitiveMoat = "Superior developer API ergonomics, 99.9% gateway uptime, and embedded fintech stack (Payroll, Capital, Banking).",
        risksAndHeadwinds = "Tightening RBI regulatory guidelines on payment aggregators and offline POS license scrutiny.",
        interviewTalkingPoints = "Demonstrate payment gateway funnel breakdown, transaction unit economics, and operational bottleneck resolution.",
        confidenceRating = 94,
        sourceGrounding = "Inc42, Entrackr, Razorpay Annual Disclosures"
      ),
      TargetCompanyMarketIntel(
        id = "intel_swiggy_01",
        targetCompanyId = "tc_swiggy",
        companyName = "Swiggy",
        headlineSummary = "Instamart dark store network expanding rapidly across Bengaluru and Tier-1 metros; dark store operational efficiency is top KPI.",
        marketSentiment = "BULLISH",
        sentimentScore = 88,
        strategicExpansionSignals = "Post-IPO capital deployment targeted at dark store logistics automation, supply forecasting, and route batching.",
        executiveLeadershipShifts = "Senior Director of Strategy & Supply Chain hired to lead 10-minute grocery margin expansion.",
        hiringTrends = "High demand for operations analysts capable of tracking supply-demand elasticity and delivery partner SLA compliance.",
        financialHealthAndGrowth = "Order frequency growing at 26% YoY; quick-commerce contribution margin improving steadily.",
        competitiveMoat = "Hyperlocal logistics network of 350,000+ delivery partners and integrated food + grocery customer base.",
        risksAndHeadwinds = "Intense competitive pressure from Blinkit and Zepto; gig worker welfare regulations.",
        interviewTalkingPoints = "Present case study on route batching algorithms, deadhead mile reduction, and margin sensitivity modeling.",
        confidenceRating = 92,
        sourceGrounding = "Swiggy Red Herring Prospectus, TechCrunch India"
      ),
      TargetCompanyMarketIntel(
        id = "intel_cred_01",
        targetCompanyId = "tc_cred",
        companyName = "CRED",
        headlineSummary = "CRED Garage vehicle platform surpasses 6M vehicles; expanding vehicle insurance and auto loan margins.",
        marketSentiment = "NEUTRAL",
        sentimentScore = 84,
        strategicExpansionSignals = "Focus on monetizing affluent user base through high-margin financial services and premium luxury retail commerce.",
        executiveLeadershipShifts = "Lean operational leadership; candidates evaluated directly on first-principles thinking.",
        hiringTrends = "Selective hiring bar; favors high curiosity, aesthetic sensibility, and quantitative rigor over standard resumes.",
        financialHealthAndGrowth = "Revenue grew 66% YoY; operating losses narrowing as lending and merchant commerce mature.",
        competitiveMoat = "High-credit-score user demographic with over 30% share of all domestic credit card bill payments in India.",
        risksAndHeadwinds = "RBI regulations on unsecured personal lending and co-branded credit card rewards.",
        interviewTalkingPoints = "Focus on customer LTV/CAC dynamics, high-retention engagement loops, and premium brand moat.",
        confidenceRating = 90,
        sourceGrounding = "CRED Press Releases, Mint Fintech Analysis"
      ),
      TargetCompanyMarketIntel(
        id = "intel_nvidia_01",
        targetCompanyId = "tc_nvidia_india",
        companyName = "NVIDIA India",
        headlineSummary = "NVIDIA partners with Reliance, Tata, and Yotta to build sovereign AI supercomputing clouds in India.",
        marketSentiment = "BULLISH",
        sentimentScore = 98,
        strategicExpansionSignals = "Bengaluru design and operations center leading sovereign AI developer alliances and enterprise GPU cloud programs.",
        executiveLeadershipShifts = "APAC Enterprise Operations expanding rapidly under regional leadership.",
        hiringTrends = "Looking for operational talents who can bridge technical compute infrastructure and enterprise business execution.",
        financialHealthAndGrowth = "Record quarterly revenue surpassing $30B globally; datacenter segment up over 150% YoY.",
        competitiveMoat = "CUDA software moat, proprietary NVLink interconnects, and dominance in enterprise AI model training.",
        risksAndHeadwinds = "Global supply chain allocation constraints and US export control regulatory adaptations.",
        interviewTalkingPoints = "Discuss GPU datacenter utilization metrics, supply chain allocation, and developer ecosystem growth.",
        confidenceRating = 98,
        sourceGrounding = "NVIDIA GTC Keynotes, Economic Times, Reuters"
      )
    )
    db.targetCompanyDao().insertMarketIntelList(marketIntelList)

    // 29. Resume Variations & Versioning (Room Local Persistence)
    val initialResumeVariations = listOf(
      ResumeVariation(
        id = "res_var_primary_core",
        title = "Aditya Shenoy - Operations Strategy Core Master",
        targetCompany = "General",
        targetRole = "Operations Strategy Lead",
        resumeText = com.example.service.ResumeJobComparisonService.DEFAULT_RESUME_TEXT,
        versionNumber = 1,
        versionLabel = "v1.0",
        versionNotes = "Baseline master resume with 42% order cycle reduction and ₹18L pipeline metrics",
        isPrimary = true,
        parentVariationId = null,
        atsCompatibilityScore = 88,
        overallMatchScore = 86,
        tags = "Core, Operations, Telemetry, SQL, Python, Dark Store",
        wordCount = 330,
        createdAt = System.currentTimeMillis() - 86400000L * 7,
        updatedAt = System.currentTimeMillis() - 86400000L * 2
      ),
      ResumeVariation(
        id = "res_var_zepto_ops_v2",
        title = "Zepto Quick Commerce & Dark Store Ops Lead",
        targetCompany = "Zepto",
        targetRole = "Lead - Strategy & Dark Store Operations",
        resumeText = com.example.service.ResumeJobComparisonService.DEFAULT_RESUME_TEXT.replace(
          "Spearheaded end-to-end supply chain telemetry redesign across 14 regional fulfillment nodes, slashing order cycle times from 28 minutes to 16.2 minutes (42% reduction).",
          "Engineered sub-10m dark store picking workflow across 14 micro-fulfilment hubs, slashing in-store order cycle time by 42% (28m to 16.2m) and maintaining 99.4% SLA adherence."
        ),
        versionNumber = 2,
        versionLabel = "v2.0",
        versionNotes = "Infused sub-10m picking SLA, dark store micro-hub picking telemetry and 99.4% on-time dispatch",
        isPrimary = false,
        parentVariationId = "res_var_primary_core",
        atsCompatibilityScore = 95,
        overallMatchScore = 93,
        tags = "Zepto, Quick Commerce, Micro-hubs, Turnaround SLA, Floor Reality",
        wordCount = 345,
        createdAt = System.currentTimeMillis() - 86400000L * 3,
        updatedAt = System.currentTimeMillis() - 3600000L * 5
      ),
      ResumeVariation(
        id = "res_var_razorpay_ops_v1",
        title = "Razorpay Merchant Ops & Risk Infrastructure",
        targetCompany = "Razorpay",
        targetRole = "Senior Business Analyst - Merchant Payments & Growth",
        resumeText = com.example.service.ResumeJobComparisonService.DEFAULT_RESUME_TEXT.replace(
          "Built automated outbound prospecting engine integrating Apollo and LinkedIn API, generating ₹18L+ in verified enterprise pipeline across Tier-1 retail accounts.",
          "Engineered automated merchant KYC and transaction funnel pipeline tracking across 400+ B2B merchants, recovering ₹18L+ in high-friction drops and maintaining 99.9% settlement SLA."
        ),
        versionNumber = 1,
        versionLabel = "v1.0",
        versionNotes = "Tailored for payment gateway success rates, UPI transaction drop-offs, and merchant dispute SLA",
        isPrimary = false,
        parentVariationId = "res_var_primary_core",
        atsCompatibilityScore = 91,
        overallMatchScore = 89,
        tags = "Razorpay, FinTech, Merchant Ops, KYC, Cohort Retention",
        wordCount = 342,
        createdAt = System.currentTimeMillis() - 86400000L * 4,
        updatedAt = System.currentTimeMillis() - 86400000L * 1
      ),
      ResumeVariation(
        id = "res_var_swiggy_instamart_v1",
        title = "Swiggy Instamart Dark Store Cluster & Logistics",
        targetCompany = "Swiggy Instamart",
        targetRole = "Operations Manager - Dark Store Cluster & Logistics",
        resumeText = com.example.service.ResumeJobComparisonService.DEFAULT_RESUME_TEXT.replace(
          "Formulated strict vendor SLA audit framework across 40+ suppliers, renegotiating lead times from 48h to 24h and recovering ₹4.2L in delay penalties.",
          "Instituted FEFO perishable inventory protocols across 12 Bengaluru pod dark stores, reducing food waste by 31% and cutting vendor breach penalties by ₹4.2L."
        ),
        versionNumber = 1,
        versionLabel = "v1.0",
        versionNotes = "Emphasized FEFO perishable shrinkage reduction, pod warehouse layout motion studies, and batch dispatch",
        isPrimary = false,
        parentVariationId = "res_var_primary_core",
        atsCompatibilityScore = 92,
        overallMatchScore = 88,
        tags = "Swiggy, Instamart, FEFO, Pod Logistics, Batch Dispatch",
        wordCount = 338,
        createdAt = System.currentTimeMillis() - 86400000L * 5,
        updatedAt = System.currentTimeMillis() - 86400000L * 2
      )
    )
    db.resumeVariationDao().insertVariations(initialResumeVariations)

    // 34. Company Intelligence Widget Cache (Room Persistence across App Restarts)
    val initialWidgetCaches = listOf(
      CompanyIntelligenceWidgetCache(
        companyName = "Zepto",
        cachedSummary = "Quick-commerce hypergrowth unicorn Zepto has reached annualised GMV exceeding $1.4B and opened major supply-chain operations in Bellandur, Bengaluru. Preparing for late-2026 domestic IPO while driving store-level EBITDA profitability.",
        timestamp = "18 Sep 2026, 11:30 AM",
        hiringUpdates = "Expanding Bellandur engineering & dark store ops team\nActively recruiting Supply Chain Business Analysts\n12 open strategic roles tracked",
        interviewAngle = "Highlight SQL-driven batch dispatch automation, perishable shrinkage reduction, and the 42% operational cycle time reduction achieved in inventory forecasting.",
        executiveShifts = "Appointed former Amazon Supply Chain Automation Director to lead dark store robotics\nCo-founders driving Mumbai-Bengaluru dual HQ strategy",
        strategicRisks = "Intense competitive pressure from Blinkit and Swiggy Instamart; margin management under high rider payout density.",
        sourcesJson = "Zepto Corporate Hub Bellandur|||https://zeptonow.com/press\nTechCrunch India Quick Commerce 2026|||https://techcrunch.com",
        confidenceScore = 96,
        isLiveGrounded = true,
        isLastSelected = true,
        lastUpdatedMillis = System.currentTimeMillis()
      ),
      CompanyIntelligenceWidgetCache(
        companyName = "Swiggy",
        cachedSummary = "Swiggy Instamart achieves dark store unit economic profitability across top 6 Indian metros. Expanding automated micro-fulfilment centers along Bangalore's Outer Ring Road and Marathahalli hubs.",
        timestamp = "18 Sep 2026, 10:00 AM",
        hiringUpdates = "Hiring Strategy & Operations Analysts in Devarabisanahalli, Bengaluru\nAccelerating analytics talent acquisition for quick commerce",
        interviewAngle = "Present metrics on inventory turnover, dark store floor plan optimization, and dispute resolution workflows.",
        executiveShifts = "Instamart leadership restructuring focusing on non-grocery 10-minute retail",
        strategicRisks = "Quick commerce category expansion risks into electronics and beauty delivery margins.",
        sourcesJson = "Swiggy Press Releases|||https://swiggy.com/news\nLiveMint Bengaluru Tech Report|||https://livemint.com",
        confidenceScore = 94,
        isLiveGrounded = true,
        isLastSelected = false,
        lastUpdatedMillis = System.currentTimeMillis() - 3600000L
      )
    )
    initialWidgetCaches.forEach { cache ->
      db.companyIntelligenceWidgetDao().insertOrUpdate(cache)
    }
  }
}

