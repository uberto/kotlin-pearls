package com.ubertob.immutableCollections

import java.util.*

data class ImmutableList<T>(private val origList: List<T>): List<T> by origList


fun boringMethodWithAList(list: List<*>){

    println(list::class.java)

    println( list )

    naughtyFun(list)

    println( list )
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

    val list = listOf("albert", "brian", "charlie")

    val mutableList = mutableListOf(1,2,3,4,5,6,7)

    val unmodifiableList = Collections.unmodifiableList( listOf("dan", "eddy", "frank"))
    val functionalList = ImmutableList(list)


    boringMethodWithAList(javaList)

    boringMethodWithAList(list)

    boringMethodWithAList(mutableList)

//    boringMethodWithAList(unmodifiableList)  Exception in thread "main" java.lang.UnsupportedOperationException

    boringMethodWithAList(functionalList)

}