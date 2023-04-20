package net.tilapiamc.communication.session

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import io.ktor.client.plugins.websocket.*
import io.ktor.utils.io.*
import io.ktor.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.runBlocking
import net.tilapiamc.common.SuspendEventTarget
import net.tilapiamc.common.json.get
import net.tilapiamc.common.json.set
import java.util.*

abstract class Session(val packetRegistry: HashMap<String, () -> SessionPacket>, val eventTargetFactory: (ignoreException: Boolean) -> SuspendEventTarget<out SessionEvent>, val webSocket: DefaultWebSocketSession) {

    val gson = GsonBuilder().create()
    @Deprecated("Possible race condition, please refer to internal #1")
    val onSessionStarted = eventTargetFactory(false) as SuspendEventTarget<SessionStartEvent>
    val onSessionClosed = eventTargetFactory(true) as SuspendEventTarget<SessionCloseEvent>
    val onPacket = eventTargetFactory(false) as SuspendEventTarget<SessionPacketEvent>

    var latestTransmissionId = 0L

    fun newTransmissionId(): Long = latestTransmissionId++



    suspend fun waitForPacket(filter: (packet: SessionPacket) -> Boolean, timeOut: Long = 40000, action: suspend () -> Unit = {}): SessionPacket? {
        var listener: suspend (SessionPacketEvent) -> Unit = {}
        var packet: SessionPacket? = null
        val lock = Object()
        listener = {
            if (filter(it.packet)) {
                packet = it.packet
                onPacket.remove(listener)
                synchronized(lock) {
                    lock.notifyAll()
                }
            }
        }
        onPacket.add(listener)
        action()
        synchronized(lock) {
            lock.wait(timeOut)
        }
        return packet
    }
    suspend inline fun <reified T: SessionPacket> waitForPacketWithType(crossinline filter: (packet: T) -> Boolean = { true }, timeOut: Long = 40000, noinline action: suspend () -> Unit = {}): T? {
        return waitForPacket({ it is T && filter(it) }, timeOut, action) as T?
    }


    suspend fun sendPacket(packet: SessionPacket) {
        try {
            onPacket(SessionPacketEvent(this, packet))
        } catch (e: Exception) {
            closeSession(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "Internal error while handling packet"))
        }
        val out = JsonObject()
        out["type"] = PacketRegistry.getPacketName(packet)
        out["data"] = packet.toJson(gson)
        webSocket.send(gson.toJson(out))
        webSocket.flush()
    }

    suspend fun startSession() {
        for (handler in onSessionStarted) {
            Thread {
                runBlocking {
                    try {
                        handler(SessionStartEvent(this@Session))
                    } catch (e: WebSocketClientError) {
                        e.printStackTrace()
                        closeSession(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, e.message!!))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        closeSession(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "Internal error while handling session start"))
                    }
                }
            }.start()
        }

        Thread.sleep(50) // Wait until everything is initialized

        // TODO: Workaround - Race condition
        // Please refer to internal #1

        try {
            while (true) {
                val frame = webSocket.incoming.receive()
                val packet = readPacket(frame)
                try {
                    onPacket(SessionPacketEvent(this@Session, packet))
                } catch (e: Exception) {
                    e.printStackTrace()
                    closeSession(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "Internal error while handling packet"))
                }
            }
        } catch (e: ClosedReceiveChannelException) {
            closeSession()
        } catch (e: WebSocketClientError) {
            e.printStackTrace()
            closeSession(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, e.message!!))
        } catch (e: Exception) {
            e.printStackTrace()
            closeSession(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "Internal error while reading frames"))
        }


    }

    var hasEmiitedSessionClose = false

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun closeSession(reason: CloseReason? = null) {
        if (!webSocket.incoming.isClosedForReceive) {
            if (reason == null) {
                webSocket.close()
            } else {
                webSocket.close(reason)
            }
            if (!hasEmiitedSessionClose) {
                hasEmiitedSessionClose = true
                onSessionClosed(SessionCloseEvent(this, true, reason))
            }
        } else {
            if (!hasEmiitedSessionClose) {
                hasEmiitedSessionClose = true
                onSessionClosed(SessionCloseEvent(this, false, webSocket.closeReason.await()))
            }
        }
    }


    private fun readPacket(frame: Frame): SessionPacket {
        println("Received: ${frame.data.decodeToString()}")
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


open class SessionEvent(open val session: Session)
class SessionPacketEvent(session: Session, val packet: SessionPacket): SessionEvent(session)
class SessionStartEvent(session: Session): SessionEvent(session)
class SessionCloseEvent(session: Session, val closedBySelf: Boolean, val closeReason: CloseReason?): SessionEvent(session)