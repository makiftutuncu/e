package e

/**
 * A RuntimeException wrapping E to be used where errors are represented as E but an Exception is needed
 *
 * @param e An E
 *
 * @see [e.E]
 * @see [java.lang.RuntimeException]
 */
data class EException(val e: E): RuntimeException(e.toString()) {
    override fun toString(): String = e.toString()
}
