package com.ubertob.functors

import com.ubertob.functionLiteralsWithReceiver.User
import com.ubertob.outcome.*
import com.ubertob.unitnothingany.flatMap
import com.ubertob.unitnothingany.map
import kotlin.math.roundToInt
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


sealed class JsonNode {
    fun asText(): Outcome<JsonError, String> =
        when (this) {
            is JsonNodeString -> this.text.asSuccess()
            else -> JsonError(this.toString(), "Expected Text but node.type is ${this::class}").asFailure()
        }

    fun asDouble(): Outcome<JsonError, Double> =
        when (this) {
            is JsonNodeNum -> this.num.asSuccess()
            else -> JsonError(this.toString(), "Expected Number but node.type is ${this::class}").asFailure()
        }

    fun asInt(): Outcome<JsonError, Int> =
        when (this) {
            is JsonNodeNum -> this.num.roundToInt().asSuccess()
            else -> JsonError(this.toString(), "Expected Number but node.type is ${this::class}").asFailure()
        }

    fun asObject(): Outcome<JsonError, Map<String, JsonNode>> =
        when (this) {
            is JsonNodeObject -> (this.fieldMap).asSuccess()
            else -> JsonError(this.toString(), "Expected Object but node.type is ${this::class}").asFailure()
        }

    fun asArray(): Outcome<JsonError, List<JsonNode>> =
        when (this) {
            is JsonNodeArray -> (this.values).asSuccess()
            else -> JsonError(this.toString(), "Expected Array but node.type is ${this::class}").asFailure()
        }
    //todo bool and null
}

data class JsonNodeString(val text: String) : JsonNode()
data class JsonNodeNum(val num: Double) : JsonNode()
data class JsonNodeBoolean(val value: Boolean) : JsonNode()
data class JsonNodeArray(val values: List<JsonNode>) : JsonNode()
data class JsonNodeObject(val fieldMap: Map<String, JsonNode>) : JsonNode()
object JsonNodeNull : JsonNode()


data class JsonError(val json: String, val reason: String) : OutcomeError {
    override val msg = reason
}

interface JsonF<T : Any> {
    fun from(node: JsonNode): Outcome<JsonError, T>
    fun toJson(value: T): JsonNode
}


object JsonString : JsonF<String> {
    override fun from(node: JsonNode): Outcome<JsonError, String> = node.asText()

    override fun toJson(value: String): JsonNode = JsonNodeString(value)

}

object JsonInt : JsonF<Int> {
    override fun from(node: JsonNode): Outcome<JsonError, Int> = node.asInt()

    override fun toJson(value: Int): JsonNode = JsonNodeNum(value.toDouble())
}

object JsonDouble : JsonF<Double> {
    override fun from(node: JsonNode): Outcome<JsonError, Double> = node.asDouble()

    override fun toJson(value: Double): JsonNode = JsonNodeNum(value)
}

data class  JsonArray<T: Any>(val helper: JsonF<T>) : JsonF<List<T>> {
    override fun from(node: JsonNode): Outcome<JsonError, List<T>> = mapFrom(node, helper::from)

    override fun toJson(value: List<T>): JsonNode = mapToJson(value, helper::toJson)

    private fun <T: Any> mapToJson(objs: List<T>, f: (T) -> JsonNode): JsonNode = JsonNodeArray(objs.map(f))
    private fun <T: Any> mapFrom(node: JsonNode, f: (JsonNode) -> Outcome<JsonError, T>): Outcome<JsonError, List<T>> =
        node.asArray().bind{ nodes -> nodes.map{ n:JsonNode -> f(n) }.sequence() }
}



fun <T : Any> readObjNode(node: JsonNode, f: (JsonNodeObject) -> Outcome<JsonError, T>): Outcome<JsonError, T> =
    (node as? JsonNodeObject)?.let (f).failIfNull(JsonError(node.toString(), "Expected json object"))

fun writeObjNode(vararg fields: Pair<String, JsonNode>): JsonNode =
    JsonNodeObject( fields.toMap() )



infix fun <P1 : Any, P2 : Any, R : Any> (Function2<P1, P2, R>).`=`(o: Outcome<JsonError, P1>): Outcome<JsonError, Function1<P2, R>> =
    o.map { p1 -> { p2: P2 -> this(p1, p2) } }

infix fun <P1 : Any, R : Any> (Outcome<JsonError, Function1<P1, R>>).`+`(o: Outcome<JsonError, P1>): Outcome<JsonError, R> =
    o.flatMap { p1: P1 -> this.map { it(p1) } }


data class JsonProp<T : Any>(val propName: String, val jf: JsonF<T>) {

    fun getFrom(node: JsonNodeObject): Outcome<JsonError, T> =
        node.fieldMap[propName]
            .flatMap { idn -> jf.from(idn) }
            .failIfNull(JsonError(node.toString(), "Not found $propName"))

    fun setTo(value: T): Pair<String, JsonNode> =
        propName to jf.toJson(value)


}

class JField<T : Any>(val jsonFSingleton: JsonF<T>) : ReadOnlyProperty<JsonF<*>, JsonProp<T>> {

    override fun getValue(thisRef: JsonF<*>, property: KProperty<*>): JsonProp<T> =
        JsonProp(property.name, jsonFSingleton)

}
