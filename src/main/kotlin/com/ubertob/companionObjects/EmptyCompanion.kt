package com.ubertob.companionObjects

data class Fruit(val name: String) {
    companion object
}


fun Fruit.Companion.apple(): Fruit = Fruit("apple")
fun Fruit.Companion.fromJson(json: String): Fruit = Fruit(json.substring(3,5))

fun main(){

    println( Fruit.apple() )

    println( Fruit.fromJson("""
        { name="banana"}
    """.trimIndent()) )

}