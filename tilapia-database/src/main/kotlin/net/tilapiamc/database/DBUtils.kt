package net.tilapiamc.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> dbQuery(db: Database, block: suspend Transaction.() -> T): T {
    return newSuspendedTransaction(Dispatchers.IO, db = db) {
        return@newSuspendedTransaction block()
    }
}
fun <T> blockingDbQuery(db: Database, block: suspend Transaction.() -> T): T {
    return runBlocking {
        dbQuery(db, block)
    }
}