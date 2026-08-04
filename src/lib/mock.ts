// Offline demo backend: keeps payments, history, campaigns and contributions in
// localStorage so every payment type works end to end without a running API.
// UPI payments auto-settle 5 seconds after creation (simulated PSP callback).

import type {
  Campaign,
  CampaignProgress,
  Contribution,
  CreatePaymentRequest,
  Payment,
  PaymentHistoryEntry,
  PaymentStatus,
} from "./api";

export type DemoAccount = {
  accountId: number;
  name: string;
  email: string;
  bank: string;
};

/** Demo people available as payer / payee. */
export const DEMO_ACCOUNTS: DemoAccount[] = [
  { accountId: 1, name: "Aarav Sharma", email: "aarav.sharma@demo.io", bank: "HDFC •••4821" },
  { accountId: 2, name: "Emily Chen", email: "emily.chen@demo.io", bank: "Chase •••7710" },
  { accountId: 3, name: "Marcus Feld", email: "marcus.feld@demo.io", bank: "N26 •••3390" },
  { accountId: 4, name: "Priya Nair", email: "priya.nair@demo.io", bank: "ICICI •••1204" },
];

export const accountName = (id: number | null | undefined) =>
  DEMO_ACCOUNTS.find((a) => a.accountId === id)?.name ?? (id ? `Account #${id}` : "—");

const STORE_KEY = "pp.demoStore.v1";
const UPI_SETTLE_MS = 5000;

type Store = {
  payments: Payment[];
  history: PaymentHistoryEntry[];
  campaigns: Campaign[];
  contributions: Contribution[];
  nextPaymentId: number;
  nextHistoryId: number;
  nextContributionId: number;
  /** paymentId -> epoch ms when a pending UPI collection should settle. */
  upiSettleAt: Record<string, number>;
};

const iso = (offsetMs: number) => new Date(Date.now() + offsetMs).toISOString();
const round2 = (n: number) => Math.round(n * 100) / 100;

function seed(): Store {
  const mk = (
    id: number,
    ref: string,
    payer: number,
    payee: number,
    type: Payment["paymentType"],
    method: Payment["paymentMethod"],
    status: PaymentStatus,
    amount: number,
    currency: string,
    settlement: string,
    fee: number,
    tax: number,
    description: string,
    ageMinutes: number,
  ): Payment => ({
    paymentId: id,
    paymentRef: ref,
    externalPaymentRef: null,
    idempotencyKey: `IDEM-${ref}`,
    payerAccountId: payer,
    payeeAccountId: payee,
    campaignId: null,
    paymentType: type,
    paymentMethod: method,
    status,
    amount,
    feeAmount: fee,
    taxAmount: tax,
    netAmount: round2(amount + fee + tax),
    sourceCurrencyCode: currency,
    settlementCurrencyCode: settlement,
    exchangeRateId: null,
    description,
    initiatedAt: iso(-ageMinutes * 60000),
    completedAt: status === "COMPLETED" ? iso(-(ageMinutes - 2) * 60000) : null,
    failedAt: status === "FAILED" ? iso(-(ageMinutes - 3) * 60000) : null,
    createdAt: iso(-ageMinutes * 60000),
    updatedAt: iso(-(ageMinutes - 1) * 60000),
  });

  const payments: Payment[] = [
    mk(
      1001,
      "PAY-AARAV01",
      1,
      2,
      "NORMAL",
      "BANK_TRANSFER",
      "COMPLETED",
      2400,
      "USD",
      "USD",
      18,
      0,
      "Aarav Sharma → Emily Chen — Bank transfer mode: NEFT · A/C 123456789012 · IFSC HDFC0001234",
      620,
    ),
    mk(
      1002,
      "PAY-EMILY02",
      2,
      3,
      "NORMAL",
      "CARD",
      "SENT",
      180,
      "EUR",
      "EUR",
      0.9,
      36,
      "Emily Chen → Marcus Feld — Card •••710 exp 09/28",
      145,
    ),
    mk(
      1003,
      "PAY-PRIYA03",
      4,
      1,
      "NORMAL",
      "WALLET_BALANCE",
      "COMPLETED",
      7500,
      "INR",
      "INR",
      0,
      1350,
      "Priya Nair → Aarav Sharma — UPI: priya@icici",
      52,
    ),
    mk(
      1004,
      "PAY-MARCUS4",
      3,
      4,
      "CRYPTO",
      "CRYPTO_WALLET",
      "FAILED",
      0.35,
      "ETH",
      "USD",
      8.93,
      0,
      "Marcus Feld → Priya Nair — Wallet: 0x8f2c…a41d · payout 1198.93 USD to A/C 998877665544 · IFSC ICIC0000123",
      18,
    ),
  ];

  const history: PaymentHistoryEntry[] = [];
  let hid = 5001;
  const chain: Record<PaymentStatus, PaymentStatus[]> = {
    CREATED: ["CREATED"],
    VALIDATED: ["CREATED", "VALIDATED"],
    SENT: ["CREATED", "VALIDATED", "SENT"],
    COMPLETED: ["CREATED", "VALIDATED", "SENT", "COMPLETED"],
    FAILED: ["CREATED", "VALIDATED", "FAILED"],
  };
  for (const p of payments) {
    const steps = chain[p.status];
    steps.forEach((s, i) => {
      history.push({
        paymentStatusHistoryId: hid++,
        paymentId: p.paymentId,
        oldStatus: i === 0 ? null : (steps[i - 1] as PaymentStatus),
        newStatus: s,
        changedByUserId: p.payerAccountId,
        changeReasonCode: s === "FAILED" ? "INSUFFICIENT_FUNDS" : "AUTO",
        changeReasonMessage:
          s === "FAILED" ? "Wallet balance below required amount" : `Transitioned to ${s}`,
        metadata: null,
        changedAt: iso(-(600 - i * 30) * 60000),
      });
    });
  }

  const campaigns: Campaign[] = [
    {
      id: 1,
      creator: 1,
      title: "Campus Robotics Lab",
      description: "Funding parts and tooling for the student robotics lab.",
      targetAmount: 25000,
      currentAmount: 11450,
      currency: "USD",
      deadline: iso(30 * 24 * 3600000),
      status: "ACTIVE",
    },
  ];

  const contributions: Contribution[] = [
    {
      id: 9001,
      campaignId: 1,
      contributorId: 2,
      paymentId: 1001,
      amount: 5000,
      currency: "USD",
      status: "COMPLETED",
      note: "Great initiative!",
      anonymous: false,
      contributedAt: iso(-4 * 24 * 3600000),
    },
    {
      id: 9002,
      campaignId: 1,
      contributorId: 4,
      paymentId: 1003,
      amount: 6450,
      currency: "USD",
      status: "COMPLETED",
      note: null,
      anonymous: true,
      contributedAt: iso(-2 * 24 * 3600000),
    },
  ];

  return {
    payments,
    history,
    campaigns,
    contributions,
    nextPaymentId: 1005,
    nextHistoryId: hid,
    nextContributionId: 9003,
    upiSettleAt: {},
  };
}

