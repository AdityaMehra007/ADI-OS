package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyBriefing
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateElevated
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.TitanCrimson
import com.example.ui.theme.TitanCyan
import com.example.ui.theme.TitanEmerald
import com.example.ui.theme.TitanGold
import com.example.ui.theme.TitanIndigo
import com.example.ui.viewmodel.TitanScreen
import com.example.ui.viewmodel.TitanViewModel

/**
 * Synthesized actionable task item
 */
data class BriefingTaskItem(
  val id: String,
  val title: String,
  val category: String,
  val priority: String,
  val priorityColor: Color,
  val targetScreen: TitanScreen? = null,
  var isCompleted: Boolean = false
)

/**
 * Upcoming interview agenda item
 */
data class UpcomingInterviewAgenda(
  val company: String,
  val role: String,
  val roundName: String,
  val scheduleTime: String,
  val focusTopic: String,
  val readinessScore: Int,
  val tierBadge: String
)

/**
 * Actionable Daily Briefing Component
 * Synthesizes career tasks, upcoming interviews, and strategic directives into a single actionable dashboard card.
 */
@Composable
fun DailyBriefingActionSummary(
  viewModel: TitanViewModel,
  dailyBriefing: DailyBriefing?,
  brutalMode: Boolean = false,
  modifier: Modifier = Modifier
) {
  val tasks = remember {
    mutableStateListOf(
      BriefingTaskItem(
        id = "task_msft_drill",
        title = "Complete 15-min Azure Cloud AI monetization drill before final round",
        category = "INTERVIEW DRILL",
        priority = "P0 CRITICAL",
        priorityColor = TitanCrimson,
        targetScreen = TitanScreen.INTERVIEWS,
        isCompleted = false
      ),
      BriefingTaskItem(
        id = "task_rzp_offer",
        title = "Model ₹18.5L Razorpay CTC in-hand tax breakdown and Koramangala commute vs MSFT",
        category = "OFFER EVALUATION",
        priority = "P0 CRITICAL",
        priorityColor = TitanCrimson,
        targetScreen = TitanScreen.APPLICATIONS,
        isCompleted = false
      ),
      BriefingTaskItem(
        id = "task_google_referral",
        title = "Dispatch personalized intro to Siddharth Menon for Associate Account Strategist opening",
        category = "NETWORK OUTREACH",
        priority = "P1 HIGH",
        priorityColor = TitanGold,
        targetScreen = TitanScreen.NETWORK,
        isCompleted = false
      ),
      BriefingTaskItem(
        id = "task_sql_portfolio",
        title = "Finalize SQL demand-forecasting case study on GitHub and link in Titan profile",
        category = "PROOF OF WORK",
        priority = "P2 STRATEGIC",
        priorityColor = TitanCyan,
        targetScreen = TitanScreen.SKILLS_PROJECTS,
        isCompleted = true
      )
    )
  }

  val upcomingInterviews = remember {
    listOf(
      UpcomingInterviewAgenda(
        company = "Microsoft",
        role = "Cloud Solution & Strategy Lead",
        roundName = "Executive Director Final Round",
        scheduleTime = "Tomorrow • 11:00 AM IST",
        focusTopic = "Commercial Enterprise Azure AI Strategy & STAR Leadership",
        readinessScore = 96,
        tierBadge = "TIER S+"
      ),
      UpcomingInterviewAgenda(
        company = "Bain & Company",
        role = "Strategy & Ops Consultant",
        roundName = "Round 2 Case Problem Solving",
        scheduleTime = "Sep 15 • 02:30 PM IST",
        focusTopic = "Retail Supply Chain Unit Economics & Market Sizing",
        readinessScore = 88,
        tierBadge = "TIER S"
      )
    )
  }

  val completedCount = tasks.count { it.isCompleted }
  val totalTasks = tasks.size

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("daily_briefing_action_summary"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = SlateCard),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (brutalMode) TitanCrimson.copy(alpha = 0.5f) else TitanGold.copy(alpha = 0.45f)
    )
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // 1. Header & AI Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(if (brutalMode) TitanCrimson.copy(alpha = 0.15f) else TitanGold.copy(alpha = 0.15f))
              .border(
                1.dp,
                if (brutalMode) TitanCrimson.copy(alpha = 0.4f) else TitanGold.copy(alpha = 0.4f),
                CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (brutalMode) Icons.Default.Bolt else Icons.Default.NotificationsActive,
              contentDescription = null,
              tint = if (brutalMode) TitanCrimson else TitanGold,
              modifier = Modifier.size(16.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = if (brutalMode) "BRUTAL EXECUTIVE SYNTHESIS" else "DAILY CAREER SYNTHESIS",
                style = MaterialTheme.typography.labelSmall,
                color = if (brutalMode) TitanCrimson else TitanGold,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.1.sp,
                fontSize = 10.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(TitanCyan.copy(alpha = 0.15f))
                  .padding(horizontal = 4.dp, vertical = 1.dp)
              ) {
                Text(
                  text = "ACTIONABLE BRIEF",
                  color = TitanCyan,
                  fontSize = 7.5.sp,
                  fontWeight = FontWeight.Black
                )
              }
            }
            Text(
              text = dailyBriefing?.dateString ?: "Sunday, Sep 10, 2026",
              style = MaterialTheme.typography.labelSmall,
              color = TextMutedDark,
              fontSize = 9.5.sp
            )
          }
        }

        // Completion Progress
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SlateElevated)
            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = if (completedCount == totalTasks) TitanEmerald else TitanGold,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "$completedCount/$totalTasks Actions Done",
              style = MaterialTheme.typography.labelSmall,
              color = TextPrimaryDark,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 2. Executive Headline & Strategic Directive
      Text(
        text = dailyBriefing?.headline ?: "Pre-Interview Sprint: Lock Microsoft Strategy & Leverage Razorpay Term Sheet",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = TextPrimaryDark,
        fontSize = 13.5.sp
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Actionable directive card
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(SlateElevated)
          .border(1.dp, if (brutalMode) TitanCrimson.copy(alpha = 0.3f) else TitanCyan.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
          .padding(10.dp)
      ) {
        Row(verticalAlignment = Alignment.Top) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = if (brutalMode) TitanCrimson else TitanCyan,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = if (brutalMode) "BRUTAL EXECUTIVE DIRECTIVE:" else "STRATEGIC DIRECTIVE:",
              style = MaterialTheme.typography.labelSmall,
              color = if (brutalMode) TitanCrimson else TitanCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 9.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = dailyBriefing?.strategicDirective ?: "Focus 80% energy on Microsoft's final evaluation. Do NOT sign Razorpay before MSFT round conclusion; use Razorpay's ₹18.5L offer as baseline leverage to push Microsoft to ₹22L+ band.",
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark,
              fontSize = 11.sp,
              lineHeight = 15.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 3. Upcoming Interviews (High-Stakes)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            tint = TitanGold,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "UPCOMING INTERVIEWS (2 ACTIVE)",
            style = MaterialTheme.typography.labelSmall,
            color = TitanGold,
            fontWeight = FontWeight.Bold,
            fontSize = 9.5.sp
          )
        }
        Text(
          text = "Tap to Prep",
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontSize = 9.sp
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        upcomingInterviews.forEach { interview ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(SlateElevated)
              .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
              .clickable { viewModel.navigateTo(TitanScreen.INTERVIEWS) }
              .padding(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(4.dp))
                      .background(TitanGold.copy(alpha = 0.2f))
                      .padding(horizontal = 4.dp, vertical = 1.dp)
                  ) {
                    Text(
                      text = interview.tierBadge,
                      color = TitanGold,
                      fontSize = 8.sp,
                      fontWeight = FontWeight.Black
                    )
                  }
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "${interview.company} • ${interview.role}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                  )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                  text = "${interview.roundName} | ${interview.scheduleTime}",
                  style = MaterialTheme.typography.bodySmall,
                  color = TitanCyan,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                  text = "Angle: ${interview.focusTopic}",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark,
                  fontSize = 9.5.sp
                )
              }

              Spacer(modifier = Modifier.width(8.dp))

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(TitanCyan.copy(alpha = 0.15f))
                  .border(1.dp, TitanCyan.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                  .padding(horizontal = 8.dp, vertical = 6.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    tint = TitanCyan,
                    modifier = Modifier.size(12.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Drill Prep",
                    color = TitanCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.5.sp
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 4. Synthesized Actionable Career Tasks
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
            contentDescription = null,
            tint = TitanCyan,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "CAREER TASKS & EXECUTION QUEUE",
            style = MaterialTheme.typography.labelSmall,
            color = TitanCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 9.5.sp
          )
        }
        Text(
          text = "Tap circle to complete",
          style = MaterialTheme.typography.labelSmall,
          color = TextMutedDark,
          fontSize = 9.sp
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        tasks.forEachIndexed { index, task ->
          val taskCompleted = task.isCompleted
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(if (taskCompleted) SlateElevated.copy(alpha = 0.6f) else SlateElevated)
              .border(
                1.dp,
                if (taskCompleted) SlateBorder.copy(alpha = 0.5f) else SlateBorder,
                RoundedCornerShape(8.dp)
              )
              .padding(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.Top
            ) {
              // Checkbox icon
              Box(
                modifier = Modifier
                  .size(22.dp)
                  .clip(CircleShape)
                  .clickable {
                    tasks[index] = task.copy(isCompleted = !task.isCompleted)
                  },
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (taskCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                  contentDescription = if (taskCompleted) "Completed" else "Incomplete",
                  tint = if (taskCompleted) TitanEmerald else TextMutedDark,
                  modifier = Modifier.size(18.dp)
                )
              }

              Spacer(modifier = Modifier.width(8.dp))

              Column(modifier = Modifier.weight(1f)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(3.dp))
                      .background(task.priorityColor.copy(alpha = 0.15f))
                      .padding(horizontal = 4.dp, vertical = 1.dp)
                  ) {
                    Text(
                      text = "${task.priority} • ${task.category}",
                      color = task.priorityColor,
                      fontSize = 8.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }

                  if (task.targetScreen != null && !taskCompleted) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { viewModel.navigateTo(task.targetScreen) }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = "Go",
                        color = TitanCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                      )
                      Spacer(modifier = Modifier.width(2.dp))
                      Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = TitanCyan,
                        modifier = Modifier.size(9.dp)
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                  text = task.title,
                  style = MaterialTheme.typography.bodySmall,
                  color = if (taskCompleted) TextMutedDark else TextPrimaryDark,
                  fontSize = 11.sp,
                  lineHeight = 15.sp,
                  textDecoration = if (taskCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
              }
            }
          }
        }
      }
    }
  }
}
