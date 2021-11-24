import com.anatawa12.compileTimeConstant.CreateConstantsTask

plugins {
    id("com.gradle.plugin-publish") version "0.18.0"
    id("com.anatawa12.compile-time-constant") version "1.0.5"
    kotlin("kapt")
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

group = project(":").group
version = project(":").version

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin-api"))
    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")
    kapt("com.google.auto.service:auto-service:1.0")
}

gradlePlugin {
    isAutomatedPublishing = false

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

publishing.publications.create<MavenPublication>("maven") {
    from(components["java"])
    configurePom()
}

java {
    withSourcesJar()
    withJavadocJar()
}
