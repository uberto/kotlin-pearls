package com.ubertob.adjunction

import com.ubertob.functors.AbstractJsonNode
import com.ubertob.functors.JsonNodeObject
import com.ubertob.functors.JsonNodeString

/*
    class Profunctor p where
    dimap :: (s -> a) -> (b -> t) -> p a b -> p s t

 */
interface Profunctor<A, B> {

    fun <S, T> dimap(f: (S) -> A, g: (B) -> T): Profunctor<S, T>
}


data class MyUser(val name: String)
typealias MyJson = AbstractJsonNode

//data class UserJson(val b: MyUser) : Profunctor<MyUser, MyJson> {
//
//    fun toJson(): MyJson = JsonNodeObject(mapOf("name" to JsonNodeString(b.name))) //example
//
//    override fun <S, T> dimap(f: (S) -> MyUser, g: (MyJson) -> T): Profunctor<S, T> {
//        Profunctor()
//    }
//
//}

//data class UserJson(val a: MyUser, val ) : Profunctor<MyJson, MyUser> {
//
////    fun toJson(): MyJson = JsonNodeObject(mapOf("name" to JsonNodeString(b.name))) //example
//
//    override fun <S, T> dimap(f: (S) -> MyJson, g: (MyUser) -> T): Profunctor<S, T> =
//        Profunctor(g(a), f)
// }
//

