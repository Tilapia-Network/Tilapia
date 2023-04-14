package net.tiapiamc.data

import io.ktor.server.sessions.*
import kotlinx.coroutines.Dispatchers
import net.tilapiamc.communication.DatabaseLogin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

object DatabaseManager {

    init {
    }

    lateinit var database: Database
    val databaseSessions = HashMap<UUID, DatabaseLogin>()

    suspend fun createSession(remoteIp: String, schemas: List<String>): DatabaseLogin {
        val sessionId = UUID.randomUUID()
        val username = "TEMP-" + generateSessionId()
        val password = generateSessionId()
        dbQuery {
            SchemaUtils.createDatabase(*schemas.toTypedArray())

            val current = TransactionManager.current()

            val identifierManager = database.identifierManager
            current.connection.prepareStatement("CREATE USER ${identifierManager.quoteIfNecessary(username)}@`${remoteIp}` IDENTIFIED BY '$password'", false).executeUpdate()
            for (schema in schemas) {
                current.connection.prepareStatement("GRANT ALL PRIVILEGES ON ${
                    identifierManager.quoteIfNecessary(schema)
                }.* TO ${
                    identifierManager.quoteIfNecessary(username)
                }@`${remoteIp}` WITH GRANT OPTION;", false).executeUpdate()
            }
            databaseSessions[sessionId] = DatabaseLogin(sessionId, remoteIp, username, password)
        }
        return DatabaseLogin(sessionId, remoteIp, username, password)
    }
    suspend fun closeSession(sessionId: UUID) {
        dbQuery {
            val login = databaseSessions[sessionId]!!
            databaseSessions.remove(sessionId)
            val identifierManager = database.identifierManager

            val current = TransactionManager.current()
            val prepareStatement = current.connection.prepareStatement("DROP USER ${
                identifierManager.quoteIfNecessary(login.username)
            }@'${login.remoteIp}'", false)
            prepareStatement.executeUpdate()
        }
    }


    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }
}