import com.anatawa12.autoToString.*;

// simple tests

@AutoToString
class NoFields

@AutoToString("value")
class SingleField(val value: Any?)

@AutoToString("value")
class SingleProperty(value: Any?) {
    private val value1 = value
    val value: Any? get() = value1
}

@AutoToString("value")
class IgnoredField(val value: Any?, val value2: Any?)

// array tests

@AutoToString("value")
class VarargField(vararg val value: Any?)

@AutoToString("value")
class ArrayField(val value: Array<Any?>)

@AutoToString("value")
class PrimitiveArrayField(val value: IntArray)

@AutoToString("value")
class NullableArrayField(val value: Array<Any?>?)

@AutoToString("value")
class NullablePrimitiveArrayField(val value: IntArray?)

/// typed tests

@AutoToString("value")
class StringField(val value: String)

@AutoToString("value")
class DisabledStringField(@SurroundedWith("", "") val value: String)

@AutoToString("value")
class NullableStringField(val value: String?)

@AutoToString("value")
class CharField(val value: Char)

@AutoToString("value")
class DisabledCharField(@SurroundedWith("", "") val value: Char)

@AutoToString("value")
class NullableCharField(val value: Char?)

// custom surrounding

@AutoToString("value")
class CustomSurrounding(
    @SurroundedWith("<", ">")
    val value: Any?,
)

@AutoToString("value")
class CustomSurroundingEvenNull(
    @SurroundedWith("<", ">", evenNull = true)
    val value: Any?
)

// extends tests

open class ExtendTestParent(val parent: Any)
@AutoToString("child", "parent")
class ExtendTestChild(val child: Any, parent: Any) : ExtendTestParent(parent)
