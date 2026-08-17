import androidx.lifecycle.ViewModel

class Job {
    fun cancel() {}
}

// Scenario 1 - Job created, never cancelled, MUST trigger the warning
class UnsafeViewModel : ViewModel() {
    <warning descr="This Job is never cancelled. Without a matching 'job.cancel()' call in onCleared(), coroutines launched on it may keep running after this ViewModel is destroyed.">private val job = Job()</warning>

    fun onCleared() {
    }
}

// Scenario 2 - Job created and properly cancelled, must NOT trigger the warning
class SafeViewModel : ViewModel() {
    private val job = Job()

    fun onCleared() {
        job.cancel()
    }
}

// Scenario 3 - Job created, no cleanup method at all, MUST trigger the warning
class NoCleanupViewModel : ViewModel() {
    <warning descr="This Job is never cancelled. Without a matching 'job.cancel()' call in onCleared(), coroutines launched on it may keep running after this ViewModel is destroyed.">private val job = Job()</warning>
}