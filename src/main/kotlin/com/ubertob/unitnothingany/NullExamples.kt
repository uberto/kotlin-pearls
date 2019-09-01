package com.ubertob.unitnothingany

fun <A:Any, B:Any> A?.map(f: (A) -> B): B? = when(this) {
    null -> null
    else -> f(this)
}

fun <A:Any, B:Any> A?.flatMap(f: (A) -> B?): B? = when(this) {
    null -> null
    else -> f(this)
}

fun <A: Any> A?.iterator() = object: Iterator<A>{
    var done = false
    override fun hasNext(): Boolean = this@iterator != null && !done
    override fun next(): A = this@iterator.orThrow(IndexOutOfBoundsException())
}

private fun <A: Any> A?.orThrow(t: Throwable): A = when(this){
    null -> throw t
    else -> this
}

private fun <A: Any> A?.onNull(block: () -> Nothing): A = when(this){
    null -> block()
    else -> this
}