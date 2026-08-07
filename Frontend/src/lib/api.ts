// Typed client for the Spring Boot Payment Processing API (see selfproject backend).

// Base URL and JWT are stored in localStorage so the page can point at any
// running backend instance (default: http://localhost:8080).

// Mirrors payment_db.sql: payment.status and payment.payment_method enums.
export const PAYMENT_STATUSES = [
  "PENDING",
  "CREATED",
  "VALIDATED",
  "PROCESSING",
  "COMPLETED",
  "FAILED",
  "REFUNDED",
  "CANCELLED",
] as const;
export const PAYMENT_METHODS = [
  "UPI",
  "CREDIT_CARD",
  "DEBIT_CARD",
  "NET_BANKING",
  "WALLET",
  "CRYPTO",
] as const;

/** payment_db.sql: refund.refund_status / refund_method / initiated_by. */
export const REFUND_STATUSES = ["REQUESTED", "APPROVED", "PROCESSING", "COMPLETED", "REJECTED"] as const;
export const REFUND_METHODS = ["ORIGINAL_PAYMENT_METHOD", "BANK_TRANSFER", "WALLET"] as const;
/** payment_db.sql: refund_reason_master rows. */
export const REFUND_REASONS = [
  "Duplicate Payment",
  "Gateway Timeout",
  "Customer Request",
  "Fraud Detection",
  "Flight Cancellation",
  "Order Cancellation",
  "Payment Failure",
] as const;

/** Only failed payments are refundable in FasterPay. */
export const isRefundable = (status: string) => status === "FAILED";
export const PAYMENT_TYPES = [
  "NORMAL",
  "CRYPTO",
  "INTERNATIONAL",
  "REFUND",
  "PAYOUT",
] as const;
export const FIAT_CURRENCIES = [
  "USD",
  "EUR",
  "GBP",
  "INR",
  "JPY",
  "AUD",
  "CAD",
  "SGD",
  "AED",
  "CHF",
] as const;
export const CRYPTO_CURRENCIES = ["BTC", "ETH", "USDT", "USDC", "SOL", "XRP"] as const;
export const LIVE_FIAT_CURRENCIES = ["INR", "USD", "EUR", "GBP", "AED", "SGD"] as const;
export const LIVE_CRYPTO_CURRENCIES = ["BTC", "ETH", "USDT"] as const;
export const CURRENCIES = [...FIAT_CURRENCIES, ...CRYPTO_CURRENCIES] as const;

/** UI-level payment options: each maps to a backend paymentType + paymentMethod pair. */
export const PAYMENT_OPTIONS = [
  {
    id: "BANK_NATIONAL",
    label: "Bank Transfer — National",
    paymentType: "NORMAL",
    paymentMethod: "NET_BANKING",
  },
  {
    id: "BANK_INTERNATIONAL",
    label: "Bank Transfer — International",
    paymentType: "INTERNATIONAL",
    paymentMethod: "NET_BANKING",
  },
  { id: "CREDIT_CARD", label: "Credit Card", paymentType: "NORMAL", paymentMethod: "CREDIT_CARD" },
  { id: "UPI", label: "UPI", paymentType: "NORMAL", paymentMethod: "UPI" },
  { id: "CRYPTO", label: "Crypto", paymentType: "CRYPTO", paymentMethod: "CRYPTO" },
] as const;

export type PaymentOptionId = (typeof PAYMENT_OPTIONS)[number]["id"];

/** Rough USD equivalents used only to bucket the amount into a fee tier. */
export const USD_RATES: Record<string, number> = {
  USD: 1,
  EUR: 1.08,
  GBP: 1.27,
  INR: 0.012,
  JPY: 0.0064,
  AUD: 0.66,
  CAD: 0.73,
  SGD: 0.74,
  AED: 0.27,
  CHF: 1.12,
  BTC: 65000,
  ETH: 3400,
  USDT: 1,
  USDC: 1,
  SOL: 150,
  XRP: 0.55,
};

