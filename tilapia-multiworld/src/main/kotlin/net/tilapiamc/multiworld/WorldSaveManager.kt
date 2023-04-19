package net.tilapiamc.multiworld

import net.tilapiamc.database.blockingDbQuery
import org.bukkit.World
import org.bukkit.WorldType
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class WorldSaveManager(val database: Database, val trashBin: Database) {

    init {
        blockingDbQuery(database) {
            SchemaUtils.createMissingTablesAndColumns(TableWorldSaves)
        }
    }

    fun delete(saveName: String) {
        val blob = blockingDbQuery(database) {
            TableWorldSaves.select { TableWorldSaves.saveName.eq(saveName) }.firstOrNull()?.get(TableWorldSaves.data)
        }?:return
        val world = blockingDbQuery(database) {
            val row = TableWorldSaves.select { TableWorldSaves.saveName.eq(saveName) }.firstOrNull()?:return@blockingDbQuery null
            TilapiaWorld(saveName, WorldType.valueOf(row[TableWorldSaves.worldType]), row[TableWorldSaves.generator], row[TableWorldSaves.generatorOptions])
        }
        if (world != null) {
            blockingDbQuery(trashBin) {
                SchemaUtils.createMissingTablesAndColumns(TableWorldSaves)
            }
            blockingDbQuery(trashBin) {
                TableWorldSaves.deleteWhere { this.saveName.eq(saveName) }
                TableWorldSaves.insert {
                    it[this.saveName] = saveName
                    it[this.autoSave] = autoSave
                    it[this.worldType] = world.worldType.name
                    it[this.generator] = world.generator?:""
                    it[this.generatorOptions] = world.generatorParameters?:""
                    it[this.data] = ExposedBlob(blob.inputStream)
                }
            }
            blockingDbQuery(database) {
                TableWorldSaves.deleteWhere { TableWorldSaves.saveName.eq(saveName) }
            }
        }

    }

    fun has(saveName: String): Boolean {
        return blockingDbQuery(database) {
            TableWorldSaves.select(TableWorldSaves.saveName.eq(saveName)).any()
        }
    }

    fun load(saveName: String, worldName: String): World? {
        val blob = blockingDbQuery(database) {
            TableWorldSaves.select { TableWorldSaves.saveName.eq(saveName) }.firstOrNull()?.get(TableWorldSaves.data)
        }?:return null
        val world = blockingDbQuery(database) {
            val row = TableWorldSaves.select { TableWorldSaves.saveName.eq(saveName) }.first()
            TilapiaWorld(worldName, WorldType.valueOf(row[TableWorldSaves.worldType]), row[TableWorldSaves.generator], row[TableWorldSaves.generatorOptions])
        }
        WorldManager.registerWorld(world)
        decompressRecursively(File(worldName), ZipInputStream(blob.inputStream))
        return WorldManager.createWorld(world)
    }

    fun save(saveName: String, world: TilapiaWorld, autoSave: Boolean) {
        val tempSaveFile = File(System.getenv("java.io.tmpdir"), "$saveName.zip")
        val worldFile = File(world.name)
        compressRecursively(worldFile, ZipOutputStream(tempSaveFile.outputStream()))
        blockingDbQuery(database) {
            TableWorldSaves.deleteWhere { this.saveName.eq(saveName) }
            TableWorldSaves.insert {
                it[this.saveName] = saveName
                it[this.autoSave] = autoSave
                it[this.worldType] = world.worldType.name
                it[this.generator] = world.generator?:""
                it[this.generatorOptions] = world.generatorParameters?:""
                it[this.data] = ExposedBlob(tempSaveFile.inputStream())
            }
        }
        tempSaveFile.delete()
    }

    fun compressRecursively(file: File, zipOutputStream: ZipOutputStream) {
        for (childrenFile in file.walk(FileWalkDirection.BOTTOM_UP)) {
            if (childrenFile.name == "uid.dat") {
                continue
            }
            if (childrenFile.name == "session.lock") {
                continue
            }
            if (childrenFile.isDirectory) continue
            try {
                zipOutputStream.putNextEntry(ZipEntry(childrenFile.name))
                childrenFile.inputStream().copyTo(zipOutputStream)
                zipOutputStream.closeEntry()
            } catch (e: ZipException) {}
        }
        zipOutputStream.close()
    }
    fun decompressRecursively(file: File, zipInputStream: ZipInputStream) {
        var entry = zipInputStream.nextEntry
        while (entry != null) {
            if (!entry.isDirectory) {
                val targetFile = File(file, entry.name)
                targetFile.parentFile.mkdirs()
                targetFile.createNewFile()
                zipInputStream.copyTo(targetFile.outputStream())
            }
            entry = zipInputStream.nextEntry
        }
        zipInputStream.close()
    }

}

