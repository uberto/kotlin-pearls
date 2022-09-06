package com.ubertob.extensions


fun (String.() -> String).foo(): String.() -> String = {this@foo("foo$this")}


fun <A,B,C>((A,B) -> C).using(a: A): (B) -> C = {this(a, it)}
fun <A,B,C>(A.(B) -> C).with(b: B): (A) -> C = {this(it, b)}

operator fun <A,B,C>(A.(B) -> C).invoke(b: B): (A) -> C = {this(it, b)}


fun main(){

    println( "ciccio".tail())

    val ffoo = String::tail.foo()

    println( "ciccio".ffoo())

    val nums = listOf(4,3,2,1,0,1,2,3,4)
    println(nums.map(String::get.using("table")))

    val words = listOf("cat", "dog", "house", "door")

    println(words.map(String::get.with(1)))

    println(words.map((String::get)(2)))
}