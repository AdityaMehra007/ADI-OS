const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- SECURITY BOUNDS TESTS ---

test("Unauthenticated user: cannot read user profiles", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Unauthenticated user: cannot write executions", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("executions").doc("exec_1").set({
    id: "exec_1",
    userId: ALICE_UID,
    directiveCode: "01",
    directiveTitle: "JD Decompiler",
    category: "CAREER",
    latencyMs: 14,
    status: "DISPATCHED",
    executedAt: new Date(),
  }));
});

test("Authenticated user: Alice can read and write her own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).set({
    userId: ALICE_UID,
    displayName: "Alice Titan",
    email: "alice@titan.ai",
    tier: "APEX_FOUNDER",
    targetCTC: 120,
    currentTitle: "Founder & CEO",
    createdAt: new Date(),
    updatedAt: new Date(),
  }));

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Cross-user isolation: Bob cannot read Alice's profile or executions", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice",
      email: "alice@titan.ai",
      tier: "APEX_FOUNDER",
      createdAt: new Date(),
      updatedAt: new Date(),
    });
    await context.firestore().collection("users").doc(ALICE_UID).collection("executions").doc("exec_1").set({
      id: "exec_1",
      userId: ALICE_UID,
      directiveCode: "01",
      directiveTitle: "JD Decompiler",
      category: "CAREER",
      latencyMs: 12,
      status: "COMPLETED",
      executedAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("executions").doc("exec_1").get());
});

test("Authenticated user: Alice can add an execution to her audit subcollection", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("executions").doc("exec_1").set({
    id: "exec_1",
    userId: ALICE_UID,
    directiveCode: "18",
    directiveTitle: "Dark Store Daily Health Audit",
    category: "OPS",
    latencyMs: 11,
    status: "COMPLETED",
    summary: "Audit completed across 4 dark stores with zero high-variance stockouts.",
    executedAt: new Date(),
  }));
});

test("Authenticated user: Alice can pin a directive", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("pinnedDirectives").doc("01").set({
    userId: ALICE_UID,
    directiveCode: "01",
    title: "JD Decompiler",
    pinnedAt: new Date(),
  }));
});
