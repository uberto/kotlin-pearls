package com.ubertob.extensions

import java.util.*

infix fun String?.`++`(other:String?): String? = if (this == null) other else if (other == null) this else this + other

val Int.isFizz: Boolean
    get() = this % 3 == 0

val Int.isBuzz: Boolean
    get() = this % 5 == 0


fun Int.fizzBuzz(): String =  "Fizz".takeIf { isFizz } `++` "Buzz".takeIf { isBuzz } ?: toString()


var Date.millis: Long
    get() = this.getTime()
    set(x) = this.setTime(x)

