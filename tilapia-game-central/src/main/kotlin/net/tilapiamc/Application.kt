package net.tilapiamc

import com.google.gson.GsonBuilder
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.runBlocking
import net.tilapiamc.common.EventTarget
import net.tilapiamc.common.SuspendEventTarget
import net.tilapiamc.config.Config
import net.tilapiamc.data.DatabaseManager
import net.tilapiamc.endpoints.private.GameService.applyGameService
import net.tilapiamc.endpoints.private.PlayerService.applyPlayerService
import net.tilapiamc.endpoints.private.ProxyService.applyProxyService
import net.tilapiamc.endpoints.private.ServerService.applyServerService
import net.tilapiamc.managers.ServerManager
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.time.Duration

fun main() {
    embeddedServer(Netty, port = Config.PORT, host = Config.HOST, module = { module(ServerManager()) })
        .start(wait = true)
}

fun Application.module(
    serverManager: ServerManager,
    database: Database = Database.connect(
        Config.DATABASE_URL,
        user = Config.DATABASE_USER,
        password = Config.DATABASE_PASSWORD
    )
) {


    DatabaseManager.database = database
    runBlocking {
        DatabaseManager.deleteOld()
    }
    install(AutoHeadResponse)
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
        this.format { "[${it.request.local.remoteAddress}] ${it.response.status()}:   ${it.request.httpMethod} ${it.request.path()}" }
    }
    install (WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    authentication {
        bearer("private-api") {
            realm = "private-api"
            authenticate {
                if (it.token == Config.API_KEY) {
                    UserIdPrincipal("private-api")
                } else {
                    null
                }
            }
        }
    }
    install(ContentNegotiation) {
        gson {

        }
    }

    val gson = GsonBuilder().create()

    applyProxyService(serverManager, gson)
    applyPlayerService(serverManager, gson)
    applyServerService(serverManager, gson)
    applyGameService(serverManager, gson)
}


private val eventLogger = LoggerFactory.getLogger("Events")

fun <T> getEventTarget(ignoreException: Boolean = false): EventTarget<T> {
    return if (ignoreException)
        EventTarget({ eventLogger.error("Error while handling event", it); true })
    else
        EventTarget({ false })
}

fun <T> getSuspendEventTarget(ignoreException: Boolean = false): SuspendEventTarget<T> {
    return if (ignoreException)
        SuspendEventTarget({ eventLogger.error("Error while handling event", it); true })
    else
        SuspendEventTarget({ false })
}