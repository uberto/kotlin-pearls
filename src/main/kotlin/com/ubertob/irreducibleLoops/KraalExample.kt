package com.ubertob.irreducibleLoops

fun main(args: Array<String>) {
    val iter = iterator {
        for (i in 1..10) {
            yield("hello")
        }
    }
    for (i in iter) {
        println(i)
    }
}