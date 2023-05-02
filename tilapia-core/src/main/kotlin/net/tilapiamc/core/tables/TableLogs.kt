package net.tilapiamc.core.tables

import net.tilapiamc.database.LongBlobColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.time
import org.jetbrains.exposed.sql.statements.api.ExposedBlob

object TableLogs: Table("logs") {

    val logTime = datetime("logTime")
    val content = registerColumn<ExposedBlob>("content", LongBlobColumnType())

}