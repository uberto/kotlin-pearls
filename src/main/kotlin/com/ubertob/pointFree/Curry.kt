package com.ubertob.pointFree

fun <A,B,C> ((A, B) -> C).curry(): (A) -> (B) -> C = {a: A -> {b:B -> this(a,b)}}

fun <A,B,C,D> ((A, B, C) -> D).curry(): (A) -> (B) -> (C) -> D = {a: A -> {b:B -> {c: C -> this(a,b,c)}}}



infix fun <A,B> ((A) -> B).`@`(a: A): B =  this(a)

infix fun <A,B,C> ((A, B) -> C).`@`(a: A): (B) -> C =  {b:B -> this(a,b)}

infix fun <A,B,C,D> ((A, B, C) -> D).`@`(a: A): (B) -> (C) -> D =  {b:B -> {c: C -> this(a,b,c)}}



fun main(){

    val f1 = String::length
    val f2 = String::plus
    val f3 = String::subSequence


    println (f1("ciccio"))
    println (f2("ciccio", "pasticcio"))


    println (f2.curry()("ciccio")("pasticcio"))

    println (f3.curry()("ciccio")(2)(4))


    println (f1 `@` "ciccio")
    println (f2 `@` "ciccio" `@` "pasticcio")
    println (f3 `@` "ciccio" `@` 2 `@` 4)

}