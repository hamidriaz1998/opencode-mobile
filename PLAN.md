# OpenCode Mobile Client - Implementation Plan

## Overview
Kotlin + Jetpack Compose Android app that connects to an `opencode web` server and allows interacting with the agent via mobile phone.

## Architecture Choices
- **HTTP Client**: Retrofit + OkHttp
- **DI**: Hilt
- **State**: ViewModel + StateFlow
- **Local Storage**: DataStore Preferences (connection list)
- **JSON**: kotlinx.serialization
- **Markdown Rendering**: compose-richtext library (`com.mikepenz:multiplatform-markdown-renderer-m3`)
- **Chat Pattern**: Async send (`/session/:id/prompt_async`) + SSE streaming (`/event`)
- **SSE**: OkHttp EventSource

## Screens & Navigation

```
Home (Connections List) ─── Settings
   │
   └── Projects List
         │
         └── Sessions List (per project)
               │
               ├── Chat Screen (messages + input)
               │     └── Review Screen (diffs)
               │
               └── [New Session FAB] → Chat Screen
```

### Bottom Navigation
- **Home tab** (connections icon) - Default start
- **Settings tab** (gear icon)

### Screen Details

#### Home Screen
- Lists saved server connections (address:port, name, status indicator)
- FAB + button opens dialog to add new connection (address, password optional)
- Tap connection → connect (health check) → navigate to Projects
- Long-press/swipe → edit or delete connection
- Auto-reconnect to last active connection on app start

#### Settings Screen
- Server info: OpenCode version, connection status
- Theme toggle: dark/light/system
- About: app version, build info
- Clear all connections

#### Projects Screen
- Fetches from `GET /project`
- Display: project worktree name, full path, last updated timestamp
- Sorted by recency (`time.updated` desc)
- "Recent" button toggles asc/desc sort
- Search bar filters by name
- Tap → Sessions Screen scoped to `directory=project.worktree`

#### Sessions Screen
- Fetches from `GET /session?directory=<path>&roots=true&limit=50`
- Display: title, relative timestamp, model info
- Sort toggle by `time.updated` asc/desc
- Search by title
- FAB creates new session (`POST /session`) → navigates to Chat
- Tap → Chat Screen for that session

#### Chat Screen

**Message Layout:**
- **User messages**: Right-aligned bubble, primary color background, rounded corners
- **Assistant messages**: Full-width document flow (no bubble), like ChatGPT web/mobile

**Part Rendering (Assistant doc, top-to-bottom):**
| Part Type | Render Style | Collapsible? |
|---|---|---|
| `text` | Markdown rendered text (headings, bold, italic, code blocks, lists) via compose-richtext | No |
| `reasoning` | Accordion header "💭 Thinking...", click to reveal | Yes |
| `tool` | Card with tool name + icon, expand for input/output JSON | Yes |
| `file`/`patch` | DiffCard embedded in document flow | Yes |
| `step-start/finish` | Hidden (grouping markers) | — |

**Chat Input:**
- Text field + send button
- Agent/model selector chip
- Async send via `POST /session/:id/prompt_async`

**Real-time Updates:**
- SSE via OkHttp EventSource to `/event`
- Filter events by current `sessionID`
- `message.part.updated` events stream new parts into current assistant message
- `session.status` shows agent thinking/running state
- Disconnect when leaving Chat

#### Review Screen
- Fetches diffs from `GET /session/:id/diff`
- List of FileDiff cards (filename, ±additions/deletions, expandable)
- Green bg for additions, red bg for deletions
- "Collapse all" / "Expand all" toggle
- Summary bar: "X files changed, +Y, -Z"

---

## Phases & Implementation Order

### Phase 1: Foundation
| Step | What | Files |
|---|---|---|
| 1.1 | Build config - Add Hilt, Retrofit, OkHttp, kotlinx-serialization, DataStore, markdown-renderer deps | `libs.versions.toml`, `build.gradle.kts`, `app/build.gradle.kts`, `AndroidManifest.xml` |
| 1.2 | Hilt setup - Application class, DI modules | `OpencodeApplication.kt`, `di/AppModule.kt`, `di/NetworkModule.kt` |
| 1.3 | Data layer - API models, Retrofit service, ConnectionStore, repositories | `data/api/models.kt`, `data/api/OpencodeApiService.kt`, `data/local/Connection.kt`, `data/local/ConnectionStore.kt`, `data/local/ConnectionManager.kt`, `data/repository/*.kt` |
| 1.4 | Navigation redesign - bottom nav, HomeScreen + SettingsScreen | `navigation/AppNavigation.kt`, `screens/home/HomeScreen.kt`, `screens/settings/SettingsScreen.kt` |

