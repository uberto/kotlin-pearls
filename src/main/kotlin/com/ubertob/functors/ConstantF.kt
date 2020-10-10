package com.ubertob.functors

import java.util.function.Function

data class ConstantOrErrorF<T>(private val value:T?, private val isError: Boolean) {
    fun <U> transform(f: (T) -> U): ConstantOrErrorF<U> =
        if (isError || value == null)
            ConstantOrErrorF(null, true)
        else
            ConstantOrErrorF(f(value), false)
}


data class ConstantF<T>(private val value:T) {
    fun <U> transform(f: (T) -> U): ConstantF<U> = ConstantF(f(value))

    companion object {
        fun <T,R> lift(f: (T) -> R): (ConstantF<T>) -> ConstantF<R> =
            { c: ConstantF<T> -> c.transform(f) }
    }
}


fun <T> identity(a: T): T = a

infix fun <A, B, C> ((A)->B).andThen(f: (B)->C): (A)->C = { a:A -> f(this(a)) }


fun <T,R> liftList(f: (T) -> R): (List<T>) -> List<R> =
    { c: List<T> -> c.map(f) }

fun main(){

    val a = ConstantF("this is a string")
    val b1 = a.transform(String::length)

    val strLenLifted = ConstantF.lift(String::length)
    val b2 = strLenLifted(a)

    println(b1==b2)

    val a1 = a.transform(::identity)
    println(a==a1)

    val splitInWords: (String) -> List<String> = { s:String -> s.split(' ') }
    val c1 = a.transform (splitInWords).transform(List<String>::size)

    val NumOfWords: (String) -> Int = splitInWords andThen List<String>::size

    val c2 = a.transform(NumOfWords)

    println(c1 == c2)
}
