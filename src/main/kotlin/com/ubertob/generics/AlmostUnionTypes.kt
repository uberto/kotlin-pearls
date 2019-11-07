package com.ubertob.generics

import java.util.*

class AlmostUnionTypes




sealed class JsonValue<out T>(val value: T) {
    class JsonString(value: String) : JsonValue<String>(value)
    class JsonBoolean(value: Boolean) : JsonValue<Boolean>(value)
    class JsonNumber(value: Number) : JsonValue<Number>(value)
    object JsonNull : JsonValue<Nothing?>(null)
    class JsonArray<V>(value: Array<V>) : JsonValue<Array<V>>(value)
    class JsonObject(value: Map<String, Any?>) : JsonValue<Map<String, Any?>>(value)
}



class Test {
    fun doSomething(el: JsonElement) {
    }
}

interface JsonElement {

}

data class JsonPrimitive<out T>(val value: T) : JsonElement

class JsonFunction<T, R>(private val self: T, private val method: T.(JsonElement) -> R) {

    operator fun invoke(arg: Boolean) = self.method(JsonPrimitive(arg))

    operator fun invoke(arg: Number) = self.method(JsonPrimitive(arg))

    operator fun invoke(arg: String) = self.method(JsonPrimitive(arg))

    operator fun invoke(arg: Char) = self.method(JsonPrimitive(arg))

    operator fun invoke(arg: JsonElement) = self.method(arg)

}

val Test.doSomethingAuto: JsonFunction<Test, Unit> get() = JsonFunction(this) { doSomething(it) }






inline fun <reified T> parse(raw: String): T? {
    val javaClass = T::class.java
    if(javaClass.isAssignableFrom(Int::class.java)) {
        return 45 as T
    } else if (javaClass.isAssignableFrom(String::class.java)) {
        return "45" as T
    } else if (javaClass.isAssignableFrom(Date::class.java)) {
        return Date() as T
    } else if (javaClass.isAssignableFrom(Boolean::class.java)) {
        return true as T
    } else
        return null
}