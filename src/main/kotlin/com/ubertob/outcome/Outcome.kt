package com.ubertob.outcome


sealed class Outcome<out E : OutcomeError, out T : Any> {

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

data class Success<T : Any>(val value: T) : Outcome<Nothing, T>() {

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


inline fun <T : Any, U : Any, E : OutcomeError> Outcome<E, T>.flatMap(f: (T) -> Outcome<E, U>): Outcome<E, U> =
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
fun <T : Any> T.asSuccess(): Outcome<Nothing, T> = Success(this)


fun <E: OutcomeError, T: Any> T?.failIfNull(error: E): Outcome<E, T> =
    if (this == null) error.asFailure() else this.asSuccess()

fun <E: OutcomeError, T: Any> Outcome<E,T>?.failIfNull(error: E): Outcome<E, T> =
    if (this == null) error.asFailure() else this