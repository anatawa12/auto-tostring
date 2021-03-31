package com.anatawa12.autoToString.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class AutoToStringGradlePlugin : KotlinCompilerPluginSupportPlugin {
    lateinit var providers: ProviderFactory

    override fun apply(target: Project) {
        providers = target.providers

        val extension = AutoToStringExtension(target)
        target.extensions.add("autoToString", extension)
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        return providers.provider(::emptyList)
    }

    override fun getCompilerPluginId(): String = "auto-tostring"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = "com.anatawa12.auto-tostring",
        artifactId = "compiler-plugin-embeddable",
        version = Constants.version,
    )

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true
}
