package com.example.service

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.data.model.ParsedResumeDocument
import com.example.data.model.ResumeJdMatchResult
import com.example.ui.components.MatchTier
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream

/**
 * Robust document parser for user resume files (PDF, TXT, MD, RTF) that extracts structured
 * candidate profiles, skills, and work experience, and compares them against job descriptions
 * for candidate match score calculation.
 */
object ResumeDocumentParser {

  private val COMMON_INDUSTRY_SKILLS = listOf(
    "sql", "python", "powerbi", "tableau", "excel", "etl", "sla", "dark store",
    "unit economics", "quick commerce", "supply chain", "inventory", "forecasting",
    "root-cause analysis", "order cycle time", "fulfillment", "logistics", "procurement",
    "vendor management", "kpi", "okr", "b2b", "stakeholder management", "financial modeling",
    "market sizing", "cohort retention", "churn", "growth", "operations", "automation",
    "cross-functional leadership", "agile", "scrum", "data analytics", "telemetry",
    "customer acquisition", "cac", "ltv", "gmv", "margin", "p&l", "ebitda", "pricing"
  )

  /**
   * Parses a user resume from an Android content Uri (PDF, text, markdown, etc.).
   */
  fun parseResumeFromUri(context: Context, uri: Uri): Result<ParsedResumeDocument> {
    return runCatching {
      val contentResolver = context.contentResolver
      val fileName = queryFileName(context, uri) ?: "uploaded_resume.pdf"
      val fileSizeBytes = queryFileSize(context, uri) ?: 0L

      val bytes = contentResolver.openInputStream(uri)?.use { inputStream ->
        readAllBytes(inputStream)
      } ?: throw IllegalArgumentException("Cannot open input stream for URI: $uri")

      val fileType = inferFileType(fileName, bytes)
      val extractedText = when (fileType) {
        "PDF" -> extractTextFromPdfBytes(bytes)
        "RTF" -> stripRtf(String(bytes, StandardCharsets.UTF_8))
        else -> String(bytes, StandardCharsets.UTF_8)
      }

      val effectiveSize = if (fileSizeBytes > 0) fileSizeBytes else bytes.size.toLong()
      LocalDocumentParser.parseDocumentText(
        fileName = fileName,
        rawText = extractedText,
        fileType = fileType,
        fileSizeBytes = effectiveSize
      )
    }
  }

  /**
   * Parses raw resume text directly into a structured document.
   */
  fun parseResumeFromText(
    fileName: String = "pasted_resume.txt",
    rawText: String,
    fileType: String = "TXT"
  ): ParsedResumeDocument {
    return LocalDocumentParser.parseDocumentText(
      fileName = fileName,
      rawText = rawText,
      fileType = fileType,
      fileSizeBytes = rawText.toByteArray(StandardCharsets.UTF_8).size.toLong()
    )
  }

  /**
   * Extracts clean human-readable text from raw PDF bytes.
   * Handles PDF streams, FlateDecode decompression, and PDF text operators (BT..ET, Tj, TJ).
   */
  fun extractTextFromPdfBytes(pdfBytes: ByteArray): String {
    if (pdfBytes.isEmpty()) return ""

    val textBuilder = StringBuilder()
    val fullRaw = String(pdfBytes, StandardCharsets.ISO_8859_1)

    // 1. Locate and process all PDF streams (handling FlateDecode zlib compression)
    val streamRegex = Regex("/Filter\\s*/FlateDecode[\\s\\S]*?stream[\\r\\n]+([\\s\\S]*?)[\\r\\n]+endstream")
    val streamMatches = streamRegex.findAll(fullRaw)

    for (match in streamMatches) {
      val streamContent = match.groupValues[1]
      val streamRawBytes = streamContent.toByteArray(StandardCharsets.ISO_8859_1)
      val decompressedBytes = decompressFlate(streamRawBytes)
      if (decompressedBytes != null) {
        val decompressedText = String(decompressedBytes, StandardCharsets.ISO_8859_1)
        val extractedFromStream = extractTextFromPdfStream(decompressedText)
        if (extractedFromStream.isNotBlank()) {
          textBuilder.append(extractedFromStream).append("\n")
        }
      }
    }

    // 2. Also search for uncompressed BT...ET blocks
    val uncompressedText = extractTextFromPdfStream(fullRaw)
    if (uncompressedText.isNotBlank()) {
      textBuilder.append(uncompressedText).append("\n")
    }

    val extracted = cleanExtractedText(textBuilder.toString())

    // 3. Fallback: If structured stream extraction yielded minimal text (e.g. non-standard font mapping),
    // extract printable ASCII sequences
    return if (extracted.length > 80) {
      extracted
    } else {
      extractPrintableAsciiSequences(pdfBytes)
    }
  }

