package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.roots.TestSourcesFilter
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.parents

class FlowCollectWithoutLifecycleInspection : LocalInspectionTool() {

    private val flowCollectFunctions = setOf("collect", "collectLatest")
    private val safeWrapperFunctions = setOf("repeatOnLifecycle", "flowWithLifecycle")

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): KtVisitorVoid {
        return object : KtVisitorVoid() {
            override fun visitCallExpression(expression: KtCallExpression) {
                super.visitCallExpression(expression)

                val calleeName = expression.calleeExpression?.text ?: return
                if (calleeName !in flowCollectFunctions) return

                if (isWrappedBySafeFunction(expression)) return
                if (isInTestSources(expression)) return
                if (!isAndroidLifecycleLibraryAvailable(expression)) return

                holder.registerProblem(
                    expression,
                    "Collecting a Flow here may run outside the intended lifecycle and leak. " +
                            "Wrap it with repeatOnLifecycle(Lifecycle.State.STARTED) or use flowWithLifecycle(...).",
                    ReplaceFlowCollectQuickFix()
                )
            }
        }
    }

    private fun isWrappedBySafeFunction(expression: KtCallExpression): Boolean {
        for (parent in expression.parents) {
            if (parent is KtCallExpression) {
                val name = parent.calleeExpression?.text
                if (name in safeWrapperFunctions) return true
            }
            if (parent is KtDotQualifiedExpression && parent.selectorExpression == expression) {
                val receiver = parent.receiverExpression
                val receiverCallName = when (receiver) {
                    is KtCallExpression -> receiver.calleeExpression?.text
                    is KtDotQualifiedExpression ->
                        (receiver.selectorExpression as? KtCallExpression)?.calleeExpression?.text
                    else -> null
                }
                if (receiverCallName in safeWrapperFunctions) return true
            }
        }
        return false
    }

    /**
     * Skip inspection entirely for test sources: lifecycle-awareness concerns
     * are much less relevant (and often noisy/false-positive) in unit or
     * instrumented tests.
     */
    private fun isInTestSources(expression: KtCallExpression): Boolean {
        val virtualFile = expression.containingFile.virtualFile ?: return false
        return TestSourcesFilter.isTestSources(virtualFile, expression.project)
    }

    /**
     * Skip inspection if the module doesn't even have the Android Lifecycle
     * library on its classpath (e.g. a pure Kotlin/JVM backend module).
     * Suggesting repeatOnLifecycle/flowWithLifecycle wouldn't make sense there.
     */
    private fun isAndroidLifecycleLibraryAvailable(expression: KtCallExpression): Boolean {
        val project = expression.project
        val scope = GlobalSearchScope.allScope(project)
        val lifecycleClass = JavaPsiFacade.getInstance(project)
            .findClass("androidx.lifecycle.Lifecycle", scope)
        return lifecycleClass != null
    }
}