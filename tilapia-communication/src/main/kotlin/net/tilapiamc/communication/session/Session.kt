package net.tilapiamc.communication.session

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import net.tilapiamc.common.SuspendEventTarget
import net.tilapiamc.common.json.get
import java.lang.RuntimeException
import java.util.*
import kotlin.collections.HashMap

abstract class Session(val packetRegistry: HashMap<String, () -> SessionPacket>, val eventTargetFactory: (ignoreException: Boolean) -> SuspendEventTarget<out SessionEvent>, val webSocket: WebSocketSession) {

    val gson = GsonBuilder().create()
    val onSessionStarted = eventTargetFactory(false) as SuspendEventTarget<SessionStartEvent>
    val onSessionClosed = eventTargetFactory(true) as SuspendEventTarget<SessionCloseEvent>
    val onPacket = eventTargetFactory(false) as SuspendEventTarget<SessionPacketEvent>


    init {

    }

    suspend fun waitForPacket(filter: (packet: SessionPacket) -> Boolean, timeOut: Long = 5000): SessionPacket? {
        var listener: suspend (SessionPacketEvent) -> Unit = {}
        var packet: SessionPacket? = null
        val lock = Object()
        listener = {
            if (filter(it.packet)) {
                packet = it.packet
                onPacket.remove(listener)
                lock.notifyAll()
            }
        }
        onPacket.add(listener)
        synchronized(lock) {
            lock.wait(timeOut)
        }
        return packet
    }
    suspend inline fun <reified T: SessionPacket> waitForPacketWithType(crossinline filter: (packet: T) -> Boolean = { true }, timeOut: Long = 5000): T? {
        return waitForPacket({ it is T && filter(it) }, timeOut) as T
    }


    suspend fun sendPacket(packet: SessionPacket) {
        try {
            onPacket(SessionPacketEvent(this, packet))
        } catch (e: Exception) {
            closeSession(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "Internal error while handling packet"))
        }
    }

    suspend fun startSession() {
        onSessionStarted(SessionStartEvent(this))
        try {
            for (frame in webSocket.incoming) {
                val packet = readPacket(frame)
                try {
                    onPacket(SessionPacketEvent(this, packet))
                } catch (e: Exception) {
                    closeSession(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "Internal error while handling packet"))
                }
            }
        } catch (e: ClosedReceiveChannelException) {
            closeSession()
        } catch (e: WebSocketClientError) {
            closeSession(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, e.message!!))
        } catch (e: Throwable) {
            closeSession(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "Internal error while reading frames"))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun closeSession(reason: CloseReason? = null) {
        if (webSocket.incoming.isClosedForReceive) {
            if (reason == null) {
                webSocket.close()
            } else {
                webSocket.close()
            }
        }
        onSessionClosed(SessionCloseEvent(this))
    }


    private fun readPacket(frame: Frame): SessionPacket {
        val json = gson.fromJson(frame.data.decodeToString(), JsonObject::class.java)
        val type: String = json[gson, "type"]?: clientError("Type is not found")
        val packetType = packetRegistry[type]?: clientError("Packet type is not registered")
        val packet = packetType()
        packet.fromJson(gson, json[gson, "data"]!!)
        return packet
    }

    fun clientError(message: String): Nothing {
        throw WebSocketClientError(message)
    }

    class WebSocketClientError(message: String): RuntimeException(message)
}


open class SessionEvent(val session: Session)
class SessionPacketEvent(session: Session, val packet: SessionPacket): SessionEvent(session)
class SessionStartEvent(session: Session): SessionEvent(session)
class SessionCloseEvent(session: Session): SessionEvent(session)