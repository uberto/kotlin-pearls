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


-- | The class saying you can always convert between:
--
-- * `f a -> b` (the ways to go out of `f`)
-- * `a -> u b` (the ways to go into `u`)
class Adjunction f u where
    leftAdjunct
        :: (f a -> b)       -- ^ the ways of going "out of" `f`
        -> (a -> u b)       -- ^ the ways of going "into" `u`

    rightAdjunct
        :: (a -> u b)       -- ^ the ways of going "into" u
        -> (f a -> b)       -- ^ the ways of going "out of" f



indexAdjunction means: if it’s possible to “extract” from u b to b using only an f () as extra information, then u might be right-adjoint to f.

tabulateAdjunction means: if it’s possible to “generate” a u b based on a function that “builds” a b from f (), then u might right-adjoint to f.

https://blog.jle.im/entry/foldl-adjunction.html

This pair is equivalent in power — you can implement rightAdjunct in terms of indexAdjunction and leftAdjunct in terms of tabulateAdjunction and vice versa. This comes from the fact that all Adjunctions in Haskell Functors arise from some idea of “indexability”.







Lens...
interface Functor<T>

//S is the big object, A is the type we want to read/change
data class Lens<S, A>(val modF: ((A) -> Functor<A>, S) -> Functor<S>)

fn identity ->set, update, const ->get
 */


    //try with Tuple + Function...(a, b) -> c can be re-written as a -> (b -> c)


//TODO implement adjunctions with specific functors pair:

//Tuple, Function
//Json, JExtract


interface Functor<T> {
    fun <U> fmap(f: (T) -> U): Functor<U>
}

interface AdjuntFunctors<A, FA: Functor<A>, B, GB: Functor<B>> {

    fun < B> leftAdjunct(f: (FA) -> B): (A) -> GB
    fun < B> rightAdjunct(f: (A) -> GB): (FA) -> B
}

/*
You “give” a Fold r b and “get” an b (and so they have opposite polarities/positions). This sort of function would make
Fold r a right adjoint, since the naked type b (the final parameter of Fold r b) is the final result, not the input.


right adj is the parser: B is the domain type, FA is a parser of JsonNodes(or tokens)

left adj is the serializer? A is JsonNode(tokens?) and GB is the serialization, a Const A that is not B... ???
 */
