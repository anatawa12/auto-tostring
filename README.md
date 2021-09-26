Auto ToString Kotlin Compiler Plugin
====

[![a12 maintenance: Slowly](https://api.anatawa12.com/short/a12-slowly-svg)](https://api.anatawa12.com/short/a12-slowly-doc)
<!--[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/com/anatawa12/auto-visitor/com.anatawa12.auto-visitor.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=gradle&logo=gradle)](https://plugins.gradle.org/plugin/com.anatawa12.auto-visitor)-->

A kotlin compiler plugin to generate toString like data class.

## How to use

First, you need to apply this gradle plugin

```kotlin
plugins {
 id("org.jetbrains.kotlin.jvm") version "<kotlin version>"
 id("com.anatawa12.auto-tostring") version "<version>"
}
```

To add compile-time library, add code below:

```groovy
autoToString {
 // for compileOnly configuration
 addLib()
 // for <source-set>CompileOnly configuration
 addLib "sourceSet"
 addLib sourceSet: "sourceSet"
 // for your configuration
 addLib configuration: "yourConfiguration"
}
```

To generate toString function, add ``@AutoToString`` annotation to class.

## Extra features

For `kotlin.String` or string it typed fields, This plugin surrounded the value with `"`
like `SomeClass(value="string body")` and also surrounded with `'` for `kotlin.Char`s.

You can customize wrapping with `@SurroundedWith(begin=, end=)`. see KDoc for more details.
