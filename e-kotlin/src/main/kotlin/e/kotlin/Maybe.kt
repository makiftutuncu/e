package e.kotlin

sealed class Maybe<out A>(open val a: A?) {
    fun isFailure(): Boolean = this is Failure<A>
    fun isSuccess(): Boolean = this is Success<A>

    inline fun <B> map(f: (A) -> B): Maybe<B> =
        when (this) {
            is Failure<A> -> Failure(e)
            is Success<A> -> Success(f.invoke(a))
        }

    inline fun <B> flatMap(f: (A) -> Maybe<B>): Maybe<B> =
        when (this) {
            is Failure<A> -> Failure(e)
            is Success<A> -> f.invoke(a)
        }
}

data class Failure<out A>(val e: E): Maybe<A>(null)

data class Success<out A>(override val a: A): Maybe<A>(a)

fun <A> E.maybe(e: E): Maybe<A> = Failure(e)

fun <A> A.maybe(): Maybe<A> = Success(this)

fun <A> A?.orE(e: E): Maybe<A> =
    when (this) {
        null -> Failure(e)
        else -> Success(this)
    }
