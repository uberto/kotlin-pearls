package com.ubertob.implementationDelegation

//printer and scanner

data class Image (val name: String)

interface Printer{
    fun turnOn()
    fun isOn(): Boolean
    fun printCopy(image: Image)
}

interface Scanner{
    fun turnOn()
    fun isOn(): Boolean
    fun scan(name: String): Image
}

object laserPrinter: Printer {
    var working = false

    override fun isOn(): Boolean = working

    override fun turnOn() {
        working = true
    }

    override fun printCopy(image: Image) {
        println("printed ${image.name}")
    }

}

object fastScanner: Scanner {
    var working = false

    override fun isOn(): Boolean = working

    override fun turnOn() {
        working = true
    }

    override fun scan(name: String): Image = Image(name)

}

class ScannerAndPrinter(scanner: Scanner, printer: Printer): Scanner by scanner, Printer by printer {

    override fun isOn(): Boolean = (this as Scanner).isOn()

    override fun turnOn() = (this as Scanner).turnOn()

    fun scanAndPrint(imageName: String) = printCopy(scan(imageName))

}

