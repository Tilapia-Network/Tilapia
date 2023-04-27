package net.tilapiamc.lobby

import net.tilapiamc.common.Config
import java.io.File

object NewsConfig: Config(File("plugins/tilapia-lobby/news-settings.json")) {
    val channel by configValue("production")
}