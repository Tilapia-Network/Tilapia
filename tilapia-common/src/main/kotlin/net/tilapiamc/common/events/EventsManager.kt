package net.tilapiamc.common.events


typealias AbstractEvent = Any

abstract class AbstractEventsManager {

    init {

    }


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
            sortedListeners.add(listener)
//            println(listener.name)

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

    inner class ListenerComparator : Comparator<EventListener> {
        private val visited: MutableSet<String>
        private val recursionStack: MutableSet<String>

        init {
            visited = HashSet()
            recursionStack = HashSet()
        }

        override fun compare(obj1: EventListener, obj2: EventListener): Int {
            visited.clear()
            recursionStack.clear()

            if (hasCycle(obj1, obj2)) {
                error("Circular dependency detected")
            }

            val result = if (obj1.mustRunAfter.contains(obj2.name)) {
                1
            } else if (obj2.mustRunAfter.contains(obj1.name)) {
                -1
            } else if (obj1.mustRunBefore.contains(obj2.name)) {
                -1
            } else if (obj2.mustRunBefore.contains(obj1.name)) {
                1
            } else 0

            println("Comparing ${obj1.name} (A: ${obj1.mustRunAfter}  B: ${obj1.mustRunBefore}) / ${obj2.name}  (A: ${obj2.mustRunAfter}  B: ${obj2.mustRunBefore})   Result: $result")
            return result

        }

        private fun hasCycle(obj1: EventListener, obj2: EventListener): Boolean {
            visited.add(obj1.name)
            recursionStack.add(obj1.name)

            for (name in obj1.mustRunAfter) {
                if (!visited.contains(name)) {
                    val next = listenersByName[name]
                    if (next != null && hasCycle(next, obj2)) {
                        return true
                    }
                } else if (recursionStack.contains(name)) {
                    return true
                }
            }

            recursionStack.remove(obj1.name)
            return false
        }
    }

}

