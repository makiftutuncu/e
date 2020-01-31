package dev.akif.e

import dev.akif.e.codec.StringEncoder
import dev.akif.e.codec.encodeWith

/**
 * E represents an error. It may contain additional information about the error.
 *
 * @constructor Builds an [E] with all its properties
 *
 * @property code    Numerical error code, indicating the type of the error
 * @property name    Name of the error, usually a machine-readable and identifier-like value, for example: "invalid-data"
 * @property message Message of the error, usually a human readable description, for example: "Value cannot be empty!"
 * @property cause   Cause of the error in the case the error is caused by some [Throwable]
 * @property data    Data related to the error that could be useful for understanding and fixing the error
 */
class E private constructor(val code: Int,
                            val name: String,
                            val message: String,
                            val cause: Throwable?,
                            val data: Map<String, String>) {
    fun hasName(): Boolean    = name.isNotBlank()
    fun hasMessage(): Boolean = message.isNotBlank()
    fun hasCause(): Boolean   = cause != null
    fun hasData(): Boolean    = data.isNotEmpty()

    fun withCode(newCode: Int): E                 = of(newCode, name,    message,    cause,    data)
    fun withName(newName: String): E              = of(code,    newName, message,    cause,    data)
    fun withMessage(newMessage: String): E        = of(code,    name,    newMessage, cause,    data)
    fun withCause(newCause: Throwable?): E        = of(code,    name,    message,    newCause, data)
    fun withData(newData: Map<String, String>): E = of(code,    name,    message,    cause,    newData)
    fun withData(k: String, v: String): E         = of(code,    name,    message,    cause,    data + (k to v))

    fun throwNow(): Nothing =
        if (cause == null) {
            throw Exception(message)
        } else {
            throw cause
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is E) return false

        return code == other.code && name == other.name && message == other.message && data == other.data;
    }

    override fun hashCode(): Int =
        code + name.hashCode() + message.hashCode() + (cause?.hashCode() ?: 0) + data.hashCode()

    override fun toString(): String = this.encodeWith(StringEncoder)

    companion object {
        @JvmStatic
        fun empty(): E = of(-1)

        @JvmStatic
        @JvmOverloads
        fun of(code: Int,
               name: String = "",
               message: String = "",
               cause: Throwable? = null,
               data: Map<String, String> = mapOf()): E = E(code, name, message, cause, data)
    }
}
