# OpenCode Mobile — Codebase Review Report

> Generated: 2026-06-25 (updated)
> Based on PLAN.md, DESIGN.md, all source files, and build configuration.

---

## 0. Recent Fixes (since initial report)

The following items from the original report were fixed across 2 commits:

| Commit | Message | Items Fixed |
|--------|---------|-------------|
| `fef65c8` | quick wins: dead code removal, formatTimestamp extraction, SSE Json lift, logging fix | S1, O1, O2, D1-D5, C6 |
| `db53762` | migrate hardcoded hex colors to MaterialTheme.colorScheme and Color.kt constants | C1, D6 |
| `03a77f6` | add Orca Markdown renderer (wertikolix/orca 0.13.0) replacing CodeBadgeText | Phase 3.1 (extra) |
| `03a77f6+` | C2/C4/C5/C8 fixes | C2, C4, C5, C8 |

Full details in the "Fixed Since Last Report" table in Section 6 below.

---

## 1. PLAN.md Adherence

### Phase 1 — Foundation (✅ Complete)

| Step | Status | Notes |
|------|--------|-------|
| 1.1 Build config | ✅ | Hilt, Retrofit, OkHttp, kotlinx-serialization, DataStore, orca-compose deps present |
| 1.2 Hilt setup | ✅ | `OpencodeApplication.kt`, `AppModule.kt`, `NetworkModule.kt` |
| 1.3 Data layer | ✅ | API models, Retrofit service, ConnectionStore, ConnectionManager, repositories |
| 1.4 Navigation | ✅ | Bottom nav (Home + Settings tabs), NavHost wiring all screens |

### Phase 2 — Core Screens (✅ Complete)

| Step | Status | Notes |
|------|--------|-------|
| 2.1 Projects Screen | ✅ | ViewModel + API, sort/filter, search |
| 2.2 Sessions Screen | ✅ | ViewModel + API, sort/filter, search |
| 2.3 Chat Screen | ✅ | Orca markdown rendering, SSE streaming, part type rendering |
| 2.4 Review Screen | ✅ | ViewModel + diff API, expandable diff cards |

### Phase 3 — Polish (❌ Not Started)

| Step | Status |
|------|--------|
| 3.1 Enhanced markdown rendering | ✅ Done via orca-compose (switched per developer decision) |
| 3.2 Diff viewer with line numbers | ❌ |
| 3.3 Message grouping (step-start/finish tool groups) | ❌ |
| 3.4 Error states, loading skeletons, empty states | ⚠️ Basic states exist, no skeletons |
| 3.5 Connection reconnection logic | ❌ |

### Specific PLAN.md Deviations

| # | PLAN Spec | Actual | Impact |
|---|-----------|--------|--------|
| 1 | Markdown via `com.mikepenz:multiplatform-markdown-renderer-m3` | **orca-compose** 0.13.0 (`ru.wertik:orca-compose`) | ✅ Intentional switch |
| 2 | DESIGN.md color system via theme | Hardcoded hex colors in 10+ files | ✅ Fixed — migrated to `MaterialTheme.colorScheme` + `Color.kt` constants |
| 3 | Settings: Server info (version, status) | **Missing** | 🟡 Medium |
| 4 | Settings: Theme toggle (dark/light/system) | **Missing** — says "Dark theme only" | 🟡 Medium |
| 5 | Settings: Clear all connections | **Missing** | 🟢 Low |
| 6 | Sessions: `roots=true&limit=50` query params | **Not passed** | 🟢 Low — API returns defaults |
| 7 | Sessions: New Session FAB | **Missing** | 🟡 Medium |
| 8 | Auto-reconnect to last active connection | **Missing** | 🟡 Medium |
| 9 | Long-press/swipe to edit/delete connections | Uses IconButtons instead | 🟢 Low — functional equivalent |
| 10 | Diff viewer with line numbers (3.2) | **Not started** | 🟢 Low — Phase 3 item |
| 11 | Message grouping (3.3) | **Not started** | 🟢 Low — Phase 3 item |
| 12 | Connection reconnection logic (3.5) | **Not started** | 🟢 Low — Phase 3 item |
| 13 | Error/loading/empty state polish (3.4) | Basic states exist, no skeletons | 🟢 Low — Phase 3 item |

---

