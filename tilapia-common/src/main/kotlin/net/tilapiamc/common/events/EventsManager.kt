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

