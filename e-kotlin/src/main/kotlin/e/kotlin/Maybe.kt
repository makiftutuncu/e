package e.kotlin

sealed class Maybe<out A>(open val e: E?, open val value: A?) {
    fun isSuccess(): Boolean = this is Success<A> && e == null && value != null

    fun <B> map(f: (A) -> B): Maybe<B> =
        when (this) {
            is Failure<A> -> failure(e)
            is Success<A> -> success(f(value))
        }

    fun <B> flatMap(f: (A) -> Maybe<B>): Maybe<B> =
        when (this) {
            is Failure<A> -> failure(e)
            is Success<A> -> f(value)
        }

    fun <B> fold(ifFailure: (E) -> B, ifSuccess: (A) -> B): B =
        when (this) {
            is Failure<A> -> ifFailure(e)
            is Success<A> -> ifSuccess(value)
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Maybe<*>) return false

        return this.e == other.e && this.value == other.value
    }

    override fun hashCode(): Int {
        var result = e?.hashCode() ?: 0
        result = 31 * result + (value?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String = fold({ it.toString() }, { it.toString() })

    companion object {
        private data class Failure<out A>(override val e: E): Maybe<A>(e, null) {
            override fun toString(): String = e.toString()
        }

        private data class Success<out A>(override val value: A): Maybe<A>(null, value) {
            override fun toString(): String = value.toString()
        }

        fun <A> failure(e: E): Maybe<A> = Failure(e)

        fun <A> success(value: A): Maybe<A> = Success(value)

        fun <A> fromNullable(value: A?, ifNull: E): Maybe<A> =
            when (value) {
                null -> failure(ifNull)
                else -> success(value)
            }

        fun <A> catching(action: () -> A, ifFailure: (Throwable) -> E): Maybe<A> =
            try {
                success(action())
            } catch (t: Throwable) {
                failure(ifFailure(t))
            }
    }
}

fun <A> A.toMaybe(): Maybe<A> = Maybe.success(this)

fun <A> A?.toMaybe(ifNull: E): Maybe<A> = Maybe.fromNullable(this, ifNull)
