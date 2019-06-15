package com.ubertob.extensions

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.jupiter.api.Test

class ExtensionPropertiesTest {

    @Test
    fun `concat strings and null`() {
        assertThat(null `++` null).isNull()
        assertThat("A" `++` null).isEqualTo("A")
        assertThat(null `++` "B").isEqualTo("B")
        assertThat("A" `++` "B").isEqualTo("AB")
    }

    @Test
    fun `fizzBuzz`() {

       val res = (1..15).map { it.fizzBuzz()}.joinToString()

        val expected ="1, 2, Fizz, 4, Buzz, Fizz, 7, 8, Fizz, Buzz, 11, Fizz, 13, 14, FizzBuzz"

        assertThat ( res ).isEqualTo(expected)

    }
}