function load(): Store {
  if (typeof window === "undefined") return seed();
  try {
    const raw = localStorage.getItem(STORE_KEY);
    if (raw) return JSON.parse(raw) as Store;
  } catch {
    // fall through to a fresh seed
  }
  const fresh = seed();
  save(fresh);
  return fresh;
}

function save(store: Store) {
  if (typeof window === "undefined") return;
  localStorage.setItem(STORE_KEY, JSON.stringify(store));
}

function pushHistory(
  store: Store,
  paymentId: number,
  oldStatus: PaymentStatus | null,
  newStatus: PaymentStatus,
  reasonCode?: string,
  reasonMessage?: string,
) {
  store.history.push({
    paymentStatusHistoryId: store.nextHistoryId++,
    paymentId,
    oldStatus,
    newStatus,
    changedByUserId: null,
    changeReasonCode: reasonCode ?? "AUTO",
    changeReasonMessage: reasonMessage ?? `Transitioned to ${newStatus}`,
    metadata: null,
    changedAt: new Date().toISOString(),
  });
}

/** Settle any pending UPI collection whose 5s simulated window has elapsed. */
function settleDueUpi(store: Store) {
  let changed = false;
  for (const [id, at] of Object.entries(store.upiSettleAt)) {
    if (Date.now() < at) continue;
    const payment = store.payments.find((p) => p.paymentId === Number(id));
    if (payment && payment.status !== "COMPLETED") {
      pushHistory(store, payment.paymentId, payment.status, "VALIDATED");
      pushHistory(store, payment.paymentId, "VALIDATED", "SENT");
      pushHistory(store, payment.paymentId, "SENT", "COMPLETED", "UPI_COLLECTED");
      payment.status = "COMPLETED";
      payment.completedAt = new Date().toISOString();
      payment.updatedAt = payment.completedAt;
      payment.externalPaymentRef = `UTR${Math.random().toString().slice(2, 14)}`;
    }
    delete store.upiSettleAt[id];
    changed = true;
  }
  if (changed) save(store);
}

const delay = <T,>(value: T, ms = 220) =>
  new Promise<T>((resolve) => setTimeout(() => resolve(value), ms));

