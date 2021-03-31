package com.anatawa12.autoToString.compiler

import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGetField
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.isClassWithFqName
import org.jetbrains.kotlin.ir.types.isNullableAny
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.FqNameUnsafe
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.constants.ArrayValue
import org.jetbrains.kotlin.resolve.constants.StringValue
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

fun IrType.isClassType(fqName: FqNameUnsafe): Boolean {
    if (this !is IrSimpleType) return false
    if (this.hasQuestionMark) return false
    return classifier.isClassWithFqName(fqName)
}

fun IrBuilderWithScope.irGetProperty(receiver: IrExpression, property: IrProperty): IrExpression {
    val backingField = property.backingField
    return if (property.modality == Modality.FINAL && backingField != null) {
        irGetField(receiver, backingField)
    } else {
        irCall(property.getter!!).apply {
            dispatchReceiver = receiver
        }
    }
}

data class AutoToStringAnnotation(val annotation: AnnotationDescriptor, val values: List<String>) {
    companion object {
        fun find(annotations: Annotations): AutoToStringAnnotation? {
            val annotation = annotations.findAnnotation(Symbols.autoToStringAnnotation) ?: return null
            val values = annotation.allValueArguments[Name.identifier("value")].safeAs<ArrayValue>()
                ?.value
                ?.map { it.value.safeAs<String>() ?: return null }
                ?: return null
            return AutoToStringAnnotation(annotation, values)
        }
    }
}
