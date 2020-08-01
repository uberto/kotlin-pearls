package com.ubertob.functors

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.ubertob.outcome.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


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
data class JsonNodeArray(val values: List<AbstractJsonNode>, override val path: List<String> = emptyList()) : AbstractJsonNode()

data class JsonNodeObject(val fieldMap: Map<String, AbstractJsonNode>, override val path: List<String> = emptyList()) : AbstractJsonNode() {
    fun <T : Any> JsonProp<T>.get(): Outcome<JsonError, T> = getFrom(this@JsonNodeObject)
    fun <T : Any> JsonPropOptional<T>.get(): Outcome<JsonError, T?> = getFrom(this@JsonNodeObject)
}

data class JsonError(val node: AbstractJsonNode?, val reason: String) : OutcomeError {
    val location = node?.path?.joinToString(separator = "/", prefix = "</", postfix = ">") ?: "parsing"
    override val msg = "error at $location - $reason"
}

interface JsonFunctors<T: Any> {
    fun from(node: AbstractJsonNode): Outcome<JsonError, T>
    fun toJson(value: T): AbstractJsonNode
}

interface JAny<T: Any> : JsonFunctors<T> {

    override fun from(node: AbstractJsonNode): Outcome<JsonError, T> = node.asObject { deserialize() }

    override fun toJson(value: T): AbstractJsonNode = serialize(value)

    fun JsonNodeObject.deserialize(): Outcome<JsonError, T>

    fun serialize(value: T): JsonNodeObject
}

object JBoolean : JsonFunctors<Boolean> {
    override fun from(node: AbstractJsonNode): Outcome<JsonError, Boolean> = node.asBoolean()

    override fun toJson(value: Boolean): AbstractJsonNode = JsonNodeBoolean(value)

}

object JString : JsonFunctors<String> {
    override fun from(node: AbstractJsonNode): Outcome<JsonError, String> = node.asText()

    override fun toJson(value: String): AbstractJsonNode = JsonNodeString(value)

}

object JInt : JsonFunctors<Int> {
    override fun from(node: AbstractJsonNode): Outcome<JsonError, Int> = node.asInt()

    override fun toJson(value: Int): AbstractJsonNode = JsonNodeInt(value)
}

object JDouble : JsonFunctors<Double> {
    override fun from(node: AbstractJsonNode): Outcome<JsonError, Double> = node.asDouble()

    override fun toJson(value: Double): AbstractJsonNode = JsonNodeDouble(value)
}

data class JArray<T : Any>(val helper: JsonFunctors<T>) : JsonFunctors<List<T>> {
    override fun from(node: AbstractJsonNode): Outcome<JsonError, List<T>> = mapFrom(node, helper::from)

    override fun toJson(value: List<T>): AbstractJsonNode = mapToJson(value, helper::toJson)

    private fun <T : Any> mapToJson(objs: List<T>, f: (T) -> AbstractJsonNode): AbstractJsonNode =
        JsonNodeArray(objs.map(f))

    private fun <T : Any> mapFrom(
        node: AbstractJsonNode,
        f: (AbstractJsonNode) -> Outcome<JsonError, T>
    ): Outcome<JsonError, List<T>> =
        node.asArray().bind { nodes -> nodes.map { n: AbstractJsonNode -> f(n) }.sequence() }
}

fun writeObjNode(vararg fields: Pair<String, AbstractJsonNode>?): JsonNodeObject =
    JsonNodeObject(
        fields.filterNotNull().toMap())


infix fun <P1 : Any, P2 : Any, R : Any> (Function2<P1, P2, R>).`=`(o: Outcome<JsonError, P1>): Outcome<JsonError, Function1<P2, R>> =
    o.map { p1 -> { p2: P2 -> this(p1, p2) } }

infix fun <P1 : Any, R : Any> (Outcome<JsonError, Function1<P1, R>>).`+`(o: Outcome<JsonError, P1>): Outcome<JsonError, R> =
    o.flatMap { p1: P1 -> this.map { it(p1) } }


data class JsonProp<T : Any>(val propName: String, val jf: JsonFunctors<T>) {

