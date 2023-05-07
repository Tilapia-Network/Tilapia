package net.tilapiamc.api.ranks

import net.tilapiamc.api.player.NetworkPlayer
import org.jetbrains.exposed.sql.Database

class RanksManager(val database: Database) {

    init {
        updateRanks()
    }

    private val ranks = ArrayList<Rank>()

    fun updateRanks() {

    }

    fun changeRank(player: NetworkPlayer, rank: Rank) {
        if (rank !in ranks) {
            throw IllegalArgumentException("The rank is not registered")
        }
        player.rank = rank
    }

}