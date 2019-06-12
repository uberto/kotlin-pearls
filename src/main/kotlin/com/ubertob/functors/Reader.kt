package com.ubertob.functors

class Reader<C, out A>(val run: (C) -> A) {

    inline fun <B> map(crossinline fa: (A) -> B): Reader<C, B> = Reader { c ->
        fa(run(c))
    }

    inline fun <B> flatMap(crossinline fa: (A) -> Reader<C, B>): Reader<C, B> = Reader { c ->
        fa(run(c)).run(c)
    }

    fun <B> zip(other: Reader<C, B>): Reader<C, Pair<A, B>> = this.flatMap { a ->
        other.map { b -> Pair(a, b) }
    }

    inline fun <D> local(crossinline fd: (D) -> C): Reader<D, A> = Reader { d ->
        run(fd(d))
    }

    companion object Factory {
        fun <C, A> pure(a: A): Reader<C, A> = Reader { _ -> a }

        fun <C> ask(): Reader<C, C> = Reader { it }
    }
}

fun <A, B> ((A) -> B).reader(): Reader<A, B> = Reader(this)

fun <C, A> Reader<C, Reader<C, A>>.flatten(): Reader<C, A> = flatMap { it }


fun main() {

    val nameReader = Reader.ask<List<String>>().map {
        it.find { it.startsWith("name=") }?.drop("name=".length)
    }

    val propReader = { ctx: List<String> ->
        Reader.ask<String>().map { propName ->
            ctx.find { it.startsWith("${propName}=") }?.drop("${propName}=".length)
        }
    }


    val props = listOf("name=fred", "surname=flintstone", "city=bedrock")
    val loadedPropReader = propReader(props)

    println(nameReader.run(props))



    println(loadedPropReader.run("city"))


}