package com.ubertob.functors


import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.ubertob.outcome.*
import com.ubertob.unitnothingany.map

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
        is JsonArray<*> -> JsonNodeArray( value.value.filterNotNull().mapIndexed { i, e -> valueToNode(e, path + "$i").onFailure { return it.asFailure() } } ).asSuccess()
        else -> JsonError(null, "type ${value::class} impossible to map! $value" ).asFailure()
    }
}

fun toKlaxon(node: JsonNodeObject): Map<String, Any?> =
    node.fieldMap.entries.map { entry: Map.Entry<String, AbstractJsonNode> ->
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
        .bind { conv.extract(it) }

fun <T: Any> toJsonString(value: T, conv: JAny<T>): Outcome<JsonError, String> = conv.serialize(value)
    .let { JsonObject(toKlaxon(it)).toJsonString().asSuccess() }

