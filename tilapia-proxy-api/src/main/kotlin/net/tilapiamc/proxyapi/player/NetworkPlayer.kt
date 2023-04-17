package net.tilapiamc.proxyapi.player

import net.tilapiamc.proxyapi.TilapiaProxyAPI
import java.util.*

abstract class NetworkPlayer(
    val proxyAPI: TilapiaProxyAPI,
    val playerName: String,
    val language: Locale,
    val uuid: UUID,
) {
}