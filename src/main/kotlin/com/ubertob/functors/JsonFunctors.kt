package com.ubertob.functors

//import com.beust.klaxon.JsonArray
//import com.beust.klaxon.JsonObject
//import com.beust.klaxon.Parser
import com.ubertob.outcome.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


interface StringWrapper {
    val raw: String
}


sealed class AbstractJsonNode {

    abstract val path: List<String>

    fun asText(): Outcome<JsonError, String> =
        when (this) {
            is JsonNodeString -> this.text.asSuccess()
            else -> JsonError(this, "Expected Text but node.type is ${this::class.simpleName}").asFailure()
        }

    fun asDouble(): Outcome<JsonError, Double> =
        when (this) {
            is JsonNodeDouble -> this.num.asSuccess()
            else -> JsonError(this, "Expected Double but found $this").asFailure()
        }

    fun asInt(): Outcome<JsonError, Int> =
        when (this) {
            is JsonNodeInt -> this.num.asSuccess()
            else -> JsonError(this, "Expected Int but found $this").asFailure()
        }

    fun asBoolean(): Outcome<JsonError, Boolean> =
        when (this) {
            is JsonNodeBoolean -> this.value.asSuccess()
            else -> JsonError(this, "Expected Boolean but found $this").asFailure()
        }

    fun <T> asObject(f: JsonNodeObject.() -> Outcome<JsonError, T>): Outcome<JsonError, T> =
        when (this) {
            is JsonNodeObject -> f(this)
            else -> JsonError(this, "Expected Object but found $this").asFailure()
        }

    fun asArray(): Outcome<JsonError, List<AbstractJsonNode>> =
        when (this) {
            is JsonNodeArray -> (this.values).asSuccess()
            else -> JsonError(this, "Expected Array but found $this").asFailure()
        }

    fun asNull(): Outcome<JsonError, Any?> =
        when (this) {
            is JsonNodeNull -> null.asSuccess()
            else -> JsonError(this, "Expected Null but found $this").asFailure()
        }


}

data class JsonNodeNull(override val path: List<String> = emptyList()) : AbstractJsonNode()
data class JsonNodeBoolean(val value: Boolean, override val path: List<String> = emptyList()) : AbstractJsonNode()
data class JsonNodeInt(val num: Int, override val path: List<String> = emptyList()) : AbstractJsonNode()
data class JsonNodeDouble(val num: Double, override val path: List<String> = emptyList()) : AbstractJsonNode()
data class JsonNodeString(val text: String, override val path: List<String> = emptyList()) : AbstractJsonNode()
data class JsonNodeArray(val values: List<AbstractJsonNode>, override val path: List<String> = emptyList()) :
    AbstractJsonNode()

data class JsonNodeObject(val fieldMap: Map<String, AbstractJsonNode>, override val path: List<String> = emptyList()) :
    AbstractJsonNode() {
    fun <T : Any> JsonPropMandatory<T>.get(): Outcome<JsonError, T> = this.getter(this@JsonNodeObject)
    fun <T : Any> JsonPropOptional<T>.get(): Outcome<JsonError, T?> = getter(this@JsonNodeObject)
}

data class JsonError(val node: AbstractJsonNode?, val reason: String) : OutcomeError {
    val location = node?.path?.joinToString(separator = "/", prefix = "</", postfix = ">") ?: "parsing"
    override val msg = "error at $location - $reason"
}

typealias JsonOutcome<T> = Outcome<JsonError, T>

interface AdjointFunctors<A, B, C : Outcome<*, A>> {
    fun pure(value: A): B
    fun extract(wrapped: B): C
}

interface JsonFunctors<T> : AdjointFunctors<T, AbstractJsonNode, JsonOutcome<T>> {
    override fun extract(node: AbstractJsonNode): JsonOutcome<T>
    override fun pure(value: T): AbstractJsonNode
}

interface JAny<T : Any> : JsonFunctors<T> {

    override fun extract(node: AbstractJsonNode): Outcome<JsonError, T> = node.asObject { deserialize() }

    override fun pure(value: T): AbstractJsonNode = serialize(value)

    fun JsonNodeObject.deserialize(): Outcome<JsonError, T>

    fun serialize(value: T): JsonNodeObject
}

object JBoolean : JsonFunctors<Boolean> {
    override fun extract(node: AbstractJsonNode): Outcome<JsonError, Boolean> = node.asBoolean()

    override fun pure(value: Boolean): AbstractJsonNode = JsonNodeBoolean(value)

}

object JString : JsonFunctors<String> {
    override fun extract(node: AbstractJsonNode): Outcome<JsonError, String> = node.asText()

