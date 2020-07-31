package com.ubertob.functors

import com.ubertob.outcome.*
import com.ubertob.unitnothingany.flatMap
import kotlin.math.roundToInt
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


sealed class AbstractJsonNode {

    var path: String = ""

    fun asText(): Outcome<JsonError, String> =
        when (this) {
            is JsonNodeString -> this.text.asSuccess()
            else -> JsonError(this, "Expected Text but node.type is ${this::class}").asFailure()
        }

    fun asDouble(): Outcome<JsonError, Double> =
        when (this) {
            is JsonNodeNum -> this.num.asSuccess()
            else -> JsonError(this, "Expected Number but node.type is ${this::class}").asFailure()
        }

    fun asInt(): Outcome<JsonError, Int> =
        when (this) {
            is JsonNodeNum -> this.num.roundToInt().asSuccess()
            else -> JsonError(this, "Expected Number but node.type is ${this::class}").asFailure()
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
data class JsonNodeNum(val num: Double) : AbstractJsonNode()
data class JsonNodeBoolean(val value: Boolean) : AbstractJsonNode()
data class JsonNodeArray(val values: List<AbstractJsonNode>) : AbstractJsonNode()
data class JsonNodeObject(val fieldMap: Map<String, AbstractJsonNode>) : AbstractJsonNode() {
    fun <T:Any> JsonProp<T>.get(): Outcome<JsonError, T> = getFrom(this@JsonNodeObject)
    fun <T:Any> JsonPropOp<T>.get(): Outcome<JsonError, T?> = getFrom(this@JsonNodeObject)
}

object JsonNodeNull : AbstractJsonNode()
object Root: AbstractJsonNode()


class JsonError(json: AbstractJsonNode, reason: String) : OutcomeError {
    override val msg = reason
    val parentLocation = json.toString()
}

interface JsonF<T> {
    fun from(node: AbstractJsonNode): Outcome<JsonError, T>
    fun toJson(value: T): AbstractJsonNode
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

    override fun toJson(value: Int): AbstractJsonNode = JsonNodeNum(value.toDouble())
}

object JsonDouble : JsonF<Double> {
    override fun from(node: AbstractJsonNode): Outcome<JsonError, Double> = node.asDouble()

    override fun toJson(value: Double): AbstractJsonNode = JsonNodeNum(value)
}

data class JsonArray<T : Any>(val helper: JsonF<T>) : JsonF<List<T>> {
    override fun from(node: AbstractJsonNode): Outcome<JsonError, List<T>> = mapFrom(node, helper::from)

    override fun toJson(value: List<T>): AbstractJsonNode = mapToJson(value, helper::toJson)

    private fun <T : Any> mapToJson(objs: List<T>, f: (T) -> AbstractJsonNode): AbstractJsonNode = JsonNodeArray(objs.map(f))
    private fun <T : Any> mapFrom(node: AbstractJsonNode, f: (AbstractJsonNode) -> Outcome<JsonError, T>): Outcome<JsonError, List<T>> =
        node.asArray().bind { nodes -> nodes.map { n: AbstractJsonNode -> f(n) }.sequence() }
}


fun writeObjNode(vararg fields: Pair<String, AbstractJsonNode>?): AbstractJsonNode =
    JsonNodeObject(fields.filterNotNull().toMap())


infix fun <P1 : Any, P2 : Any, R : Any> (Function2<P1, P2, R>).`=`(o: Outcome<JsonError, P1>): Outcome<JsonError, Function1<P2, R>> =
    o.map { p1 -> { p2: P2 -> this(p1, p2) } }

infix fun <P1 : Any, R : Any> (Outcome<JsonError, Function1<P1, R>>).`+`(o: Outcome<JsonError, P1>): Outcome<JsonError, R> =
    o.flatMap { p1: P1 -> this.map { it(p1) } }


data class JsonProp<T : Any>(val propName: String, val jf: JsonF<T>) {

    fun getFrom(node: JsonNodeObject): Outcome<JsonError, T> =
        node.fieldMap[propName]
            .flatMap { idn -> jf.from(idn) }
            ?: JsonError(node, "Not found $propName").asFailure()

    fun setTo(value: T): Pair<String, AbstractJsonNode>? =
        propName to jf.toJson(value)


}

data class JsonPropOp<T : Any>(val propName: String, val jf: JsonF<T>) {

    fun getFrom(node: JsonNodeObject): Outcome<JsonError, T?> =
        node.fieldMap[propName]
            .flatMap { idn -> jf.from(idn) }
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

class JFieldOp<T : Any>(val jsonFSingleton: JsonF<T>) : ReadOnlyProperty<JsonF<*>, JsonPropOp<T>> {

    override fun getValue(thisRef: JsonF<*>, property: KProperty<*>): JsonPropOp<T> =
        JsonPropOp(property.name, jsonFSingleton)

}
