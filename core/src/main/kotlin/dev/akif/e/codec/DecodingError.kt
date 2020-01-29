package dev.akif.e.codec

class DecodingError(override val message: String?, override val cause: Throwable? = null) : Exception(message, cause)
