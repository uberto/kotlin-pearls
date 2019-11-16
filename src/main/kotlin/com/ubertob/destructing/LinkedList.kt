package com.ubertob.destructing

sealed class LinkedList<out A>
data class Cons<A>(val head: A, val tail: LinkedList<A>):LinkedList<A>()
object Nil: LinkedList<Nothing>()

fun sum(xs: LinkedList<Int>): Int = when(xs) {
    is Cons -> xs.head + sum(xs.tail)
    is Nil -> 0
}