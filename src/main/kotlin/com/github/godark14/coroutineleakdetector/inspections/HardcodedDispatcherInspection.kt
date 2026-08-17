package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtVisitorVoid

class HardcodedDispatcherInspection : LocalInspectionTool() {

    private val contextSwitchingFunctions = setOf("withContext", "launch", "async")
    private val dispatcherNames = setOf("IO", "Main", "Default", "Unconfined")

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): KtVisitorVoid {
        return object : KtVisitorVoid() {
            override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
                super.visitDotQualifiedExpression(expression)

                val receiverText = expression.receiverExpression.text
                val selectorText = expression.selectorExpression?.text ?: return

                if (receiverText != "Dispatchers") return
                if (selectorText !in dispatcherNames) return

                // Only flag when this "Dispatchers.X" is used as a direct
                // argument to withContext/launch/async, i.e. hardcoded at
                // the call site rather than coming from an injected value.
                val valueArgument = expression.parent as? KtValueArgument ?: return
                val callExpression = valueArgument.parent?.parent as? KtCallExpression ?: return
                val calleeName = callExpression.calleeExpression?.text ?: return

                if (calleeName !in contextSwitchingFunctions) return

                holder.registerProblem(
                    expression,
                    "Hardcoding Dispatchers.$selectorText here makes this code harder to test deterministically. " +
                            "Inject the dispatcher (e.g. via a DispatcherProvider interface) instead, so tests can " +
                            "substitute a TestDispatcher and avoid flaky, non-deterministic test results.",
                    AddDispatcherInjectionTodoQuickFix()
                )
            }
        }
    }
}