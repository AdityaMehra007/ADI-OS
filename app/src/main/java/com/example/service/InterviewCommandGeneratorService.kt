package com.example.service

import android.util.Log
import com.example.ai.GeminiCareerService
import com.example.ai.GeminiContent
import com.example.ai.GeminiPart
import com.example.ai.GeminiRequest
import com.example.data.model.InterviewCommandDossier
import com.example.data.model.InterviewCommandQuestion
import com.example.data.model.StructuredTalkingPoint
import com.example.data.model.UserProfile
import com.example.data.model.VerifiedFact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Service to generate role-specific mock interview questions and structured talking points
 * based on the job title and company selected in the Application Hub.
 * Supports live Gemini reasoning with high-fidelity, corporate-grade fallbacks.
 */
class InterviewCommandGeneratorService(
  private val geminiCareerService: GeminiCareerService = GeminiCareerService()
) {

  suspend fun generateInterviewCommand(
    companyName: String,
    jobTitle: String,
    userProfile: UserProfile? = null,
    verifiedFacts: List<VerifiedFact> = emptyList()
  ): InterviewCommandDossier = withContext(Dispatchers.IO) {
    val cleanCompany = companyName.trim().ifBlank { "Zepto" }
    val cleanRole = jobTitle.trim().ifBlank { "Strategy & Operations Lead" }
    val apiKey = try {
      com.example.BuildConfig.GEMINI_API_KEY
    } catch (e: Exception) {
      ""
    }

    val candidateName = userProfile?.name ?: "Adi"
    val degree = "${userProfile?.educationDegree ?: "BBA"} (${userProfile?.educationSpecialization ?: "International Business"}) from ${userProfile?.university ?: "Dayananda Sagar University (DSU), Bengaluru"}"
    val targetRoles = userProfile?.targetRoles ?: "Business Analyst, Strategy & Operations, AI Operations"

    val factsSummary = if (verifiedFacts.isNotEmpty()) {
      verifiedFacts.joinToString("\n") { "• [${it.category}] ${it.claim} (${it.evidenceDetails})" }
    } else {
      """
      • Streamlined family enterprise supply chain operations cutting order cycle time by 42% and eliminating 60% of stockouts.
      • Saved ₹24,00,000 in working capital through automated SQL and predictive demand-forecasting pipelines.
      • Finalist in Razorpay Fintech Hackathon; holds active ₹18.5L offer from Razorpay.
      • Proficient in Agentic AI workflows, SQL, BigQuery, Python automation, and Financial Modeling.
      """.trimIndent()
    }

    val prompt = """
      You are the Lead Executive Interview Architect and Senior Bar Raiser at $cleanCompany.
      Generate an elite, role-specific Interview Command Dossier for the role of '$cleanRole' at $cleanCompany.
      
      CANDIDATE CONTEXT:
      - Candidate Name: $candidateName
      - Academic Background: $degree
      - Target Roles: $targetRoles
      - Verified Achievements & Metric Proof Points:
      $factsSummary
      
      REQUIREMENTS:
      1. Generate role-specific mock interview questions and structured talking points tailored specifically to $cleanCompany and $cleanRole.
      2. Provide structured talking points categorized into pillars:
         - 'EXECUTIVE_HOOK': A crisp 60-second opening value proposition tailored to $cleanCompany.
         - 'METRICS_PROOF': Concrete quantifiable achievements mapped to the role.
         - 'STRATEGIC_ALIGNMENT': Deep business model, unit economics, or tech architecture considerations for $cleanCompany.
         - 'STAR_FRAMEWORK': Structured Situation, Task, Action, and Result framing bullets.
         - 'PITFALLS_AVOID': Crucial pitfalls or red-flags to avoid in $cleanCompany's interview rounds.
      3. Generate 5 realistic, rigorous mock interview questions covering:
         - BEHAVIORAL_STAR: High-stakes conflict or leadership trade-off.
         - ANALYTICAL_CASE: Quantitative problem-solving, unit economics, or root-cause diagnostics.
         - STRATEGIC_FIT: Why $cleanCompany, competitive moat, and strategic vision.
         - ROLE_DOMAIN: Practical day-to-day execution for $cleanRole.
         - BAR_RAISER: Ambiguity handling, stress test, or long-term systemic ownership.
      4. For EACH question provide:
         - questionText
         - category (BEHAVIORAL_STAR, ANALYTICAL_CASE, STRATEGIC_FIT, ROLE_DOMAIN, BAR_RAISER)
         - difficulty (CORE, ADVANCED, BAR_RAISER)
         - whyCompanyAsksThis: Interviewer's evaluation criteria and signal rubric.
         - candidateProfileAnchor: Exact connection to candidate's verified achievements.
         - structuredTalkingPoints: 3-4 structured bullet points to hit when answering.
         - starSituation, starTask, starAction, starResult: Structured STAR breakdown.
         - sampleStarAnswer: Model executive response using candidate's real data points.
         - pitfallsToAvoid: 2 specific traps to avoid when answering this question.
      5. Provide 3 high-impact, strategic questions for the candidate to ask the interviewer.

      Respond STRICTLY in valid JSON matching this schema:
      {
        "companyName": "$cleanCompany",
        "jobTitle": "$cleanRole",
        "industry": "High Growth Technology",
        "executiveContext": "Brief context on $cleanCompany's interview standards for $cleanRole",
        "candidateMappingRationale": "How candidate's background maps to this role",
        "strategicTalkingPoints": [
          {
            "pillar": "EXECUTIVE_HOOK",
            "title": "...",
            "bulletPoints": ["...", "..."],
            "impactMetric": "...",
            "strategicRationale": "..."
          }
        ],
        "mockQuestions": [
          {
            "id": "q1",
            "questionText": "...",
            "category": "BEHAVIORAL_STAR",
            "difficulty": "CORE",
            "whyCompanyAsksThis": "...",
            "candidateProfileAnchor": "...",
            "structuredTalkingPoints": ["...", "..."],
            "starSituation": "...",
            "starTask": "...",
            "starAction": "...",
            "starResult": "...",
            "sampleStarAnswer": "...",
            "pitfallsToAvoid": ["..."]
          }
        ],
        "highImpactQuestionsForInterviewer": ["...", "..."]
      }
      Do not include text outside the json block.
    """.trimIndent()

    if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
      val models = listOf("gemini-3.5-flash", "gemini-3.1-flash-lite-preview", "gemini-flash-latest")
      for (model in models) {
        try {
          val req = GeminiRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt))))
          )
          val resp = geminiCareerService.executeGeminiRequest(apiKey, req, listOf(model))
          val text = resp.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
          if (!text.isNullOrBlank()) {
            val parsed = parseInterviewCommandJson(text, cleanCompany, cleanRole)
            if (parsed != null && parsed.mockQuestions.isNotEmpty()) {
              return@withContext parsed.copy(modelUsed = model, isGroundedWithGemini = true)
            }
          }
        } catch (e: Exception) {
          Log.w("InterviewCommandService", "Gemini API failed with model $model: ${e.message}")
        }
      }
    }

    // High-fidelity fallback calibrated to the company and role
    return@withContext createCalibratedInterviewCommandDossier(cleanCompany, cleanRole, candidateName, degree)
  }

  private fun parseInterviewCommandJson(
    jsonText: String,
    companyName: String,
    jobTitle: String
  ): InterviewCommandDossier? {
    try {
      val cleanJson = jsonText.substringAfter("```json")
        .substringBefore("```")
        .trim()
        .ifEmpty { jsonText.trim() }

      val root = JSONObject(cleanJson)
      val parsedCompany = root.optString("companyName", companyName)
      val parsedRole = root.optString("jobTitle", jobTitle)
      val industry = root.optString("industry", "Technology & Enterprise Solutions")
      val execContext = root.optString("executiveContext", "$companyName sets rigorous bars on ownership, data-backed execution, and strategic velocity.")
      val mappingRationale = root.optString("candidateMappingRationale", "Positioned candidate's proven supply chain and analytical track record against $companyName's operating model.")

      val talkingPointsList = mutableListOf<StructuredTalkingPoint>()
      val tpArray = root.optJSONArray("strategicTalkingPoints")
      if (tpArray != null) {
        for (i in 0 until tpArray.length()) {
          val tpObj = tpArray.getJSONObject(i)
          val bullets = mutableListOf<String>()
          val bulletsArr = tpObj.optJSONArray("bulletPoints")
          if (bulletsArr != null) {
            for (j in 0 until bulletsArr.length()) {
              bullets.add(bulletsArr.getString(j))
            }
          }
          talkingPointsList.add(
            StructuredTalkingPoint(
              id = "tp_${i + 1}",
              pillar = tpObj.optString("pillar", "STRATEGIC_ALIGNMENT"),
              title = tpObj.optString("title", "Core Strategic Anchor"),
              bulletPoints = bullets,
              impactMetric = tpObj.optString("impactMetric", ""),
              strategicRationale = tpObj.optString("strategicRationale", "")
            )
          )
        }
      }

      val questionsList = mutableListOf<InterviewCommandQuestion>()
      val qArray = root.optJSONArray("mockQuestions")
      if (qArray != null) {
        for (i in 0 until qArray.length()) {
          val qObj = qArray.getJSONObject(i)
          val points = mutableListOf<String>()
          val ptsArr = qObj.optJSONArray("structuredTalkingPoints")
          if (ptsArr != null) {
            for (j in 0 until ptsArr.length()) {
              points.add(ptsArr.getString(j))
            }
          }
          val pitfalls = mutableListOf<String>()
          val pitArr = qObj.optJSONArray("pitfallsToAvoid")
          if (pitArr != null) {
            for (j in 0 until pitArr.length()) {
              pitfalls.add(pitArr.getString(j))
            }
          }

          questionsList.add(
            InterviewCommandQuestion(
              id = qObj.optString("id", "q_${i + 1}"),
              questionText = qObj.optString("questionText", ""),
              category = qObj.optString("category", "BEHAVIORAL_STAR"),
              difficulty = qObj.optString("difficulty", "CORE"),
              whyCompanyAsksThis = qObj.optString("whyCompanyAsksThis", ""),
              candidateProfileAnchor = qObj.optString("candidateProfileAnchor", ""),
              structuredTalkingPoints = points,
              starSituation = qObj.optString("starSituation", ""),
              starTask = qObj.optString("starTask", ""),
              starAction = qObj.optString("starAction", ""),
              starResult = qObj.optString("starResult", ""),
              sampleStarAnswer = qObj.optString("sampleStarAnswer", ""),
              pitfallsToAvoid = pitfalls
            )
          )
        }
      }

      val questionsForInterviewer = mutableListOf<String>()
      val qiArray = root.optJSONArray("highImpactQuestionsForInterviewer")
      if (qiArray != null) {
        for (i in 0 until qiArray.length()) {
          questionsForInterviewer.add(qiArray.getString(i))
        }
      }

      if (questionsList.isEmpty() && talkingPointsList.isEmpty()) {
        return null
      }

      return InterviewCommandDossier(
        id = UUID.randomUUID().toString(),
        companyName = parsedCompany,
        jobTitle = parsedRole,
        industry = industry,
        executiveContext = execContext,
        candidateMappingRationale = mappingRationale,
        strategicTalkingPoints = talkingPointsList,
        mockQuestions = questionsList,
        highImpactQuestionsForInterviewer = questionsForInterviewer,
        generatedTimestamp = System.currentTimeMillis(),
        modelUsed = "gemini-3.5-flash",
        isGroundedWithGemini = true
      )
    } catch (e: Exception) {
      Log.e("InterviewCommandService", "Error parsing Interview Command JSON: ${e.message}")
      return null
    }
  }

  fun createCalibratedInterviewCommandDossier(
    company: String,
    role: String,
    candidateName: String = "Adi",
    degree: String = "BBA in International Business"
  ): InterviewCommandDossier {
    val lowerCo = company.lowercase()
    val lowerRole = role.lowercase()

    return when {
      lowerCo.contains("zepto") -> createZeptoDossier(company, role)
      lowerCo.contains("swiggy") -> createSwiggyDossier(company, role)
      lowerCo.contains("google") -> createGoogleDossier(company, role)
      lowerCo.contains("razorpay") -> createRazorpayDossier(company, role)
      lowerCo.contains("mckinsey") || lowerCo.contains("bain") || lowerCo.contains("bcg") -> createConsultingDossier(company, role)
      lowerCo.contains("flipkart") || lowerCo.contains("amazon") -> createEcommerceDossier(company, role)
      else -> createGenericDossier(company, role, candidateName, degree)
    }
  }

  // --------------------------------------------------------------------------
  // CALIBRATED COMPANY DOSSIERS
  // --------------------------------------------------------------------------

  private fun createZeptoDossier(company: String, role: String): InterviewCommandDossier {
    return InterviewCommandDossier(
      companyName = company,
      jobTitle = role,
      industry = "Quick-Commerce & Hyperlocal Logistics",
      executiveContext = "Zepto tests heavily for sub-10 minute dark store throughput, unit economics, inventory accuracy, and hands-on operational grit. Bar raisers evaluate whether you make data-driven decisions at lightning speed without waiting for perfect information.",
      candidateMappingRationale = "Anchors your 42% order cycle reduction and ₹24L capital optimization directly into dark store pick-pack velocity and inventory replenishment SLA compliance.",
      strategicTalkingPoints = listOf(
        StructuredTalkingPoint(
          pillar = "EXECUTIVE_HOOK",
          title = "60-Second Dark Store Throughput Pitch",
          bulletPoints = listOf(
            "Frame yourself as an operator who treats fulfillment speed as a financial moat, not just an operational KPI.",
            "Connect your 42% order cycle reduction in family logistics to dark store picking-to-handover SLAs.",
            "Emphasize comfort with SQL, Python automation, and micro-fulfillment floor layouts."
          ),
          impactMetric = "42% Order Cycle Reduction · Sub-10 Min SLA",
          strategicRationale = "Immediately demonstrates operational literacy that aligns with Zepto's ruthless speed culture."
        ),
        StructuredTalkingPoint(
          pillar = "METRICS_PROOF",
          title = "Quantifiable Proof Points for Zepto Operations",
          bulletPoints = listOf(
            "Saved ₹24,00,000 in working capital by eliminating dead stock and tuning re-order point algorithms.",
            "Eliminated 60% of stockouts by deploying automated SQL inventory triggers across 200+ fast-moving SKUs.",
            "Scaled order throughput under peak festival demand surges without increasing labor headcount."
          ),
          impactMetric = "₹24L Working Capital Saved · 60% Stockout Reduction",
          strategicRationale = "Provides verifiable data proof that you understand supply chain trade-offs and cash flow optimization."
        ),
        StructuredTalkingPoint(
          pillar = "STRATEGIC_ALIGNMENT",
          title = "Zepto Unit Economics & Dark Store Expansion",
          bulletPoints = listOf(
            "Highlight the delicate balance between contribution margin per order and rider batching latency.",
            "Discuss dark store slotting optimization: placing top 15% frequency SKUs near packing stations saves 18 seconds per pick.",
            "Speak knowledgeably about Zepto Cafe margin expansion and fresh produce shrinkage control."
          ),
          impactMetric = "18s Saved Per Pick · Margin Expansion",
          strategicRationale = "Shows CXO-level business acumen beyond simple task execution."
        ),
        StructuredTalkingPoint(
          pillar = "STAR_FRAMEWORK",
          title = "STAR Defense: Stockout Elimination Under Demand Spikes",
          bulletPoints = listOf(
            "Situation: Critical supplier delays during seasonal sales caused stockouts to climb toward 18%.",
            "Task: Maintain fulfillment SLAs while preventing inventory holding cost overruns.",
            "Action: Built a real-time automated SQL reorder script and negotiated vendor drop-ship buffers.",
            "Result: Cut stockouts by 60%, reduced cycle times by 42%, and protected working capital."
          ),
          impactMetric = "60% Drop in Stockouts · Zero Fulfillment Bottlenecks",
          strategicRationale = "High-impact narrative ready to deploy in any behavioral or problem-solving prompt."
        ),
        StructuredTalkingPoint(
          pillar = "PITFALLS_AVOID",
          title = "Zepto Interview Red Flags to Avoid",
          bulletPoints = listOf(
            "Never suggest slowing delivery times to solve quality issues; Zepto's brand promise is speed.",
            "Don't give theoretical management answers; highlight rolling up your sleeves on the warehouse floor.",
            "Avoid proposing expensive legacy enterprise software when lean SQL/Python scripts solve the issue immediately."
          ),
          impactMetric = "Culture Fit Guardrail",
          strategicRationale = "Protects candidate from common misalignments in fast-paced startup bar raiser rounds."
        )
      ),
      mockQuestions = listOf(
        InterviewCommandQuestion(
          id = "z_q1",
          questionText = "Walk me through how you would diagnose a sudden 15% spike in dark store order pick times in an East Bengaluru cluster during rainy weather.",
          category = "ANALYTICAL_CASE",
          difficulty = "CORE",
          whyCompanyAsksThis = "Tests structured root-cause analysis, understanding of quick-commerce bottlenecks, and speed of tactical response.",
          candidateProfileAnchor = "Maps your family enterprise supply chain diagnostics and order cycle bottleneck reduction.",
          structuredTalkingPoints = listOf(
            "Deconstruct the problem into Rider Handover, In-Store Picking, and System Latency.",
            "Examine SKU stockout substitution delays vs physical dark store floor congestion.",
            "Implement dynamic batch dispatching and wet-weather buffer re-allocations."
          ),
          starSituation = "Rain surges drive a 2x order influx while rider arrivals slow down.",
          starTask = "Isolate whether the bottleneck is picker velocity, bag staging space, or SKU misplacement.",
          starAction = "Run SQL queries across picking timestamps; reassign packers to staging; activate top-20 SKU express packing lines.",
          starResult = "Recovered picking times to under 140 seconds within 45 minutes without order cancellations.",
          sampleStarAnswer = "I begin by segmenting the pick cycle into three distinct timestamps: order drop to picker assignment, pick-and-scan duration, and bag staging at the rider dispatch bay. If picking duration is elevated, I check SKU misplacement and picker density in narrow aisles. In rainy surges, staging congestion is often the real bottleneck. I would immediately activate an express staging table for single-bag orders, assign a floating expediter, and use real-time SQL dashboards to balance order distribution.",
          pitfallsToAvoid = listOf(
            "Blaming the riders or weather instead of optimizing dark store internal flow.",
            "Suggesting hiring more pickers on the spot rather than optimizing existing shift throughput."
          )
        ),
        InterviewCommandQuestion(
          id = "z_q2",
          questionText = "Tell me about a time you had to push back on a senior stakeholder or vendor who was failing delivery SLAs.",
          category = "BEHAVIORAL_STAR",
          difficulty = "CORE",
          whyCompanyAsksThis = "Zepto operates on ruthless supplier accountability. They want to see assertiveness backed by objective metrics.",
          candidateProfileAnchor = "Anchors your real-world experience negotiating vendor contracts and cutting order cycle times by 42%.",
          structuredTalkingPoints = listOf(
            "Cite the objective SLA data rather than personal opinions.",
            "Highlight the financial impact of missed deliveries on end-customer retention.",
            "Present a collaborative penalty/bonus structure that realigns vendor incentives."
          ),
          starSituation = "Primary packaging and component vendors regularly delivered 48 hours late, tying up inventory.",
          starTask = "Enforce delivery compliance without breaking long-term supplier relationships.",
          starAction = "Aggregated 90 days of delivery telemetry; presented clear cost of stockouts; negotiated tier-based SLA penalties tied to next month's order volume.",
          starResult = "Vendor on-time delivery improved from 64% to 94%; slashed cycle times by 42%.",
          sampleStarAnswer = "When our primary packaging vendor consistently delivered 48 hours behind schedule, it created downstream inventory stalls that threatened our customer commitments. Rather than escalating emotionally, I compiled 90 days of delivery timestamps in SQL, demonstrating that their delays accounted for 70% of our out-of-stock incidents. I scheduled a sit-down with their managing director, shared the data, and established a bi-weekly SLA tier with agreed-upon delivery buffers. Within 6 weeks, their on-time delivery reached 94%, directly contributing to our 42% order cycle reduction.",
          pitfallsToAvoid = listOf(
            "Sounding aggressive rather than data-driven.",
            "Failing to articulate the commercial outcome of the negotiation."
          )
        ),
        InterviewCommandQuestion(
          id = "z_q3",
          questionText = "How do you evaluate the trade-off between higher SKU assortment and dark store picking velocity?",
          category = "STRATEGIC_FIT",
          difficulty = "ADVANCED",
          whyCompanyAsksThis = "Directly challenges your understanding of quick-commerce unit economics: assortment attracts users, but complexity destroys 10-minute SLAs.",
          candidateProfileAnchor = "Ties to your work eliminating dead stock and freeing up ₹24L in working capital.",
          structuredTalkingPoints = listOf(
            "Explain SKU velocity Pareto: 80% of orders come from top 20% of high-turnover items.",
            "Discuss storage cube constraints and pick travel distance in a 3,000 sq ft dark store.",
            "Advocate for hyper-curated local assortment with regional demand forecasting."
          ),
          sampleStarAnswer = "In a 3,000 sq ft micro-fulfillment center, space is your scarcest asset. Adding long-tail SKUs dilutes picking density, increases picker travel distance by up to 25%, and drives shrinkage. I evaluate assortment through SKU velocity tiers. Tier-1 high-velocity SKUs get prime golden-zone bin slots. Long-tail items must justify their shelf space by driving high-margin basket attach rates (like beauty or electronics accessories), otherwise they belong in regional mother hubs, not 10-minute dark stores.",
          pitfallsToAvoid = listOf(
            "Assuming more products are always better without considering picker travel time."
          )
        ),
        InterviewCommandQuestion(
          id = "z_q4",
          questionText = "If you have ₹50 Lakhs to invest across dark store automation, rider incentive bonuses, or inventory buffer stock, how do you allocate it?",
          category = "ROLE_DOMAIN",
          difficulty = "ADVANCED",
          whyCompanyAsksThis = "Evaluates capital allocation judgment, prioritization framework, and operational ROI mindset.",
          candidateProfileAnchor = "Directly leverages your financial modeling skills and ₹24L capital optimization experience.",
          structuredTalkingPoints = listOf(
            "Frame allocation around ROI payback period and customer retention impact.",
            "Allocate 50% to dark store pick-and-pack workflow optimization (highest recurring yield).",
            "Allocate 30% to high-velocity safety stock to prevent churn-inducing stockouts.",
            "Reserve 20% for dynamic surge rider incentives during peak demand spikes."
          ),
          sampleStarAnswer = "I would deploy capital based on recurring payback velocity. I'd allocate 50% (₹25L) into dark store slotting and barcode scanner workflow automation, which permanently cuts picker seconds on every single order. I'd put 30% (₹15L) into safety stock algorithms for high-frequency hero SKUs to prevent churn from stockouts, saving working capital. The remaining 20% (₹10L) would serve as a strategic surge pool for extreme weather rider incentives, maintaining customer trust when competitors go offline.",
          pitfallsToAvoid = listOf(
            "Spending all capital on one single dimension without a balanced portfolio rationale."
          )
        ),
        InterviewCommandQuestion(
          id = "z_q5",
          questionText = "Tell me about a time you had to lead a project where requirements changed entirely mid-execution.",
          category = "BAR_RAISER",
          difficulty = "BAR_RAISER",
          whyCompanyAsksThis = "Quick-commerce pivots weekly. Bar raisers want to see high stress tolerance, adaptable leadership, and zero victim mentality.",
          candidateProfileAnchor = "Demonstrates agility in scaling supply chain workflows and handling unexpected market shifts.",
          structuredTalkingPoints = listOf(
            "Acknowledge changing market conditions calmly as an operational constant.",
            "Rally team around the new constraint using transparent, daily milestones.",
            "Deliver measurable output despite ambiguity."
          ),
          sampleStarAnswer = "Midway through rolling out our demand-forecasting pipeline, sudden supply chain regulatory changes disrupted our top import channel. Instead of pausing work, I held an emergency sprint with our operations leads, stripped down the forecasting model to focus on domestic alternative suppliers, and built automated SQL alert queries within 48 hours. We pivoted without missing a single delivery deadline, and the domestic supplier network actually cut our landed procurement costs by 8%.",
          pitfallsToAvoid = listOf(
            "Complaining about leadership or shifting requirements."
          )
        )
      ),
      highImpactQuestionsForInterviewer = listOf(
        "How is Zepto balancing dark store expansion in Tier-2 satellite cities against dark store density and margin profitability in Tier-1 metros?",
        "What is the single biggest operational friction point currently between central algorithmic routing and local dark store shift managers?",
        "As Zepto Cafe and high-margin non-grocery categories expand, what architectural changes are being made to in-store packing workflows?"
      )
    )
  }

  private fun createSwiggyDossier(company: String, role: String): InterviewCommandDossier {
    return InterviewCommandDossier(
      companyName = company,
      jobTitle = role,
      industry = "Consumer Tech, Food Delivery & Quick-Commerce (Instamart)",
      executiveContext = "Swiggy emphasizes high consumer trust, algorithmic efficiency, cross-category synergy (Food + Instamart + Dineout), and operating margin discipline post-IPO.",
      candidateMappingRationale = "Aligns your analytical rigour, inventory management, and 42% order cycle reduction with Swiggy's logistics network optimization.",
      strategicTalkingPoints = listOf(
        StructuredTalkingPoint(
          pillar = "EXECUTIVE_HOOK",
          title = "Cross-Category Fulfillment & Margin Strategy",
          bulletPoints = listOf(
            "Position yourself as an analytical operator obsessed with fleet utilization and delivery unit economics.",
            "Leverage your proven track record in cutting cycle times and saving ₹24L in inventory capital.",
            "Demonstrate fluency with cohort retention, customer CAC, and dark store slotting."
          ),
          impactMetric = "₹24L Working Capital · Fleet Utilization Optimization",
          strategicRationale = "Perfect fit for Swiggy's dual-engine focus on profitable food delivery and rapid Instamart scaling."
        ),
        StructuredTalkingPoint(
          pillar = "STRATEGIC_ALIGNMENT",
          title = "Instamart vs Food Delivery Fleet Synergies",
          bulletPoints = listOf(
            "Explain how rider idle time during 3 PM - 6 PM lull hours can be bridged by Instamart scheduled deliveries.",
            "Address dark store location selection: radius overlap, zoning, and delivery density.",
            "Highlight private label margin expansion strategies (Supreme Harvest, etc.)."
          ),
          impactMetric = "Rider Utilization +18% · Margin Lift",
          strategicRationale = "Highlights strategic thinking at the intersection of consumer marketplace and physical logistics."
        )
      ),
      mockQuestions = listOf(
        InterviewCommandQuestion(
          id = "sw_q1",
          questionText = "How would you optimize rider batching between restaurant orders and Instamart dark store grocery pickups during peak lunch hours?",
          category = "ANALYTICAL_CASE",
          difficulty = "CORE",
          whyCompanyAsksThis = "Tests algorithmic trade-off intuition: batching increases efficiency but risks food temperature and grocery SLA decay.",
          candidateProfileAnchor = "Maps your family business supply chain cycle optimization.",
          structuredTalkingPoints = listOf(
            "Prioritize food delivery freshness as the non-negotiable constraint.",
            "Batch grocery orders only when dark store and restaurant geo-distance is under 400m.",
            "Define explicit latency SLAs and dynamic delivery fee offsets."
          ),
          sampleStarAnswer = "I treat customer food temperature and trust as the primary constraint. Batching a hot meal with cold grocery items introduces route divergence. I would permit batching only under three strict conditions: the restaurant prep time is verified as complete, the dark store pick is pre-staged, and total customer route detours stay under 7 minutes. Otherwise, dedicated fleet dispatching delivers higher lifetime customer value."
        ),
        InterviewCommandQuestion(
          id = "sw_q2",
          questionText = "Describe a situation where you used SQL or data analysis to uncover an unintuitive operational bottleneck.",
          category = "ROLE_DOMAIN",
          difficulty = "CORE",
          whyCompanyAsksThis = "Swiggy expects operators to be self-sufficient data investigators who query BigQuery/PostgreSQL themselves.",
          candidateProfileAnchor = "Directly highlights your automated SQL inventory pipelines and stockout reduction.",
          structuredTalkingPoints = listOf(
            "Explain the initial symptom (delayed dispatch).",
            "Detail how you queried the timestamp logs rather than accepting surface-level explanations.",
            "Show how data proved the real bottleneck was packaging restock, not worker velocity."
          ),
          sampleStarAnswer = "When our order cycle time was lagging by 42%, team leads assumed packing staff were underperforming. I wrote a SQL query joining picking timestamps with inventory bin locations and discovered that pickers were spending 38% of their time waiting for corrugated boxes to be restocked at the packing station. By restructuring packaging replenishment into automated hourly triggers, we resolved the bottleneck immediately and cut cycle time by 42% without altering team quotas."
        )
      ),
      highImpactQuestionsForInterviewer = listOf(
        "Post-IPO, how is Swiggy prioritizing capital allocation between Instamart dark store density and Bolt (10-minute food delivery)?",
        "What data telemetry is most critical for your operations leads when managing seasonal surge pricing vs customer loyalty?"
      )
    )
  }

  private fun createGoogleDossier(company: String, role: String): InterviewCommandDossier {
    return InterviewCommandDossier(
      companyName = company,
      jobTitle = role,
      industry = "Enterprise Technology, Cloud & AI Infrastructure",
      executiveContext = "Google evaluates candidates on four pillars: General Cognitive Ability (GCA), Role-Related Knowledge (RRK), Leadership, and Googleyness. Every answer must demonstrate first-principles thinking, scalability to millions of users, and intellectual humility.",
      candidateMappingRationale = "Frames your International Business background, AI automation workflow skills, and verified ₹24L financial optimization as proof of scalable, systems-level problem solving.",
      strategicTalkingPoints = listOf(
        StructuredTalkingPoint(
          pillar = "EXECUTIVE_HOOK",
          title = "Scalable Systems & First-Principles Problem Solving",
          bulletPoints = listOf(
            "Frame yourself as an operator-analyst who thinks in global scale, automated architectures, and measurable ROI.",
            "Connect your BBA in International Business to Google's cross-border market expansion and partner ecosystems.",
            "Showcase fluency with Gemini LLM agent architectures, SQL pipelines, and stakeholder navigation."
          ),
          impactMetric = "Global Scale Mindset · 42% Automation Yield",
          strategicRationale = "Maps directly to Google's GCA and Role-Related Knowledge evaluation criteria."
        ),
        StructuredTalkingPoint(
          pillar = "METRICS_PROOF",
          title = "Data-Driven Engineering & Business Metrics",
          bulletPoints = listOf(
            "Quantified ₹24,00,000 in working capital optimization through predictive models.",
            "Finalist in Tier-1 Fintech hackathon; evaluated multi-agent API architectures.",
            "Authored clean SQL and automated data workflows eliminating 60% of systemic errors."
          ),
          impactMetric = "₹24L Optimized · Zero Defect Automated Workflows",
          strategicRationale = "Demonstrates that you back every hypothesis with concrete quantitative rigor."
        )
      ),
      mockQuestions = listOf(
        InterviewCommandQuestion(
          id = "g_q1",
          questionText = "How would you design a metric dashboard to track whether Google Cloud customers are deriving real ROI from generative AI agent features?",
          category = "ANALYTICAL_CASE",
          difficulty = "ADVANCED",
          whyCompanyAsksThis = "Assesses General Cognitive Ability, customer-centric metric design, and ability to distinguish vanity metrics from true business impact.",
          candidateProfileAnchor = "Ties to your hands-on work with AI agent pipelines and quantitative dashboard modeling.",
          structuredTalkingPoints = listOf(
            "Differentiate between adoption metrics (API calls) and value metrics (task completion rate, latency reduction, cost savings).",
            "Establish counter metrics to ensure agents aren't hallucinating or wasting token budgets.",
            "Segment by enterprise tier and workload criticality."
          ),
          sampleStarAnswer = "I would avoid relying solely on token consumption, which can easily be a vanity metric or even indicate inefficient prompt loops. Instead, I define an AI Value Index based on three vectors: 1) Task Completion Velocity (reduction in human review time per document/code review), 2) Downstream Error Rate (regression or human correction frequency), and 3) Net Dollar Efficiency (cost per resolved workflow versus baseline human labor). If token usage rises while task completion stagnates, the system is failing the customer."
        ),
        InterviewCommandQuestion(
          id = "g_q2",
          questionText = "Tell me about a time you navigated an ambiguous problem with no established playbook or guidelines.",
          category = "BEHAVIORAL_STAR",
          difficulty = "CORE",
          whyCompanyAsksThis = "Google values candidates who thrive in ambiguity, construct hypotheses from first principles, and maintain forward momentum.",
          candidateProfileAnchor = "Anchors your independent diagnosis and overhaul of family supply chain bottlenecks.",
          structuredTalkingPoints = listOf(
            "State the ambiguous context: inherited an undocumented, manual supply chain.",
            "Action: structured the problem into hypothesis buckets; set up baseline data tracking.",
            "Result: created standard operating procedures and delivered 42% speed gains."
          ),
          sampleStarAnswer = "When I stepped in to analyze our family enterprise logistics, there were zero documentation files, no data logs, and entirely intuition-driven reordering. Rather than waiting for external instructions, I formulated a 30-day discovery plan. I spent two weeks shadowing pickers and recording timestamps manually on spreadsheets, built our first relational schema in SQLite, and discovered our 18% dead stock rate. I drafted our first automated replenishment SOP, which permanently eliminated 60% of stockouts and freed ₹24L in working capital."
        )
      ),
      highImpactQuestionsForInterviewer = listOf(
        "As Google embeds Gemini across workspace and cloud infrastructure, how are internal product squads evaluating the boundary between deterministic code and probabilistic AI models?",
        "What is the biggest operational hurdle when scaling enterprise AI agents across highly regulated banking and healthcare clients?"
      )
    )
  }

  private fun createRazorpayDossier(company: String, role: String): InterviewCommandDossier {
    return InterviewCommandDossier(
      companyName = company,
      jobTitle = role,
      industry = "FinTech, Payments Gateway & Banking SaaS",
      executiveContext = "Razorpay tests for deep payment flow understanding, transaction success rates (SR), fraud mitigation, merchant empathy, and speed of execution.",
      candidateMappingRationale = "Directly leverages your active ₹18.5L Razorpay offer and hackathon finalist credentials, proving you understand payment rails and reconciliation economics.",
      strategicTalkingPoints = listOf(
        StructuredTalkingPoint(
          pillar = "EXECUTIVE_HOOK",
          title = "FinTech Rails & Merchant Economics Anchor",
          bulletPoints = listOf(
            "Emphasize your hackathon background and verified understanding of UPI, cards, and netbanking success rate optimization.",
            "Point directly to your existing ₹18.5L offer from Razorpay as proof of pre-validated technical and strategic fit.",
            "Showcase capability to model merchant MDR, settlement reconciliation, and cross-border payment compliance."
          ),
          impactMetric = "Pre-Validated ₹18.5L Offer · Hackathon Finalist",
          strategicRationale = "Establishes commanding confidence and competitive leverage during discussions."
        )
      ),
      mockQuestions = listOf(
        InterviewCommandQuestion(
          id = "rz_q1",
          questionText = "If checkout drop-off rates on Razorpay's Standard Checkout increase by 2.4% after a banking partner downtime, how do you diagnose and mitigate merchant impact?",
          category = "ANALYTICAL_CASE",
          difficulty = "CORE",
          whyCompanyAsksThis = "Evaluates transaction lifecycle knowledge, dynamic routing logic, and merchant communications under outage pressure.",
          candidateProfileAnchor = "Connects to your FinTech hackathon finalist project on automated routing.",
          structuredTalkingPoints = listOf(
            "Analyze gateway routing rules: trigger auto-switching to backup acquirers.",
            "Examine whether drop-offs are in OTP delivery latency vs bank netbanking gateways.",
            "Provide transparent real-time merchant dashboard banners."
          ),
          sampleStarAnswer = "I immediately isolate the drop-off by payment method in our real-time telemetry. If Bank A's netbanking server latency exceeds 4,000ms, our dynamic routing engine should automatically downgrade its priority score and route transactions through alternative bank gateways or recommend UPI Auto-Pay. Simultaneously, I ensure proactive messaging in the merchant dashboard to prevent merchant panic and unnecessary support ticket surges."
        )
      ),
      highImpactQuestionsForInterviewer = listOf(
        "With UPI Autopay and international cross-border corridors expanding, how is Razorpay optimizing fraud detection latency on high-frequency transactions?",
        "What are the top focus areas for RazorpayX current account adoption among Indian mid-market enterprises?"
      )
    )
  }

  private fun createConsultingDossier(company: String, role: String): InterviewCommandDossier {
    return InterviewCommandDossier(
      companyName = company,
      jobTitle = role,
      industry = "Management Consulting & Corporate Strategy",
      executiveContext = "Top-tier consulting firms test structured MECE (Mutually Exclusive, Collectively Exhaustive) problem solving, numerical agility, executive presence, and commercial intuition.",
      candidateMappingRationale = "Translates your International Business degree and quantified ₹24L financial turnaround into elite case interview frameworks.",
      strategicTalkingPoints = listOf(
        StructuredTalkingPoint(
          pillar = "EXECUTIVE_HOOK",
          title = "MECE Structured Problem Solver",
          bulletPoints = listOf(
            "Frame every business challenge through structured profitability and unit economic trees.",
            "Highlight your International Business degree from DSU as the foundation for global market analysis.",
            "Back every strategic recommendation with verifiable quantitative impact (₹24L saved, 42% time reduction)."
          ),
          impactMetric = "MECE Frameworks · ₹24L Capital Impact",
          strategicRationale = "Directly satisfies the analytical rubric expected by partners and engagement managers."
        )
      ),
      mockQuestions = listOf(
        InterviewCommandQuestion(
          id = "c_q1",
          questionText = "A major retail client is seeing declining operating profit despite growing top-line revenue by 20%. How would you structure this case?",
          category = "ANALYTICAL_CASE",
          difficulty = "CORE",
          whyCompanyAsksThis = "Tests standard profitability framework application, root-cause decomposition, and numerical hypothesis generation.",
          candidateProfileAnchor = "Ties directly to your working capital and inventory optimization experience.",
          structuredTalkingPoints = listOf(
            "Deconstruct Profit into Revenue and Cost.",
            "Investigate Revenue drivers: product mix shift toward low-margin SKUs, heavy discounting.",
            "Investigate Cost drivers: fixed overhead versus variable logistics/procurement cost inflation."
          ),
          sampleStarAnswer = "I structure the problem by decomposing Profit into Revenue and Costs. Since top-line grew 20%, the profit compression must stem either from unfavorable product mix or outpaced cost expansion. On Revenue, I would check if growth came from high-volume, negative-margin loss leaders or aggressive discounting. On Costs, I would break expenses into COGS, logistics fulfillment, and customer acquisition CAC. In my own supply chain work, we found that rapid volume expansion often conceals ballooning stockout and emergency freight fees, which completely erodes operating margins."
        )
      ),
      highImpactQuestionsForInterviewer = listOf(
        "How are your strategy practice teams integrating agentic AI into client market research and commercial due diligence workflows?",
        "What differentiates an analyst who merely solves the case from one who builds lasting client trust with C-suite executives?"
      )
    )
  }

  private fun createEcommerceDossier(company: String, role: String): InterviewCommandDossier {
    return InterviewCommandDossier(
      companyName = company,
      jobTitle = role,
      industry = "E-Commerce & Supply Chain Logistics",
      executiveContext = "Large-scale e-commerce leaders evaluate network design, seller logistics compliance, fulfillment center throughput, and customer NPS.",
      candidateMappingRationale = "Directly aligns your inventory optimization and 42% order cycle reduction with modern fulfillment networks.",
      strategicTalkingPoints = listOf(
        StructuredTalkingPoint(
          pillar = "EXECUTIVE_HOOK",
          title = "Fulfillment Network & Inventory Velocity",
          bulletPoints = listOf(
            "Position yourself as an analytical supply chain operator who understands warehouse throughput.",
            "Highlight experience in stockout reduction, reorder algorithms, and vendor negotiations.",
            "Demonstrate fluency with BigQuery, SQL, and inventory turn calculations."
          ),
          impactMetric = "42% Cycle Reduction · ₹24L Capital Freed",
          strategicRationale = "Addresses the core competencies required for e-commerce category and operations managers."
        )
      ),
      mockQuestions = listOf(
        InterviewCommandQuestion(
          id = "ec_q1",
          questionText = "How do you calculate and optimize inventory turn for high-velocity versus seasonal categories in fulfillment centers?",
          category = "ROLE_DOMAIN",
          difficulty = "CORE",
          whyCompanyAsksThis = "Tests fundamental supply chain inventory accounting, working capital awareness, and seasonal buffer design.",
          candidateProfileAnchor = "Draws directly upon your ₹24L working capital optimization project.",
          structuredTalkingPoints = listOf(
            "Define Inventory Turn = COGS / Average Inventory Value.",
            "Compare fast turns (12-15x per year) for FMCG with seasonal turns (2-4x per year).",
            "Discuss storage holding fees vs stockout risk trade-offs."
          ),
          sampleStarAnswer = "Inventory turnover measures how many times inventory is sold and replaced over a period, calculated as Cost of Goods Sold divided by Average Inventory. For high-velocity categories, you target turns above 12x to minimize holding costs and obsolescence. For seasonal goods, the priority shifts to pre-season build-up and aggressive post-season markdown schedules to clear warehouse bin space. In my inventory optimization project, by adjusting safety stock thresholds dynamically, we accelerated turn velocity and liberated ₹24L in working capital."
        )
      ),
      highImpactQuestionsForInterviewer = listOf(
        "How is your supply chain network adapting to same-day delivery expectations while managing fulfillment center labor constraints?",
        "What role does predictive machine learning play in regional distribution center inventory balancing before major sale events?"
      )
    )
  }

  private fun createGenericDossier(
    company: String,
    role: String,
    candidateName: String,
    degree: String
  ): InterviewCommandDossier {
    return InterviewCommandDossier(
      companyName = company,
      jobTitle = role,
      industry = "Technology & High Growth Enterprise",
      executiveContext = "$company holds high standards for $role, focusing on analytical ownership, rapid problem solving, and quantifiable business execution.",
      candidateMappingRationale = "Mapped $candidateName's background in $degree, supply chain optimization, and automated SQL workflows into $company's strategic priorities.",
      strategicTalkingPoints = listOf(
        StructuredTalkingPoint(
          pillar = "EXECUTIVE_HOOK",
          title = "Executive 60-Second Opening Hook for $company",
          bulletPoints = listOf(
            "Introduce yourself as an operator-analyst passionate about $company's market position.",
            "Highlight your academic grounding in $degree and hands-on operational leadership.",
            "State your core value proposition: solving complex bottlenecks using data to unlock measurable capital and time savings."
          ),
          impactMetric = "42% Cycle Reduction · ₹24L Capital Freed",
          strategicRationale = "Creates an unforgettable first impression anchored in verifiable business impact."
        ),
        StructuredTalkingPoint(
          pillar = "METRICS_PROOF",
          title = "Core Verified Metrics Proof Points",
          bulletPoints = listOf(
            "Cut order fulfillment cycle time by 42% through process re-engineering.",
            "Protected ₹24,00,000 in working capital by eliminating dead stock and optimizing re-order points.",
            "Eliminated 60% of stockout errors by architecting automated SQL inventory triggers.",
            "Finalist in Razorpay Fintech Hackathon; holds active ₹18.5L offer."
          ),
          impactMetric = "₹24L Saved · 60% Error Reduction",
          strategicRationale = "Provides concrete proof that defeats skepticism in rigorous technical and behavioral rounds."
        ),
        StructuredTalkingPoint(
          pillar = "STRATEGIC_ALIGNMENT",
          title = "Strategic Alignment with $company's Operating Ethos",
          bulletPoints = listOf(
            "Articulate why $company's approach to $role is distinctive in the current market.",
            "Demonstrate familiarity with the macro headwinds and unit economic challenges facing the sector.",
            "Express clear enthusiasm for taking end-to-end ownership without needing hand-holding."
          ),
          impactMetric = "High Ownership · Strategic Value Add",
          strategicRationale = "Proves you did deep diligence on $company rather than treating this as a generic interview."
        ),
        StructuredTalkingPoint(
          pillar = "STAR_FRAMEWORK",
          title = "Model STAR Framework: Resolving Critical Operational Deadlock",
          bulletPoints = listOf(
            "Situation: Inherited a congested operational workflow with escalating turnaround times.",
            "Task: Diagnose the root cause and restore efficiency without increasing team budget.",
            "Action: Deployed SQL queries across workflow timestamps, identified bottlenecks, and automated key handoffs.",
            "Result: Slashed cycle times by 42% and saved ₹24L in working capital."
          ),
          impactMetric = "STAR Master Narrative Ready for Instant Recall",
          strategicRationale = "Equips candidate with a universally adaptable STAR story that fits any behavioral question."
        ),
        StructuredTalkingPoint(
          pillar = "PITFALLS_AVOID",
          title = "Pitfalls to Avoid in $company Rounds",
          bulletPoints = listOf(
            "Never give vague, theoretical answers without numbers and metrics.",
            "Avoid speaking negatively about past employers, colleagues, or suppliers.",
            "Don't hesitate to take 5-10 seconds to structure your thoughts before answering complex case prompts."
          ),
          impactMetric = "Zero-Blunder Guardrail",
          strategicRationale = "Ensures clean, polished delivery under intense interviewer scrutiny."
        )
      ),
      mockQuestions = listOf(
        InterviewCommandQuestion(
          id = "gen_q1",
          questionText = "Tell me about yourself and why you believe your background makes you a strong fit for $role at $company.",
          category = "BEHAVIORAL_STAR",
          difficulty = "CORE",
          whyCompanyAsksThis = "Assesses narrative clarity, self-awareness, and how purposefully you connect past achievements to $company's needs.",
          candidateProfileAnchor = "Draws on your $degree background, supply chain optimization track record, and hackathon achievements.",
          structuredTalkingPoints = listOf(
            "Open with your core identity: an analytical operator focused on measurable business outcomes.",
            "Highlight your academic foundation in $degree and commercial acumen.",
            "Share your defining achievement: reducing cycle time by 42% and freeing ₹24L in working capital.",
            "Conclude with why $company is the natural next step where you can create outsized value."
          ),
          sampleStarAnswer = "I'm an analytical operator with a BBA in International Business, driven by using data to solve high-velocity operational bottlenecks. In my work overhauling family enterprise logistics, I diagnosed systemic supply chain delays using automated SQL pipelines, cutting order cycle times by 42% and freeing ₹24,00,000 in working capital. I thrive in high-ownership environments where fast decision-making and rigorous execution matter. I'm excited about $company because of your ambitious scale and commitment to excellence in $role, where I can bring that same data-backed intensity."
        ),
        InterviewCommandQuestion(
          id = "gen_q2",
          questionText = "Walk me through a challenging problem you solved using data where the initial hypothesis turned out to be wrong.",
          category = "ANALYTICAL_CASE",
          difficulty = "CORE",
          whyCompanyAsksThis = "Tests intellectual honesty, data skepticism, and whether you pivot based on evidence rather than confirmation bias.",
          candidateProfileAnchor = "Anchors your real experience diagnosing order delays and building SQL automated scripts.",
          structuredTalkingPoints = listOf(
            "State initial hypothesis: assumed staff velocity was the primary delay driver.",
            "Describe the data investigation: joined order timestamps with packaging logs in SQL.",
            "Explain the pivot: discovered supplier packaging stockouts were stalling packing stations.",
            "Deliver the result: 42% cycle reduction and a lasting automated alert system."
          ),
          sampleStarAnswer = "When our fulfillment turnaround was stalling, our initial hypothesis was that packing personnel needed retraining. However, when I wrote SQL queries analyzing stage-by-stage timestamp logs, I realized the active picking duration was completely normal. The delay occurred at the final packaging station where workers waited an average of 22 minutes for shipping containers to be restocked. The true problem was packaging replenishment, not worker speed. By creating an automated re-order trigger, we eliminated the bottleneck and cut cycle time by 42%."
        ),
        InterviewCommandQuestion(
          id = "gen_q3",
          questionText = "What would you prioritize during your first 30, 60, and 90 days as $role at $company?",
          category = "STRATEGIC_FIT",
          difficulty = "CORE",
          whyCompanyAsksThis = "Evaluates onboarding maturity, executive planning, and balance between listening and driving impact.",
          candidateProfileAnchor = "Demonstrates structured leadership and business planning capabilities.",
          structuredTalkingPoints = listOf(
            "Days 1-30: Deep discovery, mapping existing telemetry, building stakeholder trust.",
            "Days 31-60: Identifying low-hanging efficiency bottlenecks and delivering quick quick-win optimizations.",
            "Days 61-90: Architecting scalable systems and driving long-term strategic initiatives."
          ),
          sampleStarAnswer = "In the first 30 days, my focus is intense discovery: absorbing $company's core operational metrics, shadowing frontline teams, and understanding stakeholder pain points without making hasty changes. From days 31 to 60, I identify quick-win bottlenecks where my data modeling and automation skills can deliver immediate 10-20% time or cost savings to build team trust. By days 61 to 90, I transition into proposing and executing long-term systemic improvements that align with $company's quarterly goals."
        ),
        InterviewCommandQuestion(
          id = "gen_q4",
          questionText = "Tell me about a time you had to balance conflicting priorities under tight deadlines.",
          category = "ROLE_DOMAIN",
          difficulty = "ADVANCED",
          whyCompanyAsksThis = "Tests triage framework, emotional composure, and stakeholder communication when resources are constrained.",
          candidateProfileAnchor = "Demonstrates how you handled peak demand periods and vendor contract deadlines.",
          structuredTalkingPoints = listOf(
            "Establish the prioritization criteria (business revenue impact and customer SLA).",
            "Communicate proactively with affected stakeholders before deadlines slip.",
            "Deliver high-impact items with zero defect."
          ),
          sampleStarAnswer = "During a seasonal sales surge, I had to balance an urgent demand forecasting deadline with resolving an ongoing supplier fulfillment dispute. I triaged the tasks by assessing business impact: delayed supplier resolution directly risked stockouts within 24 hours, whereas the forecasting report had a 48-hour executive window. I spent the morning negotiating an interim vendor delivery buffer, secured the stock, and then utilized our pre-built SQL automation scripts to compile the forecasting report ahead of schedule."
        ),
        InterviewCommandQuestion(
          id = "gen_q5",
          questionText = "Where do you see the biggest opportunity for $company to gain an unfair advantage in the next 2-3 years?",
          category = "BAR_RAISER",
          difficulty = "BAR_RAISER",
          whyCompanyAsksThis = "Tests strategic foresight, macro awareness, and whether the candidate thinks like an owner rather than a pure task-taker.",
          candidateProfileAnchor = "Ties to your strategic understanding of AI agent workflows and international business dynamics.",
          structuredTalkingPoints = listOf(
            "Discuss autonomous operational agent workflows reducing manual clerical latency.",
            "Emphasize unit economics discipline and real-time customer feedback loops.",
            "Frame your own desire to help lead that transition within $role."
          ),
          sampleStarAnswer = "I believe $company's biggest opportunity lies in embedding agentic AI workflows directly into operational execution loops rather than just customer-facing chatbots. By automating real-time bottleneck detection, supplier SLA tracking, and dynamic resource allocation, $company can permanently reduce operational friction and expand margins while legacy competitors remain stuck in manual spreadsheets. As $role, I want to be at the forefront of driving that efficiency."
        )
      ),
      highImpactQuestionsForInterviewer = listOf(
        "What does outstanding performance look like for $role at $company over the next 6 to 12 months?",
        "What is the single biggest bottleneck that currently prevents your team from scaling twice as fast?",
        "How has $company's operating culture evolved as the team has grown, and what has remained non-negotiable?"
      )
    )
  }
}
