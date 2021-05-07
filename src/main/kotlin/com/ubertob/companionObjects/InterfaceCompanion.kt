package com.ubertob.companionObjects


interface ICompanion<T> {
    fun buildIt(): T
}

interface IBuilder<T, SELF : IBuilder<T, SELF>> {

    companion object : ICompanion<IBuilder<*, *>> {
        override fun buildIt(): IBuilder<*, *> {
            TODO("Not yet implemented")
        }

        //does not see SELF
//        fun doIt(): SELF{
//            println("Did")
//        }

        fun doIt() {
            println("Did")
        }
    }
}

data class MyAutoBuild(val raw: String) : IBuilder<String, MyAutoBuild> {

}


interface I1 : (String) -> Boolean {
    companion object {
        fun buildIt(): I1 = object : I1 {
            override fun invoke(p1: String): Boolean = p1 == "ciao"
        }
    }
}

//functional interface
fun interface I2 : (String) -> Boolean

fun interface I3 : (String) -> Boolean {
    companion object
}


fun main() {

    val a: I1.Companion = I1
    val b = a.buildIt()
    println(b("ciaociaoAccountTrackerId"))

    val c: I3 = I3 { true}
    val d = I3
//    val e = d{1 == 2} doesn't compile


//    val b: I2 =   I2  //doesn't compile
    val strEvenLen: I2 = I2 { (it.length % 2) == 0 }

    println(strEvenLen("ciaociao"))

    val tt = MyAutoBuild("ciccio")

//    MyAutoBuild.Companion.buildIt() why is not visible?
//    MyAutoBuild.doIt()

}