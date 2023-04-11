package net.tilapiamc.lobby

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.gameextension.plugins.PluginNoBoatCrash
import net.tilapiamc.gameextension.plugins.PluginSpeedyBoat_1_8
import net.tilapiamc.gameextension.rules.impl.RuleNoDestruction
import net.tilapiamc.gameextension.rules.impl.RuleNoTimeChange
import net.tilapiamc.spigotcommon.game.lobby.LocalLobby
import org.bukkit.World

class TilapiaLobby(core: TilapiaCore, world: World, lobbyType: String): LocalLobby(core, world, lobbyType) {

    init {
        applyPlugin(PluginNoBoatCrash())
        applyPlugin(PluginSpeedyBoat_1_8(true, {6f}) {
            0.35*6
        })
        addRule(RuleNoTimeChange(this))
        addRule(
            RuleNoDestruction(this,
                protectEntities = true,
                protectUseEntities = true,
                protectPlayers = true,
                protectPlayersHunger = true,
                protectUsePlayers = true,
                protectBlockPlacement = true,
                protectItemUse = true,
                protectBlockUse = false,
                protectBlockBreak = true,
                protectEntityDestruction = true,
                protectItemPickUp = true,
                protectItemDrop = true,
                protectPlayerPhysical = true,
                protectPlayerInventoryChange = true,
                ignoreCreative = true
            )
        )
    }

    override fun onStart() {

    }

    override fun onEnd() {

    }


    override fun couldAddPlayer(networkPlayer: NetworkPlayer): Double {
        return 1.0
    }

    override fun preAddPlayer(networkPlayer: NetworkPlayer): ManagedGame.PlayerJoinResult {
        return ManagedGame.PlayerJoinResult(ManagedGame.PlayerJoinResultType.ACCEPTED)
    }

    override fun addPlayer(networkPlayer: LocalNetworkPlayer) {

    }

    override fun removePlayer(networkPlayer: LocalNetworkPlayer) {

    }


}