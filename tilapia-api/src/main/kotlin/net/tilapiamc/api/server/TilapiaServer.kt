package net.tilapiamc.api.server

import net.tilapia.api.game.GameType
import java.util.UUID

abstract class TilapiaServer(
    var proxyId: UUID,
    var nodeId: UUID,
    var serverId: UUID
) {


}