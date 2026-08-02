// Typed client for the Spring Boot Payment Processing API (see selfproject backend).
// Base URL and JWT are stored in localStorage so the page can point at any
// running backend instance (default: http://localhost:8080).

export const PAYMENT_STATUSES = ["CREATED", "VALIDATED", "SENT", "COMPLETED", "FAILED"] as const;
export const PAYMENT_METHODS = [
  "BANK_TRANSFER",
  "CARD",
  "WALLET_BALANCE",
  "CRYPTO_WALLET",
  "INTERNAL_TRANSFER",
  "MANUAL",
] as const;
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
export const CURRENCIES = [...FIAT_CURRENCIES, ...CRYPTO_CURRENCIES] as const;

/** Fee and tax are charged as a percentage of the payment amount. */
export const FEE_RATES: Record<string, number> = {
  BANK_TRANSFER: 0.5,
  CARD: 2.5,
  WALLET_BALANCE: 1,
  CRYPTO_WALLET: 1.5,
  INTERNAL_TRANSFER: 0,
  MANUAL: 0,
};
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

export type PaymentStatus = (typeof PAYMENT_STATUSES)[number];
export type PaymentMethod = (typeof PAYMENT_METHODS)[number];
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
  initiatedAt: string | null;
  completedAt: string | null;
  failedAt: string | null;
  createdAt: string | null;
  updatedAt: string | null;
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
  paymentMethod: PaymentMethod;
  amount: number;
  feeAmount?: number | null;
  taxAmount?: number | null;
  sourceCurrencyCode: string;
  settlementCurrencyCode?: string | null;
  exchangeRateId?: number | null;
  description?: string | null;
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

const BASE_KEY = "pp.apiBaseUrl";
const TOKEN_KEY = "pp.jwt";
export const DEFAULT_BASE_URL = "http://localhost:8080";

export function getApiBaseUrl() {
  if (typeof window === "undefined") return DEFAULT_BASE_URL;
  return localStorage.getItem(BASE_KEY)?.replace(/\/$/, "") || DEFAULT_BASE_URL;
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

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const token = getToken();
  const res = await fetch(`${getApiBaseUrl()}${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(init?.headers ?? {}),
    },
  });
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
  // POST /api/auth/login
  login: (email: string, password: string) =>
    request<{ token: string; tokenType: string; userId: number; email: string; role: string }>(
      "/api/auth/login",
      { method: "POST", body: JSON.stringify({ email, password }) },
    ),
  // POST /api/auth/register
  register: (data: { firstName: string; lastName: string; email: string; password: string }) =>
    request<{ token: string; tokenType: string; userId: number; email: string; role: string }>(
      "/api/auth/register",
      { method: "POST", body: JSON.stringify(data) },
    ),
  // GET /api/payments
  listPayments: () => request<Payment[]>("/api/payments"),
  // GET /api/payments/status/{status}
  listPaymentsByStatus: (status: PaymentStatus) =>
    request<Payment[]>(`/api/payments/status/${status}`),
  // GET /api/payments/{id}
  getPayment: (id: number | string) => request<Payment>(`/api/payments/${id}`),
  // GET /api/payments/{id}/history
  getPaymentHistory: (id: number | string) =>
    request<PaymentHistoryEntry[]>(`/api/payments/${id}/history`),
  // POST /api/payments
  createPayment: (data: CreatePaymentRequest) =>
    request<Payment>("/api/payments", { method: "POST", body: JSON.stringify(data) }),
  // PUT /api/payments/{id}/status
  updatePaymentStatus: (
    id: number | string,
    data: { status: PaymentStatus; reasonCode?: string | undefined; reasonMessage?: string | undefined },
  ) =>
    request<Payment>(`/api/payments/${id}/status`, {
      method: "PUT",
      body: JSON.stringify(data),
    }),
  // GET /api/crowdfunding/campaigns/{id}
  getCampaign: (id: number | string) => request<Campaign>(`/api/crowdfunding/campaigns/${id}`),
  // GET /api/crowdfunding/campaigns/{id}/progress
  getCampaignProgress: (id: number | string) =>
    request<CampaignProgress>(`/api/crowdfunding/campaigns/${id}/progress`),
  // GET /api/crowdfunding/campaigns/{id}/contributions
  getContributions: (id: number | string) =>
    request<Contribution[]>(`/api/crowdfunding/campaigns/${id}/contributions`),
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
    request<Contribution>(`/api/crowdfunding/campaigns/${id}/contributions`, {
      method: "POST",
      body: JSON.stringify(data),
    }),
};

export function formatAmount(value: string | number | null | undefined, currency?: string | null) {
  if (value === null || value === undefined || value === "") return "—";
  const n = Number(value);
  if (Number.isNaN(n)) return String(value);
  return `${n.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}${
    currency ? ` ${currency}` : ""
  }`;
}

export function formatDateTime(value: string | null | undefined) {
  if (!value) return "—";
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return value;
  return d.toLocaleString();
}