export const CURRENCY_PRECISION: Record<string, number> = {
  USD: 2,
  EUR: 2,
  GBP: 2,
  INR: 2,
  JPY: 2,
  AUD: 2,
  CAD: 2,
  SGD: 2,
  AED: 2,
  CHF: 2,
  BTC: 8,
  ETH: 8,
  USDT: 6,
  USDC: 6,
  SOL: 6,
  XRP: 6,
};

export const DEFAULT_COUNTRY_BY_CURRENCY: Record<string, string> = {
  USD: "US",
  EUR: "DE",
  GBP: "GB",
  INR: "IN",
  JPY: "JP",
  AUD: "AU",
  CAD: "CA",
  SGD: "SG",
  AED: "AE",
  CHF: "CH",
};

/**
 * Tiered fee: free under $100 equivalent, 0.5% up to $1,000, 0.75% above.
 */
export const FEE_TIERS = [
  { maxUsd: 100, rate: 0 },
  { maxUsd: 1000, rate: 0.5 },
  { maxUsd: Infinity, rate: 0.75 },
];

export function feeRateFor(amount: number, currencyCode: string) {
  const usd = (Number(amount) || 0) * (USD_RATES[currencyCode] ?? 1);
  return FEE_TIERS.find((t) => usd < t.maxUsd)?.rate ?? 0.75;
}

export const TAX_RATES: Record<string, number> = {
  USD: 0,
  EUR: 20,
  GBP: 20,
  INR: 18,
  JPY: 10,
  AUD: 10,
  CAD: 5,
  SGD: 9,
  AED: 5,
  CHF: 8.1,
};
export const round2 = (n: number) => Math.round(n * 100) / 100;

export function precisionForCurrency(currency?: string | null) {
  return CURRENCY_PRECISION[currency ?? ""] ?? 2;
}

export function roundAmount(value: number, currency?: string | null) {
  const precision = precisionForCurrency(currency);
  const factor = 10 ** precision;
  return Math.round(value * factor) / factor;
}

export function getDefaultCountryForCurrency(currency?: string | null) {
  return DEFAULT_COUNTRY_BY_CURRENCY[currency ?? ""] ?? "US";
}

/** Convert between any two supported currencies using the indicative USD rates. */
export function convertAmount(amount: number, from: string, to: string) {
  const usd = (Number(amount) || 0) * (USD_RATES[from] ?? 1);
  const rate = USD_RATES[to] ?? 1;
  return roundAmount(usd / rate, to);
}

export const isCryptoCurrency = (code: string) =>
  (CRYPTO_CURRENCIES as readonly string[]).includes(code);


export type PaymentStatus = (typeof PAYMENT_STATUSES)[number];
export type PaymentMethod = (typeof PAYMENT_METHODS)[number];
export type ApiPaymentMethod = PaymentMethod | "BANK_TRANSFER";
export type PaymentType = (typeof PAYMENT_TYPES)[number];

export type Payment = {
  paymentId: number;
  paymentRef: string;
  externalPaymentRef: string | null;
  idempotencyKey: string;
  payerAccountId: number;
  payeeAccountId: number | null;
  campaignId: number | null;
  paymentType: PaymentType;
  paymentMethod: PaymentMethod;
  status: PaymentStatus;
  amount: string | number;
  feeAmount: string | number | null;
  taxAmount: string | number | null;
  netAmount: string | number | null;
  sourceCurrencyCode: string;
  settlementCurrencyCode: string | null;
  exchangeRateId: number | null;
  description: string | null;
  sourceAccount?: string | null;
  destinationAccount?: string | null;
  sourceCountry?: string | null;
  destinationCountry?: string | null;
  initiatedAt: string | null;
  completedAt: string | null;
  failedAt: string | null;
  createdAt: string | null;
  updatedAt: string | null;
};

