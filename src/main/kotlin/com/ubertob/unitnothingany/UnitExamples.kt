package com.ubertob.unitnothingany

import com.ubertob.Lambdas

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