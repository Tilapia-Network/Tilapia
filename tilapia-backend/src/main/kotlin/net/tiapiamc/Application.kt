package net.tiapiamc

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.tiapiamc.config.Config

fun main() {
    embeddedServer(Netty, port = Config.PORT, host = Config.HOST, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
}
