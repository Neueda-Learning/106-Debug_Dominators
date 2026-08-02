import { Link } from "@tanstack/react-router";
import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { toast } from "sonner";
import { Plug, ShieldCheck } from "lucide-react";
import {
  DEFAULT_BASE_URL,
  api,
  getApiBaseUrl,
  getToken,
  setApiBaseUrl,
  setAuthUser,
  setToken,
} from "@/lib/api";


export function AppHeader() {
  const [open, setOpen] = useState(false);
  const [baseUrl, setBaseUrl] = useState(DEFAULT_BASE_URL);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [hasToken, setHasToken] = useState(false);
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    setBaseUrl(getApiBaseUrl());
    setHasToken(Boolean(getToken()));
  }, []);

  const saveBase = () => {
    setApiBaseUrl(baseUrl);
    toast.success(`API base set to ${baseUrl}`);
  };

  const login = async () => {
    setBusy(true);
    try {
      setApiBaseUrl(baseUrl);
      const res = await api.login(email, password);
      setToken(res.token);
      setAuthUser({ userId: res.userId, email: res.email, role: res.role });
      setHasToken(true);
      toast.success(`Signed in as ${res.email} (${res.role})`);

      setOpen(false);
    } catch (e) {
      toast.error(e instanceof Error ? e.message : "Login failed");
    } finally {
      setBusy(false);
    }
  };

  return (
    <header className="sticky top-0 z-30 border-b border-border/70 bg-background/85 backdrop-blur">
      <div className="mx-auto flex max-w-7xl items-center gap-6 px-6 py-4">
        <Link to="/" className="flex items-center gap-2.5">
          <span className="grid size-8 place-items-center rounded-md bg-primary/15 text-primary">
            <ShieldCheck className="size-4" />
          </span>
          <span className="text-sm font-semibold tracking-tight">Ledger Payments Console</span>
        </Link>
        <nav className="hidden items-center gap-1 text-sm md:flex">
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
        <div className="ml-auto flex items-center gap-3">
          <span className="mono-tag hidden sm:inline">{baseUrl}</span>
          <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
              <Button variant="secondary" size="sm">
                <Plug className="mr-1.5 size-3.5" />
                {hasToken ? "Connected" : "Connect API"}
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Backend connection</DialogTitle>
                <DialogDescription>
                  Point the console at your Spring Boot API and sign in to get a JWT for protected
                  endpoints.
                </DialogDescription>
              </DialogHeader>
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="baseUrl">API base URL</Label>
                  <div className="flex gap-2">
                    <Input
                      id="baseUrl"
                      value={baseUrl}
                      onChange={(e) => setBaseUrl(e.target.value)}
                      placeholder={DEFAULT_BASE_URL}
                    />
                    <Button variant="outline" onClick={saveBase}>
                      Save
                    </Button>
                  </div>
                </div>
                <div className="grid gap-3 sm:grid-cols-2">
                  <div className="space-y-2">
                    <Label htmlFor="email">Email</Label>
                    <Input
                      id="email"
                      type="email"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="password">Password</Label>
                    <Input
                      id="password"
                      type="password"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                    />
                  </div>
                </div>
                <div className="flex justify-between gap-2">
                  <Button
                    variant="ghost"
                    onClick={() => {
                      setToken(null);
                      setHasToken(false);
                      toast.success("Token cleared");
                    }}
                  >
                    Clear token
                  </Button>
                  <Button onClick={login} disabled={busy || !email || !password}>
                    {busy ? "Signing in…" : "Sign in"}
                  </Button>
                </div>
              </div>
            </DialogContent>
          </Dialog>
        </div>
      </div>
    </header>
  );
}
