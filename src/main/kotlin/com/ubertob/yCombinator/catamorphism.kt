package com.ubertob.yCombinator


fun <A> fix(f: (EndoMorph<A>) -> EndoMorph<A>): EndoMorph<A> =
    f(fix(f))  //Fixed point combinator: not working because is not lazy

fun <A> lazyFix(f: (() -> EndoMorph<A>) -> EndoMorph<A>): EndoMorph<A> =
    f({ lazyFix(f) }) // working because is lazy (but f is not a simple function)



////////
typealias EndoMorph<A> = (A) -> A //Endomorphism: a function that return same type as the input

data class LazyFix<A>(val callIt: (LazyFix<A>) -> EndoMorph<A>) //a data class to make a FixPoint lazy


fun <A> yCombinator(recursiveFun: (EndoMorph<A>) -> EndoMorph<A>): EndoMorph<A> =
    runLazily(LazyFix { rec -> recursiveFun { x -> runLazily(rec)(x) } })


fun <A> runLazily(lazyFix: LazyFix<A>): EndoMorph<A> = lazyFix.callIt(lazyFix)


//////////////

fun <A> fixIt(recursiveFun: (EndoMorph<A>) -> EndoMorph<A>): EndoMorph<A> {
    val lazyFix: LazyFix<A> = LazyFix { lazyFun: LazyFix<A> -> recursiveFun { x: A -> (lazyFun.callIt(lazyFun))(x) } }
    return lazyFix.callIt(lazyFix)
}


////


fun fac(f: EndoMorph<Long>): EndoMorph<Long> = { x -> if (x <= 1) 1 else x * f(x - 1) }

fun fib(f: EndoMorph<Int>): EndoMorph<Int> = { x -> if (x <= 2) 1 else f(x - 1) + f(x - 2) }

fun reverse(f: EndoMorph<String>): EndoMorph<String> = { s -> if (s.isEmpty()) "" else s.last() + f(s.dropLast(1)) }


fun lazyFib(f: () -> EndoMorph<Int>): EndoMorph<Int> = { x -> if (x <= 2) 1 else f()(x - 1) + f()(x - 2) }


fun main() {

    println("Lazy Fib " + lazyFix(::lazyFib)(10))

    print("Factorial(1..10)   : ")
    for (i in 1..10L) print("${yCombinator(::fac)(i)}  ")
    print("\nFibonacci(1..10)   : ")
    for (i in 1..10) print("${yCombinator(::fib)(i)}  ")
    println()
    println("reverse ${yCombinator(::reverse)("uberto barbini")}")


    print("\nFibonacci(1..10) lazyFix  : ")
    for (i in 1..10) {
        val r = lazyFix(::lazyFib)(i)
        print("$r  ")
    }


}