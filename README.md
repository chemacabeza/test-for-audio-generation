# AI Voice Studio — Text-to-Speech Generator

A full-stack application that converts text to natural-sounding speech using the [OpenAI TTS API](https://platform.openai.com/docs/guides/text-to-speech).

- **Backend**: Java 21 · Spring Boot 3 · Maven
- **Frontend**: React 18 · Vite 5
- **Communication**: REST (JSON request → binary audio response)
- **Security**: OpenAI API key lives **only** on the backend — never exposed to the browser

---

## Architecture Overview

```
Browser (React)
     │  POST /api/tts/generate  { text, voice, instructions, model }
     ▼
Spring Boot (port 8080)
     │  POST https://api.openai.com/v1/audio/speech
     ▼
OpenAI TTS API
     │  MP3 binary
     ▼
Spring Boot → byte[] → HTTP 200 audio/mpeg
     │
     ▼
Browser → <audio> player
```

```
backend/
├── pom.xml
└── src/main/
    ├── java/com/example/tts/
    │   ├── TtsApplication.java
    │   ├── client/
    │   │   └── OpenAiTtsClient.java      ← OpenAI integration only
    │   ├── config/
    │   │   ├── CorsConfig.java
    │   │   ├── OpenAiProperties.java
    │   │   └── WebClientConfig.java
    │   ├── controller/
    │   │   └── TtsController.java        ← POST /api/tts/generate
    │   ├── dto/
    │   │   ├── ErrorResponse.java
    │   │   └── TtsRequest.java
    │   ├── exception/
    │   │   ├── GlobalExceptionHandler.java
    │   │   └── OpenAiException.java
    │   └── service/
    │       └── TtsService.java
    └── resources/
        └── application.yml

frontend/
├── index.html
├── package.json
├── vite.config.js
└── src/
    ├── App.jsx
    ├── App.module.css
    ├── index.css
    ├── main.jsx
    ├── components/
    │   ├── AudioPlayer/
    │   │   ├── AudioPlayer.jsx
    │   │   └── AudioPlayer.module.css
    │   ├── ErrorAlert/
    │   │   ├── ErrorAlert.jsx
    │   │   └── ErrorAlert.module.css
    │   ├── LoadingSpinner/
    │   │   ├── LoadingSpinner.jsx
    │   │   └── LoadingSpinner.module.css
    │   └── TtsForm/
    │       ├── TtsForm.jsx
    │       └── TtsForm.module.css
    └── services/
        └── ttsApi.js
```

---

## 🐳 Running with Docker (recommended)

The easiest way to run the app — **no Java or Node.js required on the host.**  
A convenience script `start.sh` at the root handles everything for you.

**Works on:** macOS (Apple Silicon M1/M2/M3 + Intel) · Ubuntu Linux · any Docker-capable host.

### Prerequisites

| Tool           | Version |
|----------------|---------|
| Docker         | 24+     |
| Docker Compose | 2.20+   |

---

### `start.sh` — command reference

```
Usage: ./start.sh <command>

  start    Build images and start all containers (detached / background)
  stop     Stop and remove containers
  restart  Stop then start
  logs     Tail live logs from all containers
  status   Show running container status
```

---

### First-time setup

```bash
# 1 — Copy the env example and set your real OpenAI API key
cp backend/.env.example backend/.env
# Open backend/.env and set:
#   OPENAI_API_KEY=sk-proj-...

# 2 — Start the application
./start.sh start
```

The script will:
- ✅ Check Docker is installed
- ✅ Validate the API key is not still a placeholder
- 🏗️ Build both Docker images (Spring Boot + Nginx/React)
- 🚀 Start all containers in the background

| Service  | URL                     | Notes                           |
|----------|-------------------------|---------------------------------|
| Frontend | http://localhost        | React UI via Nginx (port 80)    |
| Backend  | http://localhost:8080   | Spring Boot API (direct access) |

---

### Day-to-day commands

```bash
# Start the app
./start.sh start

# Stop the app
./start.sh stop

# Restart (e.g. after changing application.yml)
./start.sh restart

# Watch live logs from all containers
./start.sh logs

# Check what is running
./start.sh status
```

---

### Health check

```bash
curl http://localhost:8080/actuator/health
# {"status":"UP"}
```

---

### Advanced — raw Docker Compose commands

```bash
# Rebuild only the backend (after Java changes)
docker compose up --build backend -d

# Rebuild only the frontend (after React changes)
docker compose up --build frontend -d
```


---

## Running Locally Without Docker

### Prerequisites

| Tool    | Version  |
|---------|----------|
| Java    | 21+      |
| Maven   | 3.9+     |
| Node.js | 18+      |
| npm     | 9+       |

---

## Local Setup

### 1 — Clone the repo

```bash
git clone <repo-url>
cd test-for-audio-generation.git
```

### 2 — Configure the backend secret

```bash
cd backend
cp .env.example .env          # Copy the example file
# Open .env and set your real OpenAI API key:
#   OPENAI_API_KEY=sk-proj-...
```

> ⚠️ **Never commit `.env`** – it is already in `.gitignore`.

### 3 — Run the backend

```bash
# Load the env file and start Spring Boot
cd backend
export $(grep -v '^#' .env | xargs)
./mvnw spring-boot:run
```

The backend starts on **http://localhost:8080**.

> **Windows PowerShell alternative:**
> ```powershell
> $env:OPENAI_API_KEY="sk-proj-YOUR_KEY"
> ./mvnw.cmd spring-boot:run
> ```

### 4 — Run the frontend (separate terminal)

```bash
cd frontend
npm install
npm run dev
```

The frontend starts on **http://localhost:5173**.

---

## Health Check

```bash
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}
```

---

## Example API Request (curl)

```bash
curl -X POST http://localhost:8080/api/tts/generate \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Hello! This is a test of the OpenAI text-to-speech system.",
    "voice": "nova",
    "instructions": "Speak warmly and clearly, with gentle enthusiasm.",
    "model": "gpt-4o-mini-tts",
    "responseFormat": "mp3"
  }' \
  --output test-speech.mp3

# Play the file (macOS)
open test-speech.mp3

# Play the file (Linux)
xdg-open test-speech.mp3
```

---

## Available Voices

| Voice   | Character                     |
|---------|-------------------------------|
| alloy   | Neutral & balanced            |
| ash     | Clear & composed              |
| ballad  | Warm & emotive                |
| coral   | Friendly & bright             |
| echo    | Smooth & expressive           |
| fable   | Storytelling                  |
| nova    | Energetic & lively            |
| onyx    | Deep & authoritative          |
| sage    | Calm & thoughtful             |
| shimmer | Airy & uplifting              |
| verse   | Versatile & natural           |

---

## Configuration Reference

`backend/src/main/resources/application.yml`:

| Property                    | Default         | Description                              |
|-----------------------------|-----------------|------------------------------------------|
| `openai.api-key`            | *(env var)*     | From `OPENAI_API_KEY` — never hardcode  |
| `openai.model`              | gpt-4o-mini-tts | TTS model                                |
| `openai.default-voice`      | alloy           | Fallback when frontend omits voice       |
| `openai.response-format`    | mp3             | Audio format                             |
| `openai.timeout-seconds`    | 60              | HTTP timeout for OpenAI calls            |

---

## Security Notes

- The OpenAI API key is read from the `OPENAI_API_KEY` **environment variable** only
- It is injected into Spring via `${OPENAI_API_KEY}` in `application.yml`
- The React frontend **never** sees or uses the key
- `.env` files are in `.gitignore` — only `.env.example` (with placeholders) is committed
- CORS is restricted to `http://localhost:5173` in development; update `CorsConfig.java` for production

---

## Production Deployment Notes

1. Set `OPENAI_API_KEY` as a secret/environment variable in your deployment platform (e.g. Railway, Fly.io, AWS ECS)
2. Build the backend JAR: `./mvnw clean package -DskipTests`
3. Run: `java -jar target/tts-backend-0.0.1-SNAPSHOT.jar`
4. Build the frontend: `npm run build` → deploy `dist/` to a CDN or static host
5. Update `CorsConfig.java` with your production frontend domain
6. Set `VITE_API_BASE_URL` to your production backend URL in the frontend `.env`
