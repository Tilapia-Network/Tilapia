package net.tilapiamc.common

class SuspendEventTarget<T>(val exceptionHandler: (suspend (Throwable) -> Boolean)? = null,
                            val handlers: ArrayList<suspend (T) -> Unit> = ArrayList()): MutableList<suspend (T) -> Unit> by handlers {

    constructor(ignoreExceptions: Boolean): this({ ignoreExceptions })


    suspend operator fun invoke(event: T) {
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