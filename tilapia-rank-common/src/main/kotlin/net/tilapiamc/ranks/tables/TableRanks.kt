package net.tilapiamc.ranks.tables

import org.jetbrains.exposed.sql.Table

object TableRanks: Table("Ranks") {

    val name = varchar("name", 50)
    override val primaryKey: PrimaryKey = PrimaryKey(name)

    val previous = varchar("previous", 50)
        .references(name)

    val default = bool("default").default(false)
    val op = bool("op").default(true)


}