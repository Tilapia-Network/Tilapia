package net.tilapiamc.clientintegration.debugmenu

interface DebugMenuSender {

    fun setDebugInfo(infoName: String, value: Any)
    fun removeDebugInfo(infoName: String)

    val adapters: MutableList<DebugToStringAdapter>

    fun setToStringAdapter(filter: (content: Any) -> Boolean, toString: (content: Any) -> String) {
        adapters.add(0, DebugToStringAdapter(filter, toString))
    }
    fun toString(content: Any): String {
        for (adapter in adapters) {
            if (adapter.filter(content)) {
                return adapter.toString(content)
            }
        }
        return content.toString()
    }

}

class DebugToStringAdapter(val filter: (content: Any) -> Boolean, val toString: (content: Any) -> String)