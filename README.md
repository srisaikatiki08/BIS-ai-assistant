# BIS Intelligent Assistant (भारतीय मानक ब्यूरो - AI सहचर)

A modern, full-stack regulatory and compliance intelligence web application for the **Bureau of Indian Standards (BIS)**, Ministry of Consumer Affairs, Food & Public Distribution, Government of India.

The platform provides intelligent guidance on Indian Standards (IS), mandatory Quality Control Orders (QCOs), testing protocols, certification schemes (Scheme-I ISI Mark, Scheme-II CRS, FMCS, Hallmarking HUID, LRS), and consumer affairs.

---

## 🏛️ System Architecture

```
                                  FULL-STACK ARCHITECTURE
                                  
   ┌────────────────────────┐
   │      Web Browser       │
   │  (React 18 + Vite UI)  │ ◄─── http://localhost:5173
   └───────────┬────────────┘
               │
               │  • POST /api/chat (session-aware Gemini AI query)
               │  • GET  /api/standards (Indian Standards catalog)
               │  • GET  /api/services, /api/laboratories, /api/updates
               │  • GET  /api/health
               ▼
   ┌────────────────────────┐
   │   Java Spring Boot     │
   │      REST Backend      │ ◄─── http://localhost:8080
   │  (Port 8080, Maven)    │
   └─────┬────────────┬─────┘
         │            │
         │            │ 1. Injects BIS Domain Grounding & Session History
         │            │ 2. Reads GEMINI_API_KEY from Server Environment
         │            ▼
         │     ┌────────────────────────┐
         │     │    Google Gemini API   │
         │     │  (gemini-2.5-flash)    │ ◄─── https://generativelanguage.googleapis.com
         │     └────────────────────────┘
         │
         │ Spring Data JPA / Hibernate (ddl-auto=update)
         ▼
   ┌────────────────────────┐
   │     PostgreSQL 18      │
   │      Database          │ ◄─── bis_assistant (port 5432)
   │  (Entities & History)  │
   └────────────────────────┘
```

### Key Architectural Principles:
1. **Zero Secret Exposure**: The Gemini API key and database passwords are never bundled in frontend JavaScript or stored in browser `localStorage`. They are securely read by Spring Boot from server environment variables `GEMINI_API_KEY`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
2. **PostgreSQL 18 Relational Data Model**: 12 JPA Entities with proper relationships (`Standard`, `StandardDocument`, `TestingRequirement`, `CertificationScheme`, `Product`, `Laboratory`, `BISService`, `HallmarkingInformation`, `BISUpdate`, `KnowledgeChunk`, `Conversation`, `Message`).
3. **Automatic Database Seeding**: On first run, Spring Boot seeds authentic verified BIS standards, accredited laboratories, services, hallmarking rules, and gazette notifications into PostgreSQL.
4. **Authentic AI Generation**: Chat answers are generated directly by Google Gemini with BIS domain grounding. Hardcoded fake keyword responses (`if question contains earphones`) are completely eliminated; if the AI service or key is unavailable, an honest status message is returned.
5. **Session & Message Persistence**: All chat messages and conversations are persisted in PostgreSQL tables (`conversations`, `messages`).

---

## 📋 Prerequisites (Windows)

Make sure the following software is installed on your Windows machine:

1. **PostgreSQL 18**: Running as a Windows service on default port `5432`.
   - Target database: `bis_assistant`
2. **Java JDK**: Java 17 or higher (tested with Java 20.0.1)
   - Verify in PowerShell/CMD:
     ```powershell
     java -version
     ```
3. **Node.js & npm**: Node.js v18+ (tested with v24.19.0)
   - Verify in PowerShell/CMD:
     ```powershell
     node -v
     npm -v
     ```
