package com.anatawa12.autoToString.compiler

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension

class ToStringResolveExtension : SyntheticResolveExtension {
    override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> {
        thisDescriptor.annotations.findAnnotation(Symbols.autoToStringAnnotation) ?: return emptyList()
        return listOf(Name.identifier("toString"))
    }

    override fun generateSyntheticMethods(
        thisDescriptor: ClassDescriptor,
        name: Name,
        bindingContext: BindingContext,
        fromSupertypes: List<SimpleFunctionDescriptor>,
        result: MutableCollection<SimpleFunctionDescriptor>,
    ) {
        val annotation = AutoToStringAnnotation.find(thisDescriptor.annotations) ?: return
        if (name.asString() != "toString") return

        val desc = SimpleFunctionDescriptorImpl.create(
            thisDescriptor,
            Annotations.EMPTY,
            name,
            CallableMemberDescriptor.Kind.SYNTHESIZED,
            thisDescriptor.source,
        )

        desc.initialize(
            /* extension receiver = */null,
            thisDescriptor.thisAsReceiverParameter,
            /* type params = */emptyList(),
            /* value params = */emptyList(),
            thisDescriptor.builtIns.stringType,
            Modality.OPEN,
            DescriptorVisibilities.PUBLIC,
            mapOf(
                ToStringMethodData to ToStringMethodData(annotation.values)
            )
        )
        result.add(desc)
    }
}
