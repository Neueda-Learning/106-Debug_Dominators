import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";
import type { PaymentStatus } from "@/lib/api";

const styles: Record<string, string> = {
  CREATED: "bg-status-created/15 text-status-created border-status-created/40",
  VALIDATED: "bg-status-validated/15 text-status-validated border-status-validated/40",
  PROCESSING: "bg-status-sent/15 text-status-sent border-status-sent/40",
  COMPLETED: "bg-status-completed/15 text-status-completed border-status-completed/40",
  FAILED: "bg-status-failed/15 text-status-failed border-status-failed/40",
  REFUNDED: "bg-status-validated/10 text-status-validated border-status-validated/30",
  CANCELLED: "bg-muted text-muted-foreground border-border",
};

export function StatusBadge({
  status,
  className,
}: {
  status: PaymentStatus | string;
  className?: string;
}) {
  return (
    <Badge
      variant="outline"
      className={cn(
        "font-mono text-[0.7rem] tracking-wide",
        styles[status] ?? "bg-muted text-muted-foreground border-border",
        className,
      )}
    >
      {status}
    </Badge>
  );
}
