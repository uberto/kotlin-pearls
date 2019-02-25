package com.ubertob.functionLiteralsWithReceiver

val x: Double = 5.0
val name: String = "Uberto"
val square: (Int) -> Int = { it*it }


//use example
fun main(){
    println(x)          // 5.0
    println(name)       // Uberto
    println(square(3))  // 9
}