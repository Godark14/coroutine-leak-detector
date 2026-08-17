package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.parents

class UncancelledScopeInspection : LocalInspectionTool() {

    private val scopeCreatingFunctions = setOf("Job", "SupervisorJob", "CoroutineScope")

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): KtVisitorVoid {
        return object : KtVisitorVoid() {
            override fun visitProperty(property: KtProperty) {
                super.visitProperty(property)

                val initializer = property.initializer as? KtCallExpression ?: return
                val calleeName = initializer.calleeExpression?.text ?: return
                if (calleeName !in scopeCreatingFunctions) return

                val propertyName = property.name ?: return

                val containingClass = property.parents
                    .filterIsInstance<KtClass>()
                    .firstOrNull() ?: return

                val suggestedScope = ScopeContextDetector.detectForClass(containingClass)
                if (suggestedScope == ScopeContextDetector.SuggestedScope.NONE) return

                if (hasCancelCallInCleanupMethod(containingClass, suggestedScope.cleanupMethodName, propertyName)) {
                    return
                }

                holder.registerProblem(
                    property,
                    "This ${calleeName} is never cancelled. Without a matching '$propertyName.cancel()' call " +
                            "in ${suggestedScope.cleanupMethodName}(), coroutines launched on it may keep running " +
                            "after this ${if (suggestedScope == ScopeContextDetector.SuggestedScope.VIEW_MODEL) "ViewModel" else "component"} is destroyed.",
                    AddCancelCallQuickFix(suggestedScope.cleanupMethodName)
                )
            }
        }
    }

    private fun hasCancelCallInCleanupMethod(
        containingClass: KtClass,
        cleanupMethodName: String,
        propertyName: String
    ): Boolean {
        val classBody = containingClass.body as? KtClassBody ?: return false
        val cleanupMethod = classBody.functions.firstOrNull { it.name == cleanupMethodName } ?: return false

        var found = false
        cleanupMethod.accept(object : org.jetbrains.kotlin.psi.KtTreeVisitorVoid() {
            override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
                super.visitDotQualifiedExpression(expression)
                val receiverText = expression.receiverExpression.text
                val selectorText = expression.selectorExpression?.text ?: return
                if (receiverText == propertyName && selectorText.startsWith("cancel")) {
                    found = true
                }
            }
        })
        return found
    }
}