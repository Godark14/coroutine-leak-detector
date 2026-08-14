// Déclarations factices pour éviter toute dépendance externe dans le test

interface Flow<T>

fun interface FlowCollector<T> {
    fun emit(value: T)
}

fun <T> Flow<T>.collect(action: (T) -> Unit) {
    // no-op factice
}

fun <T> Flow<T>.collectLatest(action: (T) -> Unit) {
    // no-op factice
}

fun <T> Flow<T>.flowWithLifecycle(dummy: Int): Flow<T> = this

fun repeatOnLifecycle(state: Int, block: () -> Unit) {
    block()
}

// Scénario 1 — non protégé, DOIT déclencher le warning
fun unsafeCollect(flow: Flow<Int>) {
    flow.<warning descr="Collecting a Flow here may run outside the intended lifecycle and leak. Wrap it with repeatOnLifecycle(Lifecycle.State.STARTED) or use flowWithLifecycle(...).">collect { value ->
        val x = value
    }</warning>
}

// Scénario 2 — protégé par repeatOnLifecycle, ne DOIT PAS déclencher le warning
fun safeCollectRepeatOnLifecycle(flow: Flow<Int>) {
    repeatOnLifecycle(1) {
        flow.collect { value ->
            val x = value
        }
    }
}

// Scénario 3 — protégé par flowWithLifecycle chaîné, ne DOIT PAS déclencher le warning
fun safeCollectFlowWithLifecycle(flow: Flow<Int>) {
    flow.flowWithLifecycle(1).collect { value ->
        val x = value
    }
}