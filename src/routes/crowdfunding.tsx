import { createFileRoute } from "@tanstack/react-router";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { toast } from "sonner";
import { AppHeader } from "@/components/AppHeader";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Progress } from "@/components/ui/progress";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Skeleton } from "@/components/ui/skeleton";
import { Switch } from "@/components/ui/switch";
import { Textarea } from "@/components/ui/textarea";
import { api, formatAmount, formatDateTime } from "@/lib/api";
import { AlertTriangle, HeartHandshake } from "lucide-react";

export const Route = createFileRoute("/crowdfunding")({
  head: () => ({
    meta: [
      { title: "Crowdfunding Campaigns — Contribute & Track Progress" },
      {
        name: "description",
        content:
          "Look up a campaign, see remaining amount and funding progress, contribute with preset or custom amounts, and review contribution history.",
      },
      { property: "og:title", content: "Crowdfunding Campaigns — Contribute & Track Progress" },
      {
        property: "og:description",
        content: "Campaign progress, remaining amount, contributions, and donation presets.",
      },
    ],
  }),
  component: CrowdfundingPage;
});

const PRESETS = ["100", "250", "500", "custom"];

function CrowdfundingPage() {
  const [campaignInput, setCampaignInput] = useState("1");
  const [campaignId, setCampaignId] = useState("1");
  const [preset, setPreset] = useState("100");
  const [customAmount, setCustomAmount] = useState("");
  const [contributorId, setContributorId] = useState("1");
  const [note, setNote] = useState("");
  const [anonymous, setAnonymous] = useState(false);
  const queryClient = useQueryClient();

  const campaign = useQuery({
    queryKey: ["campaign", campaignId],
    queryFn: () => api.getCampaign(campaignId),
    retry: false,
  });
  const progress = useQuery({
    queryKey: ["campaign-progress", campaignId],
    queryFn: () => api.getCampaignProgress(campaignId),
    retry: false,
  });
  const contributions = useQuery({
    queryKey: ["campaign-contributions", campaignId],
    queryFn: () => api.getContributions(campaignId),
    retry: false,
  });

  const amount = preset === "custom" ? Number(customAmount) : Number(preset);
  const remaining = progress.data ? Number(progress.data.remainingAmount) : null;
  const pct = progress.data ? Math.min(100, Number(progress.data.progressPercentage) || 0) : 0;

  const contribute = useMutation({
    mutationFn: () =>
      api.contribute(campaignId, {
        contributorId: contributorId ? Number(contributorId) : null,
        amount,
        note: note || null,
        anonymous,
      }),
    onSuccess: (c) => {
      toast.success(`Contribution of ${formatAmount(c.amount, c.currency)} recorded (${c.status})`);
      queryClient.invalidateQueries({ queryKey: ["campaign", campaignId] });
      queryClient.invalidateQueries({ queryKey: ["campaign-progress", campaignId] });
      queryClient.invalidateQueries({ queryKey: ["campaign-contributions", campaignId] });
      setNote("");
    },
    onError: (e) => toast.error(e instanceof Error ? e.message : "Contribution rejected"),
  });

  const invalid =
    !amount || amount <= 0 || (remaining !== null && remaining > 0 && amount > remaining);

  return (
    <div className="min-h-screen">
      <AppHeader />
      <main className="mx-auto max-w-6xl px-6 py-10">
        <div className="flex flex-wrap items-end justify-between gap-4">
          <div>
            <p className="mono-tag">GET /api/crowdfunding/campaigns/{"{id}"}</p>
            <h1 className="mt-2 text-3xl font-semibold tracking-tight">Crowdfunding</h1>
            <p className="mt-1 text-sm text-muted-foreground">
              Track a campaign's progress and record contributions against it.
            </p>
          </div>
          <div className="flex items-end gap-2">
            <div className="space-y-1.5">
              <Label className="text-xs text-muted-foreground">Campaign ID</Label>
              <Input
                className="w-28"
                value={campaignInput}
                onChange={(e) => setCampaignInput(e.target.value)}
              />
            </div>
            <Button variant="outline" onClick={() => setCampaignId(campaignInput || "1")}>
              Load
            </Button>
          </div>
        </div>

        {campaign.isError || progress.isError ? (
          <div className="panel mt-8 flex items-start gap-3 p-6 text-sm">
            <AlertTriangle className="mt-0.5 size-4 text-status-failed" />
            <p className="text-muted-foreground">
              {((campaign.error ?? progress.error) as Error)?.message}
            </p>
          </div>
        ) : null}

        <div className="mt-8 grid gap-6 lg:grid-cols-[1fr_360px]">
          <div className="panel p-6">
            {campaign.isLoading ? (
              <Skeleton className="h-32 w-full" />
            ) : campaign.data ? (
              <>
                <div className="flex items-start justify-between gap-4">
                  <div>
                    <h2 className="text-xl font-semibold">{campaign.data.title}</h2>
                    <p className="mt-1 text-sm text-muted-foreground">
                      {campaign.data.description || "No description"}
                    </p>
                  </div>
                  <span className="mono-tag">{campaign.data.status}</span>
                </div>

                <div className="mt-6">
                  <Progress value={pct} className="h-2.5" />
                  <div className="mt-3 flex flex-wrap justify-between gap-2 text-sm">
                    <span className="font-mono">
                      {formatAmount(campaign.data.currentAmount, campaign.data.currency)} raised
                    </span>
                    <span className="text-muted-foreground">
                      {pct.toFixed(1)}% of{" "}
                      {formatAmount(campaign.data.targetAmount, campaign.data.currency)}
                    </span>
                  </div>
                  {remaining !== null ? (
                    <p className="mt-2 text-sm text-muted-foreground">
                      Remaining:{" "}
                      <span className="font-mono text-foreground">
                        {formatAmount(remaining, campaign.data.currency)}
                      </span>
                    </p>
                  ) : null}
                  <p className="mono-tag mt-1">
                    deadline {formatDateTime(campaign.data.deadline)}
                  </p>
                </div>
              </>
            ) : null}

            <div className="mt-8 border-t border-border pt-6">
              <p className="mono-tag">
                GET /api/crowdfunding/campaigns/{campaignId}/contributions
              </p>
              <h3 className="mt-1.5 text-base font-semibold">Contribution history</h3>
              {contributions.isLoading ? (
                <Skeleton className="mt-4 h-20 w-full" />
              ) : (contributions.data ?? []).length === 0 ? (
                <p className="mt-3 text-sm text-muted-foreground">No contributions yet.</p>
              ) : (
                <ul className="mt-4 divide-y divide-border">
                  {(contributions.data ?? []).map((c) => (
                    <li key={c.id} className="flex items-center justify-between gap-3 py-3">
                      <div>
                        <p className="font-mono text-sm">{formatAmount(c.amount, c.currency)}</p>
                        <p className="mono-tag">
                          {c.anonymous ? "anonymous" : `contributor #${c.contributorId ?? "—"}`} ·{" "}
                          {c.status}
                        </p>
                      </div>
                      <div className="text-right">
                        <p className="text-xs text-muted-foreground">{c.note || "—"}</p>
                        <p className="mono-tag">{formatDateTime(c.contributedAt)}</p>
                      </div>
                    </li>
                  ))}
                </ul>
              )}
            </div>
          </div>

          <div className="panel h-fit p-6">
            <p className="mono-tag">POST /campaigns/{campaignId}/contributions</p>
            <h2 className="mt-1.5 flex items-center gap-2 text-lg font-semibold">
              <HeartHandshake className="size-4 text-primary" /> Contribute
            </h2>

            <div className="mt-4 space-y-4">
              <div className="space-y-2">
                <Label className="text-xs text-muted-foreground">Amount</Label>
                <RadioGroup value={preset} onValueChange={setPreset} className="grid grid-cols-2">
                  {PRESETS.map((p) => (
                    <Label
                      key={p}
                      className="flex cursor-pointer items-center gap-2 rounded-md border border-border px-3 py-2 text-sm"
                    >
                      <RadioGroupItem value={p} />
                      {p === "custom" ? "Custom" : p}
                    </Label>
                  ))}
                </RadioGroup>
                {preset === "custom" ? (
                  <Input
                    type="number"
                    step="0.01"
                    value={customAmount}
                    onChange={(e) => setCustomAmount(e.target.value)}
                    placeholder="Enter amount"
                  />
                ) : null}
              </div>

              <div className="space-y-1.5">
                <Label className="text-xs text-muted-foreground">Contributor ID</Label>
                <Input
                  value={contributorId}
                  onChange={(e) => setContributorId(e.target.value)}
                  type="number"
                />
              </div>

              <div className="space-y-1.5">
                <Label className="text-xs text-muted-foreground">Note</Label>
                <Textarea rows={2} value={note} onChange={(e) => setNote(e.target.value)} />
              </div>

              <div className="flex items-center justify-between rounded-md border border-border px-3 py-2">
                <Label className="text-sm">Contribute anonymously</Label>
                <Switch checked={anonymous} onCheckedChange={setAnonymous} />
              </div>

              {invalid ? (
                <p className="text-xs text-status-failed">
                  Amount must be greater than 0 and cannot exceed the remaining campaign amount.
                </p>
              ) : null}

              <Button
                className="w-full"
                disabled={invalid || contribute.isPending}
                onClick={() => contribute.mutate()}
              >
                {contribute.isPending ? "Submitting…" : "Contribute"}
              </Button>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
