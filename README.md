# BIS Intelligent Assistant (Bureau of Indian Standards - AI Companion)

A modern, full-stack regulatory and compliance intelligence platform for the **Bureau of Indian Standards (BIS)**, Ministry of Consumer Affairs, Food & Public Distribution, Government of India.

The platform combines **Retrieval-Augmented Generation (RAG)**, an **ordered multi-model Google Gemini fallback architecture**, and authenticated conversation persistence to provide guidance on Indian Standards (IS), mandatory Quality Control Orders (QCOs), testing protocols, certification schemes (Scheme-I ISI Mark, Scheme-II CRS, FMCS, Hallmarking HUID), and consumer protection services.

---

## 🚀 Live Demo

| Component | Provider | Live URL |
| :--- | :--- | :--- |
| **Frontend Web App** | Vercel | [https://bis-ai-assistant-nine.vercel.app](https://bis-ai-assistant-nine.vercel.app) |
| **Backend REST API** | Render | [https://bis-ai-assistant-backend.onrender.com](https://bis-ai-assistant-backend.onrender.com) |
| **System Health Check** | Render | [https://bis-ai-assistant-backend.onrender.com/api/health](https://bis-ai-assistant-backend.onrender.com/api/health) |

---

## ✨ Features

### 🧠 AI & RAG Intelligence
- **Domain-Grounded Question Answering**: Chat queries are answered using BIS standards and knowledge-base documents, testing requirements, and regulatory gazette data.
- **RAG Knowledge Pipeline**: Dynamic retrieval of relevant `KnowledgeChunk` records from PostgreSQL based on user queries.
- **PDF Document Ingestion**: Upload standard PDFs with automated text extraction, section identification, and chunking via Apache PDFBox.
- **Source Citations**: Answers include structured references (`[Source 1]`, `[Source 2]`) linking back to standard numbers, clauses, and portal URLs.
- **Multi-Model Gemini Fallback**: Configurable ordered sequential fallback cascade across 6 Gemini models to ensure high availability.

### 🔐 User & Conversation Management
- **JWT Authentication**: Stateless authentication with BCrypt password hashing and bearer token verification.
- **User-Scoped Conversations**: Authenticated users access their own private chat sessions and history.
- **Message Persistence**: Multi-turn dialogue history is persisted in PostgreSQL.

### 🏛️ BIS Domain Catalog & Services
- **Indian Standards Catalog**: Searchable repository of Indian Standards (IS codes, titles, departments, scopes).
- **Certification Schemes**: Guidance on Scheme-I (ISI Mark), Scheme-II (Compulsory Registration Scheme - CRS), FMCS, and Hallmarking.
- **Testing Laboratories**: Directory of Central, Regional, Branch, and recognized partner testing laboratories.
- **Regulatory Updates & QCOs**: Tracking mandatory Quality Control Orders and Gazette notifications.
- **Product Catalog**: Standard mapping for consumer and industrial products.
- **Gold & Silver Hallmarking**: 6-digit HUID guidelines, purity grades, and assay center rules.

---

## 🏗️ Architecture

```
                                  SYSTEM ARCHITECTURE

   ┌─────────────────────────────────────────────────────────────┐
   │                    Web Browser Client                       │
   │               (React 18 + Vite UI on Vercel)                │
   │           https://bis-ai-assistant-nine.vercel.app          │
   └──────────────────────────────┬──────────────────────────────┘
                                  │
                                  │ HTTPS REST / Bearer JWT
                                  ▼
   ┌─────────────────────────────────────────────────────────────┐
   │                  Spring Boot 3.3.4 Backend                  │
   │                  (Deployed on Render)                       │
   │         https://bis-ai-assistant-backend.onrender.com       │
   │                                                             │
   │  ├── Security & JWT Filter (Stateless Sessions)             │
   │  ├── Auth & User Controller (/api/auth)                     │
   │  ├── Conversation Controller (/api/conversations)           │
   │  ├── BIS Catalog & Standard Controller (/api/standards)     │
   │  ├── PDF Knowledge Ingestion Service (/api/knowledge/upload)│
   │  ├── RAG Knowledge Retrieval Service                        │
   │  └── Gemini Model Fallback Router                           │
   └──────────────┬───────────────────────────────┬──────────────┘
                  │                               │
   Spring Data JPA│                               │ HTTPS / JSON Payload
   Hibernate (DDL)│                               │ (Reused Grounded Prompt)
                  ▼                               ▼
   ┌──────────────────────────────┐ ┌─────────────────────────────┐
   │        PostgreSQL 18         │ │    Google Gemini API        │
   │     (Render PostgreSQL)      │ │ Ordered Fallback Cascade:   │
   │                              │ │                             │
   │ • Users & Profiles           │ │ 1. gemini-3.6-flash (Primary│
   │ • Conversations & Messages   │ │       ↓ failure (503/429)   │
   │ • RAG Knowledge Chunks       │ │ 2. gemini-3.8-flash         │
   │ • Standards Catalog (IS)     │ │       ↓ failure (503/429)   │
   │ • Services & Schemes         │ │ 3. gemini-3.7-flash         │
   │ • Laboratories & Updates     │ │       ↓ failure (503/429)   │
   │ • Products & Hallmarking     │ │ 4. gemini-3-flash-preview   │
   │                              │ │       ↓ failure (503/429)   │
   │                              │ │ 5. gemini-3.5-flash-lite    │
   │                              │ │       ↓ failure (503/429)   │
   │                              │ │ 6. gemini-2.5-flash-lite    │
   └──────────────────────────────┘ └─────────────────────────────┘
```

---

## 🧠 AI + RAG Pipeline

```
  BIS PDF Documents (e.g. IS 4151, IS 17017)
              │
              ▼
   PDF Ingestion & Text Extraction (Apache PDFBox 3.0)
              │
              ▼
   Section, Clause & Page-Aware Text Chunking
              │
              ▼
   PostgreSQL `knowledge_chunks` Persistence
              │
              │ (User asks compliance question)
              ▼
   Knowledge Retrieval (Keyword & Section Matching) ◄── [Performed ONCE]
              │
              ▼
   Construct Grounded System Instruction & Context  ◄── [Built ONCE]
              │
              ▼
   Gemini Model Router (Ordered Fallback Cascade)
      ├── 1. Try gemini-3.6-flash
      │        ├── Success ──► Return generated answer
      │        └── Retryable error (503 / 429 / 502 / timeout)
      │                 │
      │                 ▼
      ├── 2. Try gemini-3.8-flash (Reuses same prompt)
      │        ├── Success ──► Return generated answer
      │        └── Retryable error
      │                 │
      │                 ▼
      └── 3. Cascade to remaining configured models in sequence
              │
              ▼
   Extract & Validate `[Source N]` Citations
              │
              ▼
   Return Structured Answer with BIS Source References to Frontend
```

### Fallback Strategy Highlights:
- **Single Context Retrieval**: RAG retrieval runs **once** per request. The constructed prompt is reused across fallback attempts without repeated database reads.
- **Fail-Fast Safety**: Authentication/permission errors (`HTTP 401`, `HTTP 403`) or malformed payloads (`HTTP 400`) fail fast immediately without wasting calls on subsequent models.
- **Transient Error Failover**: Overloaded states (`HTTP 503`), rate limits (`HTTP 429`), gateway issues (`HTTP 502/504/500`), model not found (`HTTP 404`), and connection/read timeouts trigger automatic failover to the next candidate model in the configured fallback list.
- **No Retry Storms**: Each model is attempted at most once per request.
- **Automated Test Coverage**: The ordered multi-model fallback logic and failover scenarios are covered by automated unit and integration tests.

---

## 🔐 Authentication & Security

```
User (Login / Signup)
        │
        ▼
POST /api/auth/login
        │
        ▼
Spring Security (BCrypt Password Verification)
        │
        ▼
Issue Signed JWT Token (JJWT 0.12.6)
        │
        ▼
Frontend stores token & attaches `Authorization: Bearer <JWT_TOKEN>`
        │
        ▼
JwtAuthenticationFilter verifies token on protected endpoints
        │
        ▼
User-Specific Conversation Isolation & Message Persistence
```

### Security Highlights:
- **Server-Side Credentials**: `GEMINI_API_KEY`, database credentials, and JWT signing keys exist only in the server-side environment.
- **Frontend Safe**: No secret keys, API credentials, or database connection strings are bundled in client JavaScript.
- **API Key Redaction**: Low-level HTTP transport and logger sanitize error messages, replacing any credential strings with `[REDACTED_API_KEY]`.
- **CORS Restricted**: Allowed origins explicitly map to the production Vercel domain and verified local development ports.
- **User Scoping**: Chat conversations and message histories are strictly scoped to the authenticated user ID.

---

## 🛠️ Tech Stack

### Frontend
- **Framework**: React 18.3
- **Build Tool**: Vite 6.2
- **Icons**: Lucide React
- **Styling**: Vanilla CSS (Custom Design System with responsive themes)

### Backend
- **Language**: Java 17+ (production Docker runtime: Eclipse Temurin Java 21)
- **Framework**: Spring Boot 3.3.4
- **Security**: Spring Security + JJWT 0.12.6 (Stateless JWT Auth)
- **Data Access**: Spring Data JPA / Hibernate
- **PDF Processing**: Apache PDFBox 3.0.3
- **Build Tool**: Maven (`mvnw`)

### Database
- **Engine**: PostgreSQL 18 (Render PostgreSQL)
- **Testing**: H2 In-Memory Database (isolated unit/integration testing)

### AI Integration
- **API**: Google Gemini REST API (`generateContent`)
- **Transport**: Spring `RestTemplate` with 10s connect / 30s read timeouts
- **Model Architecture**: 6-model ordered fallback configuration

### Hosting & Deployment
- **Frontend**: Vercel
- **Backend & Database**: Render

---

## 📁 Project Structure

```text
BIS-ai-assistant/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/bis/assistant/
│   │   │   │   ├── config/            # SecurityConfig, CorsConfig, DatabaseMigrationService
│   │   │   │   ├── controller/        # Auth, Chat, Catalog, Knowledge, Standards
│   │   │   │   ├── dto/               # Request, Response, and ModelResult DTOs
│   │   │   │   ├── model/             # 13 JPA Relational Entities
│   │   │   │   ├── repository/        # Spring Data JPA Repositories
│   │   │   │   ├── security/          # JwtTokenProvider, JwtAuthenticationFilter
│   │   │   │   └── service/           # GeminiService, GeminiModelRouter, GeminiClient, RAG
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/                      # 106 Backend Unit and Integration Tests
│   ├── pom.xml
│   ├── Dockerfile
│   └── setup_database.sql
├── frontend/
│   ├── src/
│   │   ├── components/                # Navigation, ChatInterface, Modals, Auth
│   │   ├── context/                   # AppContext (Auth, Chat & Theme State)
│   │   ├── services/                  # apiService, aiAssistantService
│   │   ├── App.jsx
│   │   ├── index.css
│   │   └── main.jsx
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
├── .env.example
└── README.md
```

---

## 🗄️ Database Entities (PostgreSQL)

The system uses 13 JPA entities mapped with relational constraints:

1. **`User`**: User account credentials, BCrypt hashed password, email, and registration metadata.
2. **`Conversation`**: User-scoped conversation thread with session identifiers and titles.
3. **`Message`**: Chat messages (`user` or `model` role, message body, timestamps).
4. **`KnowledgeChunk`**: Segmented text chunks from BIS standard PDFs with document, title, section, clause, and page metadata.
5. **`Standard`**: Indian Standards catalog (IS code, title, department, status, scope, year).
6. **`StandardDocument`**: Associated downloadable standard documentation.
7. **`TestingRequirement`**: Specific laboratory test methods, tolerances, and compliance parameters.
8. **`BISService`**: Core departmental services (Conformity Assessment, Hallmarking, Training).
9. **`CertificationScheme`**: Information on ISI Mark, CRS, FMCS, and Hallmarking schemes.
10. **`Laboratory`**: BIS Central, Regional, Branch, and accredited partner laboratories.
11. **`BISUpdate`**: Gazette notifications, amendments, and mandatory Quality Control Orders (QCOs).
12. **`Product`**: Product catalog mapped to applicable mandatory and voluntary Indian Standards.
13. **`HallmarkingInformation`**: Precious metal hallmarking guidelines, assay centers, and HUID rules.

---

## 📡 REST API Reference

| Endpoint | Method | Auth Required | Description |
| :--- | :---: | :---: | :--- |
| `/api/health` | `GET` | No | System health, DB connection, active Gemini model check |
| `/api/status` | `GET` | No | Basic service status check |
| `/api/gemini/diagnostic` | `GET` | No | Diagnostic connection test reporting safe endpoint status |
| `/api/test-gemini` | `POST` | No | Executes a minimal ping connectivity test to Gemini |
| `/api/auth/signup` | `POST` | No | Register a new user account and receive a JWT token |
| `/api/auth/login` | `POST` | No | Authenticate credentials and receive a JWT token |
| `/api/auth/me` | `GET` | **Yes** | Get profile of the currently authenticated user |
| `/api/chat` | `POST` | **Yes** | Submit a compliance question, execute RAG + Gemini fallback, return answer |
| `/api/conversations` | `GET` | **Yes** | Retrieve all conversation sessions for the authenticated user |
| `/api/conversations/{sessionId}` | `GET` | **Yes** | Retrieve a single conversation with message history |
| `/api/conversations/{sessionId}` | `DELETE` | **Yes** | Delete a conversation session |
| `/api/standards` | `GET` | **Yes** | Retrieve all Indian Standards |
| `/api/standards/{id}` | `GET` | **Yes** | Retrieve an Indian Standard by database ID |
| `/api/standards/code/{code}` | `GET` | **Yes** | Retrieve an Indian Standard by IS code (e.g. `IS 4151:2015`) |
| `/api/standards/search?query=...` | `GET` | **Yes** | Search standards by title, number, department, or scope |
| `/api/services` | `GET` | **Yes** | Retrieve BIS services catalog |
| `/api/certification-schemes` | `GET` | **Yes** | Retrieve certification schemes (ISI, CRS, FMCS) |
| `/api/laboratories` | `GET` | **Yes** | Retrieve laboratory directory |
| `/api/updates` | `GET` | **Yes** | Retrieve Gazette Quality Control Order updates |
| `/api/products` | `GET` | **Yes** | Retrieve product catalog with applicable standards |
| `/api/hallmarking` | `GET` | **Yes** | Retrieve gold and silver hallmarking information |
| `/api/knowledge/upload` | `POST` | **Yes** | Ingest a BIS PDF standard into PostgreSQL KnowledgeChunks |

---

## 📄 BIS Knowledge Ingestion (RAG)

The backend provides a multipart ingestion endpoint (`POST /api/knowledge/upload`) that parses PDF documents using Apache PDFBox, splits content into clean searchable chunks, and saves them to `knowledge_chunks`.

### Multipart Parameters:
- `file`: PDF binary file (*required*)
- `document`: Standard code, e.g. `IS 17017:2026` (*optional, defaults to filename*)
- `title`: Title of the standard (*optional*)
- `section`: Section or category (*optional*)
- `sourceUrl`: Official BIS / Gazette URL (*optional*)
- `overwrite`: `true`/`false` (*optional, default `true`*)

### Example Upload via cURL:
```bash
curl -X POST "https://bis-ai-assistant-backend.onrender.com/api/knowledge/upload" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -F "file=@IS_4151_Helmets.pdf" \
  -F "document=IS 4151:2015" \
  -F "title=Protective Helmets for Motorcycle Riders" \
  -F "section=Section 7 Impact Attenuation" \
  -F "sourceUrl=https://manakonline.in" \
  -F "overwrite=true"
```

---

## ⚙️ Environment Variables

> [!IMPORTANT]
> Values shown below are **placeholders only**. Supply actual credentials through server environment variables or server configuration.
> Never place `GEMINI_API_KEY`, database credentials, JWT signing secrets, or other server-side secrets in frontend `VITE_*` variables. Vite exposes `VITE_*` variables to the client bundle.

| Variable | Scope | Description | Example / Placeholder Value |
| :--- | :--- | :--- | :--- |
| `DB_URL` | Backend | PostgreSQL JDBC Connection URL | `jdbc:postgresql://localhost:5432/bis_assistant` |
| `DB_USERNAME` | Backend | Database username | `postgres` |
| `DB_PASSWORD` | Backend | Database password | `your_postgres_password` |
| `GEMINI_API_KEY` | Backend | Google Gemini API Key | `your_gemini_api_key` |
| `GEMINI_MODELS` | Backend | Ordered comma-separated fallback model list | `gemini-3.6-flash,gemini-3.8-flash,gemini-3.7-flash,gemini-3-flash-preview,gemini-3.5-flash-lite,gemini-2.5-flash-lite` |
| `GEMINI_MODEL` | Backend | Legacy single-model fallback setting | `gemini-3.6-flash` |
| `JWT_SECRET` | Backend | JWT token signing key | `your_jwt_secret_key_at_least_32_bytes_long` |
| `SERVER_PORT` | Backend | Backend server port | `8080` |
| `VITE_API_BASE_URL`| Frontend | Frontend API target URL | `https://bis-ai-assistant-backend.onrender.com` |

---

## 💻 Local Development

### 1. Prerequisites
- **Java JDK 17+** (runtime supported on Java 17 - 21)
- **Node.js v18+ & npm**
- **PostgreSQL 18** running locally on port `5432`

### 2. Backend Setup
1. Create the database:
   ```sql
   CREATE DATABASE bis_assistant;
   ```
2. Navigate to `backend/` and set local environment variables (PowerShell):
   ```powershell
   cd backend
   $env:DB_URL="jdbc:postgresql://localhost:5432/bis_assistant"
   $env:DB_USERNAME="postgres"
   $env:DB_PASSWORD="your_postgres_password"
   $env:GEMINI_API_KEY="your_gemini_api_key"
   $env:GEMINI_MODELS="gemini-3.6-flash,gemini-3.8-flash,gemini-3.7-flash,gemini-3-flash-preview,gemini-3.5-flash-lite,gemini-2.5-flash-lite"
   ```
3. Run the backend:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
   Backend starts at `http://localhost:8080`.

### 3. Frontend Setup
1. Navigate to `frontend/`:
   ```powershell
   cd frontend
   npm install
   ```
2. Start the Vite development server:
   ```powershell
   npm run dev
   ```
3. Open `http://localhost:5173` in your browser.

---

## ☁️ Deployment

- **Frontend on Vercel**: Connects directly to the GitHub repository with build command `npm run build` and output directory `dist`. `VITE_API_BASE_URL` is configured in Vercel project environment variables pointing to Render.
- **Backend on Render**: Deployed as a web service using Maven build command `./mvnw clean package -DskipTests` and start command `java -jar target/bis-intelligent-assistant-backend-1.0.0.jar`.
- **Database on Render**: Managed PostgreSQL 18 instance with SSL enabled.
- **AI Credentials**: `GEMINI_API_KEY` and `GEMINI_MODELS` are securely stored in Render environment settings and never exposed to the client.

---

## 🧪 Testing

The backend includes a comprehensive unit and integration test suite:

- **Total Backend Tests**: **106**
- **Failures / Errors**: **0**
- **Coverage Highlights**:
  - Multi-model fallback sequence execution (Models 1 through 6).
  - High demand (`HTTP 503`), rate limit (`HTTP 429`), and connection timeout failovers.
  - Fail-fast authentication validation (`HTTP 401/403` and `HTTP 400`).
  - Single RAG retrieval context reuse verification across model attempts.
  - Parsing and boundary validation of `[Source N]` citations.
  - PDF document ingestion, text chunking, and idempotent re-ingestion.
  - JWT authentication filter, token expiration, and secure endpoints.
  - PostgreSQL idempotent schema migrations for conversation ownership.

Run the test suite:
```powershell
cd backend
.\mvnw.cmd test
```

---

## 🔒 Security & Privacy

- **Server-Side Secret Management**: Keep Google Gemini API keys, PostgreSQL credentials, and JWT secret signing keys strictly on the server side. Secrets are loaded from environment variables and never hardcoded in source files.
- **Frontend Environment Boundary**: Never place `GEMINI_API_KEY`, database credentials, JWT signing secrets, or other server-side secrets in frontend `VITE_*` variables. Vite exposes `VITE_*` variables to the client bundle.
- **Credential Placeholders**: All documentation, setup instructions, and cURL snippets use placeholder values (`your_gemini_api_key`, `your_postgres_password`, `<JWT_TOKEN>`).
- **Error Logging Sanitization**: Low-level HTTP transport and application loggers sanitize API keys and sensitive tokens before writing to log outputs.
- **Data Privacy & Scoping**: User conversations and messages are isolated and accessible only by the owning authenticated user.
- **Git History Hygiene**: Never commit `.env` files or real credentials to Git. If a secret is accidentally committed, deleting it from the latest file is not sufficient because it may remain in Git history. Revoke/rotate the exposed credential immediately and clean the repository history when necessary.

---

## 🔮 Future Scope

- **Multilingual RAG Expansion**: Native embeddings and grounding for Indian official languages (Hindi, Tamil, Telugu, Bengali, Marathi, Gujarati).
- **Automated Gazette Crawling**: Continuous webhook ingestion of newly published Quality Control Orders directly from the official BIS and Ministry portals.
- **Offline Edge Mode**: Local lightweight model quantization for offline field inspections by BIS officers in remote areas.

---

## 📜 License

This project is developed for educational, compliance intelligence, and innovation purposes in alignment with public standards published by the Bureau of Indian Standards (BIS), Government of India.
