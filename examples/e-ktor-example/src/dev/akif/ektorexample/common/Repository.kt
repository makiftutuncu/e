package dev.akif.ektorexample.common

import dev.akif.ektorexample.database.DB
import e.kotlin.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.transactions.transaction

abstract class Repository<M>(open val db: DB) {
    abstract fun convertTo(row: ResultRow): M

    fun <T> run(block: () -> T): EOr<T> =
        EOr.catching({ transaction { block() } }) { t ->
            Errors.database.cause(t.toE())
        }
}
