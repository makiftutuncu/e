package dev.akif.e.codec

class DecodingError(override val message: String?, override val cause: Throwable?) : Exception(message, cause) {
    constructor(message: String?) : this(message, null)
}
