package net.tilapiamc.common

class EventTarget<T>(val exceptionHandler: ((Throwable) -> Boolean)? = null,
                     val handlers: ArrayList<(T) -> Unit> = ArrayList()): MutableList<(T) -> Unit> by handlers {



    operator fun invoke(event: T) {
        for (allHandler in ArrayList(this)) {
            try {
                allHandler(event)
            } catch (e: Throwable) {
                if (exceptionHandler != null) {
                    if (exceptionHandler.invoke(e)) continue
                }
                throw e
            }
        }
    }

}