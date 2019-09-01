package com.ubertob.tailrec

class Recursion {
}

tailrec fun sumAll(acc: Int, x: Int):Int = when {
    x <= 1 -> acc
    else -> sumAll(acc+x, x-1)
}


fun sumAllNTR(acc: Int, x: Int):Int = when {
    x <= 1 -> acc
    else -> sumAllNTR(acc+x, x-1)
}


fun main(){
    println(sumAll(0,100000))
    println(sumAllNTR(0,100000))
}
