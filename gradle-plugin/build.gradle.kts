import com.anatawa12.compileTimeConstant.CreateConstantsTask

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("com.anatawa12:compile-time-constant:1.0.2")
    }
}

plugins {
    id("com.gradle.plugin-publish") version "0.14.0"
    kotlin("kapt")
    `kotlin-dsl`
    `java-gradle-plugin`
}

apply(plugin = "com.anatawa12.compile-time-constant")

group = project(":").group
version = project(":").version

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin-api"))
    compileOnly("com.google.auto.service:auto-service-annotations:1.0-rc7")
    kapt("com.google.auto.service:auto-service:1.0-rc7")
}

gradlePlugin {
    plugins {
        register("auto-tostring") {
            displayName = "Auto toString"
            description = "A kotlin compiler plugin to generate toString()."
            id = "com.anatawa12.auto-tostring"
            implementationClass = "com.anatawa12.autoToString.gradle.AutoToStringGradlePlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/anatawa12/auto-tostring"
    vcsUrl = "https://github.com/anatawa12/auto-tostring"
    tags = listOf("kotlin", "toString", "kotlin-compiler", "compiler-plugin")
}

fun Project.compileTimeConstant(configure: com.anatawa12.compileTimeConstant.CompileTimeConstantExtension.() -> Unit): Unit =
    (this as ExtensionAware).extensions.configure("compileTimeConstant", configure)

compileTimeConstant {
    alwaysGenerateJarFile = true
}

val createCompileTimeConstant: CreateConstantsTask by tasks

createCompileTimeConstant.apply {
    constantsClass = "com.anatawa12.autoToString.gradle.Constants"
    values(mapOf(
        "version" to version.toString()
    ))
}

tasks.compileKotlin.get().dependsOn(createCompileTimeConstant)
tasks.compileKotlin.get().kotlinOptions.freeCompilerArgs = listOf("-XXLanguage:+TrailingCommas")

apply(from = "${rootProject.projectDir}/gradle-scripts/publish-to-central-java.gradle.kts")

tasks.withType<PublishToMavenRepository>().configureEach {
    onlyIf {
        if (repository.name == "mavenCentral") {
            publication.name != "auto-tostringPluginMarkerMaven"
                    && publication.name != "pluginMaven"
        } else {
            true
        }
    }
}
