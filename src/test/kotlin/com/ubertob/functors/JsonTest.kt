package com.ubertob.functors

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ubertob.functionLiteralsWithReceiver.User
import com.ubertob.outcome.Outcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
//import strikt.api.expectThat

class JsonTest {

    @Test
    fun `JsonString`(){

        val expected = "abc"
        val json = JsonString.toJson(expected)

        val actual =JsonString.from(json).shouldSucceed()

        assertThat(actual).isEqualTo(expected)
//        expectThat(actual).isEqualTo(expected)
    }


    @Test
    fun `Json Double`(){

        val expected = 123.0
        val json = JsonDouble.toJson(expected)

        val actual =JsonDouble.from(json).shouldSucceed()

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Json Int`(){

        val expected = 124
        val json = JsonInt.toJson(expected)

        val actual =JsonInt.from(json).shouldSucceed()

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Json User and back`(){

        val expected = User(123, "abc")
        val json = JsonUser.toJson(expected)

        val actual =JsonUser.from(json).shouldSucceed()

        assertThat(actual).isEqualTo(expected)
    }
}

//todo:
// complex types (something with User inside)
// checking parsing error with the position
// integration with Klaxon




fun <T: Any> Outcome<*, T>.shouldSucceed(): T =
    this.fold({fail(it.msg)}, {it})
