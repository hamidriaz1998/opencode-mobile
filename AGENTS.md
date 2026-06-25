# OpenCode Mobile

Android app (Kotlin/Jetpack Compose) connecting to an `opencode web` server.

## Build & Run

Use the `build_project` MCP tool to build (not `./gradlew` directly). Run `./gradlew` only when MCP is unavailable.

```bash
./gradlew :app:assembleDebug          # full build (fallback)
./gradlew test                        # unit tests
./gradlew :app:lint                   # lint
```

- Gradle 9.4.1, AGP 9.2.1, Kotlin 2.2.10, Compose BOM 2026.06.00
- minSdk 31, targetSdk 36, compileSdk 36
- Version catalog: `gradle/libs.versions.toml`

## Tech Stack

- **DI**: Hilt + KSP
- **HTTP**: Retrofit 3 + OkHttp 5 (kotlinx.serialization converter)
- **Markdown**: `ru.wertik:orca-compose:0.13.0` (NOT mikepenz)
- **SSE**: OkHttp EventSource
- **Storage**: EncryptedSharedPreferences (AES256)
- **Logging**: Timber
- **Navigation**: Navigation Compose 2.9.8

## Architecture

Single-`ComponentActivity`, ViewModel + StateFlow, Repository pattern.

```
Composable → ViewModel → Repository → ApiServiceProvider → Retrofit → Server
```

Hilt modules: `AppModule` (Json), `NetworkModule` (OkHttpClient, auth interceptor).

## Navigation (`ui/navigation/Routes.kt`)

Bottom nav: Home + Settings (shown only on those tabs).

```
home → projects/{connectionId} → sessions/{projectId}/{projectWorktree} → chat/{sessionId}
                                                                           └─ review/{sessionId}
```

Always URL-encode `sessionId` and `projectWorktree` in nav routes (slashes break route matching — see `51decb1`).

## API Patterns

- `ApiServiceProvider` (singleton) — call `init(baseUrl)` before `getApi()`. Has a race condition in double-checked locking (`O6` in report.md).
- `ConnectionManager` (singleton, in-memory only) — `setConnection()` does NOT persist to DataStore (`O7`).
- Auth: Basic Auth Base64 via OkHttp interceptor, reads password from `ConnectionManager.activeConnection`.
- SSE: `connectSse()` + `createSseListener()` in `OpencodeApiService.kt`.
- Chat: async send (`POST /session/:id/prompt_async`) + SSE streaming (`/event`).
- Messages are reversed for newest-at-bottom display.
- `roots=true&limit=50` not currently passed to sessions API (`PLAN.md` deviation #6).

## Design System

Custom Material3 theme from `DESIGN.md`: onyx/lavender palette, dark-first. Theme toggle NOT implemented (dark only). See `Color.kt` for custom slots (`TopBarBg`, `CardBg`, `DiffAddBg`, etc.). Markdown via Orca Material3 style.

## Key Docs

- `PLAN.md` — authoritative implementation reference with API endpoints, package structure, data flow
- `DESIGN.md` — color system, typography, component specs
- `report.md` — code review with unfixed items (S2: encrypt passwords, O3: pagination, O6: ApiServiceProvider race, Phase 3 items)

## Research Before Implementation (MANDATORY)

Before adding libraries, choosing patterns, implementing new features, or answering "how should we do X?":

1. **Look up official docs** — use Context7 MCP (`resolve-library-id` → `query-docs`) for the latest API surface, breaking changes, and patterns.
2. **Evaluate alternatives** — use WebSearch for pros/cons, known issues, deprecation notices, and better-fit libraries.
3. **Study OSS best practices** — search how well-known projects implement similar features; verify approach follows established patterns.
4. **Justify the decision** — only proceed after steps 1-3. If the best choice differs from what was asked, recommend it to the user first.

Does NOT apply to: simple bug fixes, minor refactoring, or tasks using libraries already established in this project.

## Verification After Code Changes

Use the `build_project` MCP tool to verify changes compile. For quick file-level checks, use JetBrains MCP tools (`get_file_problems`, `getDiagnostics`). Only run `./gradlew` directly when MCP is unavailable or for final release verification.

## Known Gaps / Gotchas

- **Phase 3 not started**: diff line numbers, message grouping (step-start/finish), auto-reconnect, loading skeletons
- **No tests** — add ViewModel/Repository tests (`C3`)
- **ApiServiceProvider race** (`O6`) — double-checked locking without synchronization in `getApi()`
- **ConnectionManager** doesn't write active connection to DataStore (`O7`)
- **Cleartext HTTP** hardcoded — `Connection.scheme` supports `useTls` but manifest always sets `usesCleartextTraffic=true` (`S4`)
- **Password** stored encrypted but decrypted in-memory; Base64 Basic Auth != encryption (`S2`, `S3`)
