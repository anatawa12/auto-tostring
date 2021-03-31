package com.anatawa12.autoToString

/**
 * The annotation to specify the class to be toString generated
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AutoToString(
    /**
     * names of properties to be included in toString.
     */
    vararg val value: String,
)

/**
 * The annotation to specify the property which will be surrounded.
 * This also can be used to disable automatic surrounding for string and char.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class SurroundedWith(
    val begin: String,
    val end: String,
    val evenNull: Boolean = false,
)
