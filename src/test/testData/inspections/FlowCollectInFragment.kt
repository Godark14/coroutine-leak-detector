import androidx.fragment.app.Fragment

interface Flow<T>

fun <T> Flow<T>.collect(action: (T) -> Unit) {
    // no-op fake
}

class MyFragment : Fragment() {
    suspend fun observeData(flow: Flow<Int>) {
        flow.collect { value ->
            val x = value
        }
    }
}