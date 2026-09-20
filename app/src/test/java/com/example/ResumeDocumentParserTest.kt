package com.example

import com.example.service.ResumeDocumentParser
import com.example.ui.components.MatchTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.charset.StandardCharsets

class ResumeDocumentParserTest {

  private val sampleResumeText = """
    ADITYA SHENOY
    Bengaluru, India • aditya.shenoy@example.com • +91 98765 43210 • linkedin.com/in/adityashenoy

    EXECUTIVE SUMMARY
    High-velocity Operations and Strategy Lead with 3+ years experience driving micro-fulfillment throughput, dark store logistics, and unit economics. Scaled Bengaluru cluster GMV by 42% and automated ETL telemetry pipelines with SQL and Python.

    CORE SKILLS
    SQL, Python, PowerBI, Tableau, Dark Store Operations, Quick Commerce, Supply Chain Management, Unit Economics, Root-Cause Analysis, SLA Monitoring, Vendor Management, Inventory Forecasting.

    PROFESSIONAL EXPERIENCE
    Strategy & Operations Lead | Bengaluru Quick Logistics (2022 - Present)
    - Supervised 14 dark store micro-fulfillment centers across Bengaluru, achieving 99.2% order accuracy.
    - Designed automated SQL & Python data pipelines, reducing order cycle latency by 38%.
    - Renegotiated tier-1 FMCG vendor contracts, saving ₹18L annually in procurement expenditure.
    - Managed cross-functional pod of 35 rider leads and inventory controllers to uphold 10-minute SLA.

    Operations Analyst | Enterprise Retail Tech (2020 - 2022)
    - Modeled catchment demand forecasting algorithms in Excel and Tableau.
    - Reduced stockout rate from 4.8% to 1.1% through dynamic replenishment trigger controls.

    EDUCATION
    Bachelor of Business Administration (BBA) - Analytics & Operations | Bengaluru University (GPA 3.85/4.0)
  """.trimIndent()

  @Test
  fun testResumeTextParsing() {
    val parsedDoc = ResumeDocumentParser.parseResumeFromText(
      fileName = "Aditya_Shenoy_Resume.txt",
      rawText = sampleResumeText,
      fileType = "TXT"
    )

    assertNotNull(parsedDoc)
    assertEquals("Aditya_Shenoy_Resume.txt", parsedDoc.fileName)
    assertEquals("TXT", parsedDoc.fileType)
    assertTrue(parsedDoc.wordCount > 100)
    assertTrue(parsedDoc.skills.isNotEmpty())

    // Check specific skills parsed
    val skillStrings = parsedDoc.skills.map { it.lowercase() }
    assertTrue(skillStrings.any { it.contains("sql") })
    assertTrue(skillStrings.any { it.contains("python") })
    assertTrue(skillStrings.any { it.contains("dark store") || it.contains("supply chain") })

    // Check metrics ratio calculation
    assertTrue("Quantifiable metrics ratio should be > 0", parsedDoc.quantifiableMetricRatio > 0.2f)
    assertTrue("Action verb strength score should be > 50", parsedDoc.actionVerbStrengthScore > 50)
  }

  @Test
  fun testPdfTextStreamExtraction() {
    val syntheticPdf = """
      %PDF-1.4
      1 0 obj
      << /Type /Catalog /Pages 2 0 R >>
      endobj
      2 0 obj
      << /Type /Pages /Kids [3 0 R] /Count 1 >>
      endobj
      3 0 obj
      << /Type /Page /Parent 2 0 R /Contents 4 0 R >>
      endobj
      4 0 obj
      << /Length 120 >>
      stream
      BT
      /F1 12 Tf
      (Aditya Shenoy - Senior Operations Lead) Tj T*
      (Key Skills: SQL, Python, Dark Store Operations, Supply Chain) Tj T*
      (Reduced delivery cycle time by 42% across 14 dark stores) Tj
      ET
      endstream
      endobj
      xref
      trailer
      << /Root 1 0 R >>
      %%EOF
    """.trimIndent()

    val extractedText = ResumeDocumentParser.extractTextFromPdfBytes(
      syntheticPdf.toByteArray(StandardCharsets.ISO_8859_1)
    )

    assertTrue("Extracted text should not be blank", extractedText.isNotBlank())
    assertTrue("Should contain candidate name", extractedText.contains("Aditya Shenoy"))
    assertTrue("Should contain SQL skill", extractedText.contains("SQL"))
    assertTrue("Should contain Dark Store", extractedText.contains("Dark Store"))
  }

  @Test
  fun testCompareResumeWithJobDescriptionHighFit() {
    val parsedDoc = ResumeDocumentParser.parseResumeFromText(
      fileName = "Aditya_Shenoy_Resume.txt",
      rawText = sampleResumeText,
      fileType = "TXT"
    )

    val zeptoJd = """
      Zepto is hiring a Lead - Strategy & Dark Store Operations in Bengaluru.
      Responsibilities:
      - Drive dark store unit economics, inventory forecasting, and 10-minute quick commerce SLAs.
      - Conduct root-cause analysis on order cycle delays using SQL telemetry and automated Python scripts.
      - Lead vendor management and procurement negotiations.
      Requirements:
      - Bachelor's degree in Business or Engineering.
      - Proven experience with SQL, Python, supply chain logistics, and SLA monitoring.
    """.trimIndent()

    val matchResult = ResumeDocumentParser.compareResumeWithJobDescription(
      resume = parsedDoc,
      jobDescription = zeptoJd,
      targetRole = "Lead - Strategy & Dark Store Operations",
      targetCompany = "Zepto"
    )

    assertNotNull(matchResult)
    assertTrue("Overall score for strong match should be >= 75", matchResult.overallMatchScore >= 75)
    assertTrue("Should classify into EXCEPTIONAL or STRONG tier", matchResult.matchTier in listOf(MatchTier.EXCEPTIONAL, MatchTier.STRONG))
    assertTrue("Matched skills count should be > 0", matchResult.matchedCount > 0)

    val matchedLower = matchResult.matchedSkills.map { it.lowercase() }
    assertTrue(matchedLower.any { it.contains("sql") })
    assertTrue(matchedLower.any { it.contains("dark store") || it.contains("supply chain") })
    assertFalse(matchResult.executiveVerdict.isBlank())
    assertTrue(matchResult.strengths.isNotEmpty())
  }

  @Test
  fun testCompareResumeWithJobDescriptionGapDetection() {
    val parsedDoc = ResumeDocumentParser.parseResumeFromText(
      fileName = "Aditya_Shenoy_Resume.txt",
      rawText = sampleResumeText,
      fileType = "TXT"
    )

    val specializedFinanceJd = """
      High-Frequency Trading Desk Analyst at Citadel Securities.
      Requirements:
      - Deep mastery of financial modeling, arbitrage, market sizing, and pricing algorithms.
      - Customer acquisition cost (CAC), lifetime value (LTV), cohort retention modeling.
      - P&L and EBITDA restructuring frameworks.
    """.trimIndent()

    val matchResult = ResumeDocumentParser.compareResumeWithJobDescription(
      resume = parsedDoc,
      jobDescription = specializedFinanceJd,
      targetRole = "Trading Analyst",
      targetCompany = "Citadel Securities"
    )

    assertNotNull(matchResult)
    assertTrue("Missing skills should be identified", matchResult.missingSkills.isNotEmpty())
    val missingLower = matchResult.missingSkills.map { it.lowercase() }
    assertTrue(missingLower.any { it.contains("cac") || it.contains("ltv") || it.contains("arbitrage") || it.contains("p&l") || it.contains("ebitda") || it.contains("financial modeling") })
  }
}
