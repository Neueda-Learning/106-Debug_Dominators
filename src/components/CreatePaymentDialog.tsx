import { useEffect, useMemo, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { QRCodeSVG } from "qrcode.react";
import { Loader2, CheckCircle2, XCircle } from "lucide-react";
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
  convertAmount,
  feeRateFor,
  getAuthUser,
  isCryptoCurrency,
  round2,
  type PaymentOptionId,
  type CreatePaymentRequest,
  type Payment,
} from "@/lib/api";


function randomRef(prefix: string) {
  return `${prefix}-${Math.random().toString(36).slice(2, 10).toUpperCase()}`;
}

const BANK_MODES = [
  { id: "NEFT", label: "NEFT" },
  { id: "IMPS", label: "IMPS" },
  { id: "RTGS", label: "RTGS" },
  { id: "SWIFT", label: "SWIFT (international)" },
  { id: "OTHER", label: "Other bank transfer" },
] as const;

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

  // Method-specific fields
  const [bankMode, setBankMode] = useState<string>("NEFT");
  const [cardCvv, setCardCvv] = useState("");
  const [cardLast3, setCardLast3] = useState("");
  const [cardExpiry, setCardExpiry] = useState("");
  const [upiId, setUpiId] = useState("");
  const [walletAddress, setWalletAddress] = useState("");
  const [bankAccountNumber, setBankAccountNumber] = useState("");
  const [ifscCode, setIfscCode] = useState("");
  // UPI: after creating the payment we wait for the backend to confirm collection.
  const [awaitingPayment, setAwaitingPayment] = useState<Payment | null>(null);

  useEffect(() => {
    setPayerAccountId(getAuthUser()?.userId ?? null);
    if (open) setAwaitingPayment(null);
  }, [open]);

  const poll = useQuery({
    queryKey: ["payments", awaitingPayment?.paymentId, "poll"],
    queryFn: () => api.getPayment(awaitingPayment!.paymentId),
    enabled: !!awaitingPayment,
    refetchInterval: (q) => {
      const s = q.state.data?.status;
      return s === "COMPLETED" || s === "FAILED" ? false : 2000;
    },
  });

  const liveStatus = poll.data?.status ?? awaitingPayment?.status ?? null;
  const settled = liveStatus === "COMPLETED" || liveStatus === "FAILED";

  useEffect(() => {
    if (!awaitingPayment || !settled) return;
    queryClient.invalidateQueries({ queryKey: ["payments"] });
    if (liveStatus === "COMPLETED") toast.success("UPI collection confirmed");
    else toast.error("UPI collection failed");
  }, [settled, liveStatus, awaitingPayment, queryClient]);


  const option = PAYMENT_OPTIONS.find((o) => o.id === optionId)!;
  const cryptoMode = option.paymentMethod === "CRYPTO_WALLET";
  const bankMode_ = option.paymentMethod === "BANK_TRANSFER";
  const cardMode = option.paymentMethod === "CARD";
  const upiMode = optionId === "UPI";
  const settlementIsCrypto = isCryptoCurrency(settlementCurrencyCode);
  const cryptoNeedsBank = cryptoMode && !settlementIsCrypto;
  const needsBankDetails = bankMode_ || cryptoNeedsBank;

  const feeRate = feeRateFor(amount, sourceCurrencyCode);
  const taxRate = TAX_RATES[sourceCurrencyCode] ?? 0;

  const { feeAmount, taxAmount, total } = useMemo(() => {
    const base = Math.max(0, Number(amount) || 0);
    const fee = round2((base * feeRate) / 100);
    const tax = round2((base * taxRate) / 100);
    return { feeAmount: fee, taxAmount: tax, total: round2(base + fee + tax) };
  }, [amount, feeRate, taxRate]);

  const settlementAmount = useMemo(
    () => convertAmount(amount, sourceCurrencyCode, settlementCurrencyCode),
    [amount, sourceCurrencyCode, settlementCurrencyCode],
  );
  const settlementTotal = useMemo(
    () => convertAmount(total, sourceCurrencyCode, settlementCurrencyCode),
    [total, sourceCurrencyCode, settlementCurrencyCode],
  );

  const upiPayload = useMemo(() => {
    const pa = upiId.trim() || "merchant@ledger";
    const params = new URLSearchParams({
      pa,
      pn: "Ledger Payments",
      am: total.toFixed(2),
      cu: sourceCurrencyCode === "INR" ? "INR" : sourceCurrencyCode,
      tn: description.trim() || "Ledger payment",
    });
    return `upi://pay?${params.toString()}`;
  }, [upiId, total, sourceCurrencyCode, description]);

  const onOptionChange = (id: PaymentOptionId) => {
    setOptionId(id);
    const next = PAYMENT_OPTIONS.find((o) => o.id === id)!;
    const nextCrypto = next.paymentMethod === "CRYPTO_WALLET";
    setBankMode(id === "BANK_INTERNATIONAL" ? "SWIFT" : "NEFT");
    setSourceCurrencyCode((cur) => {
      const isCryptoCur = (CRYPTO_CURRENCIES as readonly string[]).includes(cur);
      if (nextCrypto && !isCryptoCur) return "BTC";
      if (!nextCrypto && isCryptoCur) return "USD";
      if (id === "UPI") return "INR";
      return cur;
    });
  };

  const bankDetail = () =>
    `A/C ${bankAccountNumber.trim()} · IFSC ${ifscCode.trim().toUpperCase()}`;

  const methodDetail = () => {
    if (bankMode_) return `Bank transfer mode: ${bankMode} · ${bankDetail()}`;
    if (cardMode) return `Card •••${cardLast3} exp ${cardExpiry}`;
    if (upiMode) return `UPI: ${upiId.trim() || "merchant@ledger"}`;

    if (cryptoMode)
      return cryptoNeedsBank
        ? `Wallet: ${walletAddress.trim()} · payout ${settlementTotal} ${settlementCurrencyCode} to ${bankDetail()}`
        : `Wallet: ${walletAddress.trim()}`;
    return "";
  };

  const mutation = useMutation({
    mutationFn: () => {
      const detail = methodDetail();
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
        description: [description.trim(), detail].filter(Boolean).join(" — "),
        feeAmount,
        taxAmount,
      };
      return api.createPayment(payload);
    },
    onSuccess: (payment) => {
      queryClient.invalidateQueries({ queryKey: ["payments"] });
      if (upiMode) {
        setAwaitingPayment(payment);
        return;
      }
      toast.success(`Payment ${payment.paymentRef} created (${payment.status})`);
      onOpenChange(false);
    },
    onError: (e) => toast.error(e instanceof Error ? e.message : "Failed to create payment"),
  });

  const cardValid =
    /^\d{3,4}$/.test(cardCvv) && /^\d{3}$/.test(cardLast3) && /^(0[1-9]|1[0-2])\/\d{2}$/.test(cardExpiry);
  const bankValid =
    bankAccountNumber.trim().length >= 6 && ifscCode.trim().length >= 6;
  const invalid =
    amount <= 0 ||
    (payeeAccountId !== null && payeeAccountId < 0) ||
    (cardMode && !cardValid) ||
    (needsBankDetails && !bankValid) ||
    (upiMode && !/^[\w.\-]{2,}@[\w.\-]{2,}$/.test(upiId.trim())) ||
    (cryptoMode && walletAddress.trim().length < 8);

  if (awaitingPayment) {
    return (
      <Dialog open={open} onOpenChange={onOpenChange}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>
              {liveStatus === "COMPLETED"
                ? "Payment received"
                : liveStatus === "FAILED"
                  ? "Payment failed"
                  : "Waiting for payment confirmation…"}
            </DialogTitle>
            <DialogDescription>
              {awaitingPayment.paymentRef} · {total} {sourceCurrencyCode} to{" "}
              {upiId.trim() || "merchant@ledger"}
            </DialogDescription>
          </DialogHeader>

          <div className="flex flex-col items-center gap-4 py-2">
            {settled ? (
              liveStatus === "COMPLETED" ? (
                <CheckCircle2 className="size-14 text-emerald-400" />
              ) : (
                <XCircle className="size-14 text-destructive" />
              )
            ) : (
              <>
                <div className="rounded-md bg-white p-3">
                  <QRCodeSVG value={upiPayload} size={148} level="M" />
                </div>
                <div className="flex items-center gap-2 text-sm text-muted-foreground">
                  <Loader2 className="size-4 animate-spin" />
                  Waiting for payment confirmation…
                </div>
              </>
            )}
            <p className="text-xs text-muted-foreground">
              Status <span className="font-mono text-foreground">{liveStatus}</span> · polling every
              2s
            </p>
          </div>

          <DialogFooter>
            <Button
              variant={settled ? "default" : "ghost"}
              onClick={() => {
                setAwaitingPayment(null);
                onOpenChange(false);
              }}
            >
              {settled ? "Done" : "Close"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    );
  }

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

          {/* Method-specific details */}
          {bankMode_ ? (
            <Field label="Bank transfer mode" hint="clearing network">
              <Select value={bankMode} onValueChange={setBankMode}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {BANK_MODES.map((m) => (
                    <SelectItem key={m.id} value={m.id}>
                      {m.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </Field>
          ) : null}

          {needsBankDetails ? (
            <div className="sm:col-span-2 grid gap-3 rounded-md border border-border p-3 sm:grid-cols-2">
              <Field
                label={cryptoNeedsBank ? "Payout bank account number" : "Payer bank account number"}
                hint="6–18 digits"
              >
                <Input
                  inputMode="numeric"
                  value={bankAccountNumber}
                  onChange={(e) =>
                    setBankAccountNumber(e.target.value.replace(/\D/g, "").slice(0, 18))
                  }
                  placeholder="123456789012"
                  className="font-mono"
                />
              </Field>
              <Field
                label={bankMode === "SWIFT" ? "IFSC / SWIFT code" : "IFSC code"}
                hint="e.g. HDFC0001234"
              >
                <Input
                  value={ifscCode}
                  onChange={(e) => setIfscCode(e.target.value.toUpperCase().slice(0, 11))}
                  placeholder="HDFC0001234"
                  className="font-mono"
                />
              </Field>
              {cryptoNeedsBank ? (
                <div className="sm:col-span-2 grid gap-1 border-t border-border pt-2">
                  <Row
                    label={`Converted amount (${sourceCurrencyCode} → ${settlementCurrencyCode})`}
                    value={`${settlementAmount} ${settlementCurrencyCode}`}
                  />
                  <Row
                    label="Bank credit incl. fee & tax"
                    value={`${settlementTotal} ${settlementCurrencyCode}`}
                    strong
                  />
                  <p className="text-xs text-muted-foreground">
                    Crypto is sold at the indicative rate and settled to this bank account.
                  </p>
                </div>
              ) : null}
            </div>
          ) : null}

          {cardMode ? (
            <div className="sm:col-span-2 grid gap-3 rounded-md border border-border p-3 sm:grid-cols-3">
              <Field label="CVV" hint="3–4 digits">
                <Input
                  inputMode="numeric"
                  maxLength={4}
                  value={cardCvv}
                  onChange={(e) => setCardCvv(e.target.value.replace(/\D/g, "").slice(0, 4))}
                  placeholder="123"
                />
              </Field>
              <Field label="Last 3 digits" hint="of account number">
                <Input
                  inputMode="numeric"
                  maxLength={3}
                  value={cardLast3}
                  onChange={(e) => setCardLast3(e.target.value.replace(/\D/g, "").slice(0, 3))}
                  placeholder="789"
                />
              </Field>
              <Field label="Expiry date" hint="MM/YY">
                <Input
                  value={cardExpiry}
                  onChange={(e) => {
                    const digits = e.target.value.replace(/\D/g, "").slice(0, 4);
                    setCardExpiry(
                      digits.length > 2 ? `${digits.slice(0, 2)}/${digits.slice(2)}` : digits,
                    );
                  }}
                  placeholder="09/28"
                />
              </Field>
            </div>
          ) : null}

          {upiMode ? (
            <div className="sm:col-span-2 grid gap-3 rounded-md border border-border p-3 sm:grid-cols-[1fr_auto] sm:items-center">
              <div className="space-y-3">
                <Field label="UPI ID" hint="payee VPA">
                  <Input
                    value={upiId}
                    onChange={(e) => setUpiId(e.target.value)}
                    placeholder="merchant@upi"
                  />
                </Field>
                <p className="text-xs text-muted-foreground">
                  Scan to pay{" "}
                  <span className="font-mono text-foreground">
                    {total} {sourceCurrencyCode}
                  </span>{" "}
                  (total charged). The QR updates as the amount changes.
                </p>
                <p className="text-xs text-muted-foreground">
                  On “Create payment” the backend records it as pending, this screen shows the QR
                  and polls <span className="font-mono">GET /api/payments/{"{id}"}</span> every 2s
                  until the status changes.
                </p>

              </div>
              <div className="justify-self-center rounded-md bg-white p-3">
                <QRCodeSVG value={upiPayload} size={132} level="M" />
              </div>

            </div>
          ) : null}

          {cryptoMode ? (
            <div className="sm:col-span-2">
              <Field label="Destination wallet address" hint={sourceCurrencyCode}>
                <Input
                  value={walletAddress}
                  onChange={(e) => setWalletAddress(e.target.value)}
                  placeholder="0x… / bc1…"
                  className="font-mono"
                />
              </Field>
            </div>
          ) : null}

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