## 2. Security Issues

| # | Issue | Location | Severity | Recommendation |
|---|-------|----------|----------|---------------|
| **S1** | `HttpLoggingInterceptor.Level.BODY` logs full request bodies, including the Base64 `Authorization` header with password | `di/NetworkModule.kt:45` | 🔴 **High** | ✅ Fixed — changed to `Level.HEADERS` |
| **S2** | Password stored in plaintext in DataStore (no encryption) | `data/local/ConnectionStore.kt` | 🟡 **Medium** | Use `EncryptedSharedPreferences` or encrypt with Android Keystore before persisting |
| **S3** | Basic Auth uses Base64 (not encryption) over cleartext HTTP | `di/NetworkModule.kt:1306` | 🔴 **High** | Use `https://` by default; add HTTPS support toggle |
| **S4** | Cleartext HTTP hardcoded (`"http://"` prefix) for all connections | `ui/navigation/AppNavigation.kt:1466`, `AndroidManifest.xml:354` | 🟡 **Medium** | Derive scheme from connection config; allow user to choose http/https |
| **S5** | Force-unwrap (`!!`) on nullable `connection.password` | `di/NetworkModule.kt:1304` | 🟢 **Low** | Replace `connection.password!!` with safe access — currently guarded but brittle |

---

## 3. Optimization Opportunities

| # | Issue | Location | Recommendation |
|---|-------|----------|---------------|
| **O1** | **Duplicate `formatTimestamp`** function — identical code in both screens | `ProjectsScreen.kt:165`, `SessionsScreen.kt:191` | ✅ Fixed — extracted to `util/TimeFormatter.kt` |
| **O2** | **`Json` instance created per SSE event** inside `onEvent` callback | `OpencodeApiService.kt:909` | ✅ Fixed — lifted to file-level `val` |
| **O3** | **No pagination** for sessions — API supports `limit`/`offset` but always fetches all | `SessionRepository.kt` | Add pagination for large session lists |
| **O4** | **`reversed()` on full message list** called after every SSE reload | `ChatViewModel.kt:57,190` | Consider API parameter for order, or track incrementally |
| **O5** | **Sessions reloads on every composition** — `LaunchedEffect(projectWorktree)` re-triggers even for same worktree | `SessionsScreen.kt:35` | Guard with `remember(projectWorktree)` or check if already loaded |
| **O6** | **`ApiServiceProvider.getApi()` has a race condition** — double-checked locking without synchronization | `ApiServiceProvider.kt:959-962` | Use `synchronized` block or `AtomicReference` |
| **O7** | **ConnectionManager doesn't persist to DataStore** — `setConnection()` only updates in-memory StateFlow | `ConnectionManager.kt:10` | Also call `connectionStore.setActiveConnection()` |
| **O8** | **`allMessages` `remember` with mutable list reference** — may not recompute if list content changes but reference doesn't | `ChatScreen.kt:121` | Use `snapshotFlow` or pass explicit recomposition keys |
| **O9** | **SSE event source accumulated on nav away** — `onCleared()` cancels it, but rapid nav might leave dangling connections | `ChatViewModel.kt:101-112` | Track lifecycle, cancel SSE in `DisposableEffect` in composable |

---

## 4. Dead / Unused Code

| # | Component | File | Status |
|---|-----------|------|--------|
| **D1** | **`DarkAppBar.kt`** | ~~`ui/components/DarkAppBar.kt`~~ | ✅ Deleted |
| **D2** | **`ListItemCard.kt`** | ~~`ui/components/ListItemCard.kt`~~ | ✅ Deleted |
| **D3** | **`CommandWidget.kt`** | ~~`ui/components/CommandWidget.kt`~~ | ✅ Deleted |
| **D4** | **`PlaceholderScreen()` composable** | ~~`ui/navigation/AppNavigation.kt:1538`~~ | ✅ Removed |
| **D5** | **`CommandWidget` import** | ~~`ui/screens/chat/ChatScreen.kt:29`~~ | ✅ Removed |
| **D6** | **Legacy color constants** | ~~`ui/theme/Color.kt:1636-1641`~~ | ✅ Removed |

---

## 5. Code Cleanup & Best Practices