### Phase 2: Core Screens
| Step | What | Files |
|---|---|---|
| 2.1 | Projects Screen - ViewModel + API | `screens/projects/ProjectsScreen.kt` (rewrite), `screens/projects/ProjectsViewModel.kt` |
| 2.2 | Sessions Screen - ViewModel + API | `screens/sessions/SessionsScreen.kt` (rewrite), `screens/sessions/SessionsViewModel.kt` |
| 2.3 | Chat Screen - ViewModel, message rendering, input, SSE | Complete rewrite of `screens/chat/ChatScreen.kt` + `screens/chat/ChatViewModel.kt` |
| 2.4 | Review Screen - ViewModel + diff API | Rewrite `screens/review/ReviewScreen.kt` + `screens/review/ReviewViewModel.kt` |

### Phase 3: Polish
| Step | What |
|---|---|
| 3.1 | Enhanced markdown rendering |
| 3.2 | Diff viewer with line numbers |
| 3.3 | Message grouping (step-start/finish tool groups) |
| 3.4 | Error states, loading skeletons, empty states |
| 3.5 | Connection reconnection logic |

---

## Package Structure
```
com.example.opencode_mobile/
├── OpencodeApplication.kt
├── MainActivity.kt
├── di/
│   ├── AppModule.kt
│   └── NetworkModule.kt
├── data/
│   ├── api/
│   │   ├── models.kt         (all DTOs)
│   │   └── OpencodeApiService.kt
│   ├── local/
│   │   ├── Connection.kt
│   │   ├── ConnectionStore.kt
│   │   └── ConnectionManager.kt
│   └── repository/
│       ├── ProjectRepository.kt
│       ├── SessionRepository.kt
│       └── MessageRepository.kt
├── ui/
│   ├── components/
│   │   ├── DarkAppBar.kt
│   │   ├── SearchBar.kt
│   │   ├── ListItemCard.kt
│   │   ├── CodeBadgeText.kt
│   │   ├── CommandWidget.kt
│   │   ├── DiffCard.kt
│   │   └── ... (add MarkdownText, CollapsibleBlock, ConnectionDialog)
│   ├── screens/
│   │   ├── home/HomeScreen.kt (+ ViewModel)
│   │   ├── projects/ProjectsScreen.kt (+ ViewModel)
│   │   ├── sessions/SessionsScreen.kt (+ ViewModel)
│   │   ├── chat/ChatScreen.kt (+ ViewModel)
│   │   ├── review/ReviewScreen.kt (+ ViewModel)
│   │   └── settings/SettingsScreen.kt
│   ├── navigation/AppNavigation.kt
│   └── theme/
└── util/
    └── ... (TimeFormatter.kt, etc.)
```

---

## API Endpoints Used

| Endpoint | Method | Purpose |
|---|---|---|
| `/global/health` | GET | Health check on connect |
| `/project` | GET | List projects |
| `/session` | GET | List sessions (`?directory=&roots=true&limit=&offset=&search=`) |
| `/session` | POST | Create session |
| `/session/:id` | GET/DELETE/PATCH | Session detail, delete, update |
| `/session/:id/message` | GET | List messages (`?limit=&before=`) |
| `/session/:id/message` | POST | Send message (blocking) |
| `/session/:id/prompt_async` | POST | Send message (non-blocking, for SSE) |
| `/session/:id/diff` | GET | Get diffs (`?messageID=`) |
| `/session/:id/abort` | POST | Abort running session |
| `/session/:id/revert` | POST | Revert a message |
| `/event` | GET | SSE event stream |
| `/agent` | GET | List agents |
| `/command` | GET | List commands |

---

## Data Flow
```
User Action → Compose UI → ViewModel (StateFlow) → Repository → OpencodeApiService (Retrofit) → Server

SSE (EventSource) → Event Flow → ViewModel collects → UI updates reactively

ConnectionStore (DataStore) ←→ ConnectionManager (in-memory singleton)
```

## Expected Outcomes
1. Connect to any `opencode serve` or `opencode web` instance via address:port
2. Browse projects sorted by recency
3. Browse sessions per project with recency sorting
4. Chat with the agent - user messages in right-aligned bubbles, agent responses as full-width flowing documents with collapsible reasoning, tool calls, and diffs
5. Real-time streaming via SSE
6. Review all diffs in a session with expandable/collapsible cards
7. Multiple saved connections with password support
8. Dark theme with the DESIGN.md color system
