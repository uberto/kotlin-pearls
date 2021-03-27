package com.ubertob.functors

import com.ubertob.outcome.*
import com.ubertob.outcome.Outcome.Companion.tryThis
import com.ubertob.unlearnoop.JsonNode
import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/*
a couple parser/render form an adjunction (https://en.wikipedia.org/wiki/Adjoint_functors)

The laws are (no id because we cannot reconstruct a wrong json from the error):

render `.` parse `.` render = render
parse `.` render `.` parse = parse

where:
f `.` g: (x) -> g(f(x))
render : JsonOutcome<T> -> JSON
parse : JSON -> JsonOutcome<T>

JSON here can be either the Json string or the JsonNode


See also
https://neoeinstein.github.io/blog/2015/12-13-chiron-json-ducks-monads/index.html
--------

ToJson<A> => (A) -> JsonNode
FromJson<A> => (JsonNode) -> A




from
https://blog.jle.im/entry/foldl-adjunction.html
---
unit :: a -> Fold r (EnvList r a)
counit :: EnvList r (Fold r a) -> a

leftAdjunct :: (EnvList r a -> b) -> (a -> Fold r b)
rightAdjunct :: (a -> Fold r b) -> (EnvList r a -> b)

tabulateAdjunction :: (EnvList r () -> b) -> Fold r b
indexAdjunction :: Fold r b -> EnvList r a -> b

zipR :: Fold r a -> Fold r b -> Fold r (a, b)

------------
assuming:

EnvList r a ==> ToJson<JsonNode, A> ==> ToJson<A>
Fold r a  ==> FromJson<JsonNode, A> ==> FromJson<A>

---------
we have:

unit :: (A) -> FromJson<ToJson<A>>
counit ::  (ToJson<FromJson<A>>) -> A

leftAdjunct :: (ToJson<A> -> B) -> (A -> FromJson<B>)
rightAdjunct :: (A -> FromJson<B>) -> (ToJson<A> -> B)

tabulateAdjunction :: (ToJson<Unit> -> B) -> FromJson<B>
indexAdjunction :: (FromJson<B>) -> ToJson<A> -> B






 */




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
            is JsonNodeLong -> this.num.toDouble().asSuccess()
            is JsonNodeInt -> this.num.toDouble().asSuccess()
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
            is JsonNodeInt -> this.num.toLong().asSuccess()
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

    operator fun <T> JsonProperty<T>.unaryPlus(): T =
        getter(this@JsonNodeObject)
            .orThrow { JsonParsingException(it) }

}

data class JsonError(val node: AbstractJsonNode?, val reason: String) : OutcomeError {
    val location = node?.path?.joinToString(separator = "/", prefix = "</", postfix = ">") ?: "parsing"
    override val msg = "error at $location reason: $reason"
}

typealias JsonOutcome<T> = Outcome<JsonError, T>

interface Functor<A>{
    fun <B> transform(f: (A) -> B): Functor<B>
}

/* Kotlin doesn't have HKT

also this doesn't work:
interface Functor<A, SELF: Functor<A, SELF>>{
    fun <B, SELFB: SELF<B, SELFB>> transform(f: (A) -> B): Functor<B, SELFB>
}



interface Adjunction<A, LEFT: Functor<_>, RIGHT: Functor<_>> {
    fun <B> leftAdjunct(f: (LEFT<A>) -> B): (A) -> RIGHT<B>
    fun <B> rightAdjunct(f: (A) -> RIGHT<B>): (LEFT<A>) -> B
}
 */

data class FromJson<A>(val parse: (JsonNode) -> A): Functor<A> {
    override fun <B> transform(f: (A) -> B): FromJson<B>
    = FromJson{ jn -> f(parse(jn)) }
}

//doesn't compile:
//data class ToJson<A>(val render: (A) ->JsonNode): Functor<A> {
//    override fun <B> transform(f: (A) -> B): ToJson<B>
//            = ToJson{ b -> render(f(b)) }
//}

data class ToJson<A>(val node: JsonNode, val value: A): Functor<A> {
    override fun <B> transform(f: (A) -> B): ToJson<B>
            = ToJson(node, f(value))
}



