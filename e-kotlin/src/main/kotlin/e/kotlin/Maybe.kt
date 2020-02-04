package e.kotlin

sealed class Maybe<out A>(open val e: E?, open val value: A?) {
    fun isFailure(): Boolean = this is Failure<A>
    fun isSuccess(): Boolean = this is Success<A>

    inline fun <B> map(f: (A) -> B): Maybe<B> =
        when (this) {
            is Failure<A> -> Failure(e)
            is Success<A> -> Success(f(value))
        }

    inline fun <B> flatMap(f: (A) -> Maybe<B>): Maybe<B> =
        when (this) {
            is Failure<A> -> Failure(e)
            is Success<A> -> f(value)
        }

    inline fun <B> fold(ifFailure: (E) -> B, ifSuccess: (A) -> B): B =
        when (this) {
            is Failure<A> -> ifFailure(e)
            is Success<A> -> ifSuccess(value)
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Maybe<*>) return false

        if ((this is Failure<*> && other !is Failure<*>) || (this is Success<*> && other !is Success<*>)) return false

        return this.e == other.e && this.value == other.value
    }

    override fun hashCode(): Int {
        var result = e?.hashCode() ?: 0
        result = 31 * result + (value?.hashCode() ?: 0)
        return result
    }


}

data class Failure<out A>(override val e: E): Maybe<A>(e, null)

data class Success<out A>(override val value: A): Maybe<A>(null, value)

fun <A> E.maybe(): Maybe<A> = Failure(this)

fun <A> A.maybe(): Maybe<A> = Success(this)

fun <A> A?.orE(e: E): Maybe<A> =
    when (this) {
        null -> Failure(e)
        else -> Success(this)
    }
