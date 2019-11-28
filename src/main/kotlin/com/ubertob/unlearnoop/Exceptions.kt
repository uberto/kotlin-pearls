package com.ubertob.unlearnoop


class JsonNode {
    fun get(path: String): JsonNode {
        return TODO("not implemented")
    }

}


fun JsonNode.asContractId(): ContractId? = TODO()
fun JsonNode.asUserId(): UserId? = TODO()
fun JsonNode.asAmount(): Amount? = TODO()


fun parseContract(json: JsonNode): Contract {
    val id = json.get("id").asContractId()
    val userId = json.get("user-id").asUserId()
    val amount = json.get("amount").asAmount()

    return Contract(id!!, userId!!, amount!!)
}

fun parseContractSafe(json: JsonNode): Contract? =
    json.run {
        get("id").asContractId() and
        get("user-id").asUserId() and
        get("amount").asAmount()
    }?.map3(::Contract)



infix fun <A : Any, B : Any> A?.and(other: B?): Pair<A, B>? =
    this?.let { other?.let { this to other } }

fun <A : Any, B : Any, C : Any, R : Any>
        Pair<Pair<A, B>, C>.map3(f: (A, B, C) -> R): R {
    val (pair, c) = this
    val (a, b) = pair
    return f(a, b, c)
}