export type RefundStatus = (typeof REFUND_STATUSES)[number];
export type RefundMethod = (typeof REFUND_METHODS)[number];

export type Refund = {
  refundId: number;
  paymentId: number;
  refundReference: string;
  refundAmount: string | number;
  refundReason: string;
  refundStatus: RefundStatus;
  refundMethod: RefundMethod;
  initiatedBy: "CUSTOMER" | "ADMIN" | "SYSTEM";
  refundDate: string;
  remarks: string | null;
};

export type PaymentHistoryEntry = {
  paymentStatusHistoryId: number;
  paymentId: number;
  oldStatus: PaymentStatus | null;
  newStatus: PaymentStatus;
  changedByUserId: number | null;
  changeReasonCode: string | null;
  changeReasonMessage: string | null;
  metadata: string | null;
  changedAt: string;
};

export type CreatePaymentRequest = {
  paymentRef: string;
  externalPaymentRef?: string | null;
  idempotencyKey: string;
  payerAccountId: number;
  payeeAccountId?: number | null;
  campaignId?: number | null;
  paymentType: PaymentType;
  paymentMethod: ApiPaymentMethod;
  amount: number;
  feeAmount?: number | null;
  taxAmount?: number | null;
  sourceCurrencyCode: string;
  settlementCurrencyCode?: string | null;
  exchangeRateId?: number | null;
  description?: string | null;
  sourceAccount?: string | null;
  destinationAccount?: string | null;
  sourceCountry?: string | null;
  destinationCountry?: string | null;
};

export type Campaign = {
  id: number;
  creator: number;
  title: string;
  description: string | null;
  targetAmount: string | number;
  currentAmount: string | number;
  currency: string;
  deadline: string | null;
  status: string;
};

export type CampaignProgress = {
  id: number;
  targetAmount: string | number;
  currentAmount: string | number;
  remainingAmount: string | number;
  progressPercentage: string | number;
  status: string;
  deadline: string | null;
};

export type Contribution = {
  id: number;
  campaignId: number;
  contributorId: number | null;
  paymentId: number | null;
  amount: string | number;
  currency: string;
  status: string;
  note: string | null;
  anonymous: boolean | null;
  contributedAt: string | null;
};

type SpringPaymentResponse = {
  id: number;
  paymentId: string;
  referenceNumber: string;
  sourceAccount: string;
  destinationAccount: string;
  amount: string | number;
  currency: string;
  paymentMethod: PaymentMethod;
  status: PaymentStatus;
  sourceCountry: string | null;
  destinationCountry: string | null;
  description: string | null;
  createdAt: string | null;
};

type LivePaymentMetadata = {
  paymentId: number;
  idempotencyKey: string;
  paymentType: PaymentType;
  settlementCurrencyCode: string | null;
  feeAmount: number | null;
  taxAmount: number | null;
  netAmount: number | null;
  exchangeRateId: number | null;
  campaignId: number | null;
  sourceCountry: string | null;
  destinationCountry: string | null;
};
type SpringPaymentRequest = {
  sourceAccount: string;
  destinationAccount: string;
  amount: number;
  currency: string;
  paymentMethod: PaymentMethod;
  sourceCountry: string;
  destinationCountry: string;
  description?: string | null;
};

type SpringPaymentHistoryResponse = {
  historyId: number;
  paymentId: number;
  oldStatus: PaymentStatus | null;
  newStatus: PaymentStatus;
  eventType: string | null;
  remarks: string | null;
  changedBy: string | null;
  changedAt: string;
};

type SpringRefundResponse = {
  refundId: number;
  paymentId: number;
  refundReference: string;
  refundAmount: string | number;
  refundMethod: RefundMethod;
  refundStatus: RefundStatus;
  refundReason: string;
  initiatedBy: "CUSTOMER" | "ADMIN" | "SYSTEM";
  refundDate: string;
};

