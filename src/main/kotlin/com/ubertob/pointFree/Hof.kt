package com.ubertob.pointFree


fun hofStyle1(x: Int): (Int) -> Int = fun(a: Int)= a + x

fun hofStyle2(x: Int): (Int) -> Int = { a-> a + x }


fun repeatExplicit(times: Int): (String) -> String = fun(s: String)= s.repeat(times)

fun repeatCompact(times: Int): (String) -> String = { s -> s.repeat(times) }
