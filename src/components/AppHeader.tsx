import { Link } from "@tanstack/react-router";

export function AppHeader() {
  return (
    <header className="sticky top-0 z-30 border-b border-border/70 bg-background/85 backdrop-blur">
      <div className="mx-auto flex max-w-7xl items-center gap-6 px-6 py-4">
        <Link to="/" className="flex items-center gap-2.5">
          <span className="grid size-8 place-items-center rounded-md bg-primary/15 text-primary">
            <img src="/favicon.ico" alt="FasterPay logo" className="size-5" />
          </span>
          <span className="text-sm font-semibold tracking-tight">FasterPay</span>
        </Link>
        <nav className="ml-auto hidden items-center gap-1 text-sm md:flex">
          <Link
            to="/"
            className="rounded-md px-3 py-1.5 text-muted-foreground transition-colors hover:bg-accent hover:text-foreground"
            activeProps={{ className: "bg-accent text-foreground" }}
            activeOptions={{ exact: true }}
          >
            Payments
          </Link>
          <Link
            to="/crowdfunding"
            className="rounded-md px-3 py-1.5 text-muted-foreground transition-colors hover:bg-accent hover:text-foreground"
            activeProps={{ className: "bg-accent text-foreground" }}
          >
            Crowdfunding
          </Link>
        </nav>
      </div>
    </header>
  );
}
