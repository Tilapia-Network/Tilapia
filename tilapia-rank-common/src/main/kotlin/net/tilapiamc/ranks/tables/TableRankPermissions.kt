package net.tilapiamc.ranks.tables

import org.jetbrains.exposed.sql.Table

object TableRankPermissions: Table("RankPermissions") {

    val rank = varchar("rank", 50)
        .references(TableRanks.name)
    val permission = varchar("permission", 50)
    val value = bool("value")

}