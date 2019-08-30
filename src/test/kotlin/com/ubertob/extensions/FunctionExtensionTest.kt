package com.ubertob.extensions

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ubertob.functionLiteralsWithReceiver.User
import org.junit.jupiter.api.Test

class FunctionExtensionTest {

    @Test
    fun `head and tail`() {
        assertThat("abcde".head()).isEqualTo("a")
        assertThat("abcde".tail()).isEqualTo("bcde")
    }


    @Test
    fun `foo anything`() {
        assertThat(123.foo()).isEqualTo("foo 123")
        assertThat(User(1, "Frank").foo()).isEqualTo("foo User(id=1, name=Frank)")
        assertThat(null.foo()).isEqualTo("foo null")
    }

    @Test
    fun `double it`() {
        assertThat(123.doubleIt()).isEqualTo(246.0)
        assertThat(123.4.doubleIt()).isEqualTo(246.8)
        assertThat(123L.doubleIt()).isEqualTo(246.0)
    }

}