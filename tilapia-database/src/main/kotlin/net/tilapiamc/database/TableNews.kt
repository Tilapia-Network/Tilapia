package net.tilapiamc.database

import org.jetbrains.exposed.sql.Table

object TableNews: Table("news") {

    val channel = varchar("channel", 100)
    val content = text("content").default("")

}