package com.ubertob.gotcha

import com.ubertob.extensions.with

fun numToStr(num: Int): String = num.toString()

fun dummy(x: Any): String = "42"

typealias IntToStr = (Int) -> String

fun applyToNum(x: Int, f: IntToStr): String = f(x)

fun main() {
    val x1 = applyToNum(100, ::numToStr) // 100
    val x2 = applyToNum(100, ::dummy) // 42

    val baa = applyRepeatedly(5, "ba", String::plus.with("na")) // "bananananana"
    println(baa)
}


fun <T> applyRepeatedly(times: Int, initial: T, f: (T) -> T): T =
    (1..times).fold(initial) { acc, t -> f(acc) }
