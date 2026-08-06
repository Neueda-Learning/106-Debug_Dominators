import { createFileRoute, Link } from "@tanstack/react-router";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { toast } from "sonner";
import { AppHeader } from "@/components/AppHeader";
import { StatusBadge } from "@/components/StatusBadge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  REFUND_METHODS,
  REFUND_REASONS,
  api,
  formatAmount,
  formatDateTime,
  isRefundable,
  type RefundMethod,
} from "@/lib/api";
import { AlertTriangle, ArrowLeft } from "lucide-react";

export const Route = createFileRoute("/payments/$paymentId")({
  head: () => ({
    meta: [
      { title: "Payment Detail & Status History — Payments Console" },
      {
        name: "description",
        content:
          "Inspect a single payment: amounts, currencies, failure codes, and the full audit trail of status transitions.",
      },
      { property: "og:title", content: "Payment Detail & Status History" },
      {
        property: "og:description",
        content: "Amounts, currencies, error codes, and the full status transition audit trail.",
      },
    ],
  }),
  component: PaymentDetailPage,
});

function PaymentDetailPage() {
  const { paymentId } = Route.useParams();
  const queryClient = useQueryClient();
  const [refundReason, setRefundReason] = useState<string>(REFUND_REASONS[0]);
  const [refundMethod, setRefundMethod] = useState<RefundMethod>("ORIGINAL_PAYMENT_METHOD");
  const [remarks, setRemarks] = useState("");

  const payment = useQuery({
    queryKey: ["payment", paymentId],
    queryFn: () => api.getPayment(paymentId),
    retry: false,
  });
  const history = useQuery({
    queryKey: ["payment-history", paymentId],
    queryFn: () => api.getPaymentHistory(paymentId),
    retry: false,
  });

  const refunds = useQuery({
    queryKey: ["refunds", paymentId],
    queryFn: () => api.listRefunds(paymentId),
    retry: false,
  });

  const requestRefund = useMutation({
    mutationFn: () =>
      api.requestRefund(paymentId, { refundReason, refundMethod, remarks: remarks || null }),
    onSuccess: (r) => {
      toast.success(`Refund ${r.refundReference} requested`);
      queryClient.invalidateQueries({ queryKey: ["payment", paymentId] });
      queryClient.invalidateQueries({ queryKey: ["payment-history", paymentId] });
      queryClient.invalidateQueries({ queryKey: ["refunds", paymentId] });
      queryClient.invalidateQueries({ queryKey: ["payments"] });
      setRemarks("");
    },
    onError: (e) => toast.error(e instanceof Error ? e.message : "Refund request rejected"),
  });

  const p = payment.data;

  const resolveParty = (
    rawAccount: string | null | undefined,
    id: number | null | undefined,
    fallback: string,
  ) => {
    const raw = rawAccount?.trim();
    if (raw) {
      if (/^\d+$/.test(raw)) return accountName(Number(raw));
      return raw;
    }
    if (id && id > 0) return accountName(id);
    return fallback;
  };

  return (
    <div className="min-h-screen">
      <AppHeader />
      <main className="mx-auto max-w-6xl px-6 py-10">
        <Button asChild variant="ghost" size="sm" className="mb-6 -ml-2">
          <Link to="/">
            <ArrowLeft className="mr-1.5 size-4" /> All payments
          </Link>
        </Button>

        {payment.isLoading ? (
          <Skeleton className="h-40 w-full" />
        ) : payment.isError ? (
          <div className="panel flex items-start gap-3 p-6 text-sm">
            <AlertTriangle className="mt-0.5 size-4 text-status-failed" />
            <p className="text-muted-foreground">{(payment.error as Error).message}</p>
          </div>
        ) : p ? (
          <>
            <div className="panel p-6">
              <div className="flex flex-wrap items-start justify-between gap-4">
                <div>
                  <h1 className="mt-2 font-mono text-2xl font-semibold tracking-tight">
                    {p.paymentRef}
                  </h1>
                  <p className="mt-1 text-sm text-muted-foreground">
                    {p.description || "No description"}
                  </p>
                </div>
                <div className="text-right">
                  <StatusBadge status={p.status} />
                  <p className="mt-2 font-mono text-2xl font-semibold tabular-nums">
                    {formatAmount(p.amount, p.sourceCurrencyCode)}
                  </p>
                </div>
              </div>

              <div className="mt-6 grid gap-x-8 gap-y-4 border-t border-border pt-6 sm:grid-cols-2 lg:grid-cols-3">
                <Detail label="Payment ID" value={p.paymentRef} mono />
                <Detail label="Payment type" value={p.paymentType} />
                <Detail label="Payment method" value={p.paymentMethod} />
                <Detail label="Idempotency key" value={p.idempotencyKey || "—"} mono />
                <Detail label="Source currency" value={p.sourceCurrencyCode} />
                <Detail label="Source country" value={p.sourceCountry ?? "—"} />
                <Detail label="Destination country" value={p.destinationCountry ?? "—"} />
                <Detail
                  label="Paid from"
                  value={resolveParty(p.sourceAccount, p.payerAccountId, "—")}
                />
                <Detail
                  label="Paid to"
                  value={resolveParty(p.destinationAccount, p.payeeAccountId, "—")}
                />
                <Detail label="Campaign" value={p.campaignId ?? "—"} />
                <Detail label="Fee" value={formatAmount(p.feeAmount, p.sourceCurrencyCode)} />
                <Detail label="Tax" value={formatAmount(p.taxAmount, p.sourceCurrencyCode)} />
                <Detail label="Net" value={formatAmount(p.netAmount, p.settlementCurrencyCode)} />
                <Detail label="Settlement currency" value={p.settlementCurrencyCode ?? "—"} />
                <Detail label="External ref" value={p.externalPaymentRef ?? "—"} mono />
                <Detail label="Exchange rate ID" value={p.exchangeRateId ?? "—"} />
                <Detail label="Initiated" value={formatDateTime(p.initiatedAt)} />
                <Detail label="Completed" value={formatDateTime(p.completedAt)} />
                <Detail label="Failed" value={formatDateTime(p.failedAt)} />
                <Detail label="Created" value={formatDateTime(p.createdAt)} />
                <Detail label="Updated" value={formatDateTime(p.updatedAt)} />
              </div>
            </div>

            <div className="mt-6 grid gap-6 lg:grid-cols-[1fr_340px]">
              <div className="panel p-6">
                <h2 className="mt-1.5 text-lg font-semibold">Status history</h2>

                {history.isLoading ? (
                  <Skeleton className="mt-4 h-24 w-full" />
                ) : history.isError ? (
                  <p className="mt-4 text-sm text-muted-foreground">
                    {(history.error as Error).message}
                  </p>
                ) : (history.data ?? []).length === 0 ? (
                  <p className="mt-4 text-sm text-muted-foreground">No transitions recorded yet.</p>
                ) : (
                  <ol className="mt-5 space-y-5">
                    {(history.data ?? []).map((h) => (
                      <li key={h.paymentStatusHistoryId} className="relative pl-6">
                        <span className="absolute left-0 top-1.5 size-2 rounded-full bg-primary" />
                        <span className="absolute left-[3px] top-5 h-[calc(100%+0.6rem)] w-px bg-border last:hidden" />
                        <div className="flex flex-wrap items-center gap-2">
                          {h.oldStatus ? <StatusBadge status={h.oldStatus} /> : null}
                          {h.oldStatus ? (
                            <span className="text-xs text-muted-foreground">→</span>
                          ) : null}
                          <StatusBadge status={h.newStatus} />
                          <span className="mono-tag ml-auto">{formatDateTime(h.changedAt)}</span>
                        </div>
                        {(h.changeReasonCode || h.changeReasonMessage) && (
                          <p className="mt-1.5 text-sm text-muted-foreground">
                            {h.changeReasonCode ? (
                              <span className="font-mono text-xs text-status-failed">
                                {h.changeReasonCode}
                              </span>
                            ) : null}
                            {h.changeReasonCode && h.changeReasonMessage ? " · " : ""}
                            {h.changeReasonMessage}
                          </p>
                        )}
                        {h.changedByUserId ? (
                          <p className="mono-tag mt-1">user #{h.changedByUserId}</p>
                        ) : h.metadata ? (
                          <p className="mono-tag mt-1">by {h.metadata}</p>
                        ) : null}
                      </li>
                    ))}
                  </ol>
                )}
              </div>

              <div className="panel h-fit p-6">
                <h2 className="mt-1.5 text-lg font-semibold">Refund</h2>

                {(refunds.data ?? []).length > 0 ? (
                  <div className="mt-4 space-y-3">
                    {(refunds.data ?? []).map((r) => (
                      <div key={r.refundId} className="rounded-md border border-border p-3">
                        <div className="flex items-center justify-between gap-2">
                          <span className="font-mono text-xs">{r.refundReference}</span>
                          <StatusBadge status={r.refundStatus} />
                        </div>
                        <p className="mt-2 text-sm">
                          {formatAmount(r.refundAmount, p.sourceCurrencyCode)} · {r.refundReason}
                        </p>
                        <p className="mono-tag mt-1">
                          {r.refundMethod} · by {r.initiatedBy} · {formatDateTime(r.refundDate)}
                        </p>
                        {r.remarks ? (
                          <p className="mt-1.5 text-xs text-muted-foreground">{r.remarks}</p>
                        ) : null}
                      </div>
                    ))}
                  </div>
                ) : isRefundable(p.status) ? (
                  <div className="mt-4 space-y-3">
                    <p className="text-xs text-muted-foreground">
                      This payment failed, so you can request a refund of{" "}
                      {formatAmount(p.netAmount ?? p.amount, p.sourceCurrencyCode)}.
                    </p>
                    <div className="space-y-1.5">
                      <Label className="text-xs text-muted-foreground">Reason</Label>
                      <Select value={refundReason} onValueChange={setRefundReason}>
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          {REFUND_REASONS.map((r) => (
                            <SelectItem key={r} value={r}>
                              {r}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>
                    <div className="space-y-1.5">
                      <Label className="text-xs text-muted-foreground">Refund to</Label>
                      <Select
                        value={refundMethod}
                        onValueChange={(v) => setRefundMethod(v as RefundMethod)}
                      >
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          {REFUND_METHODS.map((m) => (
                            <SelectItem key={m} value={m}>
                              {m.replaceAll("_", " ").toLowerCase()}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>
                    <div className="space-y-1.5">
                      <Label className="text-xs text-muted-foreground">Remarks</Label>
                      <Input
                        value={remarks}
                        onChange={(e) => setRemarks(e.target.value)}
                        placeholder="Optional note for support"
                      />
                    </div>
                    <Button
                      className="w-full"
                      onClick={() => requestRefund.mutate()}
                      disabled={requestRefund.isPending}
                    >
                      {requestRefund.isPending ? "Requesting…" : "Request refund"}
                    </Button>
                  </div>
                ) : (
                  <p className="mt-4 text-xs text-muted-foreground">
                    Refunds are only available for FAILED payments. Statuses such as CREATED,
                    VALIDATED, PROCESSING and COMPLETED are set by FasterPay and the payment
                    gateway — they can't be changed from this console.
                  </p>
                )}
              </div>
            </div>
          </>
        ) : null}
      </main>
    </div>
  );
}

function Detail({
  label,
  value,
  mono,
}: {
  label: string;
  value: string | number;
  mono?: boolean;
}) {
  return (
    <div>
      <p className="mono-tag">{label}</p>
      <p className={`mt-1 text-sm ${mono ? "break-all font-mono text-xs" : ""}`}>{value}</p>
    </div>
  );
}
