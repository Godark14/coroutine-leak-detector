import androidx.lifecycle.ViewModel

fun <T> runBlocking(block: () -> T): T = block()

class MyViewModel : ViewModel() {
    fun loadData() {
        runBlocking {
            val x = 1
        }
    }
}