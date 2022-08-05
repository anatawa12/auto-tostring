plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("multiplatform") version "1.6.0" apply false
    kotlin("plugin.allopen") version "1.6.0" apply false
    id("org.jetbrains.intellij") version "1.8.0" apply false
    id("com.github.johnrengelman.shadow") version "6.1.0" apply false
}

group = "com.anatawa12.auto-tostring"
version = property("version")!!

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

allprojects {
    afterEvaluate {
        java {
            targetCompatibility = JavaVersion.VERSION_1_8
            sourceCompatibility = JavaVersion.VERSION_1_8
        }
    }
}