| # | Issue | Location | Recommendation |
|---|-------|----------|---------------|
| **C1** | **Hardcoded hex colors** in 10+ files instead of `MaterialTheme.colorScheme.*` or `Color.kt` constants | All screens & components | ✅ Fixed — migrated to `MaterialTheme.colorScheme` + custom `Color.kt` constants |
| **C2** | **`AppModule.kt` is empty** (abstract class with no bindings or provides) | `di/AppModule.kt` | ✅ Fixed — added `Json` provides binding |
| **C3** | **No ViewModel/Repository unit tests** — only example tests exist | `test/`, `androidTest/` | Add unit tests for ViewModels and Repositories |
| **C4** | **`catch (\_: Exception)` silently swallows errors** in multiple places | `ConnectionStore.kt`, `OpencodeApiService.kt`, `ChatViewModel.kt` | ✅ Fixed — all catches now log via `Timber.e()` |
| **C5** | **`SearchBar.kt` uses `BasicTextField`** instead of Material3 `TextField` | `ui/components/SearchBar.kt:73` | ✅ Fixed — replaced with `OutlinedTextField` |
| **C6** | **`network_security_config.xml` defined but not referenced** in manifest | ~~`res/xml/network_security_config.xml`~~ | ✅ Deleted (unused, cleartext flag used instead) |
| **C7** | **No explicit Content-Type** on SSE request | `OpencodeApiService.kt:934` | Already has `Accept`, which is likely sufficient — minor |
| **C8** | **`HomeScreen.kt:131` — `CardDefaults.outlinedCardBorder()` ignored** — `border` param not supported by `Card(onClick=...)` overload | `HomeScreen.kt:131-133` | ✅ Fixed — removed unused `.copy()` expression |
| **C9** | **`rememberOrcaMaterialStyle()` called inside both `AgentMessageContent` and `StreamingContent`** — creates two instances per render | `ChatScreen.kt:203,239` | Could be lifted to `ChatScreen` scope and passed down |

---

## 6. Summary

| Category | Count | Fixed |
|----------|-------|-------|
| Phase 1-2 items (complete) | 8 / 8 | — |
| Phase 3 items (not started) | 3 / 4 | — |
| PLAN deviations | 13 items | 1 ✅ |
| Security issues | 5 | 1 ✅ (S1) |
| Optimization opportunities | 9 | 2 ✅ (O1, O2) |
| Dead/unused code items | 6 | 6 ✅ (D1-D6) |
| Cleanup/best-practice items | 9 | 6 ✅ (C1, C2, C4, C5, C6, C8) |

### Fixed Since Last Report

| # | Description | Commit |
|---|-------------|--------|
| S1 | `HttpLoggingInterceptor` changed from `Level.BODY` to `Level.HEADERS` | `fef65c8` |
| O1 | `formatTimestamp` extracted to `util/TimeFormatter.kt`; deduplicated in ProjectsScreen & SessionsScreen | `fef65c8` |
| O2 | Per-event `Json` instance lifted to file-level `val` in `OpencodeApiService.kt` | `fef65c8` |
| D1-D5 | Deleted `DarkAppBar.kt`, `ListItemCard.kt`, `CommandWidget.kt`; removed orphaned `PlaceholderScreen` and dead import | `fef65c8` |
| D6 | Removed legacy `Purple80`/`Pink80` constants from `Color.kt` | `db53762` |
| C1 | Migrated all hardcoded hex colors to `MaterialTheme.colorScheme.*` + 16 custom `Color.kt` constants across 10 files | `db53762` |
| C6 | Deleted orphaned `network_security_config.xml` (unused since cleartext flag) | `fef65c8` |
| C2 | Added `Json` provides binding to `AppModule.kt` | (pending commit) |
| C4 | All silent `catch (_: Exception)` now log via `Timber.e()` in ConnectionStore, OpencodeApiService, ChatViewModel | (pending commit) |
| C5 | Replaced `BasicTextField` with `OutlinedTextField` in SearchBar | (pending commit) |
| C8 | Removed unused `CardDefaults.outlinedCardBorder().copy()` in HomeScreen | (pending commit) |

### Remaining Quick Wins (highest impact, least effort)

1. **S2** — Encrypt password storage with Android Keystore
2. **C3** — Add unit tests for ViewModels and Repositories
3. **O3** — Add pagination for sessions (API supports `limit`/`offset`)
