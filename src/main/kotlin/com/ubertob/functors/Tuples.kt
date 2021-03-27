package com.ubertob.functors

import com.ubertob.outcome.bind

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

fun <A, B, R> ((A, B) -> R).curry1(): (A) -> (B) -> R = { a: A -> { b: B -> this(a, b) } }

fun <A, B, C, R> ((A, B, C) -> R).curry1(): (A, B) -> (C) -> R = { a: A, b: B -> { c: C -> this(a, b, c) } }

fun <A, B, C, D, R> ((A, B, C, D) -> R).curry1(): (A, B, C) -> (D) -> R =
    { a: A, b: B, c: C -> { d: D -> this(a, b, c, d) } }


typealias P0 = Unit
typealias P1<A> = Pair<Unit, A>
typealias P2<A, B> = Pair<P1<A>, B>
typealias P3<A, B, C> = Pair<P2<A, B>, C>
typealias P4<A, B, C, D> = Pair<P3<A, B, C>, D>

fun <A, A1> P1<A>.map(fa: (A) -> A1): P1<A1> = Pair(Unit, fa(second))

fun <A, A1> P1<A>.apply(other: P1<(A) -> A1>): P1<A1> = Pair(Unit, other.second(second))

fun <A> P1<JsonOutcome<A>>.outcome(): JsonOutcome<P1<A>> =
    second.map { b -> Pair(Unit, b) }

fun <A, A1> A.applyToAll(other: P1<(A) -> A1>): P1<A1> = Pair(Unit, other.second(this))

fun <A, A1> P1<A>.fromJson(fa: (A) -> A1): A1 = fa(second)


fun <A, B, A1, B1> P2<A, B>.map(fa: (A) -> A1, fb: (B) -> B1): P2<A1, B1> =
    Pair(first.map(fa), fb(second))

@JvmName("applyAB")
fun <A, B, A1, B1> P2<A, B>.apply(other: P2<(A) -> A1, (B) -> B1>): P2<A1, B1> =
    Pair(first.apply(other.first), other.second(second))

fun <A, B, Z> P2<A, B>.fromJson(f: (A, B) -> Z): Z =
    first.fromJson(f.curry1())(second)

@JvmName("applyToAllAB")
fun <A, A1, B1> A.applyToAll(other: P2<(A) -> A1, (A) -> B1>): P2<A1, B1> = Pair(applyToAll(other.first), other.second(this))

@JvmName("outcomeAB")
fun <A, B> P2<JsonOutcome<A>, JsonOutcome<B>>.outcome(): JsonOutcome<P2<A, B>> =
    first.outcome().bind { a -> second.map { b -> Pair(a, b) } }


fun <A, B, C, A1, B1, C1> P3<A, B, C>.map(fa: (A) -> A1, fb: (B) -> B1, fc: (C) -> C1): P3<A1, B1, C1> =
    Pair(first.map(fa, fb), fc(second))

//fun <A, B, C, A1, B1, C1> P3<A, B, C>.apply3(other: P3<(A) -> A1, (B) -> B1, (C) -> C1>): P3<A1, B1, C1> =
//    Pair(other.first(first), second.apply(other.second))

fun <A, B, C, Z> P3<A, B, C>.fromJson(f: (A, B, C) -> Z): Z = first.fromJson(f.curry1())(second)

@JvmName("applyToAllABC")
fun <A, A1, B1, C1> A.applyToAll(other: P3<(A) -> A1, (A) -> B1, (A) -> C1>): P3<A1, B1, C1> = Pair(applyToAll(other.first), other.second(this))


//fun <A, A1, B1, C1> A.applySingle3(other: P3<(A) -> A1, (A) -> B1, (A) -> C1>): P3<A1, B1, C1> =
//    Pair(other.first(this), this.applySingle(other.second))

@JvmName("outcomeABC")
fun <A, B, C> P3<JsonOutcome<A>, JsonOutcome<B>, JsonOutcome<C>>.outcome(): JsonOutcome<P3<A, B, C>> =
    first.outcome().bind { a -> second.map { b -> Pair(a, b) } }


fun <A, B, C, D, A1, B1, C1, D1> P4<A, B, C, D>.map(
    fa: (A) -> A1,
    fb: (B) -> B1,
    fc: (C) -> C1,
    fd: (D) -> D1
): P4<A1, B1, C1, D1> =
    Pair(first.map(fa, fb, fc), fd(second))


infix fun <A, B> B.and(a: A) = Pair(a, this)

