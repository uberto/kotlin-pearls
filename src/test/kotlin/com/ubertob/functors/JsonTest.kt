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
        val json = JsonString.toJson(expected).shouldSucceed()

        val actual =JsonString.from(json).shouldSucceed()

        assertThat(actual).isEqualTo(expected)
//        expectThat(actual).isEqualTo(expected)
    }


    @Test
    fun `JsonNumber`(){

        val expected = 123.0
        val json = JsonNumber.toJson(expected).shouldSucceed()

        val actual =JsonNumber.from(json).shouldSucceed()

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `JsonObject`(){

        val expected = User(123, "abc")
        val json = JsonUser.toJson(expected).shouldSucceed()

        val actual =JsonUser.from(json).shouldSucceed()

        assertThat(actual).isEqualTo(expected)
    }
}




fun <T: Any> Outcome<*, T>.shouldSucceed(): T =
    this.fold({fail(it.msg)}, {it})