    fun getFrom(node: JsonNodeObject): Outcome<JsonError, T> =
        node.fieldMap[propName]
            ?.let { idn -> jf.from(idn) }
            ?: JsonError(node, "Not found $propName").asFailure()

    fun setTo(value: T): Pair<String, AbstractJsonNode>? =
        propName to jf.toJson(value)


}


//todo: would JProp<T?> would work instead of JsonPropOptional?
data class JsonPropOptional<T : Any>(val propName: String, val jf: JsonFunctors<T>) {

    fun getFrom(node: JsonNodeObject): Outcome<JsonError, T?> =
        node.fieldMap[propName]
            ?.let { idn -> jf.from(idn) }
            ?: null.asSuccess()

    fun setTo(value: T?): Pair<String, AbstractJsonNode>? =
        value?.let {
            propName to jf.toJson(it)
        }
}


class JField<T : Any>(private val jsonFunctors: JsonFunctors<T>) : ReadOnlyProperty<JsonFunctors<*>, JsonProp<T>> {

    override fun getValue(thisRef: JsonFunctors<*>, property: KProperty<*>): JsonProp<T> =
        JsonProp(property.name, jsonFunctors)

}

class JFieldOptional<T : Any>(private val jsonFunctors: JsonFunctors<T>) : ReadOnlyProperty<JsonFunctors<*>, JsonPropOptional<T>> {

    override fun getValue(thisRef: JsonFunctors<*>, property: KProperty<*>): JsonPropOptional<T> =
        JsonPropOptional(property.name, jsonFunctors)

}


//----klaxon part


fun klaxonConvert(json: String): Outcome<JsonError, JsonNodeObject> =
    Outcome.tryThis { Parser.default().parse(StringBuilder(json)) as JsonObject }
        .mapFailure { JsonError(null, it.msg) }
        .bind { fromKlaxon(it.map, emptyList()) }

fun fromKlaxon( map: MutableMap<String, Any?>, path: List<String>): Outcome<JsonError, JsonNodeObject> =
    map.entries.mapNotNull { entry ->
        entry.value?.let {
            entry.key to valueToNode( it, path + entry.key).onFailure { return it.asFailure() }
        }
    }.toMap().let { fieldMap -> JsonNodeObject(fieldMap, path) }.asSuccess()

private fun valueToNode(value: Any, path: List<String>): Outcome<JsonError, AbstractJsonNode> {
    return when (value) {
        is Int -> JsonNodeInt(value, path).asSuccess()
        is Double -> JsonNodeDouble(value, path).asSuccess()
        is String -> JsonNodeString(value, path).asSuccess()
        is Boolean -> JsonNodeBoolean(value, path).asSuccess()
        is JsonObject -> fromKlaxon(value.map, path)
        is JsonArray<*> -> JsonNodeArray( value.value.filterNotNull().mapIndexed { i,e -> valueToNode(e, path + "$i").onFailure { return it.asFailure() } } ).asSuccess()
        else -> JsonError(null, "type ${value::class} impossible to map! $value" ).asFailure()
    }
}

fun toKlaxon(node: JsonNodeObject): Map<String, Any?> =
    node.fieldMap.entries.map { entry ->
        entry.key to nodeToValue(entry.value)
    }.toMap()

private fun nodeToValue(node: AbstractJsonNode): Any? {
    return when (node) {
        is JsonNodeBoolean -> node.value
        is JsonNodeString -> node.text
        is JsonNodeInt -> node.num
        is JsonNodeDouble -> node.num
        is JsonNodeArray -> node.values.map(::nodeToValue)
        is JsonNodeObject -> toKlaxon(node)
        is JsonNodeNull -> null
    }
}


fun <T: Any> fromJsonString(json: String, conv: JAny<T>): Outcome<JsonError, T> =
    klaxonConvert(json)
        .bind { conv.from(it) }

fun <T: Any> toJsonString(value: T, conv: JAny<T>): Outcome<JsonError, String> = conv.serialize(value)
    .let { JsonObject(toKlaxon(it)).toJsonString().asSuccess() }

