package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
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

                if (!isWrappedBySafeFunction(expression)) {
                    holder.registerProblem(
                        expression,
                        "Collecting a Flow here may run outside the intended lifecycle and leak. " +
                                "Wrap it with repeatOnLifecycle(Lifecycle.State.STARTED) or use flowWithLifecycle(...).",
                        ReplaceFlowCollectQuickFix()
                    )
                }
            }
        }
    }

    /**
     * Remonte tous les ancêtres PSI de l'appel collect/collectLatest
     * et vérifie si l'un d'eux est repeatOnLifecycle(...) ou flowWithLifecycle(...).
     */
    private fun isWrappedBySafeFunction(expression: KtCallExpression): Boolean {
        for (parent in expression.parents) {
            // Cas 1 : collect { } est imbriqué dans repeatOnLifecycle { ... }
            if (parent is KtCallExpression) {
                val name = parent.calleeExpression?.text
                if (name in safeWrapperFunctions) return true
            }

            // Cas 2 : flow.flowWithLifecycle(...).collect { } — chaînage direct
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
}