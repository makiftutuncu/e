package e.kotlin

class EException(val e: E): Exception(e.toString(), e.cause())
