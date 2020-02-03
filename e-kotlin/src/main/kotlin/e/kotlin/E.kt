package e.kotlin

import e.AbstractE

data class E(private val code: Int = EMPTY_CODE,
             private val name: String = "",
             private val message: String = "",
             private val cause: Throwable? = null,
             private val data: Map<String, String> = mapOf()) : AbstractE<Throwable?, Map<String, String>>(code, name, message, cause, data) {
    override fun code(c: Int): E                 = E(c,   name(), message(), cause(), data())
    override fun name(n: String): E              = E(code(), n,   message(), cause(), data())
    override fun message(m: String): E           = E(code(), name(), m,   cause(), data())
    override fun cause(c: Throwable?): E         = E(code(), name(), message(), c,   data())
    override fun data(d: Map<String, String>): E = E(code(), name(), message(), cause(), d)
    fun data(key: String, value: String): E      = E(code(), name(), message(), cause(), data() + (key to value))
    fun data(pair: Pair<String, String>): E      = E(code(), name(), message(), cause(), data() + pair)

    override fun hasCause(): Boolean = cause != null

    override fun hasData(): Boolean = data.isNotEmpty()

    override fun toException(): Exception = Exception(message(), cause())

    override fun toString(): String = JsonStringEncoder.encode(this)

    companion object {
        fun empty(): E = E()
    }
}
