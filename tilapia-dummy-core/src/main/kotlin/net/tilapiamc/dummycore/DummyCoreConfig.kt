package net.tilapiamc.dummycore

import net.tilapiamc.common.Config
import java.io.File

object DummyCoreConfig: Config(File("plugins/tilapia-dummy-core/config.json")) {

    val databaseUrl by configValue("jdbc:mysql://localhost:3306")
    val databaseUsername by configValue("root")
    val databasePassword by configValue("password")

}