// Déclarations factices pour éviter toute dépendance externe dans le test
object GlobalScope {
    fun launch(block: () -> Unit) {
        block()
    }
}

class CoroutineScope {
    fun launch(block: () -> Unit) {
        block()
    }
}

fun unsafeLaunch() {
    <warning descr="Avoid using GlobalScope: this coroutine is never cancelled automatically and can leak. Prefer a lifecycle-aware scope (viewModelScope, lifecycleScope) or a custom CoroutineScope you cancel explicitly.">GlobalScope.launch {
        val x = 1
    }</warning>
}

fun safeLaunch(scope: CoroutineScope) {
    scope.launch {
        val x = 1
    }
}