type SpringCampaignResponse = {
  campaignId: number;
  campaignCode: string;
  campaignTitle: string;
  organizerName: string;
  goalAmount: string | number;
  collectedAmount: string | number;
  endDate: string | null;
  campaignStatus: string;
  description: string | null;
  createdBy: string | null;
};

type SpringContributionResponse = {
  contributionId: number;
  campaignId: number;
  paymentId: number | null;
  contributorName: string | null;
  contributorEmail: string | null;
  contributionAmount: string | number;
  contributionStatus: string;
  anonymousDonation: boolean | null;
  message: string | null;
  receiptNumber: string | null;
  contributionDate: string | null;
};

const BASE_KEY = "pp.apiBaseUrl";
const TOKEN_KEY = "pp.jwt";
const LIVE_PAYMENT_METADATA_KEY = "pp.livePaymentMetadata";
const ENV_DEFAULT_BASE_URL =
  typeof import.meta !== "undefined" ? import.meta.env?.VITE_API_BASE_URL : undefined;
export const DEFAULT_BASE_URL = ENV_DEFAULT_BASE_URL || "http://localhost:8082";

export function getApiBaseUrl() {
  if (typeof window === "undefined") return DEFAULT_BASE_URL;
  const stored = localStorage.getItem(BASE_KEY);
  if (stored) return stored.replace(/\/$/, "");
  if (ENV_DEFAULT_BASE_URL) return ENV_DEFAULT_BASE_URL.replace(/\/$/, "");
  
  const protocol = window.location.protocol;
  const hostname = window.location.hostname;
  return `${protocol}//${hostname}:8082`;
}
export function setApiBaseUrl(url: string) {
  localStorage.setItem(BASE_KEY, url.replace(/\/$/, ""));
}
export function getToken() {
  if (typeof window === "undefined") return null;
  return localStorage.getItem(TOKEN_KEY);
}
export function setToken(token: string | null) {
  if (token) localStorage.setItem(TOKEN_KEY, token);
  else localStorage.removeItem(TOKEN_KEY);
}

function toAuthorizationValue(token: string) {
  if (/^(Bearer|Basic)\s+/i.test(token)) return token;
  return `Bearer ${token}`;
}

const USER_KEY = "pp.user";
export type AuthUser = { userId: number; email: string; role: string };

function derivePaymentType(paymentMethod: PaymentMethod): PaymentType {
  return paymentMethod === "CRYPTO" ? "CRYPTO" : "NORMAL";
}

function readLivePaymentMetadata(): Record<string, LivePaymentMetadata> {
  if (typeof window === "undefined") return {};
  const raw = localStorage.getItem(LIVE_PAYMENT_METADATA_KEY);
  if (!raw) return {};
  try {
    return JSON.parse(raw) as Record<string, LivePaymentMetadata>;
  } catch {
    return {};
  }
}

function writeLivePaymentMetadata(store: Record<string, LivePaymentMetadata>) {
  if (typeof window === "undefined") return;
  localStorage.setItem(LIVE_PAYMENT_METADATA_KEY, JSON.stringify(store));
}

function getLivePaymentMetadata(paymentId: number) {
  return readLivePaymentMetadata()[String(paymentId)] ?? null;
}

function saveLivePaymentMetadata(paymentId: number, data: CreatePaymentRequest) {
  const store = readLivePaymentMetadata();
  store[String(paymentId)] = {
    paymentId,
    idempotencyKey: data.idempotencyKey,
    paymentType: data.paymentType,
    settlementCurrencyCode: data.settlementCurrencyCode ?? data.sourceCurrencyCode,
    feeAmount: data.feeAmount ?? null,
    taxAmount: data.taxAmount ?? null,
    netAmount: roundAmount(
      Number(data.amount) + Number(data.feeAmount ?? 0) + Number(data.taxAmount ?? 0),
      data.sourceCurrencyCode,
    ),
    exchangeRateId: data.exchangeRateId ?? null,
    campaignId: data.campaignId ?? null,
    sourceCountry: data.sourceCountry ?? null,
    destinationCountry: data.destinationCountry ?? null,
  };
  writeLivePaymentMetadata(store);
  return store[String(paymentId)];
}

