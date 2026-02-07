# Override Sense Code Analysis & Refactoring Plan

This document outlines identified issues, code quality concerns, and recommended refactoring steps for the Override Sense codebase.

## 🔴 Critical Issues (High Priority)

### 1. Memory Leaks in `RealMonitorRepository`
**Location:** `org/override/sense/feature/monitor/data/RealMonitorRepository.kt`
- **Problem:** A `CoroutineScope(Dispatchers.IO)` is created as a local variable inside the `init` block and inside `startWork()`.
- **Impact:** These scopes are never cancelled. If the repository is recreated or operations hang, these coroutines will leak and continue consuming resources (zombie coroutines).
- **Fix:** 
  - Make `CoroutineScope` a class property.
  - Inject the scope or use a lifecycle-aware scope.
  - Ensure cancellation when the component is destroyed (though Repositories are usually singletons, explicit scope management is cleaner).

### 2. MonitorWorker Execution Model
**Location:** `org/override/sense/feature/monitor/work/MonitorWorker.kt`
- **Problem:** The worker uses a `while` loop to collect flow emissions from `AudioRecorder`. `WorkManager` (specifically `OneTimeWorkRequest`) is designed for deferrable tasks, not continuous long-running services (even with `Expedited` and `Foreground` info). 
- **Impact:** The OS may kill the worker after ~10 minutes (execution limit), stopping sound detection silently.
- **Fix:** 
  - Migrate to a standard `ForegroundService` for "always-on" listening.
  - Use `WorkManager` only to *start* the service or for periodic checks, not for the continuous loop itself.

### 3. AudioRecorder Resource Usage
**Location:** `org/override/sense/feature/monitor/data/AudioRecorder.kt`
- **Problem:** `startRecording` creates a new `AudioRecord` instance every time it is called.
- **Impact:** Frequent toggling of monitoring could lead to resource exhaustion if `release()` isn't called perfectly or if the OS is slow to release hardware handles.
- **Fix:** Reuse the `AudioRecord` instance or ensure strict state machine logic to prevent multiple creations. (Current `AtomicBoolean` helps but explicit state management is better).

### 4. UI State Stale Data Bug
**Location:** `org/override/sense/feature/monitor/presentation/MonitorViewModel.kt`
- **Problem:** The `state` flow uses `combine` to check if a sound is recent. However, `combine` only re-evaluates when a source emits. If no *new* sound is detected, the `lastDetection` will remain "recent" (visually active) indefinitely because the flow doesn't re-emit based on time passing.
- **Impact:** The UI shows "Alert" state long after the sound has stopped.
- **Fix:** Introduce a "ticker" flow (e.g., `flow { while(true) { emit(Unit); delay(1000) } }`) combined into the state to force re-evaluation of the "is recent" logic every second.

## 🟡 Code Quality & Architecture (Medium Priority)

### 5. Hardcoded Models & Labels
**Location:** `org/override/sense/feature/monitor/data/SoundClassifier.kt`
- **Problem:** Model filename `"1.tflite"` and labels (indexes mapping to strings) are hardcoded in the class.
- **Impact:** Hard to update the model or add support for new languages/sounds without recompiling code.
- **Fix:** Move labels to a resource file (JSON/CSV in assets) or string resources (`strings.xml`) mapped by ID. Load model filename from configuration.

### 6. Repository-Worker Circular Dependency
**Location:** `org/override/sense/feature/monitor/work/MonitorWorker.kt`
- **Problem:** The Worker injects `MonitorRepository` and casts it to `RealMonitorRepository` to call `emitEventFromWorker`.
- **Impact:** Breaks Clean Architecture (Worker depends on Concrete Data Implementation). Tightly couples Worker to Repository implementation.
- **Fix:** 
  - Use a `SharedFlow` or `EventBus` that both Repository and Worker can access.
  - Or, have the Worker write to `DataStore`/Database directly, and the Repository observe that source.

### 7. Hardcoded Settings Defaults
**Location:** `org/override/sense/feature/settings/data/SettingsRepositoryImpl.kt`
- **Problem:** Default values (e.g., `SensitivityLevel.MEDIUM`, `1.0f`) are hardcoded in the `map` lambda.
- **Impact:** Inconsistent defaults if accessed from other places.
- **Fix:** Define constant `DEFAULT_` values in the domain models or a configuration object.

## 🟢 Minor Improvements (Low Priority)

### 8. AppNotificationManager Implementation
**Location:** `org/override/sense/core/common/notification/AppNotificationManager.kt`
- **Problem:** Logic for creating channels and notifications is mixed with configuration.
- **Fix:** Extract channel IDs and configuration to a separate config object.

### 9. Feature Module Isolation
- **Problem:** Features (`monitor`, `settings`, `home`) are in the same module structure.
- **Recommendation:** In the future, extract these into separate Gradle modules (`:feature:monitor`, `:feature:settings`) to enforce separation of concerns and improve build times.

## 📋 Recommended Refactoring Plan

1.  **Phase 1 (Stability):** Fix the `MonitorViewModel` timer bug and `RealMonitorRepository` scope leaks.
2.  **Phase 2 (Reliability):** Refactor `MonitorWorker` to use a `ForegroundService` for robust long-running execution.
3.  **Phase 3 (Maintainability):** Externalize Sound Classifier labels and model configuration.
