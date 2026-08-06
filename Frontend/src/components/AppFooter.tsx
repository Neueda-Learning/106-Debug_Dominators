import { Link } from "@tanstack/react-router";

export function AppFooter() {
  const year = new Date().getFullYear();

  return (
    <footer className="border-t border-border/70 bg-background/90">
      <div className="mx-auto flex w-full max-w-7xl flex-col gap-4 px-6 py-6 text-sm text-muted-foreground md:flex-row md:items-center md:justify-between">
        <div className="space-y-1">
          <p className="font-medium text-foreground">Developed with love by Debug Dominators</p>
          <p>FasterPay Payment Processing Console</p>
        </div>

        <nav className="flex flex-wrap items-center gap-4">
          <Link
            to="/"
            className="transition-colors hover:text-foreground"
            activeProps={{ className: "text-foreground" }}
            activeOptions={{ exact: true }}
          >
            Payments
          </Link>
          <Link
            to="/crowdfunding"
            className="transition-colors hover:text-foreground"
            activeProps={{ className: "text-foreground" }}
          >
            Crowdfunding
          </Link>
          <Link
            to="/login"
            className="transition-colors hover:text-foreground"
            activeProps={{ className: "text-foreground" }}
          >
            Login
          </Link>
        </nav>

        <div className="space-y-1 text-xs md:text-right">
          <p>Privacy | Terms | Support</p>
          <p>© {year} FasterPay. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
}
