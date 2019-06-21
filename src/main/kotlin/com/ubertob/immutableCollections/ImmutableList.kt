package com.ubertob.immutableCollections

import java.util.*

class ImmutableList<T>(private val origList: List<T>): List<T> by origList


fun boringMethodToPrintAList(list: List<*>){

    println(list::class.java)

    val size = list.size

    for (i in (0 until size)){

        println("element $i ${list[i]}")

        naughtyFun(list)

    }
}

fun <T> naughtyFun(list: List<T>) {

    if (list.size > 1 && list is MutableList<T>){
        val e = list[0]
        list[0] = list[1]
        list[1] = e
    }

}

fun <T> sincereFun(list: MutableList<T>) {

    if (list.size > 1){
        val e = list[0]
        list[0] = list[1]
        list[1] = e
    }

}



fun main() {

    val javaList = ArrayList<Int>().apply {
        add(1)
        add(2)
        add(3)
    }

    val immutableList = listOf("albert", "brian", "charlie")

    val mutableList = mutableListOf(1,2,3,4,5,6,7)

    val functionalList = ImmutableList(immutableList)

//    boringMethodToPrintAList(functionalList)

    boringMethodToPrintAList(javaList)

    boringMethodToPrintAList(immutableList)

    boringMethodToPrintAList(mutableList)

}