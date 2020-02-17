package e.kotlin

import e.AbstractE

data class E(private val name: String = "",
             private val message: String = "",
             private val code: Int = EMPTY_CODE,
             private val cause: Throwable? = null,
             private val data: Map<String, String> = mapOf()) : AbstractE<Throwable?, Map<String, String>>(name, message, code, cause, data) {
    override fun name(n: String): E              = E(n,      message(), code(), cause(), data())
    override fun message(m: String): E           = E(name(), m,         code(), cause(), data())
    override fun code(c: Int): E                 = E(name(), message(), c     , cause(), data())
    override fun cause(c: Throwable?): E         = E(name(), message(), code(), c,   data())
    override fun data(d: Map<String, String>): E = E(name(), message(), code(), cause(), d)
    fun <A> data(key: String, value: A): E       = E(name(), message(), code(), cause(), data() + (key to value.toString()))
    fun <A> data(pair: Pair<String, A>): E       = E(name(), message(), code(), cause(), data() + (pair.first to pair.second.toString()))

    override fun hasCause(): Boolean = cause != null

    override fun hasData(): Boolean = data.isNotEmpty()

    override fun toException(): Exception = Exception(message(), cause())

    fun <A> toMaybe(): Maybe<A> = Maybe.failure(this)

    override fun toString(): String = JsonStringEncoder.encode(this)

    companion object {
        fun empty(): E = E()
    }
}
