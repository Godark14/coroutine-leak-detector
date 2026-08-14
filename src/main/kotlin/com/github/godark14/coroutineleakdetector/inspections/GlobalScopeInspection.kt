package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtVisitorVoid

class GlobalScopeInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): KtVisitorVoid {
        return object : KtVisitorVoid() {
            override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
                super.visitDotQualifiedExpression(expression)

                val receiverText = expression.receiverExpression.text
                val selectorText = expression.selectorExpression?.text ?: return

                if (receiverText == "GlobalScope" &&
                    (selectorText.startsWith("launch") || selectorText.startsWith("async"))
                ) {
                    holder.registerProblem(
                        expression,
                        "Avoid using GlobalScope: this coroutine is never cancelled automatically and can leak. Prefer a lifecycle-aware scope (viewModelScope, lifecycleScope) or a custom CoroutineScope you cancel explicitly.",
                        ReplaceGlobalScopeQuickFix()
                    )
                }
            }
        }
    }
}