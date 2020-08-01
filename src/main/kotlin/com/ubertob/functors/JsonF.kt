package com.ubertob.functors

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.ubertob.outcome.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


sealed class AbstractJsonNode {

    var path: String = ""

    fun asText(): Outcome<JsonError, String> =
        when (this) {
            is JsonNodeString -> this.text.asSuccess()
            else -> JsonError(this, "Expected Text but node.type is ${this::class.simpleName}").asFailure()
        }

    fun asDouble(): Outcome<JsonError, Double> =
        when (this) {
            is JsonNodeDouble -> this.num.asSuccess()
            else -> JsonError(this, "Expected Double but node.type is ${this::class.simpleName}").asFailure()
        }

    fun asInt(): Outcome<JsonError, Int> =
        when (this) {
            is JsonNodeInt -> this.num.asSuccess()
            else -> JsonError(this, "Expected Int but node.type is ${this::class.simpleName}").asFailure()
        }

    fun asBoolean(): Outcome<JsonError, Boolean> =
        when (this) {
            is JsonNodeBoolean -> this.value.asSuccess()
            else -> JsonError(this, "Expected Boolean but node.type is ${this::class}").asFailure()
        }

    fun <T> asObject(f: JsonNodeObject.() -> Outcome<JsonError, T>): Outcome<JsonError, T> =
        when (this) {
            is JsonNodeObject -> f(this)
            else -> JsonError(this, "Expected Object but node.type is ${this::class}").asFailure()
        }

    fun asArray(): Outcome<JsonError, List<AbstractJsonNode>> =
        when (this) {
            is JsonNodeArray -> (this.values).asSuccess()
            else -> JsonError(this, "Expected Array but node.type is ${this::class}").asFailure()
        }
    //todo null


}

data class JsonNodeString(val text: String) : AbstractJsonNode()
data class JsonNodeInt(val num: Int) : AbstractJsonNode()
data class JsonNodeDouble(val num: Double) : AbstractJsonNode()
data class JsonNodeBoolean(val value: Boolean) : AbstractJsonNode()
data class JsonNodeArray(val values: List<AbstractJsonNode>) : AbstractJsonNode()
data class JsonNodeObject(val fieldMap: Map<String, AbstractJsonNode>) : AbstractJsonNode() {
    fun <T : Any> JsonProp<T>.get(): Outcome<JsonError, T> = getFrom(this@JsonNodeObject)
    fun <T : Any> JsonPropOp<T>.get(): Outcome<JsonError, T?> = getFrom(this@JsonNodeObject)
}

object JsonNodeNull : AbstractJsonNode()


data class JsonError(val node: AbstractJsonNode?, val reason: String) : OutcomeError {
    val location = node?.path ?: "parsing"
    override val msg = "error at $location ${node?.toString().orEmpty()} - $reason"
}

interface JsonF<T> {
    fun from(node: AbstractJsonNode): Outcome<JsonError, T>
    fun toJson(value: T): AbstractJsonNode
}

interface JsonObj<T> : JsonF<T> {

    override fun from(node: AbstractJsonNode): Outcome<JsonError, T> = node.asObject { deserialize() }

    override fun toJson(value: T): AbstractJsonNode = serialize(value)

    fun JsonNodeObject.deserialize(): Outcome<JsonError, T>

    fun serialize(value: T): JsonNodeObject
}

object JsonBoolean : JsonF<Boolean> {
    override fun from(node: AbstractJsonNode): Outcome<JsonError, Boolean> = node.asBoolean()

    override fun toJson(value: Boolean): AbstractJsonNode = JsonNodeBoolean(value)

}

object JsonString : JsonF<String> {
    override fun from(node: AbstractJsonNode): Outcome<JsonError, String> = node.asText()

    override fun toJson(value: String): AbstractJsonNode = JsonNodeString(value)

}

object JsonInt : JsonF<Int> {
    override fun from(node: AbstractJsonNode): Outcome<JsonError, Int> = node.asInt()

    override fun toJson(value: Int): AbstractJsonNode = JsonNodeInt(value)
}

