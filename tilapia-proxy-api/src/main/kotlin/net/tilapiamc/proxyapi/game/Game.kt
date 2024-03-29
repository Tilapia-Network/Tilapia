package net.tilapiamc.proxyapi.game

import com.google.gson.JsonObject
import net.tilapiamc.proxyapi.player.NetworkPlayer
import net.tilapiamc.proxyapi.servers.TilapiaServer
import java.util.*

abstract class Game(
    override val server: TilapiaServer,
    override val gameType: GameType,
    override val gameId: UUID,
    override val managed: Boolean,
    override val properties: JsonObject
): IGame {

    override val players = ArrayList<NetworkPlayer>()

    override fun hashCode(): Int {
        return gameId.hashCode()
    }
}