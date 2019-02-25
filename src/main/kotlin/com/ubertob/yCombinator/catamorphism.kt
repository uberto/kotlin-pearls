package com.ubertob.yCombinator


typealias F<A> = (A) -> A


data class Fix<A>(val invIso: (Fix<A>) -> F<A>)


fun<A> cataMorphism(functor:(F<A>) -> F<A>): F<A> = isoMorphism(Fix { r -> functor{ x:A -> isoMorphism(r)(x) } })


fun <A> isoMorphism(rec: Fix<A>):F<A> = rec.invIso(rec)


fun <A> yComp(functor:(F<A>) -> F<A>): F<A> = cataMorphism(functor)