package com.ubertob.yCombinator


typealias F<A> = (A) -> A




////////

data class Fix<A>(val invIso: (Fix<A>) -> F<A>)


fun<A> cataMorphism(functor:(F<A>) -> F<A>): F<A> = isoMorphism(Fix { rec -> functor{ x -> isoMorphism(rec)(x) } })


fun <A> isoMorphism(recursive: Fix<A>):F<A> = recursive.invIso(recursive)


fun <A> yCombinator(functor:(F<A>) -> F<A>): F<A> = cataMorphism(functor)

//////////////


fun <A> fix (f:(F<A>) -> F<A>): F<A> = f( fix( f )) //not working because is not lazy
fun <A> lazyFix (f:(() -> F<A>) -> F<A>): F<A> = f( { lazyFix( f )}) // working because is lazy (but f is different)



fun fac(f: F<Int>): F<Int> = { x -> if (x <= 1) 1 else x * f(x - 1) }

fun fib(f: F<Int>): F<Int> = { x -> if (x <= 2) 1 else f(x - 1) + f(x - 2) }

fun reverse(f: F<String>): F<String> = { s -> if (s.isEmpty()) "" else s.last() + f(s.dropLast(1)) }



fun lazyFib(f: () -> F<Int>): F<Int> = { x -> if (x <= 2) 1 else f()(x - 1) + f()(x - 2) }


fun main() {
    print("Factorial(1..10)   : ")
    for (i in 1..10) print("${yCombinator(::fac)(i)}  ")
    print("\nFibonacci(1..10)   : ")
    for (i in 1..10) print("${yCombinator(::fib)(i)}  ")
    println()
    println("reverse ${yCombinator(::reverse)("uberto barbini")}")


    print("\nFibonacci(1..10) lazyFix  : ")
    for (i in 1..10){
        val r = lazyFix( ::lazyFib )(i)
        print("$r  ")
    }


}