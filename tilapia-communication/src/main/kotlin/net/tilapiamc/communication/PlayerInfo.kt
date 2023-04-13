package net.tilapiamc.communication

import java.util.Locale
import java.util.UUID

data class PlayerInfo(
    val playerName: String,
    val uniqueId: UUID,
    val locale: Locale
)