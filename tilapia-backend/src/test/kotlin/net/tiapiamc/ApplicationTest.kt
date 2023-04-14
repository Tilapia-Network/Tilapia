package net.tiapiamc

import io.ktor.server.testing.*

class ApplicationTest {
    fun testRoot() = testApplication {
        application {
            module()
        }
    }
}
