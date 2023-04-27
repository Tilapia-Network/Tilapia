pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
    }

    plugins {
        kotlin("jvm").version(extra["kotlin.version"] as String)
        kotlin("multiplatform").version(extra["kotlin.version"] as String)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
    }
}


rootProject.name = "tilapia"
include("tilapia-common")
include("tilapia-lobby")
include("tilapia-api")
include("tilapia-core")
include("fan87-plugin-dev-kit")
include("tilapia-spigot-common")
include("tilapia-game-extension")
include("tilapia-command")
include("tilapia-fleetwars")
include("tilapia-language")
include("tilapia-multiworld")
include("tilapia-util-commands")
include("tilapia-sandbox")
include("tilapia-game-central")
include("tilapia-communication")
include("tilapia-proxy-api")
include("tilapia-proxy-core")
include("tilapia-proxy-util-commands")
include("tilapia-proxy-news")
include("tilapia-database")
include("spigot-docker")
include("proxy-docker")
include("game-central-docker")
include("tilapia-auto-op")
include("tilapia-client-integration")
include("panel")
include("fastasyncworldedit")
include("tilapia-dummy-core")
