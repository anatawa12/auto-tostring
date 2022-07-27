import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    `maven-publish`
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
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")

    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")
    kapt("com.google.auto.service:auto-service:1.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}


publishing.publications.create<MavenPublication>("maven") {
    from(components["java"])
    configurePom()
}

java {
    withSourcesJar()
    withJavadocJar()
}
