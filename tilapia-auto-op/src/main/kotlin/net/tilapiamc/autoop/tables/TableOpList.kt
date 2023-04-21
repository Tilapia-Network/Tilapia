package net.tilapiamc.autoop.tables

import org.jetbrains.exposed.sql.Table

object TableOpList: Table("OpList") {

    val playerUuid = uuid("playerUuid")
    val playerNameCache = varchar("playerNameCache", 16)

}