package com.anatawa12.autoToString.compiler

import com.anatawa12.autoToString.compiler.AutoToStringErrors.FIELD_NOT_FOUND
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.lang.reflect.Constructor

class AutoToString {
    @Test
    fun test0() {
        @Suppress("LocalVariableName")
        RunningTestFactory.runTest(fileName("test0")) { loader ->
            class TestClass(name: String, vararg types: Class<*>) {
                private val constructor: Constructor<out Any> =
                    Class.forName(name, false, loader)
                        .getConstructor(*types)

                operator fun invoke(vararg args: Any?) = constructor.newInstance(*args)
            }

            // simple tests
            val NoFields = TestClass("NoFields")
            val SingleField = TestClass("SingleField", Any::class.java)
            val SingleProperty = TestClass("SingleProperty", Any::class.java)
            val IgnoredField = TestClass("IgnoredField", Any::class.java, Any::class.java)

            assertEquals("NoFields()", NoFields().toString())

            assertEquals("SingleField(value=test)", SingleField("test").toString())
            assertEquals("SingleField(value=null)", SingleField(null).toString())

            assertEquals("SingleProperty(value=test)", SingleProperty("test").toString())
            assertEquals("SingleProperty(value=null)", SingleProperty(null).toString())

            assertEquals("IgnoredField(value=test)", IgnoredField("test", "ignored").toString())
            assertEquals("IgnoredField(value=null)", IgnoredField(null, "ignored").toString())

            // array tests
            val VarargField = TestClass("VarargField", Array<Any>::class.java)
            val ArrayField = TestClass("ArrayField", Array<Any>::class.java)
            val PrimitiveArrayField = TestClass("PrimitiveArrayField", IntArray::class.java)
            val NullableArrayField = TestClass("NullableArrayField", Array<Any>::class.java)
            val NullablePrimitiveArrayField = TestClass("NullablePrimitiveArrayField", IntArray::class.java)

            assertEquals("VarargField(value=[test])", VarargField(arrayOf("test")).toString())
            assertEquals("VarargField(value=[null])", VarargField(arrayOf<Any?>(null)).toString())

            assertEquals("ArrayField(value=[test])", ArrayField(arrayOf("test")).toString())
            assertEquals("ArrayField(value=[null])", ArrayField(arrayOf<Any?>(null)).toString())

            assertEquals("PrimitiveArrayField(value=[0])", PrimitiveArrayField(intArrayOf(0)).toString())

            assertEquals("NullableArrayField(value=[test])", NullableArrayField(arrayOf("test")).toString())
            assertEquals("NullableArrayField(value=[null])", NullableArrayField(arrayOf<Any?>(null)).toString())
            assertEquals("NullableArrayField(value=null)", NullableArrayField(null).toString())

            assertEquals("NullablePrimitiveArrayField(value=[0])", NullablePrimitiveArrayField(intArrayOf(0)).toString())
            assertEquals("NullablePrimitiveArrayField(value=null)", NullablePrimitiveArrayField(null).toString())

            // typed tests
            val StringField = TestClass("StringField", String::class.java)
            val DisabledStringField = TestClass("DisabledStringField", String::class.java)
            val NullableStringField = TestClass("NullableStringField", String::class.java)
            val CharField = TestClass("CharField", Char::class.java)
            val DisabledCharField = TestClass("DisabledCharField", Char::class.java)
            val NullableCharField = TestClass("NullableCharField", Char::class.javaObjectType)

            assertEquals("StringField(value=\"value\")", StringField("value").toString())
            assertEquals("DisabledStringField(value=value)", DisabledStringField("value").toString())
            assertEquals("NullableStringField(value=\"value\")", NullableStringField("value").toString())
            assertEquals("NullableStringField(value=null)", NullableStringField(null).toString())

            assertEquals("CharField(value='c')", CharField('c').toString())
            assertEquals("DisabledCharField(value=c)", DisabledCharField('c').toString())
            assertEquals("NullableCharField(value='c')", NullableCharField('c').toString())
            assertEquals("NullableCharField(value=null)", NullableCharField(null).toString())

            // custom surrounding
            val CustomSurrounding = TestClass("CustomSurrounding", Any::class.java)
            val CustomSurroundingEvenNull = TestClass("CustomSurroundingEvenNull", Any::class.java)

            assertEquals("CustomSurrounding(value=<test>)", CustomSurrounding("test").toString())
            assertEquals("CustomSurrounding(value=null)", CustomSurrounding(null).toString())

            assertEquals("CustomSurroundingEvenNull(value=<test>)", CustomSurroundingEvenNull("test").toString())
            assertEquals("CustomSurroundingEvenNull(value=<null>)", CustomSurroundingEvenNull(null).toString())

            // extends tests
            val ExtendTestChild = TestClass("ExtendTestChild", Any::class.java, Any::class.java)

            assertEquals("ExtendTestChild(child=child, parent=parent)", ExtendTestChild("child", "parent").toString())
        }
    }

    @Test
    fun `no-field-0`() {
        CETestFactory.runTest(fileName("no-field-0")) {
            expectError(CompilerMessageSeverity.ERROR, FIELD_NOT_FOUND)
        }
    }

    private fun fileName(name: String): String {
        return "AutoToString.$name.kt"
    }
}
