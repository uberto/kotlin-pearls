package com.ubertob.pureFunctions

//ˈ U+02C8


fun pitagora(a: Double, b: Double) = Math.sqrt( a*a + b*b)

fun pitagoraˈ(a: Double, b: Double): Double =                                                                                 {fun
    consider(
        x: Double = (a * a),
        y: Double = (b * b)
    ) = Math.sqrt(x+y)
                                                                                                                     consider()}()

fun pitagoraˈˈ(a: Double, b: Double): String =                                                                                 {fun
    LET(
        x: Double = (a * a),
        y: Double = (b * b),
        z: String = "$a $b $x $y"
    ) = z + Math.sqrt(x+y)
                                                                                                                     LET()}()


//
//fun pitagoraˈ(a: Double, b: Double) =
//    { x: Double = (a * a),
//        y: Double = (b * b)
//        ->
//        Math.sqrt(a * a + b * b)
//
//    }

fun add(
    a: Int,
    x: Int = (a * 5),
    y: Long = (System.currentTimeMillis())
) = x + y