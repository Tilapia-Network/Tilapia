package net.tilapiamc.api.events

typealias EventFilter = (event: AbstractEvent) -> Boolean
typealias EventHandler = (event: AbstractEvent) -> Unit

open class EventListener(val name: String, val mustRunBefore: Set<String>, val mustRunAfter: Set<String>, val eventHandler: EventHandler) {

    open operator fun invoke(event: AbstractEvent) {
        eventHandler(event)
    }

}

open class FilteredEventListener(name: String,
                                 mustRunBefore: Set<String>,
                                 mustRunAfter: Set<String>,
                                 val filter: EventFilter,
                                 eventHandler: EventHandler):
        EventListener(name, mustRunBefore, mustRunAfter, {
            if (filter(it)) {
                eventHandler(it)
            }
        })

open class ClassEventListener<T: AbstractEvent>(name: String,
                              mustRunBefore: Set<String>,
                              mustRunAfter: Set<String>,
                              eventHandler: (event: T) -> Unit,
                              val clazz: Class<T>,
                              val subClasses: Boolean = true,
                              filter: (event: AbstractEvent) -> Boolean = { true }
    ):
        FilteredEventListener(name, mustRunBefore, mustRunAfter,
            { if (subClasses) clazz.isAssignableFrom(it.javaClass) && filter(it) else clazz == it.javaClass && filter(it) },
            {
                eventHandler(it as T)
            }
        )