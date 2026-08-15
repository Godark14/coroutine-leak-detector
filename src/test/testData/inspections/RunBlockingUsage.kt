// Fake declaration to avoid any external dependency in the test
fun <T> runBlocking(block: () -> T): T = block()

fun onClick() {
    <warning descr="runBlocking blocks the calling thread until its coroutine completes. If this runs on the main/UI thread, it can cause ANRs. It is generally safe in main() entry points or tests, but prefer launch/async with a proper CoroutineScope in Android UI code.">runBlocking {
        val x = 1
    }</warning>
}