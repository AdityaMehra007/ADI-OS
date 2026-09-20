package com.example.ui.components

import com.example.data.model.RequirementFulfillmentStatus
import com.example.data.model.TargetCompanyRole

/**
 * Builds an interactive HTML5 + SVG visualization powered by D3.js
 * to visualize user progress against target company job role requirements over time.
 */
object D3CareerVelocityHtmlBuilder {

  fun buildHtml(role: TargetCompanyRole): String {
    val timelineJson = role.timeline.joinToString(prefix = "[", postfix = "]") { pt ->
      """{"label":"${escapeJson(pt.periodLabel)}","month":${pt.monthIndex},"userProgress":${pt.userProgressPercent},"targetBaseline":${pt.targetRequirementBaseline},"velocityDelta":${pt.velocityDelta},"isProjection":${pt.isProjection},"milestone":"${escapeJson(pt.milestoneAchieved ?: "")}"}"""
    }

    val requirementsJson = role.requirements.joinToString(prefix = "[", postfix = "]") { req ->
      """{"id":"${escapeJson(req.id)}","name":"${escapeJson(req.name)}","category":"${escapeJson(req.category)}","weight":${req.weightPercent},"required":${req.requiredProficiency},"current":${req.currentProgress},"status":"${escapeJson(req.status.name)}","statusLabel":"${escapeJson(req.status.label)}","isFulfilled":${req.isFulfilled}}"""
    }

    val companyName = escapeJson(role.companyName)
    val roleTitle = escapeJson(role.roleTitle)
    val readiness = role.overallReadinessScore
    val velocityRate = role.monthlyVelocityRate
    val daysLeft = role.daysToFullReadiness

    return """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
  <title>D3 Career Velocity Dashboard</title>
  <script src="https://d3js.org/d3.v7.min.js"></script>
  <style>
    * {
      box-sizing: border-box;
      margin: 0;
      padding: 0;
      -webkit-tap-highlight-color: transparent;
      user-select: none;
    }
    body {
      background-color: #090D16;
      color: #F1F5F9;
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
      padding: 12px;
      overflow-x: hidden;
    }
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
    }
    .badge-d3 {
      background: rgba(245, 158, 11, 0.15);
      border: 1px solid rgba(245, 158, 11, 0.4);
      color: #F59E0B;
      font-size: 9px;
      font-weight: 800;
      padding: 3px 6px;
      border-radius: 4px;
      letter-spacing: 0.5px;
    }
    .chart-container {
      position: relative;
      width: 100%;
      height: 240px;
      background: #0F172A;
      border: 1px solid #1E293B;
      border-radius: 12px;
      overflow: hidden;
    }
    svg {
      width: 100%;
      height: 100%;
      display: block;
    }
    .axis-line, .domain {
      stroke: #1E293B;
      stroke-width: 1;
    }
    .tick text {
      fill: #64748B;
      font-size: 9px;
      font-weight: 600;
    }
    .grid line {
      stroke: rgba(30, 41, 59, 0.5);
      stroke-dasharray: 3,3;
    }
    .tooltip-hud {
      position: absolute;
      top: 10px;
      left: 12px;
      background: rgba(15, 23, 42, 0.92);
      border: 1px solid rgba(0, 229, 255, 0.35);
      border-radius: 8px;
      padding: 6px 10px;
      pointer-events: none;
      backdrop-filter: blur(4px);
      box-shadow: 0 4px 12px rgba(0,0,0,0.5);
      transition: opacity 0.15s ease;
    }
    .tooltip-title {
      font-size: 11px;
      font-weight: 700;
      color: #F8FAFC;
    }
    .tooltip-stat {
      font-size: 10px;
      color: #94A3B8;
      display: flex;
      gap: 6px;
      margin-top: 2px;
    }
    .tooltip-stat strong {
      color: #00E5FF;
    }
    .req-list {
      margin-top: 14px;
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    .req-item {
      background: #0F172A;
      border: 1px solid #1E293B;
      border-radius: 8px;
      padding: 8px 10px;
    }
    .req-head {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 11px;
      font-weight: 600;
      margin-bottom: 4px;
    }
    .req-bar-bg {
      width: 100%;
      height: 6px;
      background: #1E293B;
      border-radius: 3px;
      overflow: hidden;
      position: relative;
    }
    .req-bar-fill {
      height: 100%;
      border-radius: 3px;
      background: linear-gradient(90deg, #00E5FF, #818CF8);
      transition: width 0.6s ease;
    }
    .req-bar-target {
      position: absolute;
      top: 0;
      bottom: 0;
      width: 2px;
      background: #10B981;
    }
  </style>
</head>
<body>

  <div class="card-header">
    <div>
      <div style="font-size: 9.5px; font-weight: 700; color: #00E5FF; letter-spacing: 0.8px;">CAREER VELOCITY RADAR (D3.js ENGINE)</div>
      <div style="font-size: 13px; font-weight: 700; color: #F8FAFC; margin-top: 2px;">$companyName • $roleTitle</div>
    </div>
    <span class="badge-d3">D3 v7 SVG</span>
  </div>

  <div class="chart-container" id="d3-container">
    <div class="tooltip-hud" id="hud">
      <div class="tooltip-title" id="hud-title">Scrub timeline to inspect</div>
      <div class="tooltip-stat">User Progress: <strong id="hud-user">$readiness%</strong> | Target: <span style="color:#10B981;">100%</span></div>
      <div class="tooltip-stat" id="hud-milestone">Current Velocity: +${velocityRate}% / mo</div>
    </div>
    <svg id="velocity-svg" viewBox="0 0 600 240" preserveAspectRatio="none"></svg>
  </div>

  <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 12px;">
    <div style="font-size: 10px; font-weight: 700; color: #94A3B8; letter-spacing: 0.5px;">ROLE REQUIREMENTS BREAKDOWN & FULFILLMENT</div>
    <div style="font-size: 10px; font-weight: 600; color: #10B981;">$daysLeft Days to 100% ETA</div>
  </div>

  <div class="req-list" id="req-list-container"></div>

  <script>
    const timelineData = $timelineJson;
    const requirementsData = $requirementsJson;

    function renderD3Chart() {
      const container = document.getElementById("velocity-svg");
      const width = 600;
      const height = 240;
      const margin = { top: 25, right: 25, bottom: 35, left: 35 };

      // Clear existing
      container.innerHTML = "";

      const svg = d3.select("#velocity-svg");

      // Defs: Gradients & filters
      const defs = svg.append("defs");

      // User progress area gradient
      const areaGradient = defs.append("linearGradient")
        .attr("id", "userAreaGrad")
        .attr("x1", "0%").attr("y1", "0%")
        .attr("x2", "0%").attr("y2", "100%");
      areaGradient.append("stop").attr("offset", "0%").attr("stop-color", "#00E5FF").attr("stop-opacity", 0.35);
      areaGradient.append("stop").attr("offset", "100%").attr("stop-color", "#00E5FF").attr("stop-opacity", 0.0);

      // Target threshold area gradient
      const targetGrad = defs.append("linearGradient")
        .attr("id", "targetAreaGrad")
        .attr("x1", "0%").attr("y1", "0%")
        .attr("x2", "0%").attr("y2", "100%");
      targetGrad.append("stop").attr("offset", "0%").attr("stop-color", "#10B981").attr("stop-opacity", 0.12);
      targetGrad.append("stop").attr("offset", "100%").attr("stop-color", "#10B981").attr("stop-opacity", 0.0);

      // Scales
      const xScale = d3.scaleLinear()
        .domain([0, timelineData.length - 1])
        .range([margin.left, width - margin.right]);

      const yScale = d3.scaleLinear()
        .domain([0, 105])
        .range([height - margin.bottom, margin.top]);

      // Gridlines
      const yGrid = d3.axisLeft(yScale)
        .ticks(5)
        .tickSize(-(width - margin.left - margin.right))
        .tickFormat("");

      svg.append("g")
        .attr("class", "grid")
        .attr("transform", "translate(" + margin.left + ",0)")
        .call(yGrid);

      // X Axis
      const xAxis = d3.axisBottom(xScale)
        .ticks(timelineData.length)
        .tickFormat((d, i) => timelineData[i] ? timelineData[i].label.split(" ")[0] : "");

      svg.append("g")
        .attr("class", "x-axis")
        .attr("transform", "translate(0," + (height - margin.bottom) + ")")
        .call(xAxis)
        .selectAll("text")
        .attr("dy", "10px");

      // Y Axis
      const yAxis = d3.axisLeft(yScale)
        .ticks(5)
        .tickFormat(d => d + "%");

      svg.append("g")
        .attr("class", "y-axis")
        .attr("transform", "translate(" + margin.left + ",0)")
        .call(yAxis);

      // 100% Role Requirement Baseline Target Line
      svg.append("line")
        .attr("x1", margin.left)
        .attr("x2", width - margin.right)
        .attr("y1", yScale(100))
        .attr("y2", yScale(100))
        .attr("stroke", "#10B981")
        .attr("stroke-width", 1.5)
        .attr("stroke-dasharray", "4,4");

      svg.append("text")
        .attr("x", width - margin.right - 5)
        .attr("y", yScale(100) - 6)
        .attr("fill", "#10B981")
        .attr("font-size", "9px")
        .attr("font-weight", "700")
        .attr("text-anchor", "end")
        .text("100% ROLE REQUIREMENT THRESHOLD");

      // Target Baseline Curve & Area
      const targetArea = d3.area()
        .x((d, i) => xScale(i))
        .y0(height - margin.bottom)
        .y1(d => yScale(d.targetBaseline))
        .curve(d3.curveMonotoneX);

      const targetLine = d3.line()
        .x((d, i) => xScale(i))
        .y(d => yScale(d.targetBaseline))
        .curve(d3.curveMonotoneX);

      svg.append("path")
        .datum(timelineData)
        .attr("fill", "url(#targetAreaGrad)")
        .attr("d", targetArea);

      svg.append("path")
        .datum(timelineData)
        .attr("fill", "none")
        .attr("stroke", "#818CF8")
        .attr("stroke-width", 1.8)
        .attr("stroke-dasharray", "3,3")
        .attr("d", targetLine);

      // User Progress Curve & Area
      const userArea = d3.area()
        .x((d, i) => xScale(i))
        .y0(height - margin.bottom)
        .y1(d => yScale(d.userProgress))
        .curve(d3.curveMonotoneX);

      const userLine = d3.line()
        .x((d, i) => xScale(i))
        .y(d => yScale(d.userProgress))
        .curve(d3.curveMonotoneX);

      svg.append("path")
        .datum(timelineData)
        .attr("fill", "url(#userAreaGrad)")
        .attr("d", userArea);

      const userPath = svg.append("path")
        .datum(timelineData)
        .attr("fill", "none")
        .attr("stroke", "#00E5FF")
        .attr("stroke-width", 2.5)
        .attr("stroke-linecap", "round")
        .attr("d", userLine);

      // Milestones Points on Curve
      svg.selectAll(".milestone-circle")
        .data(timelineData)
        .enter()
        .append("circle")
        .attr("cx", (d, i) => xScale(i))
        .attr("cy", d => yScale(d.userProgress))
        .attr("r", d => d.isProjection ? 4 : 5.5)
        .attr("fill", d => d.isProjection ? "#818CF8" : "#00E5FF")
        .attr("stroke", "#0F172A")
        .attr("stroke-width", 2);

      // Interactive Crosshair & Scrubbing
      const crosshair = svg.append("line")
        .attr("y1", margin.top)
        .attr("y2", height - margin.bottom)
        .attr("stroke", "#00E5FF")
        .attr("stroke-width", 1)
        .attr("stroke-dasharray", "2,2")
        .style("opacity", 0);

      const focusCircle = svg.append("circle")
        .attr("r", 7)
        .attr("fill", "#00E5FF")
        .attr("stroke", "#F8FAFC")
        .attr("stroke-width", 2)
        .style("opacity", 0);

      function updateHud(point) {
        const hud = document.getElementById("hud");
        document.getElementById("hud-title").innerText = point.label;
        document.getElementById("hud-user").innerText = Math.round(point.userProgress) + "%";
        document.getElementById("hud-milestone").innerText = point.milestone 
          ? ("★ " + point.milestone) 
          : ("Velocity: +" + point.velocityDelta + "%/mo");
        hud.style.opacity = "1";
      }

      function handleMove(e) {
        const rect = container.getBoundingClientRect();
        const clientX = e.touches ? e.touches[0].clientX : e.clientX;
        const xPos = clientX - rect.left;
        const normX = (xPos / rect.width) * width;
        
        const index = Math.round(xScale.invert(normX));
        if (index >= 0 && index < timelineData.length) {
          const pt = timelineData[index];
          const cx = xScale(index);
          const cy = yScale(pt.userProgress);

          crosshair
            .attr("x1", cx)
            .attr("x2", cx)
            .style("opacity", 1);

          focusCircle
            .attr("cx", cx)
            .attr("cy", cy)
            .style("opacity", 1);

          updateHud(pt);
        }
      }

      // Touch & Pointer scrubbing events
      const chartBox = document.getElementById("d3-container");
      chartBox.addEventListener("mousemove", handleMove);
      chartBox.addEventListener("touchmove", handleMove, { passive: true });
      chartBox.addEventListener("touchstart", handleMove, { passive: true });
    }

    function renderRequirementsList() {
      const container = document.getElementById("req-list-container");
      container.innerHTML = "";

      requirementsData.forEach(req => {
        const item = document.createElement("div");
        item.className = "req-item";

        const statusColor = req.isFulfilled ? "#10B981" : (req.status === "CRITICAL_GAP" ? "#EF4444" : "#00E5FF");

        item.innerHTML = `
          <div class="req-head">
            <div>
              <span style="color: #F8FAFC;">` + req.name + `</span>
              <span style="font-size: 9px; color: #64748B; margin-left: 6px;">` + req.category + ` • ` + req.weight + `% wt</span>
            </div>
            <div style="color: ` + statusColor + `; font-size: 10.5px;">` + req.current + `% / ` + req.required + `%</div>
          </div>
          <div class="req-bar-bg">
            <div class="req-bar-fill" style="width: ` + Math.min(100, req.current) + `%;"></div>
            <div class="req-bar-target" style="left: ` + Math.min(100, req.required) + `%;"></div>
          </div>
        `;
        container.appendChild(item);
      });
    }

    // Initialize
    window.addEventListener("DOMContentLoaded", () => {
      renderD3Chart();
      renderRequirementsList();
    });

    // Fallback if D3 CDN is slow or in test environment
    if (typeof d3 === "undefined") {
      const script = document.createElement("script");
      script.src = "https://cdn.jsdelivr.net/npm/d3@7";
      script.onload = () => { renderD3Chart(); renderRequirementsList(); };
      document.head.appendChild(script);
    }
  </script>
</body>
</html>
    """.trimIndent()
  }

  private fun escapeJson(str: String): String {
    return str
      .replace("\\", "\\\\")
      .replace("\"", "\\\"")
      .replace("\n", "\\n")
      .replace("\r", "\\r")
      .replace("\t", "\\t")
  }
}
