package com.ubertob.sealedClass

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.size
import org.junit.jupiter.api.Test

internal class CircularSawTest{

    @Test
    fun `get price from powertools`(){

        val tools = listOf<PowerTool>(
            CircularSaw(6, true, "abc", 12.3),
            DrillPress(3000, "jkl", 23.45),
            CircularSaw(8, false, "fgh", 14.5)
            )

        val prices = tools.map { it.price }

        assertThat(prices).all{
            contains(12.3)
            contains(23.45)
            contains(14.5)
            size().isEqualTo(3)
        }
    }
}
