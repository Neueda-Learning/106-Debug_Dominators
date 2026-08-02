import { useMemo, useState } from "react";
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
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  CRYPTO_CURRENCIES,
  FEE_RATES,
  FIAT_CURRENCIES,
  PAYMENT_METHODS,
  PAYMENT_TYPES,
  TAX_RATES,
  api,
  round2,
  type CreatePaymentRequest,
} from "@/lib/api";

function randomRef(prefix: string) {
  return `${prefix}-${Math.random().toString(36).slice(2, 10).toUpperCase()}`;
}

type FormState = Omit<CreatePaymentRequest, "paymentRef" | "idempotencyKey"> & {
  feeRate: number;
  taxRate: number;
};

const isCrypto = (method: string, type: string) =>
  method === "CRYPTO_WALLET" || type === "CRYPTO";

export function CreatePaymentDialog({
  open,
  onOpenChange,
}: {
  open: boolean;
  onOpenChange: (v: boolean) => void;
}) {
  const queryClient = useQueryClient();
  const [form, setForm] = useState<FormState>(() => ({
    payerAccountId: 1,
    payeeAccountId: 2,
    paymentType: "NORMAL",
    paymentMethod: "BANK_TRANSFER",
    amount: 100,
    sourceCurrencyCode: "USD",
    settlementCurrencyCode: "USD",
    description: "",
    feeRate: FEE_RATES["BANK_TRANSFER"] ?? 0,
    taxRate: TAX_RATES["USD"] ?? 0,
  }));

  const set = <K extends keyof FormState>(key: K, value: FormState[K]) =>
    setForm((f) => ({ ...f, [key]: value }));

  const cryptoMode = isCrypto(form.paymentMethod, form.paymentType);

  const { feeAmount, taxAmount, total } = useMemo(() => {
    const amount = Number(form.amount) || 0;
    const fee = round2((amount * form.feeRate) / 100);
    const tax = round2((amount * form.taxRate) / 100);
    return { feeAmount: fee, taxAmount: tax, total: round2(amount + fee + tax) };
  }, [form.amount, form.feeRate, form.taxRate]);

  const onMethodChange = (method: CreatePaymentRequest["paymentMethod"]) =>
    setForm((f) => {
      const nextCrypto = isCrypto(method, f.paymentType);
      const source =
        nextCrypto && !CRYPTO_CURRENCIES.includes(f.sourceCurrencyCode as never)
          ? "BTC"
          : !nextCrypto && CRYPTO_CURRENCIES.includes(f.sourceCurrencyCode as never)
            ? "USD"
            : f.sourceCurrencyCode;
      return {
        ...f,
        paymentMethod: method,
        sourceCurrencyCode: source,
        feeRate: FEE_RATES[method] ?? f.feeRate,
      };
    });

  const onTypeChange = (type: CreatePaymentRequest["paymentType"]) =>
    setForm((f) => {
      const nextCrypto = isCrypto(f.paymentMethod, type);
      const source =
        nextCrypto && !CRYPTO_CURRENCIES.includes(f.sourceCurrencyCode as never)
          ? "BTC"
          : f.sourceCurrencyCode;
      return { ...f, paymentType: type, sourceCurrencyCode: source };
    });

  const onSourceCurrencyChange = (code: string) =>
    setForm((f) => ({
      ...f,
      sourceCurrencyCode: code,
      taxRate: TAX_RATES[code] ?? 0,
    }));

  const mutation = useMutation({
    mutationFn: () => {
      const { feeRate: _feeRate, taxRate: _taxRate, ...payload } = form;
      return api.createPayment({
        ...payload,
        paymentRef: randomRef("PAY"),
        idempotencyKey: randomRef("IDEM"),
        feeAmount,
        taxAmount,
      });
    },
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
            POST /api/payments — reference and idempotency key are generated automatically.
          </DialogDescription>
        </DialogHeader>

        <div className="grid gap-4 sm:grid-cols-2">
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
              onValueChange={(v) => onTypeChange(v as CreatePaymentRequest["paymentType"])}
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
              onValueChange={(v) => onMethodChange(v as CreatePaymentRequest["paymentMethod"])}
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
          <Field
            label="Source currency"
            hint={cryptoMode ? "crypto assets" : "country currencies"}
          >
            <Select value={form.sourceCurrencyCode} onValueChange={onSourceCurrencyChange}>
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {cryptoMode ? (
                  <SelectGroup>
                    <SelectLabel>Crypto</SelectLabel>
                    {CRYPTO_CURRENCIES.map((c) => (
                      <SelectItem key={c} value={c}>
                        {c}
                      </SelectItem>
                    ))}
                  </SelectGroup>
                ) : (
                  <SelectGroup>
                    <SelectLabel>Fiat</SelectLabel>
                    {FIAT_CURRENCIES.map((c) => (
                      <SelectItem key={c} value={c}>
                        {c}
                      </SelectItem>
                    ))}
                  </SelectGroup>
                )}
              </SelectContent>
            </Select>
          </Field>
          <Field label="Settlement currency" hint="fiat or crypto">
            <Select
              value={form.settlementCurrencyCode ?? "USD"}
              onValueChange={(v) => set("settlementCurrencyCode", v)}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectGroup>
                  <SelectLabel>Fiat</SelectLabel>
                  {FIAT_CURRENCIES.map((c) => (
                    <SelectItem key={c} value={c}>
                      {c}
                    </SelectItem>
                  ))}
                </SelectGroup>
                <SelectGroup>
                  <SelectLabel>Crypto</SelectLabel>
                  {CRYPTO_CURRENCIES.map((c) => (
                    <SelectItem key={c} value={c}>
                      {c}
                    </SelectItem>
                  ))}
                </SelectGroup>
              </SelectContent>
            </Select>
          </Field>
          <Field label="Fee rate %" hint={`${form.paymentMethod} default`}>
            <Input
              type="number"
              step="0.01"
              value={form.feeRate}
              onChange={(e) => set("feeRate", Number(e.target.value))}
            />
          </Field>
          <Field label="Tax rate %" hint={`${form.sourceCurrencyCode} default`}>
            <Input
              type="number"
              step="0.01"
              value={form.taxRate}
              onChange={(e) => set("taxRate", Number(e.target.value))}
            />
          </Field>
          <div className="sm:col-span-2 grid gap-2 rounded-md border border-border p-3 text-sm">
            <Row
              label={`Fee (${form.feeRate}%)`}
              value={`${feeAmount} ${form.sourceCurrencyCode}`}
            />
            <Row
              label={`Tax (${form.taxRate}%)`}
              value={`${taxAmount} ${form.sourceCurrencyCode}`}
            />
            <Row label="Total charged" value={`${total} ${form.sourceCurrencyCode}`} strong />
          </div>
          <div className="sm:col-span-2">
            <Field label="Description">
              <Textarea
                rows={2}
                value={form.description ?? ""}
                onChange={(e) => set("description", e.target.value)}
                placeholder="Invoice settlement, payout, transfer…"
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

function Row({ label, value, strong }: { label: string; value: string; strong?: boolean }) {
  return (
    <div className="flex items-center justify-between">
      <span className="text-xs text-muted-foreground">{label}</span>
      <span className={`font-mono tabular-nums ${strong ? "font-semibold" : "text-sm"}`}>
        {value}
      </span>
    </div>
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
