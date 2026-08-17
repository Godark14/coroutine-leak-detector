# Coroutine Leak Detector

An Android Studio / IntelliJ IDEA plugin that catches common Kotlin coroutine and Flow lifecycle mistakes before they cause memory leaks, crashes, ANRs, or flaky tests in production.

## What it detects

- **`GlobalScope` misuse** — coroutines launched with `GlobalScope.launch`/`GlobalScope.async` are never automatically cancelled and can leak.
- **Unsafe Flow collection** — collecting a `Flow` with `collect`/`collectLatest` without `repeatOnLifecycle` or `flowWithLifecycle` can run outside the intended lifecycle.
- **Risky `runBlocking` usage** — flags `runBlocking` calls, which can block the main/UI thread and cause ANRs if misused in Android UI code.
- **Unused `async` result** — detects `async { }` calls whose `Deferred` result is never used, which can silently swallow exceptions.
- **Uncancelled `Job`/`CoroutineScope`** — detects `Job()`/`SupervisorJob()`/`CoroutineScope(...)` properties created in a `ViewModel`/`Fragment`/`Activity` without a matching `.cancel()` call in `onCleared()`/`onDestroy()`.
- **Hardcoded dispatchers** — flags `Dispatchers.IO`/`Main`/`Default`/`Unconfined` hardcoded directly in `withContext`/`launch`/`async`, which makes tests harder to control deterministically and can lead to flaky tests.

## Smart, context-aware quick fixes

Each detection comes with a quick fix (`Alt+Enter`). When the plugin can determine the surrounding class context (`ViewModel`, `Fragment`, `Activity`), it goes beyond a simple TODO comment:

| Inspection | Inside `ViewModel` | Inside `Fragment`/`Activity` | Elsewhere |
|---|---|---|---|
| `GlobalScope` | Replaces with `viewModelScope` directly | Replaces with `lifecycleScope` directly | Adds a migration TODO |
| `Flow.collect` | — | Wraps with `repeatOnLifecycle(Lifecycle.State.STARTED) { ... }` directly | Adds a migration TODO |
| `runBlocking` | TODO suggesting `viewModelScope` | TODO suggesting `lifecycleScope` | Generic review TODO |
| Unused `async` | Replaces with `launch { }` | Replaces with `launch { }` | Replaces with `launch { }` |
| Uncancelled `Job`/`CoroutineScope` | Adds `.cancel()` in `onCleared()` (or a TODO if it doesn't exist) | Adds `.cancel()` in `onDestroy()` (or a TODO if it doesn't exist) | — |
| Hardcoded dispatcher | Adds a dispatcher-injection TODO | Adds a dispatcher-injection TODO | Adds a dispatcher-injection TODO |

The `Flow.collect`, `runBlocking`, and `UncancelledScope` inspections also skip test sources and/or modules that don't have the Android Lifecycle library on the classpath, to avoid false positives outside Android UI code.

## Installation

Currently, the plugin is not yet published on the JetBrains Marketplace. You can install it manually:

1. Clone this repository
2. Run `./gradlew buildPlugin`
3. In Android Studio: `Settings/Preferences` > `Plugins` > `⚙️` > `Install Plugin from Disk...`
4. Select the generated `.zip` file from `build/distributions/`
5. Restart Android Studio

## Development

This project uses the [IntelliJ Platform Gradle Plugin](https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html).

- `./gradlew build` — compile and run tests
- `./gradlew test` — run tests only
- `./gradlew runIde` — launch a sandbox Android Studio instance with the plugin installed
- `./gradlew buildPlugin` — generate the distributable `.zip`

See [CHANGELOG.md](./CHANGELOG.md) for release history.

## Contributing

Issues and pull requests are welcome.

## License

This project is licensed under the [MIT License](./LICENSE).