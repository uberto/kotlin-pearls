package com.ubertob.functors

import com.ubertob.functionLiteralsWithReceiver.User
import com.ubertob.outcome.*


sealed class JsonNode {
    fun asText(): Outcome<JsonError, String> =
        when (this) {
            is JsonNodeString -> this.text.asSuccess()
            else -> JsonError(this.toString(), "Expected Text but node.type is ${this::class}").asFailure()
        }

    fun asDouble(): Outcome<JsonError, Double> =
        when (this) {
            is JsonNodeNum -> this.num.asSuccess()
            else -> JsonError(this.toString(), "Expected Text but node.type is ${this::class}").asFailure()
        }

    fun asInt(): Outcome<JsonError, Int> =
        when (this) {
            is JsonNodeNum -> this.num.toInt().asSuccess()
            else -> JsonError(this.toString(), "Expected Text but node.type is ${this::class}").asFailure()
        }

    fun asObject(): Outcome<JsonError, Map<String, JsonNode>> =
        when (this) {
            is JsonNodeObject -> (this.obj).asSuccess()
            else -> JsonError(this.toString(), "Expected Text but node.type is ${this::class}").asFailure()
        }

}

data class JsonNodeString(val text: String) : JsonNode()
data class JsonNodeNum(val num: Double) : JsonNode()
data class JsonNodeBoolean(val value: Boolean) : JsonNode()
data class JsonNodeArray(val values: List<JsonNode>) : JsonNode()
data class JsonNodeObject(val obj: Map<String, JsonNode>) : JsonNode()
object JsonNodeNull : JsonNode()


data class JsonError(val json: String, val reason: String) : OutcomeError {
    override val msg = reason
}

interface Jsonable<T : Any> {
    fun from(node: JsonNode): Outcome<JsonError, T>
    fun toJson(value: T): Outcome<JsonError, JsonNode>
}


object JsonString : Jsonable<String> {
    override fun from(node: JsonNode): Outcome<JsonError, String> = node.asText()

    override fun toJson(value: String): Outcome<JsonError, JsonNode> =
        JsonNodeString(value).asSuccess()

}

object JsonInt : Jsonable<Int> {
    override fun from(node: JsonNode): Outcome<JsonError, Int> = node.asInt()

    override fun toJson(value: Int): Outcome<JsonError, JsonNode> =
        JsonNodeNum(value.toDouble()).asSuccess()
}

object JsonDouble : Jsonable<Double> {
    override fun from(node: JsonNode): Outcome<JsonError, Double> = node.asDouble()

    override fun toJson(value: Double): Outcome<JsonError, JsonNode> =
        JsonNodeNum(value).asSuccess()
}

object JsonUser : Jsonable<User> {
    val id = "id" to JsonInt
    val name = "name" to JsonString

    override fun from(node: JsonNode): Outcome<JsonError, User> = node.asObject().flatMap {
        val v = it[id.first]!!
        val k = id.second.from(v)

        k.flatMap { id ->

            val v2 = it[name.first]!!
            val k2 = name.second.from(v2)

            k2.map {
                User(id, it)
            }
        }
    }

    override fun toJson(value: User): Outcome<JsonError, JsonNode> =

        id.second.toJson(value.id).flatMap { idv ->
            name.second.toJson(value.name).flatMap { nav ->
                JsonNodeObject(
                    mapOf(
                        id.first to idv,
                        name.first to nav
                    )
                ).asSuccess()
            }
        }
}