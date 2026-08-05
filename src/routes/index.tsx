import { createFileRoute, Link } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import { useMemo, useState } from "react";
import { AppHeader } from "@/components/AppHeader";
import { CreatePaymentDialog } from "@/components/CreatePaymentDialog";
import { StatusBadge } from "@/components/StatusBadge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  PAYMENT_STATUSES,
  api,
  formatAmount,
  formatDateTime,
  type Payment,
  type PaymentStatus,
} from "@/lib/api";

import { AlertTriangle, ArrowRight, Plus, RefreshCw, Search } from "lucide-react";

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      { title: "FasterPay — Send Money & Track Every Payment" },
      {
        name: "description",
        content:
          "Pay people and merchants with FasterPay: UPI, cards, bank transfers and crypto, with full status history and refunds on failed payments.",
      },
      { property: "og:title", content: "FasterPay — Send Money & Track Every Payment" },
      {
        property: "og:description",
        content:
          "Pay people and merchants, track status history, and request refunds on failed payments.",
      },
    ],
  }),
  component: PaymentsPage,
});

type SortKey = "createdAt" | "amount" | "paymentId";

function PaymentsPage() {
  const [status, setStatus] = useState<PaymentStatus | "ALL">("ALL");
  const [search, setSearch] = useState("");
  const [sort, setSort] = useState<SortKey>("createdAt");
  const [dialogOpen, setDialogOpen] = useState(false);

  const query = useQuery({
    queryKey: ["payments", status],
    queryFn: () => (status === "ALL" ? api.listPayments() : api.listPaymentsByStatus(status)),
    retry: false,
  });

  const payments = useMemo(() => {
    const rows = query.data ?? [];
    const term = search.trim().toLowerCase();
    const filtered = term
      ? rows.filter((p) =>
          [p.paymentRef, p.description, p.paymentMethod, p.paymentType, String(p.paymentId)]
            .filter(Boolean)
            .some((v) => String(v).toLowerCase().includes(term)),
        )
      : rows;
    return [...filtered].sort((a, b) => {
      if (sort === "amount") return Number(b.amount) - Number(a.amount);
      if (sort === "paymentId") return b.paymentId - a.paymentId;
      return (
        new Date(b.createdAt ?? 0).getTime() - new Date(a.createdAt ?? 0).getTime()
      );
    });
  }, [query.data, search, sort]);

  const counts = useMemo(() => {
    const rows = query.data ?? [];
    const base: Record<string, number> = { TOTAL: rows.length };
    for (const s of PAYMENT_STATUSES) base[s] = rows.filter((p) => p.status === s).length;
    return base;
  }, [query.data]);

  return (
    <div className="min-h-screen">
      <AppHeader />
      <main className="mx-auto max-w-7xl px-6 py-10">
        <div className="flex flex-wrap items-end justify-between gap-4">
          <div>
            <p className="mono-tag">GET /api/payments</p>
            <h1 className="mt-2 text-3xl font-semibold tracking-tight">
              Payments
            </h1>
            <p className="mt-1 text-sm text-muted-foreground">
              CREATED → VALIDATED → PROCESSING → COMPLETED, with FAILED possible at any stage.
              Failed payments can be refunded.
            </p>
          </div>
          <div className="flex gap-2">
            <Button variant="outline" onClick={() => query.refetch()} disabled={query.isFetching}>
              <RefreshCw className={`mr-1.5 size-4 ${query.isFetching ? "animate-spin" : ""}`} />
              Refresh
            </Button>

            <Button onClick={() => setDialogOpen(true)}>
              <Plus className="mr-1.5 size-4" />
              New payment
            </Button>
          </div>
        </div>

        <div className="mt-8 grid gap-3 grid-cols-2 sm:grid-cols-4">
          <StatCard label="Total" value={counts["TOTAL"] ?? 0} />
          {PAYMENT_STATUSES.map((s) => (
            <StatCard key={s} label={s} value={counts[s] ?? 0} status={s} />
          ))}
        </div>

        <div className="panel mt-8 overflow-hidden">
          <div className="flex flex-wrap items-center gap-3 border-b border-border p-4">
            <div className="relative min-w-56 flex-1">
              <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Search reference, method, description…"
                className="pl-9"
              />
            </div>
            <Select value={status} onValueChange={(v) => setStatus(v as PaymentStatus | "ALL")}>
              <SelectTrigger className="w-44">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="ALL">All statuses</SelectItem>
                {PAYMENT_STATUSES.map((s) => (
                  <SelectItem key={s} value={s}>
                    {s}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <Select value={sort} onValueChange={(v) => setSort(v as SortKey)}>
              <SelectTrigger className="w-44">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="createdAt">Newest first</SelectItem>
                <SelectItem value="amount">Highest amount</SelectItem>
                <SelectItem value="paymentId">Payment ID</SelectItem>
              </SelectContent>
            </Select>
          </div>

          {query.isLoading ? (
            <div className="space-y-3 p-4">
              {[0, 1, 2, 3].map((i) => (
                <Skeleton key={i} className="h-10 w-full" />
              ))}
            </div>
          ) : query.isError ? (
            <div className="flex items-start gap-3 p-6 text-sm">
              <AlertTriangle className="mt-0.5 size-4 text-status-failed" />
              <div>
                <p className="font-medium">Could not reach the payments API</p>
                <p className="mt-1 text-muted-foreground">
                  {(query.error as Error).message}. Check that the Spring Boot backend is running
                  and reachable at the configured API base URL.
                </p>
              </div>
            </div>
          ) : payments.length === 0 ? (
            <p className="p-8 text-center text-sm text-muted-foreground">
              No payments match the current filters.
            </p>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Reference</TableHead>
                  <TableHead>Parties</TableHead>
                  <TableHead>Status</TableHead>

                  <TableHead>Type / Method</TableHead>
                  <TableHead className="text-right">Amount</TableHead>
                  <TableHead>Created</TableHead>
                  <TableHead />
                </TableRow>
              </TableHeader>
              <TableBody>
                {payments.map((p) => (
                  <PaymentRow key={p.paymentId} payment={p} />
                ))}
              </TableBody>
            </Table>
          )}
        </div>
      </main>
      <CreatePaymentDialog open={dialogOpen} onOpenChange={setDialogOpen} />
    </div>
  );
}

function PaymentRow({ payment }: { payment: Payment }) {
  const sourceLabel = payment.sourceAccount || payment.sourceCountry || "—";
  const destinationLabel = payment.destinationAccount || payment.destinationCountry || "—";

  return (
    <TableRow>
      <TableCell>
        <div className="font-mono text-xs">{payment.paymentRef}</div>
        <div className="mono-tag">#{payment.paymentId}</div>
      </TableCell>
      <TableCell className="text-xs">
        <div>{sourceLabel}</div>
        <div className="text-muted-foreground">→ {destinationLabel}</div>
      </TableCell>
      <TableCell>

        <StatusBadge status={payment.status} />
      </TableCell>
      <TableCell className="text-xs text-muted-foreground">
        {payment.paymentType}
        <span className="mx-1 opacity-50">/</span>
        {payment.paymentMethod}
      </TableCell>
      <TableCell className="text-right font-mono text-sm">
        {formatAmount(payment.amount, payment.sourceCurrencyCode)}
      </TableCell>
      <TableCell className="text-xs text-muted-foreground">
        {formatDateTime(payment.createdAt)}
      </TableCell>
      <TableCell className="text-right">
        <Button asChild variant="ghost" size="sm">
          <Link to="/payments/$paymentId" params={{ paymentId: String(payment.paymentId) }}>
            Details <ArrowRight className="ml-1 size-3.5" />
          </Link>
        </Button>
      </TableCell>
    </TableRow>
  );
}

function StatCard({
  label,
  value,
  status,
}: {
  label: string;
  value: number;
  status?: PaymentStatus;
}) {
  return (
    <div className="panel p-4">
      <div className="flex items-center justify-between">
        <p className="mono-tag">{label}</p>
        {status ? <StatusBadge status={status} className="scale-90" /> : null}
      </div>
      <p className="mt-2 text-2xl font-semibold tabular-nums">{value}</p>
    </div>
  );
}
