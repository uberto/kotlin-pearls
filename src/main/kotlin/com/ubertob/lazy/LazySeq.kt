package com.ubertob.lazy

typealias LazySeq<T> = LazyCons<T>?

val emptyLazySeq = null

fun <T> emptyLazySeq(): LazySeq<T> {
    return emptyLazySeq
}

data class LazyCons<out T>(val headThunk: Lazy<T>, val tailThunk: Lazy<LazySeq<T>>) {
    val head by headThunk
    val tail by tailThunk

    override fun toString(): String {
        val str = StringBuilder()

        tailrec fun LazyCons<T>.buildString() {
            str.append(head)
            val t = tail
            if (t != emptyLazySeq) {
                str.append(", ")
                t.buildString()
            }
        }

        str.append("[")
        buildString()
        str.append("]")

        return str.toString()
    }
}

fun <T> generateLazySeq(start: T?, next: (T) -> T?): LazySeq<T> =
    start?.let { LazyCons(lazyOf(it), lazy { generateLazySeq(next(it), next) }) }

fun <T> lazySeqOf(vararg elements: T) =
    elements.asList().asReversed().fold(emptyLazySeq<T>()) { acc, head ->
        LazyCons(lazyOf(head), lazyOf(acc))
    }

tailrec fun <T, U> LazySeq<T>.fold(start: U, f: (U, T) -> U): U = when (this) {
    emptyLazySeq -> start
    else -> tail.fold(f(start, head), f)
}

fun <T, U> LazySeq<T>.map(f: (T) -> U): LazySeq<U> = when (this) {
    emptyLazySeq -> this
    else -> LazyCons(lazy { f(head) }, lazy { tail?.map(f) })
}

fun <T> LazySeq<T>.append(appendage: LazySeq<T>): LazySeq<T> = when (this) {
    emptyLazySeq -> appendage
    else -> LazyCons(headThunk, lazy { tail.append(appendage) })
}

operator fun <T> LazySeq<T>.plus(that: LazySeq<T>) = this.append(that)

fun <T> LazySeq<T>.drop(n: Int): LazySeq<T> = when {
    n == 0 || this == emptyLazySeq -> this
    else -> tail.drop(n - 1)
}

fun <T> LazySeq<T>.take(n: Int): LazySeq<T> = when {
    n == 0 || this == emptyLazySeq -> emptyLazySeq
    else -> LazyCons(headThunk, lazy { tail.take(n - 1) })
}