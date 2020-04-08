package com.ubertob.double

import java.lang.Math.round as jround
import kotlin.math.round

fun main() {
    val r = round(4.5) + round(9.5)

    println("kotlin round 4.5+9.5 $r")

    val r2 = round(5.5) + round(9.5)

    println("kotlin round 5.5+9.5 $r2")

    val r3 = jround(4.5) + jround(9.5)

    println("java round 4.5+9.5 $r3")

    val r4 = jround(5.5) + jround(9.5)

    println("java round 5.5+9.5 $r4")

    val r5 = round(-4.5) + round(-9.5)

    println("kotlin round -4.5-9.5 $r5")

    val r6 = jround(-4.5) + jround(-9.5)

    println("java round -4.5-9.5 $r6")

}