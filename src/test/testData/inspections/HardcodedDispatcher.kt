// Fake declarations to avoid any external dependency in the test

object Dispatchers {
    val IO = Any()
    val Main = Any()
    val Default = Any()
    val Unconfined = Any()
}

interface CoroutineDispatcher

class CoroutineScope {
    fun launch(dispatcher: Any = Unit, block: () -> Unit) {
        block()
    }
}

fun withContext(dispatcher: Any, block: () -> Unit) {
    block()
}

// Scenario 1 - Dispatchers.IO hardcoded in withContext, MUST trigger the warning
suspend fun unsafeWithContext() {
    withContext(<warning descr="Hardcoding Dispatchers.IO here makes this code harder to test deterministically. Inject the dispatcher (e.g. via a DispatcherProvider interface) instead, so tests can substitute a TestDispatcher and avoid flaky, non-deterministic test results.">Dispatchers.IO</warning>) {
        val x = 1
    }
}

// Scenario 2 - Dispatchers.Main hardcoded in launch, MUST trigger the warning
fun unsafeLaunch(scope: CoroutineScope) {
    scope.launch(<warning descr="Hardcoding Dispatchers.Main here makes this code harder to test deterministically. Inject the dispatcher (e.g. via a DispatcherProvider interface) instead, so tests can substitute a TestDispatcher and avoid flaky, non-deterministic test results.">Dispatchers.Main</warning>) {
        val x = 1
    }
}

// Scenario 3 - injected dispatcher, must NOT trigger the warning
suspend fun safeWithContext(ioDispatcher: CoroutineDispatcher) {
    withContext(ioDispatcher) {
        val x = 1
    }
}