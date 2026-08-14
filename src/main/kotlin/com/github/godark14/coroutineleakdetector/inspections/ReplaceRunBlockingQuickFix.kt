package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPsiFactory

class ReplaceRunBlockingQuickFix : LocalQuickFix {

    override fun getFamilyName(): String =
        "Add TODO to review runBlocking usage"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element: PsiElement = descriptor.psiElement
        val expression = element as? KtExpression ?: return

        val factory = KtPsiFactory(project)
        val comment = factory.createComment(
            "// TODO: Confirm this runBlocking is not executed on the main/UI thread. " +
                    "If it is, replace it with launch/async on a proper CoroutineScope."
        )

        val parent = expression.parent
        parent.addBefore(comment, expression)
        parent.addBefore(factory.createNewLine(), expression)
    }
}