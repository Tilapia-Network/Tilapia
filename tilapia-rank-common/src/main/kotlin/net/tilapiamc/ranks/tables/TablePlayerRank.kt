package net.tilapiamc.ranks.tables

import net.tilapiamc.ranks.tables.TableRankMetadata.references
import org.jetbrains.exposed.sql.Table

object TablePlayerRank: Table("PlayerRank") {

    val playerId = uuid("playerId")
    override val primaryKey: PrimaryKey = PrimaryKey(playerId)
    val rank = varchar("rank", 50)
        .references(TableRanks.name)

}