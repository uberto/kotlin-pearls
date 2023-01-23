package com.ubertob.gotcha


object UnitUB

fun five(): Int = 5

fun takeUnit(x: Unit) {println(x)}

fun takeUnitLazy(x: () -> Unit) {println(x())}

fun takeUnitUBLazy(x: () -> UnitUB) {println(x())}

fun <T:Unit> takeUnitTLazy(x: () -> T) {println(x())}

fun main(){


//
//    val x: Unit = five() //doesn't compile
//
//    takeUnit(five()) //doesn't compile

    takeUnitLazy(::five) //compile

//    takeUnitUBLazy(::five) //doesn't compile

//    takeUnitTLazy(::five) //doesn't compile

    takeUnitTLazy(::println) //compile

}
