package com.ubertob.continuations

//import kotlinx.coroutines.*

//
//suspend fun doForAWhile(): String {
//        val p = suspendCancellableCoroutine{ cont: CancellableContinuation<String> ->
//            //do something blocking
//            "thread ${Thread.currentThread().name}  active ${cont.isActive}"
//        }
//        yield()
//        return "result $p"
//     }



fun fibonacci() = sequence {
    var terms = Pair(0, 1)

    val p: Number

    // this sequence is infiniteww
    while (true) {
        yield(terms.first)
        terms = Pair(terms.second, terms.first + terms.second)
    }
}.constrainOnce()


fun main() {
//
//    runBlocking {
//        println( doForAWhile() )
//    }

    val fibonacci: Sequence<Int> = fibonacci()

//    println(fibonacci.none())
//    println(fibonacci.take(10).toList())

//    val iterator = fibonacci.iterator()
    val fib = fibonacci.take(20).toList().asSequence()
    println(fib.iterator().hasNext())
    println(fib.firstOrNull())

    println(fib.take(10).toList())

    println("done")

//    val (h,t) = fibonacci.mapIndexed()

//    println(iterator.hasNext())
//
//    iterator.next()
//    val newFib = iterator.asSequence()
//    println(fibonacci.firstOrNull())

//    println(newFib.take(10).toList())
}