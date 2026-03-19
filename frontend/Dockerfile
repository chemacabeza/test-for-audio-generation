# ─────────────────────────────────────────────────────────────────────────────
# Multi-stage build — no Node.js required on the host.
#
# Stage 1: build the React app inside Docker using Node
# Stage 2: serve the static dist/ with Nginx
#
# Works on macOS (Apple Silicon & Intel) and Ubuntu Linux.
# ─────────────────────────────────────────────────────────────────────────────

# ── Stage 1: Build ────────────────────────────────────────────────────────────
FROM node:20-alpine AS build

WORKDIR /build

# Copy dependency descriptors first so Docker can cache the npm layer
COPY package.json package-lock.json ./
RUN npm ci

# Copy source and build
COPY . .
RUN npm run build

# ── Stage 2: Runtime ──────────────────────────────────────────────────────────
FROM nginx:alpine AS runtime

# Remove the default Nginx config and replace with ours
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/app.conf

# Copy the built React app from the build stage
COPY --from=build /build/dist /usr/share/nginx/html

EXPOSE 80

# nginx starts in the foreground by default in the official image
CMD ["nginx", "-g", "daemon off;"]