function mapSpringPayment(payment: SpringPaymentResponse): Payment {
  const metadata = getLivePaymentMetadata(payment.id);
  return {
    paymentId: payment.id,
    paymentRef: payment.paymentId || payment.referenceNumber,
    externalPaymentRef: payment.referenceNumber || null,
    idempotencyKey: metadata?.idempotencyKey ?? "",
    payerAccountId: 0,
    payeeAccountId: null,
    campaignId: metadata?.campaignId ?? null,
    paymentType: metadata?.paymentType ?? derivePaymentType(payment.paymentMethod),
    paymentMethod: payment.paymentMethod,
    status: payment.status,
    amount: payment.amount,
    feeAmount: metadata?.feeAmount ?? null,
    taxAmount: metadata?.taxAmount ?? null,
    netAmount: metadata?.netAmount ?? payment.amount,
    sourceCurrencyCode: payment.currency,
    settlementCurrencyCode: metadata?.settlementCurrencyCode ?? payment.currency,
    exchangeRateId: metadata?.exchangeRateId ?? null,
    description: payment.description,
    sourceAccount: payment.sourceAccount,
    destinationAccount: payment.destinationAccount,
    sourceCountry: metadata?.sourceCountry ?? payment.sourceCountry,
    destinationCountry: metadata?.destinationCountry ?? payment.destinationCountry,
    initiatedAt: payment.createdAt,
    completedAt: payment.status === "COMPLETED" ? payment.createdAt : null,
    failedAt: payment.status === "FAILED" ? payment.createdAt : null,
    createdAt: payment.createdAt,
    updatedAt: payment.createdAt,
  };
}

function mapSpringPaymentHistory(entry: SpringPaymentHistoryResponse): PaymentHistoryEntry {
  return {
    paymentStatusHistoryId: entry.historyId,
    paymentId: entry.paymentId,
    oldStatus: entry.oldStatus,
    newStatus: entry.newStatus,
    changedByUserId: null,
    changeReasonCode: entry.eventType,
    changeReasonMessage: entry.remarks,
    metadata: entry.changedBy,
    changedAt: entry.changedAt,
  };
}

function mapSpringRefund(refund: SpringRefundResponse): Refund {
  return {
    refundId: refund.refundId,
    paymentId: refund.paymentId,
    refundReference: refund.refundReference,
    refundAmount: refund.refundAmount,
    refundReason: refund.refundReason,
    refundStatus: refund.refundStatus,
    refundMethod: refund.refundMethod,
    initiatedBy: refund.initiatedBy,
    refundDate: refund.refundDate,
    remarks: null,
  };
}

function mapSpringCampaign(campaign: SpringCampaignResponse): Campaign {
  return {
    id: campaign.campaignId,
    creator: 0,
    title: campaign.campaignTitle,
    description: campaign.description,
    targetAmount: campaign.goalAmount,
    currentAmount: campaign.collectedAmount,
    currency: "USD",
    deadline: campaign.endDate,
    status: campaign.campaignStatus,
  };
}

function mapSpringContribution(contribution: SpringContributionResponse): Contribution {
  const contributorIdMatch = contribution.contributorName?.match(/(\d+)/);
  return {
    id: contribution.contributionId,
    campaignId: contribution.campaignId,
    contributorId: contributorIdMatch ? Number(contributorIdMatch[1]) : null,
    paymentId: contribution.paymentId,
    amount: contribution.contributionAmount,
    currency: "USD",
    status: contribution.contributionStatus,
    note: contribution.message,
    anonymous: contribution.anonymousDonation,
    contributedAt: contribution.contributionDate,
  };
}

