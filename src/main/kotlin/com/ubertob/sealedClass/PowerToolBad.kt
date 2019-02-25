package com.ubertob.sealedClass


sealed class PowerTool_(val name: String, val price: Double)

data class CircularSaw_(val diameter: Int, val cordless: Boolean, val name1: String, val price1: Double): PowerTool_(name1, price1)

data class DrillPress_(val rpm: Int, val name1: String, val price1: Double): PowerTool_(name1, price1)



sealed class PowerTool__()

data class CircularSaw__(val diameter: Int, val cordless: Boolean, val name: String, val price: Double): PowerTool__()

data class DrillPress__(val rpm: Int, val name: String, val price: Double): PowerTool__()



fun getPrice(tool: PowerTool__): Double =
        when(tool){
            is CircularSaw__ -> tool.price
            is DrillPress__ -> tool.price
        }
