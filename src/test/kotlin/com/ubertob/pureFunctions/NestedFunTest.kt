package com.ubertob.pureFunctions

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class NestedFunTest {


    fun pythagora(a: Double, b: Double) = Math.sqrt( a*a + b*b)

    fun pythagoraˈ(a: Double, b: Double): Double =                                                                                 {fun
        LETTING(
            x: Double = (a * a),
            y: Double = (b * b)
        ) = Math.sqrt(x+y)
                                                                                                                          LETTING()}()

    @Test
    fun `Pythagorean theorem` () {

        assertThat(pythagora(3.0, 4.0)).isEqualTo(5.0)
        assertThat(pythagoraˈ(3.0, 4.0)).isEqualTo(5.0)

        assertThat(pythagora(7.0, 9.0)).isEqualTo(pythagoraˈ(7.0, 9.0))

    }
}