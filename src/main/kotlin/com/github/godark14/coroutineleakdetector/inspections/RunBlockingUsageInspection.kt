package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtVisitorVoid

class RunBlockingUsageInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): KtVisitorVoid {
        return object : KtVisitorVoid() {
            override fun visitCallExpression(expression: KtCallExpression) {
                super.visitCallExpression(expression)

                val calleeName = expression.calleeExpression?.text ?: return
                if (calleeName != "runBlocking") return

                holder.registerProblem(
                    expression,
                    "runBlocking blocks the calling thread until its coroutine completes. " +
                            "If this runs on the main/UI thread (e.g. in an Activity, Fragment, or click listener), " +
                            "it can cause ANRs. It is generally safe in main() entry points or tests, " +
                            "but prefer launch/async with a proper CoroutineScope in Android UI code.",
                    ReplaceRunBlockingQuickFix()
                )
            }
        }
    }
}