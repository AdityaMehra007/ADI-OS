package com.example.truth

/**
 * ADI-OS TRUTH ENGINE v3: Agent Permission Model & Automation Safety
 *
 * Enforces least-privilege security across all ADI-OS agents:
 * - READ: Allowed by default for analysis, discovery, and report generation
 * - WRITE: Allowed for internal drafts, task logs, and candidate practice runs
 * - EXECUTE: Allowed for internal calculations, scoring, and local parsing
 * - EXTERNAL_ACTION: Strictly gated by human approval or External Output Firewall verification
 */
object AgentPermissionManager {

  /**
   * Evaluates whether an agent action is permitted under current operating mode.
   * Returns true if permitted, false if approval or rejection applies.
   */
  fun authorizeAction(
    agentName: String,
    requestedPermission: AgentPermission,
    isExternalFacing: Boolean = false,
    contentPayload: String = ""
  ): Boolean {
    // Read operations are always safe
    if (requestedPermission == AgentPermission.READ) {
      return true
    }

    // Write and Execute are permitted for internal systems
    if ((requestedPermission == AgentPermission.WRITE || requestedPermission == AgentPermission.EXECUTE) && !isExternalFacing) {
      return true
    }

    // External-facing actions MUST pass the External Output Firewall
    if (requestedPermission == AgentPermission.EXTERNAL_ACTION || isExternalFacing) {
      val audit = ExternalOutputFirewall.inspectAndFilterOutput(contentPayload, context = "AGENT_$agentName")
      return audit.isApproved
    }

    return false
  }
}
