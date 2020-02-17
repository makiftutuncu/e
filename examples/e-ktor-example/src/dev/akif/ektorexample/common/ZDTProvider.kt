package dev.akif.ektorexample.common

import java.time.ZonedDateTime

interface ZDTProvider {
    fun now(): ZonedDateTime
}
