package net.tilapiamc.communication

import java.util.*

data class PlayerInfo(
    val playerName: String,
    val uniqueId: UUID,
    val locale: Locale,
    val currentGame: UUID?
)