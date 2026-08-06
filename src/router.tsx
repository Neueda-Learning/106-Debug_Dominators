import { QueryClient } from "@tanstack/react-query";
import { createRouter } from "@tanstack/react-router";
import { routeTree } from "./routeTree.gen";

// Extend the router's history state so a payment identifier can travel between
// routes without ever being written into the URL (see routes/payments.details.tsx).
declare module "@tanstack/history" {
  interface HistoryState {
    paymentId?: number | string;
  }
}

export const getRouter = () => {
  const queryClient = new QueryClient();

  const router = createRouter({
    routeTree,
    context: { queryClient },
    scrollRestoration: true,
    defaultPreloadStaleTime: 0,
  });

  return router;
};
