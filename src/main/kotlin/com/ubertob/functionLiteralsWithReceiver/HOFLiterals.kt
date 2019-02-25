package com.ubertob.functionLiteralsWithReceiver


val apply5: ((Int) -> Int) -> Int = { it(5) }

val applySum: (Int) -> ((Int) -> Int) = { x -> {it + x} }

val applyInverse: ((Int) -> Int) -> ((Int) -> Int) =
    { f -> { -1 * f(it) } }


//use example
fun main(){
    println( apply5{ it * it }  )  //25
    println( applySum(4)(7) )   //11
    println( applyInverse{ it * it }(5) ) // -25
}