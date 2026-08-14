# Coroutine Leak Detector

An Android Studio / IntelliJ IDEA plugin that catches common Kotlin coroutine and Flow lifecycle mistakes before they cause memory leaks, crashes, or ANRs in production.

## What it detects

- **`GlobalScope` misuse** — coroutines launched with `GlobalScope.launch`/`GlobalScope.async` are never automatically cancelled and can leak.
- **Unsafe Flow collection** — collecting a `Flow` with `collect`/`collectLatest` without `repeatOnLifecycle` or `flowWithLifecycle` can run outside the intended lifecycle.
- **Risky `runBlocking` usage** — flags `runBlocking` calls, which can block the main/UI thread and cause ANRs if misused in Android UI code.

Each detection includes a clear explanation and a quick fix (`Alt+Enter`) to guide the migration.

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
MIT