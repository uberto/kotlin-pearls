package com.ubertob.implementationDelegation

//copier and scanner

data class Fruit (val name: String)

interface pickAFruit{
    fun pick(): Fruit
    fun eat(fruit: Fruit)
}

interface eatAFruit{
    fun peel(fruit: Fruit)
    fun eat(fruit: Fruit)
}

object eater: eatAFruit {
    override fun peel(fruit: Fruit) {
        println("peeled ${fruit.name}")
    }

    override fun eat(fruit: Fruit) {
        println("eaten ${fruit.name}")
    }

}


class pickerAndEater: pickAFruit, eatAFruit by eater {
    override fun pick(): Fruit {
        return Fruit("Apple")
    }

}