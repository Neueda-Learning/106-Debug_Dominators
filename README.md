# Frontend Foundations Kit

Build me a frontend react page and all the backend details are in the zip like the schema .sql and how the api structure should also be there

This project was built with [Lovable](https://lovable.dev).

## Build with Lovable

Continue developing this project in the [Lovable editor](https://lovable.dev/projects/ffb15abc-dd91-4a46-a82e-006b9e457ff7).

- **Ship faster**: describe what you want to build and Lovable handles the code.
- **Stay in sync**: every change made in Lovable is committed straight to this repository.
- **Full ownership**: this code is yours. Push to `main` on GitHub and your changes sync back into Lovable, ready for your next prompt.

## Development

Prefer working locally? You need Node.js and npm — [install with nvm](https://github.com/nvm-sh/nvm#installing-and-updating).

```sh
git clone <this-repository-url>
cd <repository-name>
npm i
npm run dev
```

## Jenkins, Docker, and EC2 Deployment

The startup flow now supports environment-based port and origin configuration to avoid collisions across parallel jobs/containers.

### Environment Variables

- `BACKEND_PORT` (default: `8082`)
- `FRONTEND_PORT` (default: `5173`)
- `FRONTEND_HOST` (default: `127.0.0.1`, use `0.0.0.0` on EC2)
- `API_BASE_URL` (default: `http://localhost:${BACKEND_PORT}`)
- `SERVER_PORT` (Spring Boot port override, default: `8082`)
- `APP_CORS_ALLOWED_ORIGINS` mapped to `app.cors.allowed-origins` (comma-separated list)

### Example: Local with custom ports

```sh
BACKEND_PORT=18082 FRONTEND_PORT=15173 API_BASE_URL=http://localhost:18082 ./start.sh
```

### Example: EC2

```sh
BACKEND_PORT=8082 \
FRONTEND_PORT=8081 \
FRONTEND_HOST=0.0.0.0 \
API_BASE_URL=http://<EC2_PUBLIC_DNS_OR_IP>:8082 \
APP_CORS_ALLOWED_ORIGINS=http://<EC2_PUBLIC_DNS_OR_IP>:8081 \
./start.sh
```
