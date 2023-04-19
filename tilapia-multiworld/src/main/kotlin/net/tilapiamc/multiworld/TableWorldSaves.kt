package net.tilapiamc.multiworld

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
class LongBlobColumnType : ColumnType() {
    override fun sqlType(): String = "LONGBLOB"

    override fun valueFromDB(value: Any): ExposedBlob = when (value) {
        is ExposedBlob -> value
        is Blob -> ExposedBlob(value.binaryStream)
        is InputStream -> ExposedBlob(value)
        is ByteArray -> ExposedBlob(value)
        else -> error("Unexpected value of type Blob: $value of ${value::class.qualifiedName}")
    }

    override fun notNullValueToDB(value: Any): Any {
        return if (value is Blob) {
            value.binaryStream
        } else {
            value
        }
    }

    override fun nonNullValueToString(value: Any): String = "?"

    override fun readObject(rs: ResultSet, index: Int) = when {
        currentDialect is SQLServerDialect -> rs.getBytes(index)?.let(::ExposedBlob)
        else -> rs.getBinaryStream(index)?.let(::ExposedBlob)
    }

    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
        when (val toSetValue = (value as? ExposedBlob)?.inputStream ?: value) {
            is InputStream -> stmt.setInputStream(index, toSetValue)
            null, is NULL -> stmt.setNull(index, this)
            else -> super.setParameter(stmt, index, toSetValue)
        }
    }
}
object NULL : Op<Any>() {
    override fun toQueryBuilder(queryBuilder: QueryBuilder): Unit = queryBuilder {
        append("NULL")
    }
}
