package com.ubertob.json

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.ubertob.functors.*
import com.ubertob.outcome.Outcome
import com.ubertob.outcome.asSuccess
import com.ubertob.outcome.flatMap
import sun.jvm.hotspot.oops.CellTypeState.value
import java.lang.RuntimeException


fun klaxonConvert(json: String): Outcome<JsonError, JsonNodeObject> =
    Outcome.tryThis { Parser.default().parse(json) as JsonObject }
        .mapFailure { JsonError(JsonNodeNull, it.msg) }
        .map { fromKlaxon(it.map) }

fun fromKlaxon(map: MutableMap<String, Any?>): JsonNodeObject =
    map.entries.mapNotNull {entry ->
        entry.value?.let {
            when(it){
                is Int -> entry.key to JsonNodeNum(it.toDouble())
                is Double -> entry.key to JsonNodeNum(it)
                is String -> entry.key to JsonNodeString(it)
                is Boolean -> entry.key to JsonNodeBoolean(it)
                is JsonObject -> entry.key to fromKlaxon(it.map)
                else -> throw RuntimeException("!!!! array?")
            }
        }
    }.toMap().let { JsonNodeObject(it) }

fun toKlaxon(node: JsonNodeObject): Map<String, Any?> =

    node.fieldMap.entries.map { entry ->
        when(val v = entry.value) {
            is JsonNodeBoolean -> entry.key to v.value
            is JsonNodeString -> entry.key to v.text
            is JsonNodeNum -> entry.key to v.num
            is JsonNodeArray -> TODO()
            is JsonNodeObject -> entry.key to toKlaxon(v)
            JsonNodeNull -> entry.key to null
        }
    }.toMap()




fun <T> fromJsonString(json: String, conv: JsonF<T>): Outcome<JsonError, T> = klaxonConvert(json).flatMap {
    conv.from(it)
}

fun <T> toJsonString(value : T, conv: JsonObj<T>): Outcome<JsonError, String> = conv.serialize(value)
    .let {  JsonObject( toKlaxon(it)).toJsonString().asSuccess() }



fun main() {

    val parser: Parser = Parser.default()
    val stringBuilder: StringBuilder = StringBuilder("{\"name\":\"Cedric Beust\", \"age\":23, \"obj\": {\"field\": \"value\", \"int\":3}   }")
    val json: JsonObject = parser.parse(stringBuilder) as JsonObject

    println("klaxxon " + json.map["age"]!!::class.java)
}