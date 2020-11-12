package com.ubertob.outcome

import java.lang.RuntimeException


sealed class Outcome<out E : OutcomeError, out T> {

    fun <U : Any> map(f: (T) -> U): Outcome<E, U> =
        when (this) {
            is Success -> Success(f(this.value))
            is Failure -> this
        }

    fun <U : OutcomeError> mapFailure(f: (E) -> U): Outcome<U, T> =
        when (this) {
            is Success -> this
            is Failure -> Failure(f(this.error))
        }

    fun <U> fold(onErr: (E) -> U, onSucc: (T) -> U): U =
        when (this) {
            is Success -> onSucc(this.value)
            is Failure -> onErr(this.error)
        }

    fun orThrow(block: (E) -> RuntimeException): T =
        when (this) {
            is Success -> value
            is Failure -> throw block(error)
        }

    abstract operator fun iterator(): Iterator<T>

    companion object {
        fun <T : Any> tryThis(block: () -> T): Outcome<ThrowableError, T> =
            try {
                Success(block())
            } catch (e: Throwable) {
                Failure(ThrowableError(e))
            }
    }
}

data class Success<T>(val value: T) : Outcome<Nothing, T>() {

    override operator fun iterator() = object : Iterator<T> {
        private var called = false

        override fun hasNext() = !called
        override fun next() = value.also { called = true }
    }

}

data class Failure<E : OutcomeError>(val error: E) : Outcome<E, Nothing>(){

    override operator fun iterator() = object : Iterator<Nothing> {
        override fun hasNext() = false
        override fun next() = throw ArrayIndexOutOfBoundsException()
    }

}

inline fun <T, U, E : OutcomeError> Outcome<E, T>.bind(f: (T) -> Outcome<E, U>): Outcome<E, U> = flatMap(f)

inline fun <T, U, E : OutcomeError> Outcome<E, T>.flatMap(f: (T) -> Outcome<E, U>): Outcome<E, U> =
    when (this) {
        is Success<T> -> f(value)
        is Failure<E> -> this
    }

inline fun <E : OutcomeError, T : Any> Outcome<E, T>.mapNullableError(f: (T) -> E?): Outcome<E, Unit> =
    when (this) {
        is Success<T> -> {
            val error = f(this.value)
            if (error == null) Success(Unit) else Failure(error)
        }
        is Failure<E> -> this
    }

inline fun <T : Any, E : OutcomeError> Outcome<E, T>.onFailure(block: (E) -> Nothing): T =
    when (this) {
        is Success<T> -> value
        is Failure<E> -> block(error)
    }

interface OutcomeError {
    val msg: String
}

data class ThrowableError(val t: Throwable) : OutcomeError {
    override val msg: String
        get() = t.message.orEmpty()
}

fun <T : OutcomeError> T.asFailure(): Outcome<T, Nothing> = Failure(this)
fun <T> T.asSuccess(): Outcome<Nothing, T> = Success(this)


fun <E: OutcomeError, T: Any> T?.failIfNull(error: E): Outcome<E, T> =
    this?.asSuccess() ?: error.asFailure()

fun <E: OutcomeError, T: Any> Outcome<E,T>?.failIfNull(error: E): Outcome<E, T> =
    this ?: error.asFailure()


fun <E: OutcomeError, T: Any> Iterable<Outcome<E, T>>.sequence(): Outcome<E, List<T>> =
    fold(emptyList<T>().asSuccess()){
            acc: Outcome<E, Iterable<T>>, e: Outcome<E, T> -> acc.flatMap { list -> e.map { list + it } }
    }

fun <ERR: OutcomeError, A, R: Any> liftA(
    f: (A) -> R,
    a: Outcome<ERR, A>
): Outcome<ERR, R> = a.map { av ->
    f(av)
}

fun <ERR: OutcomeError, A, B, R: Any> liftA2(
    f: (A, B) -> R,
    a: Outcome<ERR, A>,
    b: Outcome<ERR, B>
): Outcome<ERR, R> = a.flatMap { av -> b.map { bv ->
    f(av,bv)
} }

fun <ERR: OutcomeError, A, B, C, R: Any> liftA3(
    f: (A, B, C) -> R,
    a: Outcome<ERR, A>,
    b: Outcome<ERR, B>,
    c: Outcome<ERR, C>
): Outcome<ERR, R> = a.flatMap { av -> b.flatMap { bv -> c.map { cv  ->
    f(av,bv,cv)
} } }

fun <ERR: OutcomeError, A, B, C, D, R: Any> liftA4(
    f: (A, B, C, D) -> R,
    a: Outcome<ERR, A>,
    b: Outcome<ERR, B>,
    c: Outcome<ERR, C>,
    d: Outcome<ERR, D>
): Outcome<ERR, R> = a.flatMap { av -> b.flatMap { bv -> c.flatMap { cv -> d.map { dv ->
    f(av,bv,cv,dv)
} } } }

fun <ERR: OutcomeError, A, B, C, D, E, R: Any> liftA5(
    f: (A, B, C, D, E) -> R,
    a: Outcome<ERR, A>,
    b: Outcome<ERR, B>,
    c: Outcome<ERR, C>,
    d: Outcome<ERR, D>,
    e: Outcome<ERR, E>
): Outcome<ERR, R> = a.flatMap { av -> b.flatMap { bv -> c.flatMap { cv -> d.flatMap { dv -> e.map { ev ->
    f(av,bv,cv,dv,ev)
} } } } }