package com.ubertob.functors

import com.ubertob.outcome.Outcome
import com.ubertob.outcome.flatMap

data class T1<A>(val first: A) {
    fun <A1> map(fa: (A) -> A1): T1<A1> = T1(fa(first))
    fun <A1> apply(other: T1<(A) -> A1>): T1<A1> = T1(other.first(first))
    fun <Z> extract(f: (A) -> Z): Z = f(first)
}

data class T2<A, B>(val first: A, val second: B) {
    fun <A1, B1> map(fa: (A) -> A1, fb: (B) -> B1): T2<A1, B1> = T2(fa(first), fb(second))
    fun <A1, B1> apply(other: T2<(A) -> A1, (B) -> B1>): T2<A1, B1> = T2(other.first(first), other.second(second))
    fun <Z> extract(f: (A, B) -> Z): Z = f(first, second)
}

data class T3<A, B, C>(val first: A, val second: B, val third: C) {
    fun <A1, B1, C1> map(fa: (A) -> A1, fb: (B) -> B1, fc: (C) -> C1): T3<A1, B1, C1> =
        T3(fa(first), fb(second), fc(third))

    fun <A1, B1, C1> apply(other: T3<(A) -> A1, (B) -> B1, (C) -> C1>): T3<A1, B1, C1> =
        T3(other.first(first), other.second(second), other.third(third))

    fun <Z> extract(f: (A, B, C) -> Z): Z = f(first, second, third)
}


inline fun <T : Any, A, B, C> buildJ(
    t: T3<JsonPropBase<A>, JsonPropBase<B>, JsonPropBase<C>>,
    crossinline ctr: (A, B, C) -> T,
    crossinline dectr: T.() -> T3<A, B, C>
): JAny<T> =
    object : JAny<T> {
        override fun JsonNodeObject.deserialize(): Outcome<JsonError, T> =
            t.first.getter(this).flatMap { a ->
                t.second.getter(this).flatMap { b ->
                    t.third.getter(this).map { c ->
                       //todo tranform in T3<A,B,C>
                        //and then extract

                        ctr(a,b,c)
                    }
                }
            }

        override fun serialize(value: T): JsonNodeObject {
            val dt = dectr(value)
            //todo transform to a list
            return foldObjNode(t.first.setter(dt.first), t.second.setter(dt.second), t.third.setter(dt.third))
        }
    }


data class One(val s: String)
data class Two(val s: String, val i: Int)
data class Three(val s: String, val i: Int, val b: Boolean?)


val JThree: JAny<Three> = buildJ<Three, String, Int, Boolean?>(
    T3(
        JsonPropMandatory("first_field", JString),
        JsonPropMandatory("second_field", JInt),
        JsonPropOptional("third_field", JBoolean)
    ),
    ::Three
) { T3(s, i, b) }


fun main() {

    val t = Three("abc", 42, false)
    val j = JThree.serialize(t)

    println(j.toString())


}