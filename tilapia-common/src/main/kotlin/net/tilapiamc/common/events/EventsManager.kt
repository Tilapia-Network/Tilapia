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
        val comparator = ListenerComparator()
        listeners.sortWith(comparator)
    }
    operator fun invoke(event: AbstractEvent) {
        for (listener in ArrayList(listeners)) {
            listener(event)
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

            if (obj1.mustRunAfter.contains(obj2.name)) {
                return 1
            }
            if (obj2.mustRunAfter.contains(obj1.name)) {
                return -1
            }
            if (obj1.mustRunBefore.contains(obj2.name)) {
                return -1
            }
            return if (obj2.mustRunBefore.contains(obj1.name)) {
                1
            } else obj1.name.compareTo(obj2.name)

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

