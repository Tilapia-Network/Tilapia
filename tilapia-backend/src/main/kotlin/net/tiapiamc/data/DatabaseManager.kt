package net.tiapiamc.data

import kotlinx.coroutines.Dispatchers
import net.tiapiamc.config.Config
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object DatabaseManager {

    init {
        Database.connect(Config.DATABASE_URL, user = Config.DATABASE_USER, password = Config.DATABASE_PASSWORD)
    }


    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}