# ---------- Nginx Reverse Proxy ----------
# Serves as the single public entrypoint for the FasterPay web app.
# The frontend itself is a TanStack Start SSR app (Node/Nitro server), so
# Nginx here acts as a reverse proxy in front of it rather than serving
# static files directly (a plain static Nginx site would break SSR).
FROM nginx:alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

HEALTHCHECK --interval=15s --timeout=5s --start-period=10s --retries=3 \
  CMD wget -qO- http://localhost/ >/dev/null || exit 1

CMD ["nginx", "-g", "daemon off;"]
