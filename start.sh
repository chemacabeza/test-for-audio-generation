#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# AI Voice Studio — management script
#
# Usage:
#   ./start.sh start    Build images and start all containers in the background
#   ./start.sh stop     Stop and remove containers (keeps images & volumes)
#   ./start.sh restart  Stop then start
#   ./start.sh logs     Tail live logs from all containers
#   ./start.sh status   Show running containers
# ─────────────────────────────────────────────────────────────────────────────

set -euo pipefail

# ── Colour helpers ────────────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
CYAN='\033[0;36m'; BOLD='\033[1m'; RESET='\033[0m'

info()    { echo -e "${CYAN}[INFO]${RESET}  $*"; }
success() { echo -e "${GREEN}[OK]${RESET}    $*"; }
warn()    { echo -e "${YELLOW}[WARN]${RESET}  $*"; }
error()   { echo -e "${RED}[ERROR]${RESET} $*" >&2; }

# ── Script must run from the repo root ────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# ── Prerequisite checks ───────────────────────────────────────────────────────
check_deps() {
  if ! command -v docker &>/dev/null; then
    error "Docker is not installed. Visit https://docs.docker.com/get-docker/"
    exit 1
  fi
  if ! docker compose version &>/dev/null; then
    error "Docker Compose v2 is not available. Update Docker Desktop or install the plugin."
    exit 1
  fi
}

# ── Env file guard ────────────────────────────────────────────────────────────
check_env() {
  local env_file="backend/.env"
  if [[ ! -f "$env_file" ]]; then
    warn "backend/.env not found. Creating from example…"
    cp backend/.env.example "$env_file"
    warn "Open backend/.env and set OPENAI_API_KEY before starting."
    exit 1
  fi
  # Make sure the key doesn't still have the placeholder value
  if grep -q "YOUR_API_KEY_HERE" "$env_file"; then
    error "backend/.env still contains the placeholder key."
    error "Edit backend/.env and set a real OPENAI_API_KEY."
    exit 1
  fi
}

# ── Commands ──────────────────────────────────────────────────────────────────
# ── Open browser cross-platform ───────────────────────────────────────────────
open_browser() {
  local url="$1"
  if command -v open &>/dev/null; then
    # macOS
    open "$url"
  elif command -v xdg-open &>/dev/null; then
    # Linux (X11 / Wayland)
    xdg-open "$url"
  else
    warn "Could not detect a browser opener. Visit $url manually."
  fi
}

# ── Wait for the backend to report healthy ────────────────────────────────────
wait_for_app() {
  local health_url="http://localhost:8080/actuator/health"
  local max_wait=90   # seconds
  local interval=2
  local elapsed=0

  info "Waiting for the application to be ready…"
  while [[ $elapsed -lt $max_wait ]]; do
    if curl -sf "$health_url" | grep -q '"status":"UP"'; then
      return 0
    fi
    sleep "$interval"
    elapsed=$(( elapsed + interval ))
    printf "  ⏳ %s/%s s\r" "$elapsed" "$max_wait"
  done

  warn "Application did not become healthy within ${max_wait}s."
  warn "Check logs with: ./start.sh logs"
  return 1
}

cmd_start() {
  check_deps
  check_env
  info "Building images and starting containers…"
  docker compose up --build -d

  echo ""
  # Poll until healthy, then open the browser
  if wait_for_app; then
    echo ""
    success "Application is ready!"
    echo -e "  ${BOLD}Frontend${RESET}  →  ${GREEN}http://localhost${RESET}"
    echo -e "  ${BOLD}Backend${RESET}   →  ${GREEN}http://localhost:8080${RESET}"
    echo -e "  ${BOLD}Health${RESET}    →  ${GREEN}http://localhost:8080/actuator/health${RESET}"
    echo ""
    info "Opening browser…"
    open_browser "http://localhost"
  fi

  echo ""
  info "Run '${BOLD}./start.sh logs${RESET}' to tail container logs."
  info "Run '${BOLD}./start.sh stop${RESET}' to shut everything down."
}

cmd_stop() {
  check_deps
  info "Stopping containers…"
  docker compose down
  success "All containers stopped."
}

cmd_restart() {
  cmd_stop
  echo ""
  cmd_start
}

cmd_logs() {
  check_deps
  info "Tailing logs (Ctrl+C to exit)…"
  docker compose logs -f
}

cmd_status() {
  check_deps
  docker compose ps
}

# ── Entrypoint ────────────────────────────────────────────────────────────────
COMMAND="${1:-help}"

case "$COMMAND" in
  start)   cmd_start   ;;
  stop)    cmd_stop    ;;
  restart) cmd_restart ;;
  logs)    cmd_logs    ;;
  status)  cmd_status  ;;
  help|--help|-h)
    echo -e "${BOLD}AI Voice Studio — management script${RESET}"
    echo ""
    echo "  Usage: ./start.sh <command>"
    echo ""
    echo "  Commands:"
    echo "    start    Build and start all containers (detached)"
    echo "    stop     Stop and remove containers"
    echo "    restart  Stop then start"
    echo "    logs     Tail live logs from all containers"
    echo "    status   Show running container status"
    echo ""
    ;;
  *)
    error "Unknown command: '$COMMAND'"
    echo "Run './start.sh help' for usage."
    exit 1
    ;;
esac