  /**
   * Extracts text from BT (Begin Text) to ET (End Text) blocks and Tj / TJ operators.
   */
  private fun extractTextFromPdfStream(streamText: String): String {
    val result = StringBuilder()
    val btEtRegex = Regex("BT([\\s\\S]*?)ET")
    val btMatches = btEtRegex.findAll(streamText)

    for (bt in btMatches) {
      val block = bt.groupValues[1]

      // Extract parenthesized strings with Tj: (Hello World) Tj
      val tjRegex = Regex("\\((.*?)\\)\\s*Tj")
      for (tj in tjRegex.findAll(block)) {
        val decoded = decodePdfString(tj.groupValues[1])
        if (decoded.isNotBlank()) {
          result.append(decoded).append(" ")
        }
      }

      // Extract array strings with TJ: [(Hello) 10 (World)] TJ
      val tjArrayRegex = Regex("\\[([^\\]]*?)\\]\\s*TJ")
      for (tjArray in tjArrayRegex.findAll(block)) {
        val inner = tjArray.groupValues[1]
        val stringPartRegex = Regex("\\((.*?)\\)")
        for (part in stringPartRegex.findAll(inner)) {
          val decoded = decodePdfString(part.groupValues[1])
          if (decoded.isNotBlank()) {
            result.append(decoded)
          }
        }
        result.append(" ")
      }

      // Handle newlines indicated by T* or Td with negative y
      if (block.contains("T*") || block.contains("TD") || block.contains("Td")) {
        result.append("\n")
      }
    }

    return result.toString()
  }

  /**
   * Decompresses raw zlib / FlateDecode streams.
   */
  private fun decompressFlate(data: ByteArray): ByteArray? {
    // Try standard InflaterInputStream
    try {
      val bais = ByteArrayInputStream(data)
      val inflaterStream = InflaterInputStream(bais)
      val buffer = ByteArray(1024)
      val baos = ByteArrayOutputStream()
      var len: Int
      while (inflaterStream.read(buffer).also { len = it } > 0) {
        baos.write(buffer, 0, len)
      }
      return baos.toByteArray()
    } catch (_: Exception) {}

    // Try nowrap Inflater (raw deflate without zlib header)
    try {
      val inflater = Inflater(true)
      inflater.setInput(data)
      val buffer = ByteArray(1024)
      val baos = ByteArrayOutputStream()
      while (!inflater.finished()) {
        val count = inflater.inflate(buffer)
        if (count <= 0 && inflater.needsInput()) break
        baos.write(buffer, 0, count)
      }
      inflater.end()
      val res = baos.toByteArray()
      if (res.isNotEmpty()) return res
    } catch (_: Exception) {}

    return null
  }

  /**
   * Decodes escaped PDF string sequences (e.g. \(, \), \\, \r, \n, \t).
   */
  private fun decodePdfString(input: String): String {
    return input
      .replace("\\(", "(")
      .replace("\\)", ")")
      .replace("\\\\", "\\")
      .replace("\\r", "\n")
      .replace("\\n", "\n")
      .replace("\\t", " ")
  }

  /**
   * Fallback scanner that extracts contiguous printable ASCII sequences (min length 4).
   */
  private fun extractPrintableAsciiSequences(bytes: ByteArray): String {
    val sb = StringBuilder()
    val current = StringBuilder()

    for (b in bytes) {
      val c = b.toInt().toChar()
      if (c in ' '..'~' || c == '\n' || c == '\t') {
        current.append(c)
      } else {
        if (current.length >= 4) {
          val word = current.toString().trim()
          if (word.isNotBlank() && !word.startsWith("%") && !word.startsWith("/")) {
            sb.append(word).append(" ")
          }
        }
        current.setLength(0)
      }
    }
    if (current.length >= 4) {
      sb.append(current.toString().trim())
    }

    return cleanExtractedText(sb.toString())
  }

  /**
   * Normalizes spacing and removes residual binary noise.
   */
  private fun cleanExtractedText(text: String): String {
    return text
      .replace(Regex("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]"), " ")
      .replace(Regex(" +"), " ")
      .replace(Regex("(\\r?\\n){3,}"), "\n\n")
      .trim()
  }

