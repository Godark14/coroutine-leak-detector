package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtPsiFactory

class ReplaceAsyncWithLaunchQuickFix : LocalQuickFix {

    override fun getFamilyName(): String =
        "Replace async with launch (result is unused)"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element: PsiElement = descriptor.psiElement
        val expression = element as? KtCallExpression ?: return
        val callee = expression.calleeExpression ?: return

        val factory = KtPsiFactory(project)
        val newCallee = factory.createExpression("launch")
        callee.replace(newCallee)
    }
}