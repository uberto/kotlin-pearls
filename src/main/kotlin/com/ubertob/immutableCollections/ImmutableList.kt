package com.ubertob.immutableCollections

import java.util.*

class ImmutableList<T>(private val origList: List<T>): List<T> by origList


fun boringMethodToPrintAList(list: List<*>){

    println(list::class.java)

    val size = list.size

    for (i in (0 until size)){

        println("element $i ${list[i]}")

        massageTheList(list)

    }
}

fun massageTheList(list: List<*>) {

//  Exception in thread "main" java.lang.UnsupportedOperationException
// why java.util.Arrays$ArrayList implement MutableList?

    if (list.isNotEmpty() && list is MutableList<*>){
        list.removeAt(0)
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

    boringMethodToPrintAList(functionalList)

    boringMethodToPrintAList(javaList)

    boringMethodToPrintAList(immutableList)

    boringMethodToPrintAList(mutableList)

}