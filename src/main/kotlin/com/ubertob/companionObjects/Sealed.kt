package com.ubertob.companionObjects

import kotlin.random.Random

interface BaseCompanionObject {
    fun hello(): String
}

sealed class Sealed{
    companion object {
        fun hello(obj: Sealed) = when(obj) {
            is A -> A.hello()
            is B -> B.hello()
        }
    }
}
data class A(val a: String): Sealed() {
    companion object ACO: BaseCompanionObject {
        override fun hello(): String = "Hi I'm A"
    }
}
data class B(val b: Int): Sealed() {
    companion object BCO: BaseCompanionObject {
        override fun hello(): String = "Hi I'm B"
    }
}

fun processA(a: A) = println(a.a)

fun processB(b: B) = println(b.b)

fun main(){

    val r: Sealed = if (Random.nextBoolean()) A("one") else B(2)



}
