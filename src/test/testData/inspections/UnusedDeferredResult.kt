// Fake declarations to avoid any external dependency in the test

interface Deferred<T>

class CoroutineScope {
    fun <T> async(block: () -> T): Deferred<T> = object : Deferred<T> {} as Deferred<T>
    fun launch(block: () -> Unit) {
        block()
    }
}

fun <T> Deferred<T>.await(): T {
    val result: Any? = null
    @Suppress("UNCHECKED_CAST")
    return result as T
}

fun riskyOperation() {}

// Scenario 1 - standalone async{}, result unused, MUST trigger the warning
fun unusedResult(scope: CoroutineScope) {
    scope.<warning descr="The result of this async { } call is never used. If you don't call .await() on it, exceptions thrown inside will be silently swallowed until (if ever) awaited. Either call .await(), store the Deferred and await it later, or use launch { } if you don't need a result.">async {
        riskyOperation()
    }</warning>
}

// Scenario 2 - result assigned to a variable, must NOT trigger the warning
fun assignedResult(scope: CoroutineScope) {
    val deferred = scope.async {
        riskyOperation()
    }
}

// Scenario 3 - chained with .await(), must NOT trigger the warning
fun awaitedResult(scope: CoroutineScope) {
    scope.async {
        riskyOperation()
    }.await()
}