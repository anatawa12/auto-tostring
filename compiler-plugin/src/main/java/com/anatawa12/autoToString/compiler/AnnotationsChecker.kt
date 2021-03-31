package com.anatawa12.autoToString.compiler

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.source.getPsi

class AnnotationsChecker : DeclarationChecker {
    override fun check(
        declaration: KtDeclaration,
        descriptor: DeclarationDescriptor,
        context: DeclarationCheckerContext,
    ) {
        if (descriptor !is ClassDescriptor) return
        val annotation = AutoToStringAnnotation.find(descriptor.annotations) ?: return

        for (value in annotation.values) {
            val property = descriptor.unsubstitutedMemberScope.getContributedDescriptors(DescriptorKindFilter.VARIABLES)
                .find { it.name.asString() == value }
            if (property == null)
                context.trace.report(AutoToStringErrors.FIELD_NOT_FOUND.on(
                    annotation.annotation.source.getPsi() ?: declaration, value))
        }
    }
}
