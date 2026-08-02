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
  PAYMENT_STATUSES,
  api,
  formatAmount,
  formatDateTime,
  type PaymentStatus,
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
  const [nextStatus, setNextStatus] = useState<PaymentStatus>("VALIDATED");
  const [reasonCode, setReasonCode] = useState("");
  const [reasonMessage, setReasonMessage] = useState("");

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

  const update = useMutation({
    mutationFn: () =>
      api.updatePaymentStatus(paymentId, {
        status: nextStatus,
        reasonCode: reasonCode || undefined,
        reasonMessage: reasonMessage || undefined,
      }),
    onSuccess: (p) => {
      toast.success(`Status updated to ${p.status}`);
      queryClient.invalidateQueries({ queryKey: ["payment", paymentId] });
      queryClient.invalidateQueries({ queryKey: ["payment-history", paymentId] });
      queryClient.invalidateQueries({ queryKey: ["payments"] });
      setReasonCode("");
      setReasonMessage("");
    },
    onError: (e) => toast.error(e instanceof Error ? e.message : "Transition rejected"),
  });

  const p = payment.data;

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
                  <p className="mono-tag">GET /api/payments/{p.paymentId}</p>
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
                <Detail label="Payment type" value={p.paymentType} />
                <Detail label="Payment method" value={p.paymentMethod} />
                <Detail label="Idempotency key" value={p.idempotencyKey} mono />
                <Detail label="Payer account" value={p.payerAccountId} />
                <Detail label="Payee account" value={p.payeeAccountId ?? "—"} />
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
              </div>
            </div>

            <div className="mt-6 grid gap-6 lg:grid-cols-[1fr_340px]">
              <div className="panel p-6">
                <p className="mono-tag">GET /api/payments/{p.paymentId}/history</p>
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
                        ) : null}
                      </li>
                    ))}
                  </ol>
                )}
              </div>

              <div className="panel h-fit p-6">
                <p className="mono-tag">PUT /api/payments/{p.paymentId}/status</p>
                <h2 className="mt-1.5 text-lg font-semibold">Advance lifecycle</h2>
                <div className="mt-4 space-y-3">
                  <div className="space-y-1.5">
                    <Label className="text-xs text-muted-foreground">Next status</Label>
                    <Select
                      value={nextStatus}
                      onValueChange={(v) => setNextStatus(v as PaymentStatus)}
                    >
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        {PAYMENT_STATUSES.map((s) => (
                          <SelectItem key={s} value={s}>
                            {s}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>
                  <div className="space-y-1.5">
                    <Label className="text-xs text-muted-foreground">Reason code</Label>
                    <Input
                      value={reasonCode}
                      onChange={(e) => setReasonCode(e.target.value)}
                      placeholder="E.g. INSUFFICIENT_FUNDS"
                    />
                  </div>
                  <div className="space-y-1.5">
                    <Label className="text-xs text-muted-foreground">Reason message</Label>
                    <Input
                      value={reasonMessage}
                      onChange={(e) => setReasonMessage(e.target.value)}
                      placeholder="Optional detail"
                    />
                  </div>
                  <Button
                    className="w-full"
                    onClick={() => update.mutate()}
                    disabled={update.isPending}
                  >
                    {update.isPending ? "Updating…" : `Set ${nextStatus}`}
                  </Button>
                  <p className="text-xs text-muted-foreground">
                    Invalid transitions are rejected by the backend and surfaced here.
                  </p>
                </div>
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