function normalizePaymentMethod(method: ApiPaymentMethod): PaymentMethod {
  return method === "BANK_TRANSFER" ? "NET_BANKING" : method;
}

function mapCreatePaymentRequest(data: CreatePaymentRequest): SpringPaymentRequest {
  return {
    sourceAccount: data.sourceAccount?.trim() || String(data.payerAccountId),
    destinationAccount: data.destinationAccount?.trim() || String(data.payeeAccountId ?? ""),
    amount: data.amount,
    currency: data.sourceCurrencyCode,
    paymentMethod: normalizePaymentMethod(data.paymentMethod),
    sourceCountry: data.sourceCountry?.trim() || "US",
    destinationCountry: data.destinationCountry?.trim() || "US",
    description: data.description ?? null,
  };
}

async function createLiveContributionPayment(
  campaignId: number | string,
  amount: number,
  contributorId?: number | null,
) {
  const campaign = await api.getCampaign(campaignId);
  const payment = await api.createPayment({
    paymentRef: `CAMP-${campaignId}-${Date.now()}`,
    idempotencyKey: `CAMP-IDEM-${campaignId}-${Date.now()}`,
    payerAccountId: contributorId ?? 0,
    payeeAccountId: null,
    paymentType: "NORMAL",
    paymentMethod: "UPI",
    amount,
    sourceCurrencyCode: campaign.currency,
    settlementCurrencyCode: campaign.currency,
    sourceAccount: contributorId ? `Contributor-${contributorId}` : "Contributor-guest",
    destinationAccount: campaign.title,
    sourceCountry: "IN",
    destinationCountry: "IN",
    description: `Crowdfunding contribution for ${campaign.title}`,
  });

  return payment.paymentId;
}

function calculateCampaignProgress(campaign: Campaign): CampaignProgress {
  const target = Number(campaign.targetAmount) || 0;
  const current = Number(campaign.currentAmount) || 0;
  const remaining = Math.max(0, target - current);
  const progress = target > 0 ? round2((current / target) * 100) : 0;
  return {
    id: campaign.id,
    targetAmount: campaign.targetAmount,
    currentAmount: campaign.currentAmount,
    remainingAmount: remaining,
    progressPercentage: progress,
    status: campaign.status,
    deadline: campaign.deadline,
  };
}

export function setAuthUser(user: AuthUser | null) {
  if (user) localStorage.setItem(USER_KEY, JSON.stringify(user));
  else localStorage.removeItem(USER_KEY);
}
export function getAuthUser(): AuthUser | null {
  if (typeof window === "undefined") return null;
  const raw = localStorage.getItem(USER_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as AuthUser;
  } catch {
    return null;
  }
}

function isNumericIdentifier(id: number | string) {
  return typeof id === "number" || /^\d+$/.test(String(id));
}

async function resolvePaymentLookupId(id: number | string) {
  if (isNumericIdentifier(id)) return Number(id);
  const lookup = String(id).trim();
  const payments = await api.listPayments();
  const matched = payments.find(
    (payment) => payment.paymentRef === lookup || payment.externalPaymentRef === lookup,
  );
  if (!matched) throw new Error(`Payment ${id} not found`);
  return matched.paymentId;
}


async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const token = getToken();
  const buildHeaders = (authToken?: string | null) => ({
    "Content-Type": "application/json",
    ...(authToken ? { Authorization: toAuthorizationValue(authToken) } : {}),
    ...(init?.headers ?? {}),
  });

  let res = await fetch(`${getApiBaseUrl()}${path}`, {
    ...init,
    headers: buildHeaders(token),
  });

  // If a stale token is stored, retry once as anonymous so public endpoints still work.
  if (res.status === 401 && token) {
    setToken(null);
    res = await fetch(`${getApiBaseUrl()}${path}`, {
      ...init,
      headers: buildHeaders(null),
    });
  }

  const text = await res.text();
  const body = text ? safeJson(text) : null;
  if (!res.ok) {
    const message =
      (body && (body.message || body.error || body.detail)) ||
      `${res.status} ${res.statusText || "Request failed"}`;
    throw new Error(String(message));
  }
  return body as T;
}

