package com.ubertob.extensions

import java.util.*

infix fun String?.or(other:String?): String? = when {
    this == null -> other
    other == null -> this
    else -> this + other
}

val Int.isFizz: String?
    get() = "Fizz".takeIf {this % 3 == 0 }

val Int.isBuzz: String?
    get() = "Buzz".takeIf {this % 5 == 0 }


fun Int.fizzBuzz(): String = isFizz or isBuzz  ?: toString()



var Date.millis: Long
    get() = this.getTime()
    set(x) = this.setTime(x)

