package com.anatawa12.autoToString.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.isPrimitiveArray
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.utils.addToStdlib.cast

class ToStringGenerationVisitor(
    val pluginContext: IrPluginContext,
) : IrElementVisitorVoid {
    override fun visitElement(element: IrElement) = element.acceptChildrenVoid(this)

    override fun visitSimpleFunction(declaration: IrSimpleFunction) {
        processSimpleFunction(declaration)
        super.visitSimpleFunction(declaration)
    }

    fun processSimpleFunction(declaration: IrSimpleFunction) {
        @OptIn(ObsoleteDescriptorBasedAPI::class)
        val data = declaration.descriptor.getUserData(ToStringMethodData) ?: return

        val ownerClass = declaration.parent as IrClass
        val properties = data.properties.map { name -> ownerClass.properties.first { it.name.identifier == name } }

        val receiver = declaration.dispatchReceiverParameter!!

        declaration.body = DeclarationIrBuilder(pluginContext, declaration.symbol).run {
            irBlockBody {
                val concat = irConcat()
                concat.addArgument(irString(ownerClass.name.asString() + "("))
                val it = properties.iterator()
                while (it.hasNext()) {
                    val property = it.next()
                    val surroundedWith = findSurroundedWith(property)
                    concat.addArgument(irString(property.name.identifier + "="))

                    val irPropertyValue = irGetProperty(irGet(receiver), property)

                    if (surroundedWith == null || (surroundedWith.begin == "" && surroundedWith.end == "")) {
                        concat.addArgument(toStringOrArray(irPropertyValue))
                    } else if (surroundedWith.evenNull) {
                        concat.addArgument(irString(surroundedWith.begin))
                        concat.addArgument(toStringOrArray(irPropertyValue))
                        concat.addArgument(irString(surroundedWith.end))
                    } else {
                        // surround if not null

                        concat.addArgument(irBlock(resultType = context.irBuiltIns.stringType) {
                            val variable = irTemporary(irPropertyValue)
                            +irIfNull(context.irBuiltIns.stringType,
                                subject = irGet(variable),
                                thenPart = irString("null"),
                                elsePart = irConcat().apply {
                                    addArgument(irString(surroundedWith.begin))
                                    addArgument(toStringOrArray(irGet(variable)))
                                    addArgument(irString(surroundedWith.end))
                                },
                            )
                        })
                    }

                    if (it.hasNext()) concat.addArgument(irString(", "))
                }
                concat.addArgument(irString(")"))
                +irReturn(concat)
            }
        }
    }

    private fun IrBuilderWithScope.toStringOrArray(irPropertyValue: IrExpression): IrExpression {
        return if (irPropertyValue.type.isArray() || irPropertyValue.type.isNullableArray()
            || irPropertyValue.type.isPrimitiveArray())
            irCall(context.irBuiltIns.dataClassArrayMemberToStringSymbol,
                context.irBuiltIns.stringType).apply {
                putValueArgument(0, irPropertyValue)
            }
        else irPropertyValue
    }

    private fun findSurroundedWith(property: IrProperty): SurroundedWith? {
        val annotation = property.annotations.find { it.type.isClassType(Symbols.surroundedWithAnnotation.toUnsafe()) }
            ?: return findBuiltinSurroundedWith(property)
        var begin: String? = null
        var end: String? = null
        var evenNull = false
        for (valueParameter in annotation.symbol.owner.valueParameters) {
            when (valueParameter.name.asString()) {
                "begin" -> begin = annotation.getValueArgument(valueParameter.index).cast<IrConst<*>>().value.cast()
                "end" -> end = annotation.getValueArgument(valueParameter.index).cast<IrConst<*>>().value.cast()
                "evenNull" -> evenNull = annotation.getValueArgument(valueParameter.index)
                    ?.cast<IrConst<*>>()?.value?.cast() ?: evenNull
            }
        }
        return SurroundedWith(
            begin ?: error("begin not found: $annotation"),
            end ?: error("end not found: $annotation"),
            evenNull,
        )
    }

    private fun findBuiltinSurroundedWith(property: IrProperty): SurroundedWith? {
        val type = property.backingField?.type ?: property.getter!!.returnType
        if (type.makeNotNull().isChar()) return charSurroundedWith
        if (type.makeNotNull().isString()) return stringSurroundedWith
        return null
    }

    private data class SurroundedWith(
        val begin: String,
        val end: String,
        val evenNull: Boolean,
    )

    companion object {
        private val charSurroundedWith = SurroundedWith("'", "'", evenNull = false)
        private val stringSurroundedWith = SurroundedWith("\"", "\"", evenNull = false)
    }
}
