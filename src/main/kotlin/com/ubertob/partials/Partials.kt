package com.ubertob.partials

import com.ubertob.pointFree.curry
import java.time.LocalDate
import java.util.*


object `?`

fun <A, B, Result> ((A, B) -> Result).partial(a: A, b: `?`) = fun(b: B) = this(a, b)
fun <A, B, Result> ((A, B) -> Result).partial(a: `?`, b: B) = fun(a: A) = this(a, b)
fun <A, B, C, Result> ((A, B, C) -> Result).partial(a: `?`, b: B, c: C) = fun(a: A) = this(a, b, c)
fun <A, B, C, Result> ((A, B, C) -> Result).partial(a: A, b: `?`, c: C) = fun(b: B) = this(a, b, c)
fun <A, B, C, Result> ((A, B, C) -> Result).partial(a: A, b: B, c: `?`) = fun(c: C) = this(a, b, c)
fun <A, B, C, Result> ((A, B, C) -> Result).partial(a: `?`, b: `?`, c: C) = fun(a: A, b: B) = this(a, b, c)
fun <A, B, C, Result> ((A, B, C) -> Result).partial(a: A, b: `?`, c: `?`) = fun(b: B, c: C) = this(a, b, c)
fun <A, B, C, Result> ((A, B, C) -> Result).partial(a: `?`, b: B, c: `?`) = fun(a: A, c: C) = this(a, b, c)


//Nat's example
data class Footballer(val name: String, val dob: LocalDate, val locale: Locale)

val english = ::Footballer.partial(`?`, `?`, Locale.UK)

val french = fun(name: String, dob: LocalDate) = Footballer(name = name, dob = dob, locale = Locale.FRANCE)
val davidBeckham = english("David Beckham", LocalDate.of(1975, 5, 2))
val ericCantona = french("Eric Cantona", LocalDate.of(1966, 5, 24))


private fun <A, B, C, R> ((A,B,C) -> R).third(c: C): ((A,B) -> R) = {a, b ->this(a,b,c) }

val english2 = ::Footballer.third(Locale.UK)

val davidBeckham2 = english2("David Beckham", LocalDate.of(1975, 5, 2))

val davidBeckham3 = ::Footballer.curry()("David Beckham")(LocalDate.of(1975, 5, 2))(Locale.UK)

//val davidBeckham4 = ::Footballer `!` "David Beckham" `*` LocalDate.of(1975, 5, 2) `*` Locale.UK