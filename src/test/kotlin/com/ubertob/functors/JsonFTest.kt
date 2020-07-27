package com.ubertob.functors

import com.ubertob.functionLiteralsWithReceiver.User
import com.ubertob.outcome.Outcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class JsonFTest {

    @Test
    fun `JsonString`(){

        val expected = "abc"
        val json = JsonString.toJson(expected)

        val actual =JsonString.from(json).shouldSucceed()

        expectThat(actual).isEqualTo(expected)
    }


    @Test
    fun `Json Double`(){

        val expected = 123.0
        val json = JsonDouble.toJson(expected)

        val actual =JsonDouble.from(json).shouldSucceed()

        expectThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Json Int`(){

        val expected = 124
        val json = JsonInt.toJson(expected)

        val actual =JsonInt.from(json).shouldSucceed()

        expectThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Json User and back`(){

        val expected = User(123, "abc")
        val json = JsonUser.toJson(expected)

        val actual =JsonUser.from(json).shouldSucceed()

        expectThat(actual).isEqualTo(expected)
    }

    val jsonUserArray = JsonArray(JsonUser)
    @Test
    fun `json array of Users`(){

        val expected = listOf(
            User(1, "Adam"),
            User(2, "Bob"),
            User(3,"Carol")
        )

        val node = jsonUserArray.toJson(expected)

        val actual = jsonUserArray.from(node).shouldSucceed()

        expectThat(actual).isEqualTo(expected)
    }
}

object JsonUser : JsonF<User> {

    val id by JField(JsonInt)
    val name by JField(JsonString)

    override fun from(node: JsonNode): Outcome<JsonError, User> = readObjNode(node) {
        ::User `=` id.getFrom(it) `+` name.getFrom(it)
    }

    override fun toJson(value: User): JsonNode = writeObjNode(
        id.setTo(value.id),
        name.setTo(value.name)
    )
}


//todo:
// complex types (something with User inside)
// checking parsing error with the position
// integration with Klaxon




fun <T: Any> Outcome<*, T>.shouldSucceed(): T =
    this.fold({fail(it.msg)}, {it})
