import { useEffect, useMemo, useState } from "react";
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
  FIAT_CURRENCIES,
  PAYMENT_OPTIONS,
  TAX_RATES,
  api,
  feeRateFor,
  getAuthUser,
  round2,
  type PaymentOptionId,
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
  const [payerAccountId, setPayerAccountId] = useState<number | null>(null);
  const [optionId, setOptionId] = useState<PaymentOptionId>("BANK_NATIONAL");
  const [payeeAccountId, setPayeeAccountId] = useState<number | null>(2);
  const [amount, setAmount] = useState(100);
  const [sourceCurrencyCode, setSourceCurrencyCode] = useState("USD");
  const [settlementCurrencyCode, setSettlementCurrencyCode] = useState("USD");
  const [description, setDescription] = useState("");

  useEffect(() => {
    setPayerAccountId(getAuthUser()?.userId ?? null);
  }, [open]);

  const option = PAYMENT_OPTIONS.find((o) => o.id === optionId)!;
  const cryptoMode = option.paymentMethod === "CRYPTO_WALLET";

  const feeRate = feeRateFor(amount, sourceCurrencyCode);
  const taxRate = TAX_RATES[sourceCurrencyCode] ?? 0;

  const { feeAmount, taxAmount, total } = useMemo(() => {
    const base = Math.max(0, Number(amount) || 0);
    const fee = round2((base * feeRate) / 100);
    const tax = round2((base * taxRate) / 100);
    return { feeAmount: fee, taxAmount: tax, total: round2(base + fee + tax) };
  }, [amount, feeRate, taxRate]);

  const onOptionChange = (id: PaymentOptionId) => {
    setOptionId(id);
    const next = PAYMENT_OPTIONS.find((o) => o.id === id)!;
    const nextCrypto = next.paymentMethod === "CRYPTO_WALLET";
    setSourceCurrencyCode((cur) => {
      const isCryptoCur = (CRYPTO_CURRENCIES as readonly string[]).includes(cur);
      if (nextCrypto && !isCryptoCur) return "BTC";
      if (!nextCrypto && isCryptoCur) return "USD";
      return cur;
    });
  };

  const mutation = useMutation({
    mutationFn: () => {
      const payload: CreatePaymentRequest = {
        paymentRef: randomRef("PAY"),
        idempotencyKey: randomRef("IDEM"),
        payerAccountId: payerAccountId ?? 0,
        payeeAccountId,
        paymentType: option.paymentType,
        paymentMethod: option.paymentMethod,
        amount,
        sourceCurrencyCode,
        settlementCurrencyCode,
        description,
        feeAmount,
        taxAmount,
      };
      return api.createPayment(payload);
    },
    onSuccess: (payment) => {
      toast.success(`Payment ${payment.paymentRef} created (${payment.status})`);
      queryClient.invalidateQueries({ queryKey: ["payments"] });
      onOpenChange(false);
    },
    onError: (e) => toast.error(e instanceof Error ? e.message : "Failed to create payment"),
  });

  const invalid = amount <= 0 || (payeeAccountId !== null && payeeAccountId < 0);

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-h-[90vh] overflow-y-auto sm:max-w-2xl">
        <DialogHeader>
          <DialogTitle>Create payment</DialogTitle>
          <DialogDescription>
            POST /api/payments — reference, idempotency key, fee and tax are computed automatically.
          </DialogDescription>
        </DialogHeader>

        <div className="grid gap-4 sm:grid-cols-2">
          <Field label="Paying from" hint="signed-in account">
            <Input
              readOnly
              value={
                payerAccountId === null
                  ? "Not signed in — use Connect API"
                  : `Account #${payerAccountId}`
              }
              className="font-mono"
            />
          </Field>
          <Field label="Payee account ID">
            <Input
              type="number"
              min={0}
              value={payeeAccountId ?? ""}
              onChange={(e) =>
                setPayeeAccountId(
                  e.target.value === "" ? null : Math.max(0, Number(e.target.value)),
                )
              }
            />
          </Field>
          <Field label="Payment type">
            <Select value={optionId} onValueChange={(v) => onOptionChange(v as PaymentOptionId)}>
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {PAYMENT_OPTIONS.map((o) => (
                  <SelectItem key={o.id} value={o.id}>
                    {o.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </Field>
          <Field label="Amount" hint="must be greater than 0">
            <Input
              type="number"
              min={0}
              step="0.01"
              value={amount}
              onChange={(e) => setAmount(Math.max(0, Number(e.target.value)))}
            />
          </Field>
          <Field
            label="Source currency"
            hint={cryptoMode ? "crypto assets" : "country currencies"}
          >
            <Select value={sourceCurrencyCode} onValueChange={setSourceCurrencyCode}>
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectGroup>
                  <SelectLabel>{cryptoMode ? "Crypto" : "Fiat"}</SelectLabel>
                  {(cryptoMode ? CRYPTO_CURRENCIES : FIAT_CURRENCIES).map((c) => (
                    <SelectItem key={c} value={c}>
                      {c}
                    </SelectItem>
                  ))}
                </SelectGroup>
              </SelectContent>
            </Select>
          </Field>
          <Field label="Settlement currency" hint="fiat or crypto">
            <Select value={settlementCurrencyCode} onValueChange={setSettlementCurrencyCode}>
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
          <div className="sm:col-span-2 grid gap-2 rounded-md border border-border p-3 text-sm">
            <Row label={`Fee (${feeRate}%)`} value={`${feeAmount} ${sourceCurrencyCode}`} />
            <Row label={`Tax (${taxRate}%)`} value={`${taxAmount} ${sourceCurrencyCode}`} />
            <Row label="Total charged" value={`${total} ${sourceCurrencyCode}`} strong />
            <p className="text-xs text-muted-foreground">
              Fee tiers: 0% under $100 equivalent, 0.5% up to $1,000, 0.75% above.
            </p>
          </div>
          <div className="sm:col-span-2">
            <Field label="Description">
              <Textarea
                rows={2}
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Invoice settlement, payout, transfer…"
              />
            </Field>
          </div>
        </div>

        <DialogFooter>
          <Button variant="ghost" onClick={() => onOpenChange(false)}>
            Cancel
          </Button>
          <Button onClick={() => mutation.mutate()} disabled={mutation.isPending || invalid}>
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
