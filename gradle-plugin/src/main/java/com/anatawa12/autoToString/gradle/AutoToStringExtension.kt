package com.anatawa12.autoToString.gradle

import org.gradle.api.Project
import org.gradle.util.GUtil

open class AutoToStringExtension(val project: Project) {
    private fun get(map: Map<String, Any>, defaultConfiguration: String): String {
        val sourceSetName = map["sourceSet"] ?: ""
        val configurationName = map["configuration"] ?: defaultConfiguration

        return GUtil.toLowerCamelCase("$sourceSetName $configurationName")
    }

    fun addLib() =
        addLib(mapOf())

    fun addLib(sourceSet: String) =
        addLib(mapOf("sourceSet" to sourceSet))

    fun addLib(map: Map<String, Any>) {
        project.dependencies.add(get(map, "compileOnly"),
            "com.anatawa12.auto-tostring:lib:${Constants.version}")
    }
}
