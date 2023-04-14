package net.tiapiamc.session

import net.tilapiamc.communication.session.Session
import java.util.*

object SessionManager {

    val serverSessions = HashMap<UUID, Session>()
    val servers = HashMap<UUID, Session>()
    val proxySessions = HashMap<UUID, Session>()
    val proxies = HashMap<UUID, Session>()

}

