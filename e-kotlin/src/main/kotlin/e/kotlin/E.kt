package e.kotlin

/**
 * A generic and immutable error, containing helpful information
 *
 * @constructor Creates an E with all its properties
 *
 * @property code    A numeric code identifying this error
 * @property name    A name identifying this error, usually enum-like
 * @property message A message about this error, usually human-readable
 * @property causes  Underlying cause(s) of this error, if any
 * @property data    Arbitrary data related to this error as a key-value map
 * @property time    Time when this error occurred as milliseconds, see [System.currentTimeMillis]
 */
data class E(val code: Int?                = null,
             val name: String?             = null,
             val message: String?          = null,
             val causes: List<E>           = emptyList(),
             val data: Map<String, String> = emptyMap(),
             val time: Long?               = null) {
    /**
     * Constructs an E containing given code
     *
     * @param c A code
     *
     * @return A new E containing given code
     */
    fun code(c: Int): E = copy(code = c)

    /**
     * Constructs an E containing given name
     *
     * @param n A name
     *
     * @return A new E containing given name
     */
    fun name(n: String): E = copy(name = n)

    /**
     * Constructs an E containing given message
     *
     * @param m Message to set
     *
     * @return A new E containing given message
     */
    fun message(m: String): E = copy(message = m)

    /**
     * Constructs an E adding given causes
     *
     * @param c Causes as a List
     *
     * @return A new E containing causes of this E and given causes
     */
    fun causes(c: List<E>): E = copy(causes = causes + c)

    /**
     * Constructs an E adding given causes
     *
     * @param c Causes as variable arguments
     *
     * @return A new E containing causes of this E and given causes
     */
    fun causes(vararg c: E): E = copy(causes = causes + c.toList())

    /**
     * Constructs an E adding given cause
     *
     * @param e A cause
     *
     * @return A new E containing causes of this E and given causes
     */
    fun cause(e: E): E = copy(causes = causes + e)

    /**
     * Constructs an E adding given data
     *
     * @param d Data as a key-value map
     *
     * @return A new E containing data of this E and given data
     */
    fun data(d: Map<String, String>): E = copy(data = data + d)

    /**
     * Constructs an E adding given data entry
     *
     * @param V Type of value of data entry
     *
     * @param k Key of data entry
     * @param v Value of data entry
     *
     * @return A new E containing data of this E and given data entry
     */
    fun <V> data(k: String, v: V): E = copy(data = data + (k to v.toString()))

    /**
     * Constructs an E adding given data entry
     *
     * @param V Type of value of data entry
     *
     * @param p Data entry
     *
     * @return A new E containing data of this E and given data entry
     */
    fun <V> data(p: Pair<String, V>): E = copy(data = data + (p.first to p.second.toString()))

    /**
     * Constructs an E containing given time
     *
     * @param t A time
     *
     * @return A new E containing given time
     */
    fun time(t: Long): E = copy(time = t)

    /**
     * Constructs an E containing time set to now
     *
     * @return A new E containing time set to now
     *
     * @see System.currentTimeMillis
     */
    fun now(): E = copy(time = System.currentTimeMillis())

    /**
     * Constructs an E adding given cause if condition holds
     *
     * @param condition Some condition on which to add the cause
     * @param e         A cause
     *
     * @return A new E containing causes of this E and given cause or this E as is if condition doesn't hold
     */
    fun causeIf(condition: Boolean, e: () -> E): E = if (condition) copy(causes = causes + e()) else this

    /**
     * Whether or not a code is set
     */
    val hasCode: Boolean = code != null

    /**
     * Whether or not a name is set
     */
    val hasName: Boolean = name != null

    /**
     * Whether or not a message is set
     */
    val hasMessage: Boolean = message != null

    /**
     * Whether or not a cause is set
     */
    val hasCause: Boolean = causes.isNotEmpty()

    /**
     * Whether or not a data is set
     */
    val hasData: Boolean = data.isNotEmpty()

    /**
     * Whether or not a time is set
     */
    val hasTime: Boolean = time != null

    /**
     * Converts this E to a failed EOr<A>
     *
     * @param A The A type in resulting EOr
     *
     * @return An EOr<A> containing this E
     *
     * @see e.kotlin.EOr
     */
    fun <A> toEOr(): EOr<A> = EOr.from(this)

    /**
     * Converts this E into an exception
     *
     * @return An [e.kotlin.EException] containing this E
     */
    fun toException(): EException = EException(this)

    override fun toString(): String {
        fun quote(s: String): String = s.replace("\"", "\\\"")

        return listOf(
            code?.let { """"code":$it""" },
            name?.let { """"name":"${quote(it)}"""" },
            message?.let { """"message":"${quote(it)}"""" },
            causes.takeIf { hasCause }?.let { """"causes":${it.joinToString(",", "[", "]")}""" },
            data.takeIf { hasData }?.let { """"data":${data.map { (k, v) -> """"${quote(k)}":"${quote(v)}"""" }.joinToString(",", "{", "}")}""" },
            time?.let { """"time":$it""" }
        ).flatMap {
            if (it == null) emptyList() else listOf(it)
        }.joinToString(
            ",", "{", "}"
        )
    }

    companion object {
        /**
         * An empty E
         */
        val empty: E = E()

        /**
         * Constructs an E containing given code
         *
         * @param c A code
         *
         * @return A new E containing given code
         */
        fun code(c: Int): E = E(code = c)

        /**
         * Constructs an E containing given name
         *
         * @param n A name
         *
         * @return A new E containing given name
         */
        fun name(n: String): E = E(name = n)

        /**
         * Constructs an E containing given message
         *
         * @param m Message to set
         *
         * @return A new E containing given message
         */
        fun message(m: String): E = E(message = m)

        /**
         * Constructs an E containing given causes
         *
         * @param c Causes as a List
         *
         * @return A new E containing given causes
         */
        fun causes(c: List<E>): E = E(causes = c)

        /**
         * Constructs an E containing given causes
         *
         * @param c Causes as variable arguments
         *
         * @return A new E containing given causes
         */
        fun causes(vararg c: E): E = E(causes = c.toList())

        /**
         * Constructs an E containing given cause
         *
         * @param e A cause
         *
         * @return A new E containing given cause
         */
        fun cause(e: E): E = E(causes = listOf(e))

        /**
         * Constructs an E containing given data
         *
         * @param d Data as a key-value map
         *
         * @return A new E containing given data
         */
        fun data(d: Map<String, String>): E = E(data = d)

        /**
         * Constructs an E containing given data entry
         *
         * @param V Type of value of data entry
         *
         * @param k Key of data entry
         * @param v Value of data entry
         *
         * @return A new E containing given data entry
         */
        fun <V> data(k: String, v: V): E = E(data = mapOf(k to v.toString()))

        /**
         * Constructs an E containing given data entry
         *
         * @param V Type of value of data entry
         *
         * @param p Data entry
         *
         * @return A new E containing given data entry
         */
        fun <V> data(p: Pair<String, V>): E = E(data = mapOf(p.first to p.second.toString()))

        /**
         * Constructs an E containing given time
         *
         * @param t A time
         *
         * @return A new E containing given time
         */
        fun time(t: Long): E = E(time = t)

        /**
         * Constructs an E containing time set to now
         *
         * @return A new E containing time set to now
         *
         * @see System.currentTimeMillis
         */
        fun now(): E = E(time = System.currentTimeMillis())

        /**
         * Constructs an E containing given cause if condition holds
         *
         * @param condition Some condition on which to set the cause
         * @param e         A cause
         *
         * @return A new E containing given cause or empty E if condition doesn't hold
         */
        fun causeIf(condition: Boolean, e: () -> E): E = if (condition) E(causes = listOf(e())) else empty

        /**
         * Constructs an E from given [kotlin.Throwable]
         *
         * @param throwable A Throwable
         *
         * @return A new E containing message of given Throwable or wrapped E in EException if Throwable is one
         */
        fun fromThrowable(throwable: Throwable): E =
            when (throwable) {
                is EException -> throwable.e
                else          -> E(message = throwable.message)
            }
    }
}