interface JsonAdjunction<A> {
    fun <B> leftAdjunct(f: (ToJson<A>) -> B): (A) -> FromJson<B>
    fun <B> rightAdjunct(f: (A) -> FromJson<B>): (ToJson<A>) -> B
}



interface JConverter<T> {
    fun fromJson(node: AbstractJsonNode): JsonOutcome<T>
    fun toJson(value: T): AbstractJsonNode
}

typealias NodeWriter<T> = (JsonNodeObject, T) -> JsonNodeObject
typealias NodeReader<T> = (JsonNodeObject) -> JsonOutcome<T>

abstract class JProtocol<T : Any> : JConverter<T> {

    private val nodeWriters: AtomicReference<Set<NodeWriter<T>>> = AtomicReference(emptySet())
    private val nodeReaders: AtomicReference<Set<NodeReader<*>>> = AtomicReference(emptySet())

    internal fun registerSetter(nodeWriter: NodeWriter<T>) {
        nodeWriters.getAndUpdate { set -> set + nodeWriter }
    }

    internal fun registerGetter(nodeReader: NodeReader<*>) {
        nodeReaders.getAndUpdate { set -> set + nodeReader }
    }

    override fun fromJson(node: AbstractJsonNode): Outcome<JsonError, T> = node.asObject { deserialize(this) }

    override fun toJson(value: T): AbstractJsonNode = serialize(value)

    abstract fun JsonNodeObject.tryDeserialize(): T?

    fun deserialize(from: JsonNodeObject): Outcome<JsonError, T> =
        composeFailures(nodeReaders.get(), from)
            .bind {
                tryThis {
                    from.tryDeserialize() ?: throw JsonParsingException(
                        JsonError(from, "tryDeserialize returned null!")
                    )
                }.mapFailure { throwableError ->
                    when (throwableError.t) {
                        is JsonParsingException -> throwableError.t.error // keep path info
                        else -> JsonError(from, throwableError.msg)
                    }
                }
            }

    private fun composeFailures(nodeReaders: Set<NodeReader<*>>, jsonNode: JsonNodeObject): JsonOutcome<Unit> =
        nodeReaders
            .fold(emptyList<JsonOutcome<*>>()) { acc, r -> acc + r(jsonNode) }
            .mapNotNull {
                when(it){
                    is Success -> null
                    is Failure -> it.error
                } }
            .let { errors ->
                when {
                    errors.isEmpty() -> Unit.asSuccess()
                    errors.size == 1 -> errors[0].asFailure()
                    else -> multipleErrors(jsonNode, errors).asFailure()
                }
            }

    private fun multipleErrors(jsonNode: JsonNodeObject, errors: List<OutcomeError>): JsonError =
        JsonError(jsonNode, errors.joinToString(prefix = "Multiple errors: "))


    fun serialize(value: T): JsonNodeObject =
        nodeWriters.get()
            .map { nw -> { jno: JsonNodeObject -> nw(jno, value) } }.fold(JsonNodeObject(emptyMap())) { acc, setter ->
                setter(acc)
            }
}

object JBoolean : JConverter<Boolean> {
    override fun fromJson(node: AbstractJsonNode): Outcome<JsonError, Boolean> = node.asBoolean()

    override fun toJson(value: Boolean): AbstractJsonNode = JsonNodeBoolean(value)

}

object JString : JConverter<String> {
    override fun fromJson(node: AbstractJsonNode): Outcome<JsonError, String> = node.asText()

    override fun toJson(value: String): AbstractJsonNode = JsonNodeString(value)

}

object JInt : JConverter<Int> {
    override fun fromJson(node: AbstractJsonNode): Outcome<JsonError, Int> = node.asInt()

    override fun toJson(value: Int): AbstractJsonNode = JsonNodeInt(value)
}


object JLong : JConverter<Long> {
    override fun fromJson(node: AbstractJsonNode): Outcome<JsonError, Long> = node.asLong()

    override fun toJson(value: Long): AbstractJsonNode = JsonNodeLong(value)
}

object JDouble : JConverter<Double> {
    override fun fromJson(node: AbstractJsonNode): Outcome<JsonError, Double> = node.asDouble()

    override fun toJson(value: Double): AbstractJsonNode = JsonNodeDouble(value)
}

