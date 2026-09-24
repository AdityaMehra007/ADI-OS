package com.example.util

import android.content.Context
import android.content.Intent
import com.example.data.model.UserProfile

object AtsResumeDocumentExporter {

  fun formatAtsDocument(
    profile: UserProfile?,
    targetRole: String = "Staff / Senior Software Architect",
    skills: List<String> = listOf("Kotlin", "Jetpack Compose", "Coroutines", "System Design", "Microservices", "Kubernetes", "Kafka", "AWS", "CI/CD"),
    customBullets: List<String> = emptyList()
  ): String {
    val name = profile?.name?.uppercase() ?: "ADITYA MEHRA"
    val location = profile?.location ?: "Bengaluru, India"
    val education = profile?.educationDegree ?: "BBA in International Business"
    val university = profile?.university ?: "Dayananda Sagar University (DSU), Bengaluru"

    val bullets = if (customBullets.isNotEmpty()) {
      customBullets
    } else {
      listOf(
        "• Spearheaded re-architecture of consumer checkout flow to Jetpack Compose, reducing cold start launch latency by 38% and memory consumption by 25MB across 8M+ active users.",
        "• Engineered real-time event-driven telemetry and location tracking pipeline using Kotlin Coroutines and StateFlow, scaling throughput to 15,000 events/second with 99.99% delivery reliability.",
        "• Automated CI/CD build verification pipelines with Gradle cache optimization, cutting pull request integration latency from 24 minutes to 8.5 minutes.",
        "• Architected distributed caching layer across 14 high-throughput microservices, reducing p99 API query response times by 44% and decreasing cloud infrastructure spend by \$42,000 annually.",
        "• Mentored 8 junior and mid-level software engineers across clean architecture, modularization, and test-driven development."
      )
    }

    return buildString {
      appendLine("================================================================================")
      appendLine("                                $name")
      appendLine("                 $location • aditya.verma@example.com • +91 98765 43210")
      appendLine("             LinkedIn: linkedin.com/in/aditya-verma • GitHub: github.com/aditya-v")
      appendLine("================================================================================")
      appendLine()
      appendLine("PROFESSIONAL SUMMARY")
      appendLine("--------------------------------------------------------------------------------")
      appendLine("Results-driven $targetRole with 7+ years of experience engineering high-scale,")
      appendLine("fault-tolerant distributed systems and consumer mobile architectures. Proven record of")
      appendLine("reducing system latency, automating CI/CD pipelines, and driving cross-functional delivery.")
      appendLine()
      appendLine("TECHNICAL COMPETENCIES")
      appendLine("--------------------------------------------------------------------------------")
      appendLine("Languages & Runtimes: Kotlin, Java, TypeScript, SQL, Go")
      appendLine("Architecture & Ops:   Clean Architecture, Microservices, Kubernetes, Docker, CI/CD, AWS")
      appendLine("Data & Streaming:     Kafka, Redis, PostgreSQL, Room DB, StateFlow, Coroutines")
      appendLine("Practices:            System Design, Unit Testing, Performance Profiling, Agile")
      appendLine()
      appendLine("PROFESSIONAL EXPERIENCE")
      appendLine("--------------------------------------------------------------------------------")
      appendLine("STAFF ENGINEER | Zepto Quick Commerce                       2022 - Present")
      appendLine("Bengaluru, India")
      bullets.take(3).forEach { bullet ->
        appendLine(bullet)
      }
      appendLine()
      appendLine("SENIOR SOFTWARE ENGINEER | Swiggy Food Tech                  2019 - 2022")
      appendLine("Bengaluru, India")
      bullets.drop(3).ifEmpty {
        listOf(
          "• Architected payment micro-module processing \$12M+ in monthly transactions with 99.98% reliability.",
          "• Implemented dynamic offline-first database sync, decreasing mobile network bandwidth by 42%."
        )
      }.forEach { bullet ->
        appendLine(bullet)
      }
      appendLine()
      appendLine("EDUCATION")
      appendLine("--------------------------------------------------------------------------------")
      appendLine("$education | $university")
      appendLine("Graduated First Class with Distinction")
      appendLine("================================================================================")
    }
  }

  fun shareAtsDocument(context: Context, documentText: String) {
    val sendIntent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, documentText)
      type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share ATS-Compliant Resume")
    context.startActivity(shareIntent)
  }
}
