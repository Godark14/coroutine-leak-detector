package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPsiFactory

class ReplaceGlobalScopeQuickFix : LocalQuickFix {

    override fun getFamilyName(): String =
        "Add TODO to migrate away from GlobalScope"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element: PsiElement = descriptor.psiElement
        val expression = element as? KtExpression ?: return

        val factory = KtPsiFactory(project)
        val comment = factory.createComment(
            "// TODO: Replace GlobalScope with a lifecycle-aware scope (viewModelScope, lifecycleScope) or a custom CoroutineScope you cancel explicitly."
        )

        val parent = expression.parent
        parent.addBefore(comment, expression)
        parent.addBefore(factory.createNewLine(), expression)
    }
}