data class JStringWrapper<T : StringWrapper>(val cons: (String) -> T) : JConverter<T> {

    override fun fromJson(node: AbstractJsonNode): Outcome<JsonError, T> =
        node.asText().map(cons)

    override fun toJson(value: T): AbstractJsonNode = JsonNodeString(value.raw)

}

data class JArray<T : Any>(val helper: JConverter<T>) : JConverter<List<T>> {
    override fun fromJson(node: AbstractJsonNode): Outcome<JsonError, List<T>> = mapFrom(node, helper::fromJson)

    override fun toJson(value: List<T>): AbstractJsonNode = mapToJson(value, helper::toJson)

    private fun <T : Any> mapToJson(objs: List<T>, f: (T) -> AbstractJsonNode): AbstractJsonNode =
        JsonNodeArray(objs.map(f))

    private fun <T : Any> mapFrom(
        node: AbstractJsonNode,
        f: (AbstractJsonNode) -> Outcome<JsonError, T>
    ): Outcome<JsonError, List<T>> =
        node.asArray().bind { nodes -> nodes.map { n: AbstractJsonNode -> f(n) }.sequence() }
}


interface Lens<A, B : Any, C : Outcome<*, A>> {
    fun setter(value: A): (B) -> B
    fun getter(wrapped: B): C
}


sealed class JsonProperty<T> : Lens<T, JsonNodeObject, JsonOutcome<T>> {

    abstract val propName: String

}

data class JsonParsingException(val error: JsonError) : RuntimeException()


data class JsonPropMandatory<T : Any>(override val propName: String, val jf: JConverter<T>) : JsonProperty<T>() {

    override fun getter(node: JsonNodeObject): Outcome<JsonError, T> =
        node.fieldMap[propName]
            ?.let { idn -> jf.fromJson(idn) }
            ?: JsonError(node, "Not found $propName").asFailure()

    override fun setter(value: T): (JsonNodeObject) -> JsonNodeObject =
        { wrapped ->
            wrapped.copy(fieldMap = wrapped.fieldMap + (propName to jf.toJson(value)))
        }


}


data class JsonPropOptional<T : Any>(override val propName: String, val jf: JConverter<T>) : JsonProperty<T?>() {

    override fun getter(node: JsonNodeObject): Outcome<JsonError, T?> =
        node.fieldMap[propName]
            ?.let { idn -> jf.fromJson(idn) }
            ?: null.asSuccess()

    override fun setter(value: T?): (JsonNodeObject) -> JsonNodeObject =
        { wrapped ->
            value?.let {
                wrapped.copy(fieldMap = wrapped.fieldMap + (propName to jf.toJson(it)))
            } ?: wrapped
        }

}

sealed class JFieldBase<T, PT : Any>
    : ReadOnlyProperty<JProtocol<PT>, JsonProperty<T>> {

    protected abstract val binder: (PT) -> T

    protected abstract fun buildJsonProperty(property: KProperty<*>): JsonProperty<T>

    operator fun provideDelegate(thisRef: JProtocol<PT>, prop: KProperty<*>): JFieldBase<T, PT> {
        val jp = buildJsonProperty(prop)
        thisRef.registerSetter { jno, obj -> jp.setter(binder(obj))(jno) }
        thisRef.registerGetter { jno -> jp.getter(jno) }

        return this
    }

    override fun getValue(thisRef: JProtocol<PT>, property: KProperty<*>): JsonProperty<T> =
        buildJsonProperty(property)
}

class JField<T : Any, PT : Any>(
    override val binder: (PT) -> T,
    private val JConverter: JConverter<T>,
    private val jsonFieldName: String? = null
) : JFieldBase<T, PT>() {

    override fun buildJsonProperty(property: KProperty<*>): JsonProperty<T> =
        JsonPropMandatory(jsonFieldName ?: property.name, JConverter)

}

class JFieldMaybe<T : Any, PT : Any>(
    override val binder: (PT) -> T?,
    private val JConverter: JConverter<T>,
    private val jsonFieldName: String? = null
) : JFieldBase<T?, PT>() {

    override fun buildJsonProperty(property: KProperty<*>): JsonProperty<T?> =
        JsonPropOptional(jsonFieldName ?: property.name, JConverter)

}




