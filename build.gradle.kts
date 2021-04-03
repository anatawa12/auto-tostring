plugins {
    kotlin("jvm") version "1.4.32"
    kotlin("multiplatform") version "1.4.30" apply false
    kotlin("plugin.allopen") version "1.4.30" apply false
    id("org.jetbrains.intellij") version "0.6.5" apply false
    id("com.github.johnrengelman.shadow") version "6.1.0" apply false
}

group = "com.anatawa12.auto-tostring"
version = "1.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}