    override fun pure(value: String): AbstractJsonNode = JsonNodeString(value)

}

object JInt : JsonFunctors<Int> {
    override fun extract(node: AbstractJsonNode): Outcome<JsonError, Int> = node.asInt()

    override fun pure(value: Int): AbstractJsonNode = JsonNodeInt(value)
}

object JDouble : JsonFunctors<Double> {
    override fun extract(node: AbstractJsonNode): Outcome<JsonError, Double> = node.asDouble()

    override fun pure(value: Double): AbstractJsonNode = JsonNodeDouble(value)
}

data class JStringWrapper<T : StringWrapper>(val cons: (String) -> T) : JsonFunctors<T> {

    override fun extract(node: AbstractJsonNode): Outcome<JsonError, T> =
        node.asText().map(cons)

    override fun pure(value: T): AbstractJsonNode = JsonNodeString(value.raw)

}

data class JArray<T : Any>(val helper: JsonFunctors<T>) : JsonFunctors<List<T>> {
    override fun extract(node: AbstractJsonNode): Outcome<JsonError, List<T>> = mapFrom(node, helper::extract)

    override fun pure(value: List<T>): AbstractJsonNode = mapToJson(value, helper::pure)

    private fun <T : Any> mapToJson(objs: List<T>, f: (T) -> AbstractJsonNode): AbstractJsonNode =
        JsonNodeArray(objs.map(f))

    private fun <T : Any> mapFrom(
        node: AbstractJsonNode,
        f: (AbstractJsonNode) -> Outcome<JsonError, T>
    ): Outcome<JsonError, List<T>> =
        node.asArray().bind { nodes -> nodes.map { n: AbstractJsonNode -> f(n) }.sequence() }
}


fun foldObjNode(vararg setters: (JsonNodeObject) -> JsonNodeObject): JsonNodeObject =
    setters.fold(JsonNodeObject(emptyMap())) { acc, setter -> setter(acc) }


infix fun <P1 : Any, P2 : Any, R : Any> (Function2<P1, P2, R>).`=`(o: Outcome<JsonError, P1>): Outcome<JsonError, Function1<P2, R>> =
    o.map { p1 -> { p2: P2 -> this(p1, p2) } }

infix fun <P1 : Any, R : Any> (Outcome<JsonError, Function1<P1, R>>).`+`(o: Outcome<JsonError, P1>): Outcome<JsonError, R> =
    o.flatMap { p1: P1 -> this.map { it(p1) } }


interface Lens<A, B : Any, C : Outcome<*, A>> {
    fun setter(value: A): (B) -> B
    fun getter(wrapped: B): C
}

sealed class JsonPropBase<T> : Lens<T, JsonNodeObject, JsonOutcome<T>> {
    abstract val propName: String
}

data class JsonPropMandatory<T : Any>(override val propName: String, val jf: JsonFunctors<T>) : JsonPropBase<T>() {

    override fun getter(node: JsonNodeObject): Outcome<JsonError, T> =
        node.fieldMap[propName]
            ?.let { idn -> jf.extract(idn) }
            ?: JsonError(node, "Not found $propName").asFailure()

    override fun setter(value: T): (JsonNodeObject) -> JsonNodeObject =
        { wrapped ->
            wrapped.copy(fieldMap = wrapped.fieldMap + (propName to jf.pure(value)))
        }

}


data class JsonPropOptional<T : Any>(override val propName: String, val jf: JsonFunctors<T>) : JsonPropBase<T?>() {

    override fun getter(node: JsonNodeObject): Outcome<JsonError, T?> =
        node.fieldMap[propName]
            ?.let { idn -> jf.extract(idn) }
            ?: null.asSuccess()

    override fun setter(value: T?): (JsonNodeObject) -> JsonNodeObject =
        { wrapped ->
            value?.let {
                wrapped.copy(fieldMap = wrapped.fieldMap + (propName to jf.pure(it)))
            } ?: wrapped
        }
}


class JField<T : Any>(private val jsonFunctors: JsonFunctors<T>) :
    ReadOnlyProperty<JsonFunctors<*>, JsonPropMandatory<T>> {

    override fun getValue(thisRef: JsonFunctors<*>, property: KProperty<*>): JsonPropMandatory<T> =
        JsonPropMandatory(property.name, jsonFunctors)

}

class JFieldOptional<T : Any>(private val jsonFunctors: JsonFunctors<T>) :
    ReadOnlyProperty<JsonFunctors<*>, JsonPropOptional<T>> {

    override fun getValue(thisRef: JsonFunctors<*>, property: KProperty<*>): JsonPropOptional<T> =
        JsonPropOptional(property.name, jsonFunctors)

}


