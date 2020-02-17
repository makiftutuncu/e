package e.kotlin

sealed class Maybe<A>(open val e: E?, open val value: A?) {
    fun isSuccess(): Boolean = e == null

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

    fun getOrElse(default: () -> A): A =
        when (this) {
            is Failure<A> -> default()
            is Success<A> -> value
        }

    fun orElse(alternative: () -> Maybe<A>): Maybe<A> =
        when (this) {
            is Failure<A> -> alternative()
            is Success<A> -> this
        }

    fun <B> andThen(next: () -> Maybe<B>): Maybe<B> =
        when (this) {
            is Failure<A> -> failure(e)
            is Success<A> -> next()
        }

    fun <U> forEach(f: (A) -> U): Unit {
        when (this) {
            is Success<A> -> f(value)
        }
    }

    fun filter(predicate: (A) -> Boolean, ifPredicateFails: (A) -> E): Maybe<A> =
        when (this) {
            is Failure<A> -> failure(e)
            is Success<A> -> if (predicate.invoke(value)) value.toMaybe() else ifPredicateFails(value).toMaybe()
        }

    fun filter(predicate: (A) -> Boolean): Maybe<A> =
        when (this) {
            is Failure<A> -> failure(e)
            is Success<A> -> if (predicate.invoke(value)) value.toMaybe() else E("predicate-failed", "Value did not satisfy predicate!").data("value" to value).toMaybe()
        }

    fun handleErrorWith(f: (E) -> Maybe<A>): Maybe<A> =
        when (this) {
            is Failure<A> -> f(e)
            is Success<A> -> value.toMaybe()
        }

    fun handleError(f: (E) -> A): Maybe<A> =
        when (this) {
            is Failure<A> -> f(e).toMaybe()
            is Success<A> -> value.toMaybe()
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
        private data class Failure<A>(override val e: E): Maybe<A>(e, null) {
            override fun toString(): String = e.toString()
        }

        private data class Success<A>(override val value: A): Maybe<A>(null, value) {
            override fun toString(): String = value.toString()
        }

        fun <A> failure(e: E): Maybe<A> = Failure(e)

        fun <A> success(value: A): Maybe<A> = Success(value)

        fun unit(): Maybe<Unit> = Success(Unit)

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

        fun <A> catchingMaybe(action: () -> Maybe<A>, ifFailure: (Throwable) -> E): Maybe<A> =
            try {
                action()
            } catch (t: Throwable) {
                failure(ifFailure(t))
            }
    }
}

inline fun <A> A.toMaybe(): Maybe<A> = Maybe.success(this)

inline fun <A> A?.toMaybe(ifNull: E): Maybe<A> = Maybe.fromNullable(this, ifNull)
