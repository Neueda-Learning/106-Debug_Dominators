# ---------- Build Stage ----------
FROM node:22-alpine AS builder

WORKDIR /app

# Pass API URL
ARG VITE_API_BASE_URL
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}

# Copy package files
COPY package*.json ./

# Install dependencies (including TanStack Start CLI)
RUN npm install
RUN npm install @tanstack/start

# Copy the rest of the frontend code
COPY . .

# Build static site using TanStack Start CLI
RUN npx tsr build --static

# ---------- Runtime Stage ----------
FROM nginx:alpine

# Clean default Nginx HTML directory
RUN rm -rf /usr/share/nginx/html/*

# Copy static build output
COPY --from=builder /app/.output/public /usr/share/nginx/html

EXPOSE 80
