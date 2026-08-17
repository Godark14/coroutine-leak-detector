package com.github.godark14.coroutineleakdetector.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.psiUtil.parents

class AddCancelCallQuickFix(
    private val cleanupMethodName: String
) : LocalQuickFix {

    override fun getFamilyName(): String =
        "Add cancel() call in $cleanupMethodName()"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element: PsiElement = descriptor.psiElement
        val property = element as? KtProperty ?: return
        val propertyName = property.name ?: return

        val containingClass = property.parents
            .filterIsInstance<KtClass>()
            .firstOrNull() ?: return

        val classBody = containingClass.body as? KtClassBody ?: return
        val factory = KtPsiFactory(project)

        val existingMethod = classBody.functions.firstOrNull { it.name == cleanupMethodName }

        if (existingMethod != null) {
            // Insert "<property>.cancel()" as the last statement in the existing method body
            val body = existingMethod.bodyBlockExpression ?: return
            val cancelStatement = factory.createExpression("$propertyName.cancel()")
            val rBrace = body.rBrace
            if (rBrace != null) {
                body.addBefore(cancelStatement, rBrace)
                body.addBefore(factory.createNewLine(), rBrace)
            }
        } else {
            // No cleanup method at all: fall back to a TODO comment above the property
            val comment = factory.createComment(
                "// TODO: Override $cleanupMethodName() and call $propertyName.cancel() there to avoid leaking coroutines."
            )
            val parent = property.parent
            parent.addBefore(comment, property)
            parent.addBefore(factory.createNewLine(), property)
        }
    }
}