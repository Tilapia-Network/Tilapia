package net.tilapiamc.multiworld

import net.tilapiamc.database.LongBlobColumnType
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.jetbrains.exposed.sql.vendors.SQLServerDialect
import org.jetbrains.exposed.sql.vendors.currentDialect
import java.io.InputStream
import java.sql.Blob
import java.sql.ResultSet

object TableWorldSaves: Table("WorldSaves") {

    val saveName = text("saveName")
    val autoSave = bool("autoSave")
    val generator = text("generator")
    val generatorOptions = text("generatorOptions")
    val worldType = varchar("worldType", 100)
    val data = registerColumn<ExposedBlob>("data", LongBlobColumnType())

}
