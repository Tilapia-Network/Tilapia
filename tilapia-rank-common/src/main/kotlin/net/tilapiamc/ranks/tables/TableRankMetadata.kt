package net.tilapiamc.ranks.tables

import org.jetbrains.exposed.sql.Table

object TableRankMetadata: Table("RankMetadata") {

    val rank = varchar("rank", 50)
        .references(TableRanks.name)
    val key = varchar("key", 50)
    val value = text("value")

}