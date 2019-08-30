package com.ubertob.immutableCollections

import java.util.*

import kotlin.collections.ArrayList

data class NonMutableList<T>(private val origList: List<T>): List<T> by origList

class ImplementedList: List<String>{
    override val size: Int
        get() = 2

    override fun contains(element: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun containsAll(elements: Collection<String>): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(index: Int): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun indexOf(element: String): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isEmpty(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun iterator(): Iterator<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun lastIndexOf(element: String): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun listIterator(): ListIterator<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun listIterator(index: Int): ListIterator<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

fun boringMethodWithAList(list: List<*>){

    println(list::class.java)


    if (list is java.util.List<*>){
        println("Java List")
    }


    println( "before $list" )

    naughtyFun(list)

    println( "later $list" )
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

    val list = listOf("albert", "brian", "charlie")

    val mutableList = mutableListOf(1,2,3,4)

    val javaList = ArrayList<Int>().apply {
        add(1)
        add(2)
        add(3)
    }


    val unmodifiableList = Collections.unmodifiableList( listOf("dan", "eddy", "frank"))


    val delegatedList = NonMutableList(list)


    boringMethodWithAList(ImplementedList())

    boringMethodWithAList(delegatedList)

    boringMethodWithAList(list)

    boringMethodWithAList(mutableList)

    boringMethodWithAList(javaList)

//    boringMethodWithAList(unmodifiableList)  Exception in thread "main" java.lang.UnsupportedOperationException

}