package net.tilapiamc.ranks

import net.tilapiamc.common.language.LanguageKey
import net.tilapiamc.common.language.LanguageManager
import net.tilapiamc.ranks.metadata.RankMetaDataKey
import net.tilapiamc.ranks.tables.TableRankMetadata
import net.tilapiamc.ranks.tables.TableRankPermissions
import net.tilapiamc.ranks.tables.TableRanks
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select

// A rank will have
class Rank internal constructor(val internalName: String = "") {

    private val logger = LogManager.getLogger("Rank-$internalName")
    private var loaded: Boolean = false

    var previous: Rank? = null

    var default: Boolean = false
    val permissions = HashMap<String, Boolean>()
    val metaData = HashMap<String, String>()

    val languageKey: LanguageKey by lazy {
        val key = LanguageKey("RANK_${internalName}_NAME", internalName)
        LanguageManager.instance.registerLanguageKey(key)
        key
    }

    init {
    }


    fun fromRow(ranks: HashMap<String, Rank>, row: ResultRow) {
        if (loaded) return
        this.previous = ranks[row[TableRanks.previous]]
        this.default = row[TableRanks.default]
        if (default) {
            if (ranks.values.any { it.default }) {
                this.default = false
                logger.warn("Another default rank is found, keeping default rank to the previous one")
            }
        }

        for (resultRow in TableRankPermissions.select { TableRankPermissions.rank.eq(internalName) }) {
            permissions[resultRow[TableRankPermissions.permission]] = resultRow[TableRankPermissions.value]
        }
        for (resultRow in TableRankMetadata.select { TableRankMetadata.rank.eq(internalName) }) {
            metaData[resultRow[TableRankMetadata.key]] = resultRow[TableRankMetadata.value]
        }
        loaded = true
    }

    private var permissionInheritanceCalculated = false

    internal fun loadPermissionInheritance() {
        if  (permissionInheritanceCalculated) return
        if (previous == null) return
        if (previous?.loaded == false) {
            throw IllegalStateException("The parent rank is not loaded")
        }
        val parent = previous!!
        if (!parent.permissionInheritanceCalculated) {
            parent.loadPermissionInheritance()
        }
        for (permission in parent.permissions) {
            if (permission.key in permissions) continue
            permissions[permission.key] = permission.value
        }
        permissionInheritanceCalculated = true
    }

}