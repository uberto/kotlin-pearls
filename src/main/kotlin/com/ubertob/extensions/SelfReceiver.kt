package com.ubertob.extensions


sealed class Symbol
data class Number(val x: Int) : Symbol()
data class Alpha(val text: String) : Symbol()

fun <Self : Symbol, T> Self.log(block: Self.() -> T): T {
    println("doing something with ")
    return block(this)
}

fun main() {

    Number(7).log { x+7 }
    Alpha("abc").log { text.toUpperCase() }

}