object JsonDouble : JsonF<Double> {
    override fun from(node: AbstractJsonNode): Outcome<JsonError, Double> = node.asDouble()

    override fun toJson(value: Double): AbstractJsonNode = JsonNodeDouble(value)
}

data class JsonArrayNode<T : Any>(val helper: JsonF<T>) : JsonF<List<T>> {
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
    JsonNodeObject(fields.filterNotNull().toMap())


infix fun <P1 : Any, P2 : Any, R : Any> (Function2<P1, P2, R>).`=`(o: Outcome<JsonError, P1>): Outcome<JsonError, Function1<P2, R>> =
    o.map { p1 -> { p2: P2 -> this(p1, p2) } }

infix fun <P1 : Any, R : Any> (Outcome<JsonError, Function1<P1, R>>).`+`(o: Outcome<JsonError, P1>): Outcome<JsonError, R> =
    o.flatMap { p1: P1 -> this.map { it(p1) } }


data class JsonProp<T : Any>(val propName: String, val jf: JsonF<T>) {

    fun getFrom(node: JsonNodeObject): Outcome<JsonError, T> =
        node.fieldMap[propName]
            ?.let { idn -> jf.from(idn) }
            ?: JsonError(node, "Not found $propName").asFailure()

    fun setTo(value: T): Pair<String, AbstractJsonNode>? =
        propName to jf.toJson(value)


}

data class JsonPropOp<T : Any>(val propName: String, val jf: JsonF<T>) {

    fun getFrom(node: JsonNodeObject): Outcome<JsonError, T?> =
        node.fieldMap[propName]
            ?.let { idn -> jf.from(idn) }
            ?: null.asSuccess()

    fun setTo(value: T?): Pair<String, AbstractJsonNode>? =
        value?.let {
            propName to jf.toJson(it)
        }

}


class JField<T : Any>(val jsonFSingleton: JsonF<T>) : ReadOnlyProperty<JsonF<*>, JsonProp<T>> {

    override fun getValue(thisRef: JsonF<*>, property: KProperty<*>): JsonProp<T> =
        JsonProp(property.name, jsonFSingleton)

}

class JFieldOptional<T : Any>(val jsonFSingleton: JsonF<T>) : ReadOnlyProperty<JsonF<*>, JsonPropOp<T>> {

    override fun getValue(thisRef: JsonF<*>, property: KProperty<*>): JsonPropOp<T> =
        JsonPropOp(property.name, jsonFSingleton)

}


//----klaxon part


fun klaxonConvert(json: String): Outcome<JsonError, JsonNodeObject> =
    Outcome.tryThis { Parser.default().parse(StringBuilder(json)) as JsonObject }
        .mapFailure { JsonError(null, it.msg) }
        .bind { fromKlaxon(it.map) }

fun fromKlaxon(map: MutableMap<String, Any?>): Outcome<JsonError, JsonNodeObject> =
    map.entries.mapNotNull { entry ->
        entry.value?.let {
            entry.key to valueToNode(it).onFailure { return it.asFailure() }
        }
    }.toMap().let(::JsonNodeObject).asSuccess()

private fun valueToNode(value: Any): Outcome<JsonError, AbstractJsonNode> {
    return when (value) {
        is Int -> JsonNodeInt(value).asSuccess()
        is Double -> JsonNodeDouble(value).asSuccess()
        is String -> JsonNodeString(value).asSuccess()
        is Boolean -> JsonNodeBoolean(value).asSuccess()
        is JsonObject -> fromKlaxon(value.map)
        is JsonArray<*> -> JsonNodeArray( value.value.filterNotNull().map {e -> valueToNode(e).onFailure { return it.asFailure() } } ).asSuccess()
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
        JsonNodeNull -> null
    }
}


fun <T> fromJsonString(json: String, conv: JsonObj<T>): Outcome<JsonError, T> =
    klaxonConvert(json)
        .bind { conv.from(it) }

fun <T> toJsonString(value: T, conv: JsonObj<T>): Outcome<JsonError, String> = conv.serialize(value)
    .let { JsonObject(toKlaxon(it)).toJsonString().asSuccess() }

