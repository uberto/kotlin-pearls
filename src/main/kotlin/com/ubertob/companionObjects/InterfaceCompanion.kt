package com.ubertob.companionObjects

import com.ubertob.companionObjects.IBuilder.Companion.doIt


interface ICompanion<T> {
    fun buildIt(): T
}

interface IBuilder<T, SELF: IBuilder<T, SELF>>  {

    companion object: ICompanion<IBuilder<*,*>>{
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

data class MyAutoBuild(val raw: String): IBuilder<String, MyAutoBuild>{

}


interface I1 : (String) -> Boolean {
    companion object
}


fun interface I2 : (String) -> Boolean {
//    companion object
}

fun main() {

//    val a: I1.Companion = I1
//    val b: I2 =   I2  //doesn't compile
    val c: I2 =   I2 { false }

    val tt = MyAutoBuild("ciccio")

//    MyAutoBuild.Companion.buildIt() why is not visible?
//    MyAutoBuild.doIt()

}