package com.gamasoft.namedCompanionObject

import kotlin.math.sqrt

class CompanionedClass(val x: Double, val y: Double) {

    fun distance(): Double = sqrt(x*x + y*y)




//    companion object {
//        fun shape(l: Double): CompanionedClass = CompanionedClass(l, l)
//    }

    companion object Square {
        fun shape(l: Double): CompanionedClass = CompanionedClass(l, l)
    }

    object HRect {
        fun shape(l: Double): CompanionedClass = CompanionedClass(l * 2, l)
    }

    object VRect {
        fun shape(l: Double): CompanionedClass = CompanionedClass(l, l * 2)
    }

}


fun main(){

    CompanionedClass.shape(5.0)
    CompanionedClass.Square.shape(5.0)
    CompanionedClass.HRect.shape(5.0)
    CompanionedClass.VRect.shape(5.0)
}