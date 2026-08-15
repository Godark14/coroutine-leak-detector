package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPsiFactory

class WrapWithRepeatOnLifecycleQuickFix : LocalQuickFix {

    override fun getFamilyName(): String =
        "Wrap with repeatOnLifecycle(Lifecycle.State.STARTED)"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element: PsiElement = descriptor.psiElement
        val expression = element as? KtExpression ?: return

        // Target the full "flow.collect { ... }" expression, not just "collect"
        val targetExpression = expression.parent as? KtDotQualifiedExpression ?: expression

        val factory = KtPsiFactory(project)
        val wrapped = factory.createExpression(
            "repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {\n${targetExpression.text}\n}"
        )

        targetExpression.replace(wrapped)
    }
}