4. **Google Gemini API Key**:
   - Obtain an API key from [Google AI Studio](https://aistudio.google.com/).

---

## ⚙️ Configuration & Environment Variables

Configure environment variables in your Windows terminal or `.env` before launching the backend:

| Variable | Description | Default Value |
| :--- | :--- | :--- |
| `DB_URL` | PostgreSQL JDBC Connection URL | `jdbc:postgresql://localhost:5432/bis_assistant` |
| `DB_USERNAME` | PostgreSQL User | `postgres` |
| `DB_PASSWORD` | PostgreSQL Password | *(your postgres password)* |
| `GEMINI_API_KEY` | Google Gemini API Key | *(required for AI chat)* |
| `GEMINI_MODEL` | Gemini Model Name | `gemini-2.5-flash` |
| `SERVER_PORT` | Backend Server Port | `8080` |

### Setting Environment Variables in PowerShell:
```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/bis_assistant"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_postgres_password"
$env:GEMINI_API_KEY="your_actual_gemini_api_key"
```

---

## 🚀 Running the Application

### 1. Initialize Database (One-time)
If the database `bis_assistant` does not already exist in PostgreSQL, create it once:
```powershell
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -c "CREATE DATABASE bis_assistant;"
```

### 2. Start the Java Spring Boot Backend
Open a PowerShell terminal in the `backend/` directory:

```powershell
cd backend
$env:DB_PASSWORD="your_postgres_password"
$env:GEMINI_API_KEY="your_actual_gemini_api_key"
.\mvnw.cmd spring-boot:run
```

Or run the compiled production JAR:
```powershell
java -jar target\bis-intelligent-assistant-backend-1.0.0.jar
```

The backend starts at `http://localhost:8080` and creates all database tables automatically.

### 3. Start the React + Vite Frontend
Open a separate PowerShell terminal in the `frontend/` directory:

```powershell
cd frontend
npm run dev
```

Open your browser at `http://localhost:5173`.

---

## 📡 REST API Documentation

| Endpoint | Method | Description |
| :--- | :---: | :--- |
| `/api/health` | `GET` | Health check reporting backend status, PostgreSQL connectivity, and Gemini key configuration |
| `/api/chat` | `POST` | Process chat question, persist to DB, and return Gemini response |
| `/api/test-gemini` | `POST` | Tests Gemini connectivity with a ping prompt |
| `/api/standards` | `GET` | Retrieve all Indian Standards from database |
| `/api/standards/{id}` | `GET` | Retrieve single Indian Standard by database ID |
| `/api/standards/search?query=...` | `GET` | Search standards by title, number, category, or scope |
| `/api/services` | `GET` | Retrieve BIS services catalog |
| `/api/certification-schemes` | `GET` | Retrieve certification schemes (ISI, CRS, FMCS, Hallmarking) |
| `/api/laboratories` | `GET` | Retrieve BIS Central, Regional, and recognized testing labs |
| `/api/updates` | `GET` | Retrieve Gazette Quality Control Order updates and notifications |
| `/api/products` | `GET` | Retrieve product catalog with applicable standards |
| `/api/hallmarking` | `GET` | Retrieve 6-digit HUID gold and silver hallmarking rules |
| `/api/conversations` | `GET` | Retrieve saved chat conversation sessions |
| `/api/conversations/{sessionId}` | `GET` | Retrieve single chat conversation session with messages |
| `/api/conversations/{sessionId}` | `DELETE` | Delete conversation session |
| `/api/knowledge/upload` | `POST` | Upload and ingest BIS PDF document into PostgreSQL KnowledgeChunks for RAG |
| `/api/status` | `GET` | Basic service status check |

---

## 📄 BIS PDF Document Ingestion API (RAG Pipeline)

The backend provides an ingestion endpoint (`POST /api/knowledge/upload`) that extracts text from uploaded PDF standards page-by-page, segments the content into RAG `KnowledgeChunk` records, and persists them to PostgreSQL so they are immediately accessible to the chatbot via `KnowledgeRetrievalService` and `GeminiService`.

### Multipart Form Parameters:
- `file`: PDF binary file (*required*)
- `document`: Standard identifier / document name, e.g. `IS 17017:2026` (*optional, defaults to file basename*)
- `title`: Standard or document title (*optional*)
- `section`: Section or category (*optional*)
- `sourceUrl`: Official BIS / Gazette URL (*optional*)
- `overwrite`: `true`/`false` (*optional, default `true` — cleanly updates previous chunks for this document*)

### Example 1: Uploading via PowerShell (Windows)
```powershell
$filePath = "C:\path\to\IS_17017_EV_Charging.pdf"
$form = @{
    file = Get-Item -Path $filePath
    document = "IS 17017:2026"
    title = "Electric Vehicle Conductive Charging System"
    section = "EV Safety & Battery Swapping"
    sourceUrl = "https://standardsbis.bsbedge.com"
    overwrite = "true"
}

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/knowledge/upload" `
    -Method Post `
    -Form $form

$response | ConvertTo-Json
```

### Example 2: Uploading via cURL
```bash
curl -X POST "http://localhost:8080/api/knowledge/upload" \
  -F "file=@/path/to/IS_4151.pdf" \
  -F "document=IS 4151:2015" \
  -F "title=Protective Helmets for Motorcycle Riders" \
  -F "section=Section 7 Impact Attenuation" \
  -F "sourceUrl=https://manakonline.in" \
  -F "overwrite=true"
```

### Example 3: Postman Setup
1. **Method**: `POST`
2. **URL**: `http://localhost:8080/api/knowledge/upload`
3. **Body**: Select `form-data`
   - Key: `file` | Type: `File` | Value: Choose your PDF file
   - Key: `document` | Type: `Text` | Value: `IS 4151:2015`
   - Key: `title` | Type: `Text` | Value: `Protective Helmets Specification`
   - Key: `sourceUrl` | Type: `Text` | Value: `https://manakonline.in`
   - Key: `overwrite` | Type: `Text` | Value: `true`
4. **Send**.

### Response Format:
```json
{
  "success": true,
  "document": "IS 17017:2026",
  "chunksCreated": 18,
  "totalPages": 5,
  "message": "Document 'IS 17017:2026' processed successfully with 18 chunks created.",
  "timestamp": "2026-09-06T12:00:00.000Z"
}
```