  private fun stripRtf(rtf: String): String {
    return rtf
      .replace(Regex("\\\\[a-zA-Z0-9]+ ?"), "")
      .replace(Regex("[{}\\\\]"), "")
      .trim()
  }

  private fun inferFileType(fileName: String, bytes: ByteArray? = null): String {
    val lower = fileName.lowercase()
    if (bytes != null && bytes.size >= 5) {
      val header = String(bytes.copyOfRange(0, 5), StandardCharsets.ISO_8859_1)
      if (header.startsWith("%PDF")) return "PDF"
    }
    return when {
      lower.endsWith(".pdf") -> "PDF"
      lower.endsWith(".docx") -> "DOCX"
      lower.endsWith(".doc") -> "DOC"
      lower.endsWith(".rtf") -> "RTF"
      lower.endsWith(".md") -> "MD"
      else -> "TXT"
    }
  }

  private fun queryFileName(context: Context, uri: Uri): String? {
    if (uri.scheme == "content") {
      context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
          val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
          if (nameIndex >= 0) {
            return cursor.getString(nameIndex)
          }
        }
      }
    }
    return uri.lastPathSegment
  }

  private fun queryFileSize(context: Context, uri: Uri): Long? {
    if (uri.scheme == "content") {
      context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
          val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
          if (sizeIndex >= 0) {
            return cursor.getLong(sizeIndex)
          }
        }
      }
    }
    return null
  }

  private fun readAllBytes(inputStream: InputStream): ByteArray {
    val buffer = ByteArray(4096)
    val baos = ByteArrayOutputStream()
    var bytesRead: Int
    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
      baos.write(buffer, 0, bytesRead)
    }
    return baos.toByteArray()
  }

  // =========================================================================
  // MATCH SCORE CALCULATION (RESUME VS JOB DESCRIPTION)
  // =========================================================================

  /**
   * Compares the parsed resume against the target job description to compute a detailed
   * match score, skill breakdown, and candidate alignment metrics.
   */
  fun compareResumeWithJobDescription(
    resume: ParsedResumeDocument,
    jobDescription: String,
    targetRole: String? = null,
    targetCompany: String? = null
  ): ResumeJdMatchResult {
    val jdLower = jobDescription.lowercase()
    val resumeLower = (resume.rawText + " " + resume.skills.joinToString(" ")).lowercase()

    val effectiveRole = targetRole?.takeIf { it.isNotBlank() }
      ?: extractRoleHint(jobDescription)
      ?: "Strategic Operations Lead"
    val effectiveCompany = targetCompany?.takeIf { it.isNotBlank() }
      ?: extractCompanyHint(jobDescription)
      ?: "Target Enterprise"

    // 1. Identify skills mentioned in the Job Description
    val jdSkills = extractSkillsFromJobDescription(jdLower)
    val totalJdSkills = if (jdSkills.isNotEmpty()) jdSkills else COMMON_INDUSTRY_SKILLS.take(10)

    val matchedSkills = mutableListOf<String>()
    val missingSkills = mutableListOf<String>()

    for (skill in totalJdSkills) {
      if (resumeContainsSkill(resumeLower, skill)) {
        matchedSkills.add(skill.capitalizeWords())
      } else {
        missingSkills.add(skill.capitalizeWords())
      }
    }

    // 2. Compute individual category scores
    val skillsRatio = if (totalJdSkills.isNotEmpty()) {
      matchedSkills.size.toFloat() / totalJdSkills.size.toFloat()
    } else 0.8f
    val skillsMatchScore = (skillsRatio * 100).toInt().coerceIn(30, 98)

    // Experience & seniority alignment score
    val experienceFitScore = evaluateExperienceFit(resume, jdLower)

    // Quantified metrics & impact score
    val metricsScore = (resume.quantifiableMetricRatio * 100).toInt().coerceIn(40, 95)

    // 3. Calculate Overall Weighted Match Score (45% skills, 30% experience, 25% metrics)
    val overallScore = ((skillsMatchScore * 0.45f) + (experienceFitScore * 0.30f) + (metricsScore * 0.25f))
      .toInt()
      .coerceIn(10, 99)

    // 4. Generate Strengths & Gap Pivots
    val strengths = mutableListOf<String>()
    if (matchedSkills.isNotEmpty()) {
      strengths.add("Verified proficiency in: ${matchedSkills.take(4).joinToString(", ")}")
    }
    if (resume.quantifiableMetricRatio > 0.3f) {
      strengths.add("High quantitative density: ${(resume.quantifiableMetricRatio * 100).toInt()}% of bullets contain concrete metrics.")
    }
    if (resume.strongPowerVerbsDetected.isNotEmpty()) {
      strengths.add("Action-oriented leadership: Powered by verbs like ${resume.strongPowerVerbsDetected.take(3).joinToString(", ")}.")
    }

    val gapPivots = mutableListOf<String>()
    if (missingSkills.isNotEmpty()) {
      gapPivots.add("Address missing requirements: ${missingSkills.take(3).joinToString(", ")} during technical interviews.")
    }
    gapPivots.add("Tailor executive summary to explicitly mention $effectiveCompany and $effectiveRole keywords.")

    val verdict = when {
      overallScore >= 85 -> "Outstanding alignment for $effectiveRole at $effectiveCompany. Profile demonstrates high domain competency and quantifiable track record."
      overallScore >= 70 -> "Strong baseline fit. Core competencies align well with $effectiveCompany, with minor gaps that can be bridged via focused interview prep."
      overallScore >= 50 -> "Moderate match. Foundational aptitude present, but specific requirements (${missingSkills.take(2).joinToString(", ")}) should be emphasized."
      else -> "Skill gaps detected. Proactively highlight transferable execution experience and complete targeted preparation."
    }

    return ResumeJdMatchResult(
      overallMatchScore = overallScore,
      skillsMatchScore = skillsMatchScore,
      experienceFitScore = experienceFitScore,
      metricsScore = metricsScore,
      matchedSkills = matchedSkills,
      missingSkills = missingSkills,
      totalJdSkillsCount = totalJdSkills.size,
      matchedCount = matchedSkills.size,
      executiveVerdict = verdict,
      strengths = strengths,
      gapPivots = gapPivots,
      parsedResumeName = resume.contactInfo.candidateName.ifBlank { resume.fileName },
      parsedResumeRole = resume.experiences.firstOrNull()?.roleTitle ?: "Candidate",
      targetRole = effectiveRole,
      targetCompany = effectiveCompany
    )
  }

  private fun extractSkillsFromJobDescription(jdLower: String): List<String> {
    val detected = mutableListOf<String>()
    for (skill in COMMON_INDUSTRY_SKILLS) {
      if (jdLower.contains(skill)) {
        detected.add(skill)
      }
    }
    return detected.distinct()
  }

  private fun resumeContainsSkill(resumeLower: String, skill: String): Boolean {
    val regex = Regex("\\b${Regex.escape(skill)}\\b")
    return regex.containsMatchIn(resumeLower)
  }

  private fun evaluateExperienceFit(resume: ParsedResumeDocument, jdLower: String): Int {
    var score = 75
    val hasSeniorKeywords = jdLower.contains("senior") || jdLower.contains("lead") || jdLower.contains("head")
    val hasStrategyKeywords = jdLower.contains("strategy") || jdLower.contains("operations") || jdLower.contains("expansion")

    if (resume.experiences.isNotEmpty()) score += 5
    if (resume.experiences.size >= 2) score += 5
    if (hasStrategyKeywords && resume.rawText.contains("operations", ignoreCase = true)) score += 5
    if (hasSeniorKeywords && resume.actionVerbStrengthScore > 70) score += 5

    return score.coerceIn(50, 95)
  }

  private fun extractRoleHint(jd: String): String? {
    val prefixes = listOf("Role:", "Position:", "Job Title:", "Title:")
    for (line in jd.lines()) {
      for (prefix in prefixes) {
        if (line.trim().startsWith(prefix, ignoreCase = true)) {
          val extracted = line.trim().substring(prefix.length).trim()
          if (extracted.isNotBlank()) return extracted.take(45)
        }
      }
    }
    return null
  }

  private fun extractCompanyHint(jd: String): String? {
    val companies = listOf("Zepto", "Swiggy", "Blinkit", "Razorpay", "Flipkart", "CRED", "Amazon", "Google")
    return companies.firstOrNull { jd.contains(it, ignoreCase = true) }
  }

  private fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { word ->
      if (word.length <= 3 && (word == "sql" || word == "kpi" || word == "okr" || word == "etl" || word == "sla" || word == "b2b" || word == "cac" || word == "ltv")) {
        word.uppercase()
      } else {
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
      }
    }
  }
}
