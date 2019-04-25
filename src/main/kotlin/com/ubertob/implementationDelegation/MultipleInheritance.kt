package com.ubertob.implementationDelegation

//printer and scanner

data class Image (val name: String)

interface Printer{
    fun switchOn()
    fun switchOff()
    fun print(image: Image)
}

interface Scanner{
    fun switchOn()
    fun switchOff()
    fun scan(name: String): Image
}

object laserPrinter: Printer {
    var working = false
    override fun switchOn() {
        working = true
    }

    override fun switchOff() {
        working = false
    }

    override fun print(image: Image) {
        println("printed ${image.name}")
    }

}


class scannerAndPrinter: Scanner, Printer by laserPrinter {
    override fun scan(name: String): Image = Image(name)
}