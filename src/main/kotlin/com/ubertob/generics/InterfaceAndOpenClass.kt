package com.ubertob.generics

open class C {
   fun <E> m(e:E): E = e
}

interface I {
    fun <E: I> m(e:E): E
}

class O: C(), I {
    override fun <E : I> m(e: E): E = e
}

fun main(){
    val o = O()
    val p = o.m(o)
    println(o == p) //true
}