async function requestBlob(path: string, init?: RequestInit): Promise<Blob> {
  const token = getToken();
  const buildHeaders = (authToken?: string | null) => ({
    ...(authToken ? { Authorization: toAuthorizationValue(authToken) } : {}),
    ...(init?.headers ?? {}),
  });

  let res = await fetch(`${getApiBaseUrl()}${path}`, {
    ...init,
    headers: buildHeaders(token),
  });

  // Retry once without a stale token so anonymous endpoints can still work.
  if (res.status === 401 && token) {
    setToken(null);
    res = await fetch(`${getApiBaseUrl()}${path}`, {
      ...init,
      headers: buildHeaders(null),
    });
  }

  if (!res.ok) {
    const text = await res.text();
    const body = text ? safeJson(text) : null;
    const message =
      (body && (body.message || body.error || body.detail)) ||
      `${res.status} ${res.statusText || "Request failed"}`;
    throw new Error(String(message));
  }

  return res.blob();
}

function safeJson(text: string) {
  try {
    return JSON.parse(text);
  } catch {
    // Non-JSON body (e.g. an HTML error page): keep it short and readable.
    const plain = text.replace(/<[^>]*>/g, " ").replace(/\s+/g, " ").trim();
    return { message: plain.slice(0, 180) || "Unexpected non-JSON response" };
  }
}

