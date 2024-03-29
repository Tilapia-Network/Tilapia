package net.tilapiamc.common.events.annotation

import net.tilapiamc.common.events.*
import java.lang.reflect.Method
import java.lang.reflect.Modifier


private fun Any.getListenerMethods(): Collection<Method> {
    return javaClass.methods
        .filter {
            it.isAnnotationPresent(Subscribe::class.java) &&
                    it.parameterCount == 1 &&
                    Any::class.java.isAssignableFrom(it.parameterTypes[0]) &&
                    !Modifier.isStatic(it.modifiers)
        }
}

private fun Method.getEventType(): Class<AbstractEvent> {
    return parameterTypes.first() as Class<AbstractEvent>
}
private fun Method.getListenerAnnotation(): Subscribe {
    return getAnnotation(Subscribe::class.java)
}

private val associatedListeners = HashMap<Any, List<EventListener>>()

fun AbstractEventsManager.registerAnnotationBasedListener(listener: Any, includeEventSubClasses: Boolean = true, filter: (event: AbstractEvent) -> Boolean = { true }) {
    if (listener in associatedListeners) {
        return
//        ("Listener has already been registered!")
    }
    val listenerMethods = listener.getListenerMethods()
    val listeners = ArrayList<EventListener>()
    for (method in listenerMethods) {
        val annotation = method.getListenerAnnotation()
        val classEventListener = ClassEventListener(
            annotation.name,
            annotation.mustRunBefore.toSet(),
            annotation.mustRunAfter.toSet(),
            { method.invoke(listener, it) },
            method.getEventType(),
            includeEventSubClasses,
            filter
        )
        listeners.add(classEventListener)
        registerListener(classEventListener)
    }
    associatedListeners[listener] = listeners
}

fun AbstractEventsManager.unregisterAnnotationBasedListener(listener: Any) {
    if (listener !in associatedListeners) {
        throw IllegalArgumentException("Listener has not been registered yet!")
    }
    associatedListeners[listener]!!.forEach {
        unregisterListener(it)
    }
    associatedListeners.remove(listener)
}


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Subscribe(val name: String,
                           val mustRunBefore: Array<String> = arrayOf(),
                           val mustRunAfter: Array<String> = arrayOf(),
    )