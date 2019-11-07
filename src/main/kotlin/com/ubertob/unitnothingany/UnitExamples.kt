package com.ubertob.unitnothingany

import com.ubertob.java.Lambdas

fun whatIsLove(): Unit = println("Baby don't hurt me!")


object JustAnObject {

    override fun toString(): String {
        return "JustAnObject"
    }
}

fun callMe(block: (Int) -> Unit): Unit = (1..100).forEach(block)

fun main(){

    callMe { Lambdas.printNum }

}


fun fooOne(): Unit { while (true) {} }
fun fooZero(): Nothing { while (true) {} }
//both ok
fun barOne(): Unit { println("hi")}
//fun barZero(): Nothing { println("hi") }  //error
//barZero not compiling