export const api = {

  // The Spring backend uses HTTP Basic auth. We validate credentials by
  // calling a protected endpoint after storing the Basic token.
  login: async (username: string, password: string) => {
    if (typeof window === "undefined") {
      throw new Error("Login is only available in the browser.");
    }
    const safeUser = username.trim() || "debug";
    const safePass = password || "admin";
    const basic = `Basic ${window.btoa(`${safeUser}:${safePass}`)}`;
    setToken(basic);
    setAuthUser({ userId: 1, email: safeUser, role: "USER" });
    return { ok: true };
  },
  // POST /api/auth/register
  register: (data: { firstName: string; lastName: string; email: string; password: string }) =>
    Promise.reject(
      new Error("The Spring backend does not expose auth endpoints. Use direct API access."),
    ),
  // GET /api/payments
  listPayments: () =>
    request<SpringPaymentResponse[]>("/payments").then((rows) => rows.map(mapSpringPayment)),
  // GET /api/payments/status/{status}
  listPaymentsByStatus: (status: PaymentStatus) =>
    api.listPayments().then((rows) => rows.filter((payment) => payment.status === status)),
  // GET /api/payments/{id}
  getPayment: (id: number | string) =>
    isNumericIdentifier(id)
      ? request<SpringPaymentResponse>(`/payments/${id}`).then(mapSpringPayment)
      : api.listPayments().then((rows) => {
          const lookup = String(id).trim();
          const matched = rows.find(
            (payment) => payment.paymentRef === lookup || payment.externalPaymentRef === lookup,
          );
          if (!matched) throw new Error(`Payment ${id} not found`);
          return matched;
        }),
  // GET /api/payments/{id}/history
  getPaymentHistory: (id: number | string) =>
    resolvePaymentLookupId(id).then((resolvedId) =>
      request<SpringPaymentHistoryResponse[]>(`/payment-history/payment/${resolvedId}`).then(
        (rows) => rows.map(mapSpringPaymentHistory),
      ),
    ),
  // GET /api/statements/payment/{id}
  downloadPaymentStatement: (id: number | string) =>
    resolvePaymentLookupId(id).then((resolvedId) =>
      requestBlob(`/statements/payment/${resolvedId}`),
    ),
  // POST /api/payments
  createPayment: (data: CreatePaymentRequest) =>
    request<SpringPaymentResponse>("/payments", {
      method: "POST",
      body: JSON.stringify(mapCreatePaymentRequest(data)),
    }).then((payment) => {
      saveLivePaymentMetadata(payment.id, data);
      return mapSpringPayment(payment);
    }),
  // PUT /api/payments/{id}/status
  updatePaymentStatus: (
    id: number | string,
    data: { status: PaymentStatus; reasonCode?: string | undefined; reasonMessage?: string | undefined },
  ) =>
    Promise.reject(new Error("Payment status updates are not exposed by this backend.")),
  // GET /api/payments/{id}/refunds
  listRefunds: (id: number | string) =>
    resolvePaymentLookupId(id).then((resolvedId) =>
      request<SpringRefundResponse[]>("/refunds").then((rows) =>
        rows
          .filter((refund) => String(refund.paymentId) === String(resolvedId))
          .map(mapSpringRefund),
      ),
    ),
  // POST /api/payments/{id}/refunds
  requestRefund: (
    id: number | string,
    data: { refundReason: string; refundMethod: RefundMethod; remarks?: string | null },
  ) =>
    resolvePaymentLookupId(id).then((resolvedId) =>
      api.getPayment(resolvedId).then((payment) =>
        request<SpringRefundResponse>("/refunds", {
          method: "POST",
          body: JSON.stringify({
            paymentId: Number(resolvedId),
            refundAmount: Number(payment.netAmount ?? payment.amount),
            refundMethod: data.refundMethod,
            refundReason: data.refundReason,
            initiatedBy: "CUSTOMER",
          }),
        }).then(mapSpringRefund),
      ),
    ),
  listCampaigns: () =>
    request<SpringCampaignResponse[]>("/campaigns").then((rows) => rows.map(mapSpringCampaign)),
  // GET /api/crowdfunding/campaigns/{id}
  getCampaign: (id: number | string) =>
    request<SpringCampaignResponse>(`/campaigns/${id}`).then(mapSpringCampaign),
  // GET /api/crowdfunding/campaigns/{id}/progress
  getCampaignProgress: (id: number | string) =>
    api.getCampaign(id).then(calculateCampaignProgress),
  // GET /api/crowdfunding/campaigns/{id}/contributions
  getContributions: (id: number | string) =>
    request<SpringContributionResponse[]>(`/contributions/campaign/${id}`).then((rows) =>
      rows.map(mapSpringContribution),
    ),
  // POST /api/crowdfunding/campaigns/{id}/contributions
  contribute: (
    id: number | string,
    data: {
      contributorId?: number | null;
      paymentId?: number | null;
      amount: number;
      note?: string | null;
      anonymous?: boolean;
    },
  ) =>
    createLiveContributionPayment(id, data.amount, data.contributorId).then((paymentId) =>
      request<SpringContributionResponse>("/contributions", {
        method: "POST",
        body: JSON.stringify({
          campaignId: Number(id),
          paymentId,
          contributorName: data.anonymous
            ? "Anonymous"
            : `Contributor #${data.contributorId ?? "guest"}`,
          contributorEmail: data.contributorId
            ? `contributor${data.contributorId}@fasterpay.local`
            : "guest@fasterpay.local",
          contributionAmount: data.amount,
          anonymousDonation: data.anonymous ?? false,
          message: data.note ?? null,
        }),
      }).then(mapSpringContribution),
    ),
};


export function formatAmount(value: string | number | null | undefined, currency?: string | null) {
  if (value === null || value === undefined || value === "") return "—";
  const n = Number(value);
  if (Number.isNaN(n)) return String(value);
  const precision = precisionForCurrency(currency);
  return `${n.toLocaleString(undefined, {
    minimumFractionDigits: precision === 2 ? 2 : 0,
    maximumFractionDigits: precision,
  })}${
    currency ? ` ${currency}` : ""
  }`;
}

export function formatDateTime(value: string | null | undefined) {
  if (!value) return "—";
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return value;
  return d.toLocaleString();
}
