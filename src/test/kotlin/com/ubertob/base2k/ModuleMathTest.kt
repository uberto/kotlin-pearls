package com.ubertob.base2k

import org.junit.jupiter.api.Test
import java.lang.RuntimeException

class ModuleMathTest {

    val tot = 2048 * 2048
    val bigPrime = 200560490131L

    fun shuffle(x: Int): Int = ((x * bigPrime) % tot).toInt()

    @Test
    fun `dont repeat number`() {

        val done = Array(tot) { false }

        for (i in 0 until tot) {
            val shuffled = shuffle(i)
            if (done[shuffled])
                throw RuntimeException("Repeated! $i  $shuffled")

            done[shuffled] = true
        }
    }
}