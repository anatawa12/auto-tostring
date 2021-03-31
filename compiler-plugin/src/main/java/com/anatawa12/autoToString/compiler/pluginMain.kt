package com.anatawa12.autoToString.compiler

import com.google.auto.service.AutoService
import com.intellij.mock.MockProject
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.jvm.compiler.report
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension

@AutoService(CommandLineProcessor::class)
class AutoToStringCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String get() = "auto-tostring"
    override val pluginOptions: Collection<AbstractCliOption> get() = emptyList()
}

@AutoService(ComponentRegistrar::class)
class AutoToStringComponentRegistrar : ComponentRegistrar {
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        // TODO: error for js legacy backend
        if (configuration.get(JVMConfigurationKeys.IR) == false)
            configuration.report(CompilerMessageSeverity.ERROR,
                "ir compiler is required for auto-tostring. Please enable with '-Xuse-ir'.")

        IrGenerationExtension.registerExtension(project, AutoToStringIrGenerationExtension())
        StorageComponentContainerContributor.registerExtension(project,
            AutoToStringStorageComponentContainerContributor())
        SyntheticResolveExtension.registerExtension(project, ToStringResolveExtension())
    }
}

class AutoToStringStorageComponentContainerContributor : StorageComponentContainerContributor {
    override fun registerModuleComponents(
        container: StorageComponentContainer,
        platform: TargetPlatform,
        moduleDescriptor: ModuleDescriptor,
    ) {
        container.useInstance(AnnotationsChecker())
    }
}

class AutoToStringIrGenerationExtension() : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        ToStringGenerationVisitor(pluginContext).also { visitor ->
            for (file in moduleFragment.files) {
                file.acceptVoid(visitor)
            }
        }
    }
}
