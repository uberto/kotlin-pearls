package com.ubertob.pointFree


typealias IntToInt = (Int) -> Int





infix fun <A, B, C> ((B) -> C).dot(f: (A) -> B): (A) -> C = { this( f( it ) ) }


fun <A, B, C, D, E> ciccio(a: (D) -> E, b:(C) -> D , c: (B) -> C, d: (A) -> B) : (A) -> E
 = (a dot b) dot (c dot d)


fun <A, B, C> dot1(f: (A) -> B): ((B) -> C ) -> (A) -> C = {it dot f }

fun <A, B, C> dot2(g: (B) -> C, f: (A) -> B): (A) -> C = g dot f

//val pasticcio = {x: IntToInt, y: IntToInt -> (dot1(x) ) dot (dot1(y)) }
//val pasticcio = {x: IntToInt, y: IntToInt -> (dot1(x) ) dot (dot1(y)) }



fun main() {

    val f1: IntToInt = { 3 + it }
    val f2: IntToInt = { 2 * it }


    val g1 = f1 dot f2
    val g2: IntToInt = { f1(f2(it)) }

    println(g1(7))
    println(g2(7))



    val onePlusLength = Int::inc dot String::length

    println( onePlusLength("123"))





}
