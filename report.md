# OpenCode Mobile — Codebase Review Report

> Generated: 2026-06-25
> Based on PLAN.md, DESIGN.md, all source files, and build configuration.

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
| 2 | DESIGN.md color system via theme | Hardcoded hex colors in 10+ files | 🟡 Medium — violates DESIGN.md |
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
| **S1** | `HttpLoggingInterceptor.Level.BODY` logs full request bodies, including the Base64 `Authorization` header with password | `di/NetworkModule.kt:1318` | 🔴 **High** | Change to `Level.HEADERS` or `Level.BASIC`; use `BuildConfig.DEBUG` to only log body in debug builds |
| **S2** | Password stored in plaintext in DataStore (no encryption) | `data/local/ConnectionStore.kt` | 🟡 **Medium** | Use `EncryptedSharedPreferences` or encrypt with Android Keystore before persisting |
| **S3** | Basic Auth uses Base64 (not encryption) over cleartext HTTP | `di/NetworkModule.kt:1306` | 🔴 **High** | Use `https://` by default; add HTTPS support toggle |
| **S4** | Cleartext HTTP hardcoded (`"http://"` prefix) for all connections | `ui/navigation/AppNavigation.kt:1466`, `AndroidManifest.xml:354` | 🟡 **Medium** | Derive scheme from connection config; allow user to choose http/https |
| **S5** | Force-unwrap (`!!`) on nullable `connection.password` | `di/NetworkModule.kt:1304` | 🟢 **Low** | Replace `connection.password!!` with safe access — currently guarded but brittle |

---

## 3. Optimization Opportunities

| # | Issue | Location | Recommendation |
|---|-------|----------|---------------|
| **O1** | **Duplicate `formatTimestamp`** function — identical code in both screens | `ProjectsScreen.kt:165`, `SessionsScreen.kt:191` | Extract to `util/TimeFormatter.kt` |
| **O2** | **`Json` instance created per SSE event** inside `onEvent` callback | `OpencodeApiService.kt:909` | Lift to a file-level `val` |
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
| **D1** | **`DarkAppBar.kt`** | `ui/components/DarkAppBar.kt` | Defined, **never imported or used** |
| **D2** | **`ListItemCard.kt`** | `ui/components/ListItemCard.kt` | Defined, **never imported or used** |
| **D3** | **`CommandWidget.kt`** | `ui/components/CommandWidget.kt` | Defined, **never imported or used** |
| **D4** | **`PlaceholderScreen()` composable** | `ui/navigation/AppNavigation.kt:1538` | Defined, **never called** |
| **D5** | **`CommandWidget` import** | `ui/screens/chat/ChatScreen.kt:29` | Imported but **never referenced** in the file |
| **D6** | **Legacy color constants** (`Purple80`, `PurpleGrey80`, `Pink80`, `Purple40`, `PurpleGrey40`, `Pink40`) | `ui/theme/Color.kt:1636-1641` | Never used anywhere in the codebase |

---

## 5. Code Cleanup & Best Practices

| # | Issue | Location | Recommendation |
|---|-------|----------|---------------|
| **C1** | **Hardcoded hex colors** in 10+ files instead of `MaterialTheme.colorScheme.*` or `Color.kt` constants | All screens & components | Migrate to `Color.kt` constants or theme color scheme |
| **C2** | **`AppModule.kt` is empty** (abstract class with no bindings or provides) | `di/AppModule.kt` | Remove, or add actual bindings (e.g., `ConnectionStore` could be bound here) |
| **C3** | **No ViewModel/Repository unit tests** — only example tests exist | `test/`, `androidTest/` | Add unit tests for ViewModels and Repositories |
| **C4** | **`catch (\_: Exception)` silently swallows errors** in multiple places | `ConnectionStore.kt`, `OpencodeApiService.kt`, `ChatViewModel.kt` | At minimum log the error via `Log.e()` or `Timber` |
| **C5** | **`SearchBar.kt` uses `BasicTextField`** instead of Material3 `TextField` | `ui/components/SearchBar.kt:73` | Inconsistent with rest of codebase (e.g., `OutlinedTextField` in `ConnectionDialog`) |
| **C6** | **`network_security_config.xml` defined but not referenced** in manifest | `res/xml/network_security_config.xml`, `AndroidManifest.xml` | Reference via `android:networkSecurityConfig="@xml/network_security_config"` |
| **C7** | **No explicit Content-Type** on SSE request | `OpencodeApiService.kt:934` | Already has `Accept`, which is likely sufficient — minor |
| **C8** | **`HomeScreen.kt:131` — `CardDefaults.outlinedCardBorder()` ignored** — `border` param not supported by `Card(onClick=...)` overload | `HomeScreen.kt:131-133` | Remove the unused `.copy()` expression |
| **C9** | **`rememberOrcaMaterialStyle()` called inside both `AgentMessageContent` and `StreamingContent`** — creates two instances per render | `ChatScreen.kt:203,239` | Could be lifted to `ChatScreen` scope and passed down |

---

## 6. Summary

| Category | Count |
|----------|-------|
| Phase 1-2 items (complete) | 8 / 8 |
| Phase 3 items (not started) | 3 / 4 (markdown done via orca) |
| PLAN deviations | 13 items (10 minor, 3 medium) |
| Security issues | 5 (2 High, 2 Medium, 1 Low) |
| Optimization opportunities | 9 |
| Dead/unused code items | 6 |
| Cleanup/best-practice items | 9 |

### Quick Wins (highest impact, least effort)

1. **S2** — Fix logging level (`Level.HEADERS` or conditional on debug)
2. **C1** — Start using `Color.kt` constants instead of hardcoded hex
3. **D1-D4** — Delete or wire dead component files
4. **O1** — Extract `formatTimestamp` to shared utility
5. **O2** — Lift `Json` instance to file-level `val` in `OpencodeApiService.kt`
