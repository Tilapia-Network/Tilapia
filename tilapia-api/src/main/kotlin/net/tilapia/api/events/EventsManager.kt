package net.tilapia.api.events

typealias AbstractEvent = Any

object EventsManager {

    val listenersByName = LinkedHashMap<String, EventListener>()
    val listeners = ArrayList<EventListener>()

    fun registerListener(listener: EventListener) {
        synchronized(listeners) {
            listenersByName[listener.name] = listener
            listeners.add(listener)
            sortListeners()
        }
    }

    fun unregisterListener(listener: EventListener) {
        synchronized(listeners) {
            listenersByName.remove(listener.name)
            listeners.remove(listener)
        }

    }

    private fun sortListeners() {
        val beforeEdges = mutableMapOf<String, MutableList<String>>()
        val afterEdges = mutableMapOf<String, MutableList<String>>()
        val indegrees = mutableMapOf<String, Int>()

        // Build graph
        for (listener in listeners) {
            if (!beforeEdges.containsKey(listener.name)) {
                beforeEdges[listener.name] = mutableListOf()
            }
            if (!afterEdges.containsKey(listener.name)) {
                afterEdges[listener.name] = mutableListOf()
            }
            for (before in listener.mustRunBefore.filter { it in listeners.map { it.name } }) {
                if (!beforeEdges.containsKey(before)) {
                    beforeEdges[before] = mutableListOf()
                }
                beforeEdges[before]!!.add(listener.name)
            }
            for (after in listener.mustRunAfter.filter { it in listeners.map { it.name } }) {
                if (!afterEdges.containsKey(after)) {
                    afterEdges[after] = mutableListOf()
                }
                afterEdges[after]!!.add(listener.name)
            }
            indegrees[listener.name] = beforeEdges[listener.name]!!.size
        }

        val sortedObjects = mutableListOf<EventListener>()

        while (true) {
            val candidates = mutableListOf<String>()

            // Find nodes with no incoming edges
            for ((name, indegree) in indegrees) {
                if (indegree == 0) {
                    candidates.add(name)
                }
            }

            if (candidates.isEmpty()) {
                // There is a cycle in the graph
                throw IllegalArgumentException("Circular dependency detected")
            }

            // Add nodes to the output list
            for (name in candidates) {
                sortedObjects.addAll(listeners.filter { it.name == name })
                indegrees.remove(name)

                // Remove outgoing edges
                for (nextName in afterEdges[name]!!) {
                    beforeEdges[nextName]!!.remove(name)
                    indegrees[nextName] = indegrees[nextName]!! - 1
                }
            }

            // Remove incoming edges
            for (name in candidates) {
                for (prevName in beforeEdges[name]!!) {
                    afterEdges[prevName]!!.remove(name)
                }
            }

            if (indegrees.isEmpty()) {
                // All nodes have been sorted
                break
            }
        }
        listeners.clear()
        listeners.addAll(sortedObjects)
    }

    fun fireEvent(event: AbstractEvent) {
        for (listener in listeners) {
            listener(event)
        }
    }

}
