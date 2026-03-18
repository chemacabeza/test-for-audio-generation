# ─────────────────────────────────────────────────────────────────────────────
# Pre-built image: the React app is built on the host before running docker build.
#   npm install --include=dev && npm run build
# Docker only packages the resulting dist/ — no Node/npm/network access required.
# ─────────────────────────────────────────────────────────────────────────────
FROM nginx:alpine AS runtime

# Remove the default Nginx config and replace with ours
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/app.conf

# Copy the pre-built React app from the host
COPY dist /usr/share/nginx/html

EXPOSE 80

# nginx starts in the foreground by default in the official image
CMD ["nginx", "-g", "daemon off;"]
