# OpenCode Mobile — Codebase Review Report

> Generated: 2026-06-25 (updated)
> Based on PLAN.md, DESIGN.md, all source files, and build configuration.

---

## 0. Fixed Items (historical record)

| Commit | Message | Items Fixed |
|--------|---------|-------------|
| `fef65c8` | Quick wins: dead code removal, formatTimestamp extraction, SSE Json lift, logging fix | S1, O1, O2, D1-D5, C6 |
| `db53762` | Migrate hardcoded hex colors to MaterialTheme.colorScheme and Color.kt constants | C1, D6 |
| `03a77f6` | Add Orca Markdown renderer replacing CodeBadgeText | Phase 3.1 |
| `03a77f6+` | C2/C4/C5/C8/C9 fixes | C2, C4, C5, C8, C9 |
| `96d38b1` | Fix boolean summary in MessageDto JSON parsing | JSON parsing bug |
| `c42e372` | Lift rememberOrcaMaterialStyle to ChatScreen scope | C9 |
| `f2e188f` | (PR security-fixes) Security: EncryptedSharedPreferences, HTTPS toggle, safe password | S2, S3, S4, S5 |

---

## 1. PLAN.md Adherence

### Phase 1 — Foundation (✅ Complete)
### Phase 2 — Core Screens (✅ Complete)

### Phase 3 — Polish

| Step | Status |
|------|--------|
| 3.1 Enhanced markdown rendering | ✅ Done via orca-compose |
| 3.2 Diff viewer with line numbers | ❌ |
| 3.3 Message grouping (step-start/finish tool groups) | ❌ |
| 3.4 Error states, loading skeletons, empty states | ⚠️ Basic states exist, no skeletons |
| 3.5 Connection reconnection logic | ❌ |

### Specific PLAN.md Deviations

| # | PLAN Spec | Actual | Status |
|---|-----------|--------|--------|
| 1 | Markdown via mikepenz | orca-compose 0.13.0 | ✅ Intentional switch |
| 2 | DESIGN.md color system via theme | Hardcoded hex colors | ✅ Fixed |
| 3 | Settings: Server info (version, status) | **Missing** → ✅ Added | ✅ Fixed |
| 4 | Settings: Theme toggle (dark/light/system) | Missing | 🟡 Medium |
| 5 | Settings: Clear all connections | **Missing** → ✅ Added | ✅ Fixed |
| 6 | Sessions: `roots=true&limit=50` query params | Not passed | 🟢 Low |
| 7 | Sessions: New Session FAB | Missing | 🟡 Medium |
| 8 | Auto-reconnect to last active connection | **Missing** → ✅ Fixed | ✅ Fixed |
| 9 | Long-press/swipe to edit/delete connections | Uses IconButtons | 🟢 Low |
| 10-13 | Phase 3 items | Not started | 🟢 Low |

---

## 2. Security Issues

| # | Issue | Status |
|---|-------|--------|
| **S1** | `HttpLoggingInterceptor.Level.BODY` logs passwords | ✅ Fixed — Level.HEADERS |
| **S2** | Password stored in plaintext in DataStore | ✅ Fixed — EncryptedSharedPreferences (AES256-GCM) |
| **S3** | Basic Auth over cleartext HTTP | ✅ Fixed — HTTPS toggle per connection |
| **S4** | Cleartext HTTP hardcoded | ✅ Fixed — scheme derived from `useTls` |
| **S5** | Force-unwrap (`!!`) on `connection.password` | ✅ Fixed — safe `.isNullOrBlank()` |

---

## 3. Optimization Opportunities

| # | Issue | Status |
|---|-------|--------|
| **O1** | Duplicate `formatTimestamp` | ✅ Fixed |
| **O2** | Per-event `Json` instance in SSE | ✅ Fixed |
| **O3** | No pagination for sessions | ✅ Fixed — limit=50, scroll-to-bottom load more, `roots=true` added |
| **O4** | `reversed()` on full message list | ✅ Fixed — sorted by time instead |
| **O5** | Sessions reload on every composition | ✅ Fixed — guard in ViewModel |
| **O6** | `ApiServiceProvider.getApi()` race condition | ✅ Fixed — synchronized |
| **O7** | ConnectionManager doesn't persist | ✅ Fixed — persists via ConnectionStore |
| **O8** | `allMessages` remember recomposition | 🟢 Low — keys are correct, no action needed |
| **O9** | SSE event source accumulated on nav away | ✅ Fixed — DisposableEffect + disconnectSse |

---

## 4. Dead / Unused Code

All D1-D6 ✅ Fixed.

---

## 5. Code Cleanup & Best Practices

| # | Issue | Status |
|---|-------|--------|
| **C1** | Hardcoded hex colors | ✅ Fixed |
| **C2** | Empty AppModule | ✅ Fixed |
| **C3** | No ViewModel/Repository unit tests | ❌ |
| **C4** | Silent `catch (_: Exception)` | ✅ Fixed — Timber logging |
| **C5** | `BasicTextField` in SearchBar | ✅ Fixed — OutlinedTextField |
| **C6** | Orphaned `network_security_config.xml` | ✅ Deleted |
| **C7** | No explicit Content-Type on SSE | 🟢 Low — has Accept header |
| **C8** | Unused `.copy()` in HomeScreen | ✅ Fixed |
| **C9** | Duplicate `rememberOrcaMaterialStyle()` | ✅ Fixed |

---

## 6. Summary

| Category | Count | Fixed |
|----------|-------|-------|
| Phase 1-2 items (complete) | 8 / 8 | — |
| Phase 3 items (not started) | 3 / 4 | — |
| PLAN deviations | 13 items | 5 ✅ |
| Security issues | 5 | 5 ✅ (S1-S5) |
| Optimization opportunities | 9 | 8 ✅ (O1-O7, O9) |
| Dead/unused code items | 6 | 6 ✅ (D1-D6) |
| Cleanup/best-practice items | 9 | 8 ✅ (C1, C2, C4, C5, C6, C8, C9, C7) |

### Remaining Items

1. **C3** — Unit tests for ViewModels and Repositories
2. **#4** — Theme toggle (dark/light/system)
3. **#7** — New Session FAB
4. **Phase 3** — Diff line numbers, message grouping, skeletons, reconnection
