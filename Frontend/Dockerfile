# ---------- Build Stage ----------
FROM node:22-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# ---------- Runtime Stage ----------
FROM nginx:alpine

RUN rm -rf /usr/share/nginx/html/*

# Nitro / TanStack Start output folder
COPY --from=builder /app/.output/public /usr/share/nginx/html

EXPOSE 80
