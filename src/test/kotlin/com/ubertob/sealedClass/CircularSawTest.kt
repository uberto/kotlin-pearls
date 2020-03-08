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

    data class Point2(val x: Int, val y: Int)

//    {
//        constructor(pair: Pair<Int, Int>) : this(pair.first, pair.second)
//    }

    @Test
    fun `cicico`(){

            listOf(1 to 2, 3 to 4).map{(x,y) -> Point2(x,y) }
            listOf(1 to 2, 3 to 4).map{ Point2(it.first,it.second) }
            listOf(1 to 2, 3 to 4).map(::fromPair)
            listOf(1 to 2, 3 to 4).mapPair(::Point2)


    }

    fun <A, B, R> List<Pair<A, B>>.mapPair(f: (A, B) -> R): List<R> = this.map { f(it.first, it.second) }

    fun fromPair(p: Pair<Int, Int>) = Point2(p.first, p.second)


}
