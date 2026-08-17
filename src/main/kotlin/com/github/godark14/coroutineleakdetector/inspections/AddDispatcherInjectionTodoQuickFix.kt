package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.psiUtil.parents

class AddDispatcherInjectionTodoQuickFix : LocalQuickFix {

    override fun getFamilyName(): String =
        "Add TODO to inject this dispatcher"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element: PsiElement = descriptor.psiElement
        val expression = element as? KtExpression ?: return

        // Walk up to the enclosing withContext(...)/launch(...)/async(...) call,
        // or its dot-qualified form (e.g. "scope.launch(...)"), so the TODO
        // lands above the whole statement instead of inside the argument list.
        val enclosingCall = expression.parents
            .filterIsInstance<KtCallExpression>()
            .firstOrNull() ?: return

        val statementLevelNode = enclosingCall.parent
            .let { it as? KtDotQualifiedExpression ?: enclosingCall }

        val factory = KtPsiFactory(project)
        val comment = factory.createComment(
            "// TODO: Inject this dispatcher (e.g. via a DispatcherProvider) instead of hardcoding it, " +
                    "so tests can substitute a TestDispatcher."
        )

        val parent = statementLevelNode.parent
        parent.addBefore(comment, statementLevelNode)
        parent.addBefore(factory.createNewLine(), statementLevelNode)
    }
}