//fun <T : Any, A, B, C> buildJ(
//    t: T3<JsonProperty<A>, JsonProperty<B>, JsonProperty<C>>,
//    ctr: (A, B, C) -> T,
//    dectr: T.() -> T3<A, B, C>
//): JProtocol<T> =
//    object : JProtocol<T> {
//
//        val getters = t.map({ it::getter }, { it::getter }, { it::getter })
//        val setters = t.map({ it::setter }, { it::setter }, { it::setter })
//
//        override fun JsonNodeObject.deserialize(): Outcome<JsonError, T> =
//
//            t.first.getter(this).flatMap { a ->
//                t.second.getter(this).flatMap { b ->
//                    t.third.getter(this).map { c ->
//                        //todo tranform in T3<A,B,C>
//                        //and then extract
//
//                        ctr(a, b, c)
//                    }
//                }
//            }
//
//
//        override fun serialize(value: T): JsonNodeObject {
//            val dt = dectr(value)
//            //todo transform to a list
//            return foldObjNode(t.first.setter(dt.first), t.second.setter(dt.second), t.third.setter(dt.third))
//        }
//    }
//
//
//fun <T : Any, A, B, C> buildJP(
//    t: P3<JsonProperty<A>, JsonProperty<B>, JsonProperty<C>>,
//    ctr: (A, B, C) -> T,
//    dectr: T.() -> P3<A, B, C>
//): JProtocol<T> =
//    object : JProtocol<T> {
//
//        val getters = t.map({ it::getter }, { it::getter }, { it::getter })
//        val setters = t.map({ it::setter }, { it::setter }, { it::setter })
//
//        override fun JsonNodeObject.deserialize(): Outcome<JsonError, T> =
//            this.applyToAll(getters)
//                .outcome()
//                .map { it.extract(ctr) }
//
//
//        override fun serialize(value: T): JsonNodeObject {
//            val dt = dectr(value)
//            //todo transform to a list
//            return foldObjNode(
//                t.first.setter(dt.first),
//                t.second.first.setter(dt.second.first),
//                t.second.second.setter(dt.second.second)
//            )
//        }
//    }
//
//
//fun <T : Any, A, B, C, D> buildJP4(
//    t: P4<JsonProperty<A>, JsonProperty<B>, JsonProperty<C>, JsonProperty<D>>,
//    ctr: (A, B, C, D) -> T,
//    dectr: T.() -> P4<A, B, C, D>
//): JProtocol<T> =
//    object : JProtocol<T> {
//
//        val getters = t.map({ it::getter }, { it::getter }, { it::getter }, { it::getter })
//        val setters = t.map({ it::setter }, { it::setter }, { it::setter }, { it::setter })
//
//        override fun JsonNodeObject.deserialize(): Outcome<JsonError, T> =
//            applySingle3(getters)
//                .outcome3()
//                .map { it.extract(ctr) }
//
//
//        override fun serialize(value: T): JsonNodeObject {
//            val dt = dectr(value)
//            //todo transform to a list
//            return foldObjNode(
//                t.first.setter(dt.first),
//                t.second.first.setter(dt.second.first),
//                t.second.second.setter(dt.second.second)
//            )
//        }
//    }


data class One(val s: String)
data class Two(val s: String, val i: Int)
data class Three(val s: String, val i: Int, val b: Boolean?)
data class Four(val s: String, val i: Int, val b: Boolean?, val l: Long)

//
//val JThree: JProtocol<Three> = buildJ(
//    T3(
//        JsonPropMandatory("first_field", JString),
//        JsonPropMandatory("second_field", JInt),
//        JsonPropOptional("third_field", JBoolean)
//    ),
//    ::Three
//) { T3(s, i, b) }
//
//val JThreeP: JProtocol<Three> = buildJP(
//    JsonPropMandatory("first_field", JString) and
//            JsonPropMandatory("second_field", JInt) and
//            JsonPropOptional("third_field", JBoolean),
//    ::Three
//) { s and i and b }
//
//
//val JFourP: JProtocol<Four> = buildJP4(
//    JsonPropMandatory("first_field", JString) and
//            JsonPropMandatory("second_field", JInt) and
//            JsonPropOptional("third_field", JBoolean) and
//            JsonPropMandatory("forth_field", JLong),
//    ::Four
//) { s and i and b and l }

//fun main() {
//
//    val t = Three("abc", 42, false)
//    val j = JThree.serialize(t)
//
//    println(j.toString())
//
//
//}