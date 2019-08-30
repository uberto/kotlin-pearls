package com.ubertob.sealedClass


interface Article {
    val name: String
//    val price: Double
}

sealed class PowerTool(open val price: Double, open val name: String)


data class CircularSaw(val diameter: Int,
                       val cordless: Boolean,
                       override val name: String,
                       override val price: Double): PowerTool(price, name)

data class DrillPress(val rpm: Int, override val name: String, override val price: Double): PowerTool(price, name)



