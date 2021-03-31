package com.anatawa12.autoToString.compiler

import com.anatawa12.autoToString.compiler.AutoToStringErrors.FIELD_NOT_FOUND
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory1
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.diagnostics.Severity
import org.jetbrains.kotlin.diagnostics.rendering.DefaultErrorMessages
import org.jetbrains.kotlin.diagnostics.rendering.DiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.rendering.Renderers

object AutoToStringErrors {
    @JvmField
    val FIELD_NOT_FOUND = DiagnosticFactory1.create<PsiElement, String>(Severity.ERROR)

    init {
        Errors.Initializer.initializeFactoryNamesAndDefaultErrorMessages(
            AutoToStringErrors::class.java,
            AutoToStringErrorsRendering)
    }
}

object AutoToStringErrorsRendering : DefaultErrorMessages.Extension {
    private val MAP = DiagnosticFactoryToRendererMap("AutoToString")

    init {
        // @GenerateVisitor
        MAP.put(
            FIELD_NOT_FOUND,
            "field not found: {0}",
            Renderers.TO_STRING
        )
    }

    override fun getMap(): DiagnosticFactoryToRendererMap = MAP
}
