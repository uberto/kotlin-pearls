package com.ubertob.extensions

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.ubertob.java.JavaPerson
import org.junit.jupiter.api.Test
import java.util.*

class ExtensionPropertiesTest {


    @Test
    fun `java beans`() {
        val p = JavaPerson()
        p.name = "Fred"
        p.age = 32

        assertThat(p.name).isEqualTo("Fred")
        assertThat(p.age).isEqualTo(32)
    }


    @Test
    fun `new property on Java`() {
        val d = Date()
        d.millis = 1001

        assertThat(d.millis ).isEqualTo(1001L)
        assertThat(d.millis ).isEqualTo(d.time)
    }

    @Test
    fun `concat strings and null`() {
        assertThat(null or null).isNull()
        assertThat("A" or null).isEqualTo("A")
        assertThat(null or "B").isEqualTo("B")
        assertThat("A" or "B").isEqualTo("AB")
    }

    @Test
    fun `fizzBuzz returns list of string according to the rules`() {

        val res = (1..15).map { it.fizzBuzz()}.joinToString()

        val expected ="1, 2, Fizz, 4, Buzz, Fizz, 7, 8, Fizz, Buzz, 11, Fizz, 13, 14, FizzBuzz"

        assertThat ( res ).isEqualTo(expected)

    }
}