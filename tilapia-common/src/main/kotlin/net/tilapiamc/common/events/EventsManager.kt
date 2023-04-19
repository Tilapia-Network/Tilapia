package net.tilapiamc.common.events


typealias AbstractEvent = Any

abstract class AbstractEventsManager {

    init {

    }


    val listenersByName = LinkedHashMap<String, ArrayList<EventListener>>()
    val listeners = ArrayList<EventListener>()

    fun registerListener(listener: EventListener) {
        synchronized(listeners) {
            listenersByName[listener.name] = listenersByName[listener.name]?.also { it.add(listener) }?: arrayListOf(listener)
            listeners.add(listener)
            sortListeners()
        }
    }

    fun unregisterListener(listener: EventListener) {
        synchronized(listeners) {
            listenersByName[listener.name]?.remove(listener)
            listeners.remove(listener)
        }

    }


    private fun sortListeners() {
        val sortedListeners = mutableListOf<EventListener>()
        val incomingEdges = mutableMapOf<String, MutableSet<String>>()
        val outgoingEdges = mutableMapOf<String, MutableSet<String>>()

        for (listener in listeners) {
            incomingEdges[listener.name] = mutableSetOf()
            outgoingEdges[listener.name] = mutableSetOf()
        }

        for (listener in listeners) {

            for (dependency in listener.mustRunAfter) {

                if (incomingEdges.containsKey(listener.name) && outgoingEdges.containsKey(dependency)) {
                    incomingEdges[listener.name]?.add(dependency)
                    outgoingEdges[dependency]?.add(listener.name)
                }
            }
            for (dependency in listener.mustRunBefore) {

                if (incomingEdges.containsKey(dependency) && outgoingEdges.containsKey(listener.name)) {
                    incomingEdges[dependency]?.add(listener.name)
                    outgoingEdges[listener.name]?.add(dependency)
                }
            }


        }
//        println("===== INCOMING EDGE =====")
//        for (incomingEdge in incomingEdges) {
//            println(incomingEdge.key + " - " + incomingEdge.value)
//        }

//        println("===== OUTGOING EDGE =====")
//        for (incomingEdge in outgoingEdges) {
//            println(incomingEdge.key + " - " + incomingEdge.value)
//        }


        val noIncomingEdges = mutableListOf<String>()
        for ((name, edges) in incomingEdges) {
            if (edges.isEmpty()) {
                noIncomingEdges.add(name)
            }
        }
//        println("===== SORTING =====")

        while (noIncomingEdges.isNotEmpty()) {
            val name = noIncomingEdges.removeAt(0)
            val listener = listenersByName[name] ?: continue
            sortedListeners.addAll(listener)

            for (dependentName in outgoingEdges[name]?.toList() ?: emptyList()) {
                outgoingEdges[name]?.remove(dependentName)
                incomingEdges[dependentName]?.remove(name)

                if (incomingEdges[dependentName]?.isEmpty() == true) {
                    noIncomingEdges.add(dependentName)
                }
            }
        }

        for ((name, edges) in incomingEdges) {
            if (edges.isNotEmpty()) {
                throw IllegalArgumentException("Circular dependency detected: $name")
            }
        }

        listeners.clear()
        listeners.addAll(sortedListeners)

//        println("===== SORT =====")
//        for (listener in listeners) {
//            println("${listener.name}   Must Run Before: ${listener.mustRunBefore}   Must Run After: ${listener.mustRunAfter}")
//        }
//        println("================")
    }


    open operator fun invoke(event: AbstractEvent) {
        for (listener in ArrayList(listeners)) {
            try {
                listener(event)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }


}

