# ---------- Build Stage ----------
FROM node:22-alpine AS builder

WORKDIR /app

ARG VITE_API_BASE_URL
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}

# Copy EVERYTHING into Docker
COPY . .

# Install dependencies
RUN npm install

# Build Vite project
RUN npm run build

# ---------- Runtime Stage ----------
FROM nginx:alpine

RUN rm -rf /usr/share/nginx/html/*

# Copy Vite build output
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80
