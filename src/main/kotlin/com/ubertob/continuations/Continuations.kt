package com.ubertob.continuations

import kotlinx.coroutines.*


suspend fun doForAWhile(): String {
        val p = suspendCancellableCoroutine{ cont: CancellableContinuation<String> ->
            //do something blocking
            "thread ${Thread.currentThread().name}  active ${cont.isActive}"
        }
        yield()
        return "result $p"
     }



fun fibonacci() = sequence {
    var terms = Pair(0, 1)

    // this sequence is infiniteww
    while (true) {
        yield(terms.first)
        terms = Pair(terms.second, terms.first + terms.second)
    }
}


fun main() {

    runBlocking {
        println( doForAWhile() )
    }

    println(fibonacci().take(10).toList())
}