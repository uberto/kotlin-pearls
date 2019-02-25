package com.ubertob.functionLiteralsWithReceiver


fun square(x: Int): Int =  x*x

fun squared(a: Int, f: (Int) -> Int): Int = f(square(a))

fun squared2(a: Int, f: Int.() -> Int): Int = f(square(a))


//use example
fun main(){
    println(square(5))                    // 25
    println(squared(5){ it * 2}   )       // 50
    println(squared2(5){ this * 2}   )    // 50
}