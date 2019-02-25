package com.ubertob.propertyDelegation

import kotlin.reflect.KProperty

class DelegatedClass {
    val a: String by Delegate()
    val b: String by Delegate()

    override fun toString() = "Delegated Class"
}

class Delegate() {
    operator fun getValue(thisRef: Any?, prop: KProperty<*>): String =
        "$thisRef, is '${prop.name}' ${prop.returnType}"

}

fun main() {
    val e = DelegatedClass()
    println(e.a)
    println(e.b)
}