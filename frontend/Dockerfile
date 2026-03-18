# ─────────────────────────────────────────────────────────────────────────────
# Stage 1: Build the React app with Node
# ─────────────────────────────────────────────────────────────────────────────
FROM node:20-alpine AS builder

WORKDIR /app

# Install dependencies first (cached unless package.json changes).
# NODE_ENV=development ensures devDependencies (including vite) are always installed.
COPY package.json package-lock.json* ./
ENV NODE_ENV=development
RUN npm ci

# Copy source and build the production bundle.
# VITE_API_BASE_URL is intentionally left empty here — in the Docker setup
# Nginx handles the proxy from /api → backend, so no absolute URL is needed.
COPY . .
RUN npm run build

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2: Serve with Nginx (tiny Alpine image)
# ─────────────────────────────────────────────────────────────────────────────
FROM nginx:alpine AS runtime

# Remove the default Nginx config and replace with ours
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/app.conf

# Copy the built React app from the builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

# nginx starts in the foreground by default in the official image
CMD ["nginx", "-g", "daemon off;"]
