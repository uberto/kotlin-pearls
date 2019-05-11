package com.ubertob.outcome


sealed class  Outcome<out E: Error, out T: Any> {


    fun <U: Any> map(f: (T) -> U): Outcome<E, U> =
        when (this){
            is Success -> Success(f(this.value))
            is Failure -> this
        }

    fun <U: Error> mapFailure(f: (E) -> U): Outcome<U, T> =
        when (this){
            is Success -> this
            is Failure -> Failure(f(this.error))
        }

    companion object {
        fun <T: Any> tryThis(block: () -> T): Outcome<ThrowableError, T> =
            try {
                Success(block())
            } catch (e: Throwable){
                Failure(ThrowableError(e))
            }
    }
}

data class Success<T: Any>(val value: T): Outcome<Nothing, T>()
data class Failure<E: Error>(val error: E): Outcome<E, Nothing>()


inline fun <T: Any, U: Any, E: Error> Outcome<E, T>.flatMap(f: (T) -> Outcome<E, U>): Outcome<E, U> =
    when (this) {
        is Success<T> -> f(value)
        is Failure<E> -> this
    }

inline fun <E: Error, T: Any>Outcome<E, T>.mapNullableError(f: (T) -> E?): Outcome<E, Unit> =
    when (this){
        is Success<T> -> {
            val error = f(this.value)
            if (error == null ) Success(Unit) else Failure(error)
        }
        is Failure<E> -> this
    }

inline fun <T: Any, E: Error> Outcome<E, T>.onFailure(block: (E) -> Nothing): T =
    when (this) {
        is Success<T> -> value
        is Failure<E> -> block(error)
    }

interface Error{
    val msg: String
}

data class ThrowableError(val t: Throwable): Error {
    override val msg: String
        get() = t.message.orEmpty()
}

fun <T: Error> T.toFailure(): Outcome<T, Nothing> = Failure(this)