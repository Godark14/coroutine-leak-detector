package com.github.godark14.coroutineleakdetector.inspections

import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.psiUtil.parents

object ScopeContextDetector {

    private val viewModelSupertypes = setOf("ViewModel", "AndroidViewModel")
    private val lifecycleOwnerSupertypes = setOf(
        "Fragment", "AppCompatActivity", "ComponentActivity", "FragmentActivity"
    )

    enum class SuggestedScope(val replacement: String, val cleanupMethodName: String) {
        VIEW_MODEL("viewModelScope", "onCleared"),
        LIFECYCLE_OWNER("lifecycleScope", "onDestroy"),
        NONE("", "")
    }

    fun detect(element: KtElement): SuggestedScope {
        val containingClass = element.parents
            .filterIsInstance<KtClass>()
            .firstOrNull() ?: return SuggestedScope.NONE

        return detectForClass(containingClass)
    }

    fun detectForClass(containingClass: KtClass): SuggestedScope {
        val superNames = containingClass.superTypeListEntries
            .mapNotNull { it.typeReference?.text }

        return when {
            superNames.any { name -> viewModelSupertypes.any { name.startsWith(it) } } ->
                SuggestedScope.VIEW_MODEL
            superNames.any { name -> lifecycleOwnerSupertypes.any { name.startsWith(it) } } ->
                SuggestedScope.LIFECYCLE_OWNER
            else -> SuggestedScope.NONE
        }
    }
}