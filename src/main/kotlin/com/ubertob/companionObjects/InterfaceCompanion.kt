package com.ubertob.companionObjects

interface I1 : (String) -> Boolean {
    companion object
}


fun interface I2 : (String) -> Boolean {
    companion object
}

fun main() {

    val a: I1.Companion = I1
//    val b: I2 =   I2  //doesn't compile
    val c: I2 =   I2 { false }

}