package com.ubertob.pointFree


typealias IntToInt = (Int) -> Int





infix fun <A, B, C> ((B) -> C).dot(f: (A) -> B): (A) -> C = { this( f( it ) ) }

infix fun <A, B, C> ((B) -> C).`&`(f: (A) -> B): (A) -> C = this dot f

infix fun <A, B, C> ((A) -> B).`|`(f: (B) -> C): (A) -> C = f dot this


fun <A, B, C, D, E> ciccio(a: (D) -> E, b:(C) -> D , c: (B) -> C, d: (A) -> B) : (A) -> E
 = (a dot b) dot (c dot d)


fun <A, B, C> dot1(f: (A) -> B): ((B) -> C ) -> (A) -> C = {it dot f }

fun <A, B, C> dot2(g: (B) -> C, f: (A) -> B): (A) -> C = g dot f

//val pasticcio = {x: IntToInt, y: IntToInt -> (dot1(x) ) dot (dot1(y)) }
//val pasticcio = {x: IntToInt, y: IntToInt -> (dot1(x) ) dot (dot1(y)) }

fun main() {

    val plusThree: IntToInt = { 3 + it }
    val timesTwo: IntToInt = { 2 * it }


    val g1 = plusThree dot timesTwo
    val g2: IntToInt = { plusThree(timesTwo(it)) }
    val g3 = plusThree `&` timesTwo
    val g4 = timesTwo `|` plusThree

    println(g1(7))
    println(g2(7))
    println(g3(7))
    println(g4(7))

    val onePlusLength1 = Int::inc dot String::length
    val onePlusLength2 = String::length `|` Int::inc

    println( "onePlusLength1  ${onePlusLength1("123")}")
    println( "onePlusLength2  ${onePlusLength2("123")}")





}
