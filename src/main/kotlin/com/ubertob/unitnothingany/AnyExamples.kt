package com.ubertob.unitnothingany

fun whatIcanDoWithAny(obj: Any){
    obj.toString()
}

open class MyClass: Any() {
    fun bye(): String = "bye bye"

    override operator fun equals(other: Any?) = other != null
}

class MySubClass : MyClass() {
    fun hello(): String = "hello"
}

class MyClass2 {
    fun bye(): String = "bye bye"
}

fun main(){

    val a = MyClass()
    val b = MyClass2()
    val c: Any = 123

    println(a.equals(b))
    println(a.equals(123))
    println(a == c)
    println(123.equals(a))
    println(a == MySubClass())
//    println(a == "dd") doesn't compile
}