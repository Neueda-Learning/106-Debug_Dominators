# ---------- Backend Build ----------
FROM maven:3.9.9-eclipse-temurin-17 AS backend-builder
WORKDIR /workspace/Backend/payment-processing-system

COPY Backend/payment-processing-system/pom.xml ./
RUN mvn -B dependency:go-offline

COPY Backend/payment-processing-system/src ./src
RUN mvn -B package -DskipTests

# ---------- Backend Runtime ----------
FROM eclipse-temurin:17-jre-alpine AS backend
WORKDIR /app

RUN addgroup -S fasterpay && adduser -S fasterpay -G fasterpay && apk add --no-cache wget

COPY --from=backend-builder /workspace/Backend/payment-processing-system/target/*.jar /app/app.jar

RUN chown fasterpay:fasterpay /app/app.jar
USER fasterpay

EXPOSE 8082
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8082/v3/api-docs >/dev/null || exit 1

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]

# ---------- Frontend Build ----------
FROM node:20-alpine AS frontend-builder
WORKDIR /workspace/Frontend

COPY Frontend/package*.json ./
RUN npm ci --silent

COPY Frontend/ ./

ARG VITE_API_BASE_URL=http://localhost:18082
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}

RUN npm run build

# ---------- Frontend Runtime ----------
FROM node:20-alpine AS frontend
WORKDIR /app

RUN apk add --no-cache wget

COPY --from=frontend-builder /workspace/Frontend/.output ./.output

ENV NODE_ENV=production
ENV NITRO_HOST=0.0.0.0
ENV NITRO_PORT=3000
ENV PORT=3000

EXPOSE 3000
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:3000/ >/dev/null || exit 1

CMD ["node", ".output/server/index.mjs"]
