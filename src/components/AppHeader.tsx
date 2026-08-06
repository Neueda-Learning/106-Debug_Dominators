import { Link } from "@tanstack/react-router";
import { Moon, Sun } from "lucide-react";
import { useEffect, useState } from "react";

import { Button } from "@/components/ui/button";

type ThemeMode = "dark" | "light";

const THEME_KEY = "pp.theme";

function applyTheme(theme: ThemeMode) {
  document.documentElement.classList.toggle("dark", theme === "dark");
  document.documentElement.classList.toggle("light", theme === "light");
}

export function AppHeader() {
  const [theme, setTheme] = useState<ThemeMode>("dark");

  useEffect(() => {
    const saved = typeof window === "undefined" ? null : localStorage.getItem(THEME_KEY);
    const nextTheme: ThemeMode = saved === "light" ? "light" : "dark";
    setTheme(nextTheme);
    applyTheme(nextTheme);
  }, []);

  useEffect(() => {
    if (typeof window === "undefined") return;
    localStorage.setItem(THEME_KEY, theme);
    applyTheme(theme);
  }, [theme]);

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
        <Button
          type="button"
          variant="outline"
          size="sm"
          className="ml-auto md:ml-0"
          onClick={() => setTheme((current) => (current === "dark" ? "light" : "dark"))}
        >
          {theme === "dark" ? <Sun className="mr-1.5 size-4" /> : <Moon className="mr-1.5 size-4" />}
          {theme === "dark" ? "Light mode" : "Dark mode"}
        </Button>
      </div>
    </header>
  );
}
