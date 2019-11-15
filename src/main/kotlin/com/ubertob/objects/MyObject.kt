package com.ubertob.objects

object Leaf {
    val weight = "weight $leafWeight grams"
    val color = "is Green"
}

fun main() {
    println("The leaf ${lc}") //if we comment this line the next work
    println("The leaf ${Leaf.weight}")
}