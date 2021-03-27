package com.ubertob.extensions

import kotlin.Number as KNumber

fun String.tail() = this.substring(1)

fun String.head() = this.substring(0, 1)


fun <T> T.foo() = "foo $this"

fun <T: KNumber> T.doubleIt(): Double = this.toDouble() * 2

fun <T: CharSequence> T.startWith(start: String): Boolean = this.length >= start.length //TODO finish it




