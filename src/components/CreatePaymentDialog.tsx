import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  CURRENCIES,
  PAYMENT_METHODS,
  PAYMENT_TYPES,
  api,
  type CreatePaymentRequest,
} from "@/lib/api";

function randomRef(prefix: string) {
  return `${prefix}-${Math.random().toString(36).slice(2, 10).toUpperCase()}`;
}

export function CreatePaymentDialog({
  open,
  onOpenChange,
}: {
  open: boolean;
  onOpenChange: (v: boolean) => void;
}) {
  const queryClient = useQueryClient();
  const [form, setForm] = useState<CreatePaymentRequest>(() => ({
    paymentRef: "",
    idempotencyKey: "",
    payerAccountId: 1,
    payeeAccountId: 2,
    paymentType: "NORMAL",
    paymentMethod: "BANK_TRANSFER",
    amount: 100,
    feeAmount: 0,
    taxAmount: 0,
    sourceCurrencyCode: "USD",
    settlementCurrencyCode: "USD",
    description: "",
  }));

  const set = <K extends keyof CreatePaymentRequest>(key: K, value: CreatePaymentRequest[K]) =>
    setForm((f) => ({ ...f, [key]: value }));

  const mutation = useMutation({
    mutationFn: () =>
      api.createPayment({
        ...form,
        paymentRef: form.paymentRef || randomRef("PAY"),
        idempotencyKey: form.idempotencyKey || randomRef("IDEM"),
        campaignId: form.campaignId ? Number(form.campaignId) : null,
      }),
    onSuccess: (payment) => {
      toast.success(`Payment ${payment.paymentRef} created (${payment.status})`);
      queryClient.invalidateQueries({ queryKey: ["payments"] });
      onOpenChange(false);
    },
    onError: (e) => toast.error(e instanceof Error ? e.message : "Failed to create payment"),
  });

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-h-[90vh] overflow-y-auto sm:max-w-2xl">
        <DialogHeader>
          <DialogTitle>Create payment</DialogTitle>
          <DialogDescription>
            POST /api/payments — the payment enters the lifecycle at CREATED.
          </DialogDescription>
        </DialogHeader>

        <div className="grid gap-4 sm:grid-cols-2">
          <Field label="Payment reference" hint="auto-generated if blank">
            <Input
              value={form.paymentRef}
              onChange={(e) => set("paymentRef", e.target.value)}
              placeholder="PAY-XXXXXXXX"
            />
          </Field>
          <Field label="Idempotency key" hint="auto-generated if blank">
            <Input
              value={form.idempotencyKey}
              onChange={(e) => set("idempotencyKey", e.target.value)}
              placeholder="IDEM-XXXXXXXX"
            />
          </Field>
          <Field label="Payer account ID">
            <Input
              type="number"
              value={form.payerAccountId}
              onChange={(e) => set("payerAccountId", Number(e.target.value))}
            />
          </Field>
          <Field label="Payee account ID">
            <Input
              type="number"
              value={form.payeeAccountId ?? ""}
              onChange={(e) =>
                set("payeeAccountId", e.target.value === "" ? null : Number(e.target.value))
              }
            />
          </Field>
          <Field label="Payment type">
            <Select
              value={form.paymentType}
              onValueChange={(v) => set("paymentType", v as CreatePaymentRequest["paymentType"])}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {PAYMENT_TYPES.map((t) => (
                  <SelectItem key={t} value={t}>
                    {t}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </Field>
          <Field label="Payment method">
            <Select
              value={form.paymentMethod}
              onValueChange={(v) =>
                set("paymentMethod", v as CreatePaymentRequest["paymentMethod"])
              }
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {PAYMENT_METHODS.map((m) => (
                  <SelectItem key={m} value={m}>
                    {m}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </Field>
          <Field label="Amount">
            <Input
              type="number"
              step="0.01"
              value={form.amount}
              onChange={(e) => set("amount", Number(e.target.value))}
            />
          </Field>
          <Field label="Campaign ID" hint="only for CROWDFUNDING payments">
            <Input
              type="number"
              value={form.campaignId ?? ""}
              onChange={(e) =>
                set("campaignId", e.target.value === "" ? null : Number(e.target.value))
              }
            />
          </Field>
          <Field label="Source currency">
            <Select
              value={form.sourceCurrencyCode}
              onValueChange={(v) => set("sourceCurrencyCode", v)}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {CURRENCIES.map((c) => (
                  <SelectItem key={c} value={c}>
                    {c}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </Field>
          <Field label="Settlement currency">
            <Select
              value={form.settlementCurrencyCode ?? "USD"}
              onValueChange={(v) => set("settlementCurrencyCode", v)}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {CURRENCIES.map((c) => (
                  <SelectItem key={c} value={c}>
                    {c}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </Field>
          <Field label="Fee amount">
            <Input
              type="number"
              step="0.01"
              value={form.feeAmount ?? 0}
              onChange={(e) => set("feeAmount", Number(e.target.value))}
            />
          </Field>
          <Field label="Tax amount">
            <Input
              type="number"
              step="0.01"
              value={form.taxAmount ?? 0}
              onChange={(e) => set("taxAmount", Number(e.target.value))}
            />
          </Field>
          <div className="sm:col-span-2">
            <Field label="Description">
              <Textarea
                rows={2}
                value={form.description ?? ""}
                onChange={(e) => set("description", e.target.value)}
                placeholder="Invoice settlement, payout, donation…"
              />
            </Field>
          </div>
        </div>

        <DialogFooter>
          <Button variant="ghost" onClick={() => onOpenChange(false)}>
            Cancel
          </Button>
          <Button onClick={() => mutation.mutate()} disabled={mutation.isPending}>
            {mutation.isPending ? "Creating…" : "Create payment"}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

function Field({
  label,
  hint,
  children,
}: {
  label: string;
  hint?: string;
  children: React.ReactNode;
}) {
  return (
    <div className="space-y-1.5">
      <Label className="text-xs font-medium text-muted-foreground">
        {label}
        {hint ? <span className="ml-1 font-normal opacity-70">({hint})</span> : null}
      </Label>
      {children}
    </div>
  );
}