export const mockApi = {
  resetDemoData() {
    const fresh = seed();
    save(fresh);
  },
  async listPayments() {
    const store = load();
    settleDueUpi(store);
    return delay([...store.payments]);
  },
  async listPaymentsByStatus(status: PaymentStatus) {
    const store = load();
    settleDueUpi(store);
    return delay(store.payments.filter((p) => p.status === status));
  },
  async getPayment(id: number | string) {
    const store = load();
    settleDueUpi(store);
    const payment = store.payments.find((p) => p.paymentId === Number(id));
    if (!payment) throw new Error(`Payment ${id} not found`);
    return delay({ ...payment }, 120);
  },
  async getPaymentHistory(id: number | string) {
    const store = load();
    settleDueUpi(store);
    return delay(store.history.filter((h) => h.paymentId === Number(id)));
  },
  async createPayment(data: CreatePaymentRequest) {
    const store = load();
    const isUpi = data.paymentMethod === "WALLET_BALANCE";
    const now = new Date().toISOString();
    const fee = Number(data.feeAmount ?? 0);
    const tax = Number(data.taxAmount ?? 0);
    const payment: Payment = {
      paymentId: store.nextPaymentId++,
      paymentRef: data.paymentRef,
      externalPaymentRef: data.externalPaymentRef ?? null,
      idempotencyKey: data.idempotencyKey,
      payerAccountId: data.payerAccountId,
      payeeAccountId: data.payeeAccountId ?? null,
      campaignId: data.campaignId ?? null,
      paymentType: data.paymentType,
      paymentMethod: data.paymentMethod,
      status: "CREATED",
      amount: data.amount,
      feeAmount: fee,
      taxAmount: tax,
      netAmount: round2(Number(data.amount) + fee + tax),
      sourceCurrencyCode: data.sourceCurrencyCode,
      settlementCurrencyCode: data.settlementCurrencyCode ?? data.sourceCurrencyCode,
      exchangeRateId: null,
      description: data.description ?? null,
      initiatedAt: now,
      completedAt: null,
      failedAt: null,
      createdAt: now,
      updatedAt: now,
    };
    store.payments.unshift(payment);
    pushHistory(store, payment.paymentId, null, "CREATED", "CREATED", "Payment created");

    if (isUpi) {
      store.upiSettleAt[String(payment.paymentId)] = Date.now() + UPI_SETTLE_MS;
    } else {
      pushHistory(store, payment.paymentId, "CREATED", "VALIDATED");
      payment.status = "VALIDATED";
      payment.updatedAt = new Date().toISOString();
    }
    save(store);
    return delay({ ...payment }, 320);
  },
  async updatePaymentStatus(
    id: number | string,
    data: { status: PaymentStatus; reasonCode?: string | undefined; reasonMessage?: string | undefined },
  ) {
    const store = load();
    const payment = store.payments.find((p) => p.paymentId === Number(id));
    if (!payment) throw new Error(`Payment ${id} not found`);
    pushHistory(store, payment.paymentId, payment.status, data.status, data.reasonCode, data.reasonMessage);
    payment.status = data.status;
    payment.updatedAt = new Date().toISOString();
    if (data.status === "COMPLETED") payment.completedAt = payment.updatedAt;
    if (data.status === "FAILED") payment.failedAt = payment.updatedAt;
    delete store.upiSettleAt[String(payment.paymentId)];
    save(store);
    return delay({ ...payment }, 200);
  },
  async getCampaign(id: number | string) {
    const store = load();
    const campaign = store.campaigns.find((c) => c.id === Number(id)) ?? store.campaigns[0];
    if (!campaign) throw new Error("No campaigns available");
    return delay({ ...campaign });
  },
  async getCampaignProgress(id: number | string) {
    const campaign = await mockApi.getCampaign(id);
    const target = Number(campaign.targetAmount);
    const current = Number(campaign.currentAmount);
    const progress: CampaignProgress = {
      id: campaign.id,
      targetAmount: target,
      currentAmount: current,
      remainingAmount: round2(Math.max(0, target - current)),
      progressPercentage: round2(target ? (current / target) * 100 : 0),
      status: campaign.status,
      deadline: campaign.deadline,
    };
    return progress;
  },
  async getContributions(id: number | string) {
    const store = load();
    return delay(store.contributions.filter((c) => c.campaignId === Number(id)));
  },
  async contribute(
    id: number | string,
    data: {
      contributorId?: number | null;
      paymentId?: number | null;
      amount: number;
      note?: string | null;
      anonymous?: boolean;
    },
  ) {
    const store = load();
    const campaign = store.campaigns.find((c) => c.id === Number(id));
    if (!campaign) throw new Error(`Campaign ${id} not found`);
    const contribution: Contribution = {
      id: store.nextContributionId++,
      campaignId: campaign.id,
      contributorId: data.contributorId ?? null,
      paymentId: data.paymentId ?? null,
      amount: data.amount,
      currency: campaign.currency,
      status: "COMPLETED",
      note: data.note ?? null,
      anonymous: data.anonymous ?? false,
      contributedAt: new Date().toISOString(),
    };
    store.contributions.push(contribution);
    campaign.currentAmount = round2(Number(campaign.currentAmount) + Number(data.amount));
    save(store);
    return delay({ ...contribution }, 300);
  },
  async login(email: string) {
    const account = DEMO_ACCOUNTS.find((a) => a.email === email) ?? DEMO_ACCOUNTS[0]!;
    return delay({
      token: "demo-token",
      tokenType: "Bearer",
      userId: account.accountId,
      email: account.email,
      role: "USER",
    });
  },
};
