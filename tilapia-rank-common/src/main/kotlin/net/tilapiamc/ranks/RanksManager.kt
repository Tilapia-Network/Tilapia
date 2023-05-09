package net.tilapiamc.ranks

import kotlinx.coroutines.runBlocking
import net.dzikoysk.exposed.upsert.upsert
import net.tilapiamc.database.dbQuery
import net.tilapiamc.ranks.tables.TablePlayerRank
import net.tilapiamc.ranks.tables.TableRanks
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID

class RanksManager(val database: Database) {

    companion object {
        const val DATABASE_NAME = "ranks"
    }

    init {
        runBlocking {
            updateRanks()
        }
    }

    private val _ranks = ArrayList<Rank>()

    val ranks: List<Rank>
        get() = _ranks
    val defaultRank by lazy {
        ranks.firstOrNull { it.default }?:throw IllegalStateException("No default rank is defined!")
    }

    suspend fun updateRanks() {
        dbQuery(database) {
            val allocatedRanks = HashMap<String, Rank>()
            val results = TableRanks.selectAll()
            val resultsWithName = HashMap<String, ResultRow>()
            for (result in results) {
                val name = result[TableRanks.name]
                allocatedRanks[name] = Rank()
                resultsWithName[name] = result
            }
            for (value in allocatedRanks.values) {
                value.fromRow(allocatedRanks, resultsWithName[value.internalName]!!)
            }
            for (value in allocatedRanks.values) {
                value.loadPermissionInheritance()
            }
        }
    }


    fun rankByName(name: String): Rank? = ranks.firstOrNull { it.internalName == name }

    suspend fun setRank(playerUuid: UUID, rank: Rank) {
        if (rank !in ranks) {
            throw IllegalArgumentException("Rank is not registered")
        }
        dbQuery(database) {
            TablePlayerRank.upsert(TablePlayerRank.playerId, insertBody = {
                it[this.playerId] = playerUuid
                it[this.rank] = rank.internalName
            }, updateBody = {
                it[this.rank] = rank.internalName
            })
        }
    }
    suspend fun getRank(playerUuid: UUID): Rank {
        val rankName = dbQuery(database) {
            TablePlayerRank.select { TablePlayerRank.playerId.eq(playerUuid) }.firstOrNull()?.get(TablePlayerRank.rank)
        }?:return defaultRank
        return rankByName(rankName)?:defaultRank
    }



}