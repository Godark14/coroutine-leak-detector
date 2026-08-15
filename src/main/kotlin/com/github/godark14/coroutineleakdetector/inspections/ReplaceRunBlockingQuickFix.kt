package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPsiFactory

class ReplaceRunBlockingQuickFix(
    private val isLikelyUiCode: Boolean,
    private val suggestedReplacement: String
) : LocalQuickFix {

    override fun getFamilyName(): String =
        if (isLikelyUiCode) {
            "Add TODO to replace runBlocking with $suggestedReplacement"
        } else {
            "Add TODO to review runBlocking usage"
        }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element: PsiElement = descriptor.psiElement
        val expression = element as? KtExpression ?: return

        val commentText = if (isLikelyUiCode) {
            "// TODO: This runBlocking is likely on the main thread here. " +
                    "Replace with $suggestedReplacement.launch { ... } instead."
        } else {
            "// TODO: Confirm this runBlocking is not executed on the main/UI thread. " +
                    "If it is, replace it with launch/async on a proper CoroutineScope."
        }

        val factory = KtPsiFactory(project)
        val comment = factory.createComment(commentText)

        val parent = expression.parent
        parent.addBefore(comment, expression)
        parent.addBefore(factory.createNewLine(), expression)
    }
}