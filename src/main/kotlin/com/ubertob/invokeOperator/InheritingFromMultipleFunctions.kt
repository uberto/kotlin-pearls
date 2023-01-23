package com.ubertob.invokeOperator


typealias Foo = (Int) -> String
typealias Bar = (Long, Int) -> String

//they must have a different number of arguments!
class FooBarClass: Foo, Bar {
    override fun invoke(p1: Int): String {
        TODO("Not yet implemented")
    }

    override fun invoke(p1: Long, p2: Int): String {
        TODO("Not yet implemented")
    }

}