package com.ubertob.immutableCollections

import java.util.*
import kotlin.jvm.internal.markers.KMappedMarker
import kotlin.jvm.internal.markers.KMutableList

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

// because
// is MutableList got compiled in a call to
//
// kotlin.jvm.internal.TypeIntrinsics
//    fun isMutableList(obj: Any): Boolean {
//        return obj is List<*> && (obj !is KMappedMarker || obj is KMutableList)
//    }
// (List here is java.util.List, not Kotlin List)
// so all Java List classes are MutableList unless marked with   KMappedMarker

// everytime you create a new List you are inheriting from KMappedMarker
// see the ImmutableList I created in this file
//
//// signature <T:Ljava/lang/Object;>Ljava/lang/Object;Ljava/util/List<TT;>;Lkotlin/jvm/internal/markers/KMappedMarker;
//// declaration: com/ubertob/immutableCollections/ImmutableList<T> implements java.util.List<T>, kotlin.jvm.internal.markers.KMappedMarker
//public final class com/ubertob/immutableCollections/ImmutableList implements java/util/List kotlin/jvm/internal/markers/KMappedMarker {


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

//    boringMethodToPrintAList(functionalList)

    boringMethodToPrintAList(javaList)

    boringMethodToPrintAList(immutableList)

    boringMethodToPrintAList(mutableList)

}