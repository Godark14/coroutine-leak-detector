# Coroutine Leak Detector Changelog

## [Unreleased]

## [0.1.0] - 2026-08-14

### Added

- Initial release
- Inspection: detect `GlobalScope.launch`/`GlobalScope.async` usage, which can lead to uncancelled coroutines and memory leaks
- Inspection: detect `Flow.collect`/`collectLatest` calls without lifecycle awareness (missing `repeatOnLifecycle` or `flowWithLifecycle`)
- Quick fix: insert a migration TODO comment above unsafe `GlobalScope` usage