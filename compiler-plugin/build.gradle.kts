import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
}

group = project(":").group
version = project(":").version

tasks.withType<KotlinCompile>() {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            "-Xjvm-default=compatibility",
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }
}

kapt.includeCompileClasspath = false

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("compiler"))

    testImplementation(project(":lib"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")

    compileOnly("com.google.auto.service:auto-service-annotations:1.0-rc7")
    kapt("com.google.auto.service:auto-service:1.0-rc7")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

apply(from = "${rootProject.projectDir}/gradle-scripts/publish-to-central-java.gradle.kts")
