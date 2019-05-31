package com.ubertob.extensions

import java.lang.RuntimeException


fun String.tail() = this.substring(1)

fun String.head() = this.substring(0, 1)


fun <T> T.foo() = "foo $this"

fun <T: Number> T.doubleIt(): Double = this.toDouble() * 2