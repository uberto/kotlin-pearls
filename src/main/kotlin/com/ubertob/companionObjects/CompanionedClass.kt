package com.ubertob.companionObjects

import kotlin.math.sqrt

interface IShape {

    companion object {
        fun doSomething() {
            println("!!!")}
    }
}

class CompanionedClass private constructor(val x: Double, val y: Double) {

    fun distance(): Double = sqrt(x*x + y*y)


//    companion object {
//        fun shape(l: Double): CompanionedClass = CompanionedClass(l, l)
//    }

    companion object Square: IShape {
        fun shape(l: Double): CompanionedClass = CompanionedClass(l, l)
    }

    object HRect:IShape {
        fun shape(l: Double): CompanionedClass = CompanionedClass(l * 2, l)
    }

    object VRect:IShape {
        fun shape(l: Double): CompanionedClass = CompanionedClass(l, l * 2)
    }

}


fun main(){
    val myShape = CompanionedClass.shape(5.0)
//    myShape.shape(4.5) don't work

    CompanionedClass.shape(5.0)
    CompanionedClass.Square.shape(5.0)
    CompanionedClass.HRect.shape(5.0)
    CompanionedClass.VRect.shape(5.0)
    val vRect: IShape = CompanionedClass.VRect
//    vRect.doSomething()
    IShape.doSomething()
}