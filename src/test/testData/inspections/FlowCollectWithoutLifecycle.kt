// Fake declarations to avoid any external dependency in the test

interface Flow<T>

fun interface FlowCollector<T> {
    fun emit(value: T)
}

fun <T> Flow<T>.collect(action: (T) -> Unit) {
    // no-op fake
}

fun <T> Flow<T>.collectLatest(action: (T) -> Unit) {
    // no-op fake
}

fun <T> Flow<T>.flowWithLifecycle(dummy: Int): Flow<T> = this

fun repeatOnLifecycle(state: Int, block: () -> Unit) {
    block()
}

// Scenario 1 - unprotected, MUST trigger the warning
fun unsafeCollect(flow: Flow<Int>) {
    flow.<warning descr="Collecting a Flow here may run outside the intended lifecycle and leak. Wrap it with repeatOnLifecycle(Lifecycle.State.STARTED) or use flowWithLifecycle(...).">collect { value ->
        val x = value
    }</warning>
}

// Scenario 2 - protected by repeatOnLifecycle, must NOT trigger the warning
fun safeCollectRepeatOnLifecycle(flow: Flow<Int>) {
    repeatOnLifecycle(1) {
        flow.collect { value ->
            val x = value
        }
    }
}

// Scenario 3 - protected by chained flowWithLifecycle, must NOT trigger the warning
fun safeCollectFlowWithLifecycle(flow: Flow<Int>) {
    flow.flowWithLifecycle(1).collect { value ->
        val x = value
    }
}