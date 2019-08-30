package com.ubertob.pureFunctions

import com.ubertob.outcome.Failure
import com.ubertob.outcome.Outcome
import com.ubertob.outcome.OutcomeError
import com.ubertob.outcome.Success

fun `example from Roman Elizarov`() {
//    flow { for(a in ...) for(b in ...) .... emit(result) }
}

fun createList() {
    val l: MutableList<Pair<Char, Int>> = mutableListOf()

    for (a in 1..2)
        for (b in 3..4)
            for (c in 'a'..'b') {
                l.add(c to a * b)
            }

    println(l)
}


data class HalfError(override val msg: String) : OutcomeError

fun half(x: Int): Outcome<HalfError, Int> =
    if (x % 2 == 0) Success(x / 2) else Failure(HalfError("$x not even"))


fun combineOutcome() {

    val hIter = half(42).iterator()

    println(hIter.hasNext())
    println(hIter.hasNext())
    println(hIter.next())
    println(hIter.hasNext())
    println(hIter.hasNext())

    for (a in 1..20) for (b in half(a)) for (c in half(b))
        println(a to c)

}


fun main() {

    createList()

    combineOutcome()
}