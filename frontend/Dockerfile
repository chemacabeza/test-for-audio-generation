# ─────────────────────────────────────────────────────────────────────────────
# Stage 1: Build the React app with Node
# ─────────────────────────────────────────────────────────────────────────────
FROM node:20-alpine AS builder

WORKDIR /app

# Install deps and build in one RUN so node_modules/.bin is guaranteed in PATH.
# --include=dev ensures vite (a devDependency) is always installed even if
# NODE_ENV is set to production by a parent environment.
COPY package.json package-lock.json* ./
# Copy source before installing so COPY doesn't overwrite node_modules
COPY . .
# node_modules is pre-installed on the host (npm install --include=dev) and
# copied in via COPY . . — no npm registry access needed during Docker build.
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
