package net.tilapiamc.fleetwars.config

import net.tilapiamc.common.Config
import java.io.File

object FleetWarsConfig: Config(File("plugins/tilapia-fleetwars/config.json")) {

    val fireballSpeed by configValue(0.3)

}