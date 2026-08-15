package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtPsiFactory

class ReplaceGlobalScopeWithLifecycleAwareScopeQuickFix(
    private val replacement: String
) : LocalQuickFix {

    override fun getFamilyName(): String =
        "Replace GlobalScope with $replacement"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element: PsiElement = descriptor.psiElement
        val expression = element as? KtDotQualifiedExpression ?: return

        val factory = KtPsiFactory(project)
        val newReceiver = factory.createExpression(replacement)
        val oldReceiver = expression.receiverExpression
        oldReceiver.replace(newReceiver)
    }
}