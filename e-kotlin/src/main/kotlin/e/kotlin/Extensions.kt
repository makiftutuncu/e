package e.kotlin

/**
 * Computes a new EOr using E in this, if it exists, with given flat mapping function
 *
 * @param A Type of value
 *
 * @param f E flat mapping function
 *
 * @return This EOr or a computed EOr if this one has E
 */
inline fun <reified A> EOr<A>.flatMapError(crossinline f: (E) -> EOr<A>): EOr<A> =
    fold({ e -> f(e) }, { this })

/**
 * Gets the value in this or falls back to given default value
 *
 * @param A Type of value
 *
 * @param default Default value to use in case this has E
 *
 * @return Value in this or given default value
 */
inline fun <reified A> EOr<A>.getOrElse(crossinline default: () -> A): A =
    fold({ default() }, { a -> a })

/**
 * Provides an alternative EOr if this one has E, ignoring the E
 *
 * @param A Type of value
 *
 * @param alternative Alternative EOr in case this one has E
 *
 * @return This EOr or alternative if this one has E
 */
inline fun <reified A> EOr<A>.orElse(crossinline alternative: () -> EOr<A>): EOr<A> =
    fold({ alternative() }, { this })

/**
 * Converts this value to a successful EOr
 *
 * @return An EOr containing this value
 *
 * @see e.kotlin.EOr
 */
fun <A> A.toEOr(): EOr<A> =
    EOr.from(this)

/**
 * Constructs an EOr from this nullable value
 *
 * @param A Type of value
 *
 * @param ifNull An E to use in case this value is null
 *
 * @return An EOr containing either this value or given E
 */
fun <A> A?.toEOr(ifNull: () -> E): EOr<A> =
    EOr.fromNullable(this, ifNull)

/**
 * Constructs an EOr from this computation that can throw
 *
 * @param A Type of value
 *
 * @param ifFailure An E conversion function
 *
 * @return An EOr containing either computed value or an E computed by given function
 */
fun <A> (() -> A).catching(ifFailure: (Throwable) -> E = { t -> E.fromThrowable(t) }): EOr<A> =
    EOr.catching(this, ifFailure)

/**
 * Constructs an E from this [[java.lang.Throwable]]
 *
 * @return A new E containing message of this Throwable
 */
fun Throwable.toE(f: (Throwable) -> E = { t -> E.fromThrowable(t) }): E =
    f(this)
