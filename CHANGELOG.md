# Coroutine Leak Detector Changelog

## [Unreleased]

### Added

- Inspection: detect `runBlocking` usage that may block the main/UI thread, with a quick fix to add a review TODO
- Quick fix: insert a migration TODO comment above unsafe `Flow.collect`/`collectLatest` usage

### Changed

- Quick fix: `GlobalScope` inspection now suggests a direct replacement (`viewModelScope` or `lifecycleScope`) when the surrounding class context is detected, falling back to a TODO comment otherwise
- Inspection: `FlowCollectWithoutLifecycle` now skips test sources and modules without the Android Lifecycle library on the classpath, reducing false positives
- Quick fix: `FlowCollectWithoutLifecycle` inspection now suggests wrapping with `repeatOnLifecycle(Lifecycle.State.STARTED)` directly when inside a `Fragment`/`Activity` context, falling back to a TODO comment otherwise
- Quick fix: `runBlocking` inspection now suggests a specific replacement (`viewModelScope` or `lifecycleScope`) in its TODO message when the surrounding class context is detected

## [0.1.0] - 2026-08-14

### Added

- Initial release
- Inspection: detect `GlobalScope.launch`/`GlobalScope.async` usage, which can lead to uncancelled coroutines and memory leaks
- Inspection: detect `Flow.collect`/`collectLatest` calls without lifecycle awareness (missing `repeatOnLifecycle` or `flowWithLifecycle`)
- Quick fix: insert a migration TODO comment above unsafe `GlobalScope` usage