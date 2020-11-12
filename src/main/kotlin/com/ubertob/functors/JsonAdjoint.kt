package com.ubertob.functors

import com.ubertob.outcome.*
import com.ubertob.outcome.Outcome.Companion.tryThis
import java.util.concurrent.atomic.AtomicReference
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

    fun asLong(): Outcome<JsonError, Long> =
        when (this) {
            is JsonNodeLong -> this.num.asSuccess()
            else -> JsonError(this, "Expected Long but found $this").asFailure()
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
data class JsonNodeLong(val num: Long, override val path: List<String> = emptyList()) : AbstractJsonNode()
data class JsonNodeDouble(val num: Double, override val path: List<String> = emptyList()) : AbstractJsonNode()
data class JsonNodeString(val text: String, override val path: List<String> = emptyList()) : AbstractJsonNode()
data class JsonNodeArray(val values: List<AbstractJsonNode>, override val path: List<String> = emptyList()) :
    AbstractJsonNode()

data class JsonNodeObject(val fieldMap: Map<String, AbstractJsonNode>, override val path: List<String> = emptyList()) :
    AbstractJsonNode() {
    fun <T : Any> JsonPropMandatory<T>.get(): Outcome<JsonError, T> = getter(this@JsonNodeObject)
    fun <T : Any> JsonPropOptional<T>.get(): Outcome<JsonError, T?> = getter(this@JsonNodeObject)

 operator   fun <T : Any> JsonPropMandatory<T>.unaryPlus():  T =
     getter(this@JsonNodeObject)
         .orThrow { JsonParsingException(it) }

    operator   fun <T : Any> JsonPropOptional<T>.unaryPlus():  T? =
        getter(this@JsonNodeObject)
            .orThrow { java.lang.RuntimeException("!!!") }

}

data class JsonError(val node: AbstractJsonNode?, val reason: String) : OutcomeError {
    val location = node?.path?.joinToString(separator = "/", prefix = "</", postfix = ">") ?: "parsing"
    override val msg = "error at $location - $reason"
}

typealias JsonOutcome<T> = Outcome<JsonError, T>

interface AdjointFunctors<A, B, C : Outcome<*, A>> {
    fun build(value: A): B
    fun extract(wrapped: B): C
}

interface JsonAdjoint<T> : AdjointFunctors<T, AbstractJsonNode, JsonOutcome<T>> {
    override fun extract(node: AbstractJsonNode): JsonOutcome<T>
    override fun build(value: T): AbstractJsonNode
}

typealias NodeWriter<T> = (JsonNodeObject, T) -> JsonNodeObject
typealias NodeReader<T> = (JsonNodeObject) -> JsonOutcome<T>

abstract class JProtocol<T : Any> : JsonAdjoint<T> {

    private val nodeWriters: AtomicReference<Set<NodeWriter<T>>> = AtomicReference(emptySet())
    private val nodeReaders: AtomicReference<Set<NodeReader<*>>> = AtomicReference(emptySet())

    fun registerSetter(nodeWriter: NodeWriter<T>) {
        nodeWriters.getAndUpdate { set -> set + nodeWriter }
    }

    fun registerGetter(nodeReader: NodeReader<*>) {
        nodeReaders.getAndUpdate { set -> set + nodeReader }
    }

    override fun extract(node: AbstractJsonNode): Outcome<JsonError, T> = node.asObject { deserialize(this) }

    override fun build(value: T): AbstractJsonNode = serialize(value)

    abstract fun JsonNodeObject.tryDeserialize(): T?

    fun deserialize(from: JsonNodeObject): Outcome<JsonError, T> =
        tryThis {
            from.tryDeserialize() ?: throw RuntimeException("constructor returning null!")
        }.mapFailure { JsonError(from, it.msg) }

    fun serialize(value: T): JsonNodeObject =
        foldObjNode(
            *nodeWriters.get().map { nw -> { jno: JsonNodeObject -> nw(jno, value) } } .toTypedArray()
//            id.setter(value.id),
//            vat.setter(value.vat),
//            customer.setter(value.customer),
//            items.setter(value.items),
//            total.setter(value.total)
        )
}

object JBoolean : JsonAdjoint<Boolean> {
    override fun extract(node: AbstractJsonNode): Outcome<JsonError, Boolean> = node.asBoolean()

    override fun build(value: Boolean): AbstractJsonNode = JsonNodeBoolean(value)

}

object JString : JsonAdjoint<String> {
    override fun extract(node: AbstractJsonNode): Outcome<JsonError, String> = node.asText()

    override fun build(value: String): AbstractJsonNode = JsonNodeString(value)

}

object JInt : JsonAdjoint<Int> {
    override fun extract(node: AbstractJsonNode): Outcome<JsonError, Int> = node.asInt()

    override fun build(value: Int): AbstractJsonNode = JsonNodeInt(value)
}


object JLong : JsonAdjoint<Long> {
    override fun extract(node: AbstractJsonNode): Outcome<JsonError, Long> = node.asLong()

    override fun build(value: Long): AbstractJsonNode = JsonNodeLong(value)
}

object JDouble : JsonAdjoint<Double> {
    override fun extract(node: AbstractJsonNode): Outcome<JsonError, Double> = node.asDouble()

    override fun build(value: Double): AbstractJsonNode = JsonNodeDouble(value)
}

data class JStringWrapper<T : StringWrapper>(val cons: (String) -> T) : JsonAdjoint<T> {

    override fun extract(node: AbstractJsonNode): Outcome<JsonError, T> =
        node.asText().map(cons)

    override fun build(value: T): AbstractJsonNode = JsonNodeString(value.raw)

}

data class JArray<T : Any>(val helper: JsonAdjoint<T>) : JsonAdjoint<List<T>> {
    override fun extract(node: AbstractJsonNode): Outcome<JsonError, List<T>> = mapFrom(node, helper::extract)

    override fun build(value: List<T>): AbstractJsonNode = mapToJson(value, helper::build)

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


interface Lens<A, B : Any, C : Outcome<*, A>> {
    fun setter(value: A): (B) -> B
    fun getter(wrapped: B): C
}


sealed class JsonProperty<T> : Lens<T, JsonNodeObject, JsonOutcome<T>> {

    abstract val propName: String

}

data class JsonParsingException(val error: JsonError) : RuntimeException()


data class JsonPropMandatory<T : Any>(override val propName: String, val jf: JsonAdjoint<T>) : JsonProperty<T>() {

    override fun getter(node: JsonNodeObject): Outcome<JsonError, T> =
        node.fieldMap[propName]
            ?.let { idn -> jf.extract(idn) }
            ?: JsonError(node, "Not found $propName").asFailure()

    override fun setter(value: T): (JsonNodeObject) -> JsonNodeObject =
        { wrapped ->
            wrapped.copy(fieldMap = wrapped.fieldMap + (propName to jf.build(value)))
        }


}


data class JsonPropOptional<T : Any>(override val propName: String, val jf: JsonAdjoint<T>) : JsonProperty<T?>() {

    override fun getter(node: JsonNodeObject): Outcome<JsonError, T?> =
        node.fieldMap[propName]
            ?.let { idn -> jf.extract(idn) }
            ?: null.asSuccess()

    override fun setter(value: T?): (JsonNodeObject) -> JsonNodeObject =
        { wrapped ->
            value?.let {
                wrapped.copy(fieldMap = wrapped.fieldMap + (propName to jf.build(it)))
            } ?: wrapped
        }

    operator fun JsonNodeObject.unaryPlus(): T? {
        return getter(this)
            .orThrow { java.lang.RuntimeException("!!!") }
    }
}

sealed class JFieldBase<T, PT:Any>
    : ReadOnlyProperty<JProtocol<PT>, JsonProperty<T>>
{

}

class JField<T : Any, PT : Any>(
    private val jsonAdjoint: JsonAdjoint<T>,
    private val binder: (PT) -> T,
    private val jsonFieldName: String? = null
): JFieldBase<T,PT>()  {

    override fun getValue(thisRef: JProtocol<PT>, property: KProperty<*>): JsonPropMandatory<T> =
        JsonPropMandatory(jsonFieldName ?: property.name, jsonAdjoint)
            .also {
                thisRef.registerSetter { jno, obj -> it.setter(binder(obj))(jno) }
                thisRef.registerGetter { jno -> it.getter(jno) }
            }

}

class JFieldMaybe<T : Any, PT : Any>(
    private val jsonAdjoint: JsonAdjoint<T>,
    private val binder: (PT) -> T?,
    private val jsonFieldName: String? = null
) : JFieldBase<T?,PT>(){

    override fun getValue(thisRef: JProtocol<PT>, property: KProperty<*>): JsonPropOptional<T> =
        JsonPropOptional(jsonFieldName ?: property.name, jsonAdjoint)
            .also {
                thisRef.registerSetter { jno, obj-> it.setter(binder(obj))(jno) }
                thisRef.registerGetter { jno -> it.getter(jno) }
            }

}


//todo
// add pre-check for multiple failures in parsing
// add test for multiple reuse
// add tests for concurrency reuse
// add test for different kind of failures

