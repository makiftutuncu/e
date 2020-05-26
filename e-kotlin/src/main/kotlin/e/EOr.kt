package e

/**
 * A container that can either be a Failure containing an E or Success containing a value
 *
 * @constructor Base constructor providing both E and value
 *
 * @param A Type of the value this EOr can contain
 *
 * @param error E in this EOr
 * @param value Value in this EOr
 *
 * @see e.E
 */
sealed class EOr<out A>(open val error: E?, open val value: A?) {
    /**
     * Whether or not this contains an E
     */
    fun isFailure(): Boolean = this is Failure

    /**
     * Whether or not this contains a value
     */
    fun isSuccess(): Boolean = this is Success<A>

    /**
     * Converts value in this, if it exists, using given mapping function to make a new EOr
     *
     * @param f Mapping function
     *
     * @tparam B Type of the new value
     *
     * @return A new EOr containing either the new value or E in this one
     */
    fun <B> map(f: (A) -> B): EOr<B> =
        when (this) {
            is Failure -> this.error.toEOr()
            is Success -> Success(f(this.value))
        }

    /**
     * Computes a new EOr using value in this, if it exists, with given flat mapping function
     *
     * @param f Flat mapping function
     *
     * @tparam B Type of the new value
     *
     * @return Computed EOr or a new EOr containing E in this one
     */
    fun <B> flatMap(f: (A) -> EOr<B>): EOr<B> =
        when (this) {
            is Failure -> this.error.toEOr()
            is Success -> f(this.value)
        }

    /**
     * Converts E in this, if it exists, using given mapping function to make a new EOr
     *
     * @param f E mapping function
     *
     * @return This EOr or a new EOr containing computed E if this one has E
     */
    fun mapError(f: (E) -> E): EOr<A> =
        when (this) {
            is Failure -> f(this.error).toEOr()
            is Success -> this
        }

    /**
     * Folds this into a single value, handling both E and value conversions with given functions
     *
     * @param B Type of the desired result
     *
     * @param ifFailure Conversion function for E
     * @param ifSuccess Conversion function for value
     *
     * @return Converted result
     */
    fun <B> fold(ifFailure: (E) -> B, ifSuccess: (A) -> B): B =
        when (this) {
            is Failure -> ifFailure(this.error)
            is Success -> ifSuccess(this.value)
        }

    /**
     * Provides a next EOr if this one has a value, ignoring the value
     *
     * @param next Next EOr in case this one has a value
     *
     * @tparam B Type of value in next EOr
     *
     * @return Next EOr or a new EOr containing E in this one
     */
    fun <B> andThen(next: () -> EOr<B>): EOr<B> =
        when (this) {
            is Failure -> this.error.toEOr()
            is Success -> next()
        }

    /**
     * Performs a side-effect using value in this, if it exists
     *
     * @param f Side-effecting function
     *
     * @tparam U Type of result of the side-effect
     */
    fun <U> forEach(f: (A) -> U) {
        when (this) {
            is Failure -> {}
            is Success -> f(this.value)
        }
    }

    /**
     * Filters this EOr by value in it, if it exists, using given function
     *
     * @param condition     Filtering function
     * @param filteredError E conversion function
     *
     * @return This EOr of a new EOr containing an E computed by given conversion function
     */
    fun filter(condition: (A) -> Boolean,
               filteredError: (A) -> E = { a -> EOr.filteredError.data("value", a) }): EOr<A> =
        when (this) {
            is Failure -> this
            is Success -> if (condition(this.value)) this else filteredError(this.value).toEOr()
        }

    companion object {
        data class Failure<A>(override val error: E): EOr<A>(error, null) {
            override fun toString(): String = error.toString()
        }

        data class Success<A>(override val value: A): EOr<A>(null, value) {
            override fun toString(): String = value.toString()
        }

        /**
         * A default E to be used when condition does not hold while filtering an EOr
         *
         * @see e.EOr.filter
         */
        val filteredError: E = E(name = "filtered", message = "Condition does not hold!")

        /**
         * A successful EOr of type Unit
         */
        val unit: EOr<Unit> = Success(Unit)

        /**
         * Constructs a failed EOr containing given E
         *
         * @param A Type of value of resulting EOr
         *
         * @param e An E
         *
         * @return A new failed EOr containing given E
         */
        fun <A> from(e: E): EOr<A> = Failure(e)

        /**
         * Constructs a successful EOr containing given value
         *
         * @param A Type of value of resulting EOr
         *
         * @param a A value
         *
         * @return A new failed EOr containing given value
         */
        fun <A> from(a: A): EOr<A> = Success(a)

        /**
         * Constructs an EOr from a nullable value
         *
         * @param A Type of value
         *
         * @param a      A nullable value
         * @param ifNull An error to use in case value is null
         *
         * @return An EOr containing either value or given E
         */
        fun <A> fromNullable(a: A?, ifNull: () -> E): EOr<A> = if (a == null) Failure(ifNull()) else Success(a)

        /**
         * Constructs an EOr from a computation that can throw
         *
         * @param A Type of value
         *
         * @param f         A computation that can throw
         * @param ifFailure An E conversion function
         *
         * @return An EOr containing either computed value or an E computed by given function
         */
        fun <A> catching(f: () -> A, ifFailure: (Throwable) -> E): EOr<A> =
            try {
                Success(f())
            } catch (t: Throwable) {
                Failure(ifFailure(t))
            }
    }
}
