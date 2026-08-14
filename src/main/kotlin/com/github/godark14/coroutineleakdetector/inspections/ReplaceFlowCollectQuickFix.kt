package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPsiFactory

class ReplaceFlowCollectQuickFix : LocalQuickFix {

    override fun getFamilyName(): String =
        "Add TODO to wrap collection with repeatOnLifecycle"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element: PsiElement = descriptor.psiElement
        val expression = element as? KtExpression ?: return

        // On remonte à l'expression englobante complète (flow.collect { ... })
        // plutôt que de s'insérer juste avant "collect" isolé.
        val targetExpression = expression.parent as? KtDotQualifiedExpression ?: expression

        val factory = KtPsiFactory(project)
        val comment = factory.createComment(
            "// TODO: Wrap this Flow collection with repeatOnLifecycle(Lifecycle.State.STARTED) { ... } or use flowWithLifecycle(lifecycle) to avoid leaking outside the intended lifecycle."
        )

        val parent = targetExpression.parent
        parent.addBefore(comment, targetExpression)
        parent.addBefore(factory.createNewLine(), targetExpression)
    }
}