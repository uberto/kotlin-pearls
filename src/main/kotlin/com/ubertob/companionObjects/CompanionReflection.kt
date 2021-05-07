package com.ubertob.companionObjects

import kotlin.reflect.full.companionObjectInstance

interface IWrapped

interface IComp<T : IWrapped> {
    fun validate(x: T): Boolean
}

data class Something(val field: String) : IWrapped {
    companion object : IComp<Something> {
        override fun validate(x: Something): Boolean = x.field.length > 2
    }
}

data class SomethingElse(val field: Int) : IWrapped {
    companion object : IComp<SomethingElse> {
        override fun validate(x: SomethingElse): Boolean = x.field > 2
    }
}

data class SomethingWrong(val field: String) : IWrapped {
}

//reified bug?
inline fun <reified T : IWrapped, reified TC : IComp<T>, reified LL: List<T>> validate(x: T): Boolean {

    println("wrapped class ${T::class} ")
    println("comp class ${TC::class} ")
    println("is comp? ${TC::class.isCompanion} ") //require kotlin.reflect
    val comp = T::class.companionObjectInstance as TC
    val comp2 = T::class.companionObjectInstance as IComp<T>
    val comp3 = TC::class.objectInstance  //null
    println("obj instance $comp")
    println("obj instance $comp2")
    println("obj instance $comp3")
    return comp.validate(x)
}

fun main() {

    val r = listOf(1,2,3).drop(4)


    val valid = Something("ciao")
    val invalid = Something("ci")
    println(Something.validate(valid))
    println(Something.validate(invalid))
    val validNUm = SomethingElse(10)

    println(validate(validNUm))
    println(validate(valid))
    println(validate(SomethingWrong("no companion"))) //runtime error! null cannot be cast to non-null type




}