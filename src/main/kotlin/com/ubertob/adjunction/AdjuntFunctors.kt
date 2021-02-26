package com.ubertob.adjunction


//if Kotlin had HKT:
//interface AdjuntFunctors<F: Functor<*>, G: Functor<*>> {
//
//    fun <A,B> leftAdjunct(f: (F<A>) -> B): (A) -> G<B>
//    fun <A,B> rightAdjunct(f: (A) -> G<B>): (F<A>) -> B
//
//
//    fun indexAdjunction(gb:GB, fu: Functor<Unit>): B = rightAdjunct {Unit -> gb}(fu)
//    fun tabulateAdjunction(fu: (Functor<Unit>) -> B): GB = leftAdjunct(fu)(Unit)
//
//}


/*
indexAdjunction means: if it’s possible to “extract” from u b to b using only an f () as extra information, then u might be right-adjoint to f.

tabulateAdjunction means: if it’s possible to “generate” a u b based on a function that “builds” a b from f (), then u might right-adjoint to f.

https://blog.jle.im/entry/foldl-adjunction.html

This pair is equivalent in power — you can implement rightAdjunct in terms of indexAdjunction and leftAdjunct in terms of tabulateAdjunction and vice versa. This comes from the fact that all Adjunctions in Haskell Functors arise from some idea of “indexability”.

 */


    //try with Tuple + Function...(a, b) -> c can be re-written as a -> (b -> c)


//TODO implement adjunctions with specific functors pair:

//Tuple, Function
//Json, JExtract


