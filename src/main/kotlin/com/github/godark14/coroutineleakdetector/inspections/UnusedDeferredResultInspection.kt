package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtVisitorVoid

class UnusedDeferredResultInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): KtVisitorVoid {
        return object : KtVisitorVoid() {
            override fun visitCallExpression(expression: KtCallExpression) {
                super.visitCallExpression(expression)

                val calleeName = expression.calleeExpression?.text ?: return
                if (calleeName != "async") return

                // The statement-level node is either the call itself
                // ("async { ... }") or its enclosing dot-qualified expression
                // ("scope.async { ... }") when async is called on a receiver.
                val statementLevelNode = expression.parent
                    .let { it as? KtDotQualifiedExpression ?: expression }

                val isStandaloneStatement = statementLevelNode.parent is KtBlockExpression

                if (!isStandaloneStatement) return

                holder.registerProblem(
                    expression,
                    "The result of this async { } call is never used. If you don't call .await() on it, " +
                            "exceptions thrown inside will be silently swallowed until (if ever) awaited. " +
                            "Either call .await(), store the Deferred and await it later, or use launch { } " +
                            "if you don't need a result.",
                    ReplaceAsyncWithLaunchQuickFix()
                )
            }
        }
    }
}